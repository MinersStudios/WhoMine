package com.minersstudios.mscore.plugin.status;

import org.jetbrains.annotations.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Handles plugin statuses and runs registered watchers when a status is set
 *
 * @see PluginStatus
 * @see StatusWatcher
 */
public final class StatusHandler {
    private final List<StatusWatcher> watcherList;
    private final Set<PluginStatus> lowStatusSet;
    private final AtomicReference<PluginStatus> highStatus;

    /**
     * Constructs a new status handler
     */
    @ApiStatus.Internal
    public StatusHandler() {
        this.watcherList = new CopyOnWriteArrayList<>();
        this.lowStatusSet = new CopyOnWriteArraySet<>();
        this.highStatus = new AtomicReference<>(null);
    }

    /**
     * @return An unmodifiable view of the watcher list
     */
    public @NotNull @UnmodifiableView List<StatusWatcher> watcherList() {
        return Collections.unmodifiableList(this.watcherList);
    }

    /**
     * @return An unmodifiable view of the low-priority status set
     */
    public @NotNull @UnmodifiableView Set<PluginStatus> lowStatusSet() {
        return Collections.unmodifiableSet(this.lowStatusSet);
    }

    /**
     * @return An unmodifiable set of all statuses
     */
    public @NotNull @Unmodifiable Set<PluginStatus> statusSet() {
        final var list = new HashSet<>(this.lowStatusSet);
        final PluginStatus highStatus = this.highStatus.get();

        if (highStatus != null) {
            list.add(highStatus);
        }

        return Collections.unmodifiableSet(list);
    }

    /**
     * @param status Status to be checked
     * @return A new list of watchers that contain the specified status
     * @throws UnsupportedOperationException If the status type is not supported
     */
    @Contract("_ -> new")
    public @NotNull List<StatusWatcher> getWatchers(final @NotNull PluginStatus status) throws UnsupportedOperationException {
        final var list = new ArrayList<StatusWatcher>();
        final SuccessStatus successStatus;
        final FailureStatus failureStatus;

        if (status instanceof final SuccessStatus success) {
            successStatus = success;
            failureStatus = null;
        } else if (status instanceof final FailureStatus failure) {
            successStatus = null;
            failureStatus = failure;
        } else {
            throw new UnsupportedOperationException("Unsupported status type: " + status.getClass().getName());
        }

        for (final var watcher : this.watcherList) {
            if (successStatus != null) {
                if (watcher.containsSuccess(successStatus)) {
                    list.add(watcher);
                }
            } else {
                if (watcher.containsFailure(failureStatus)) {
                    list.add(watcher);
                }
            }
        }

        return list;
    }

    /**
     * Adds the specified watcher and runs it with all the statuses that are
     * already present
     *
     * @param watcher Watcher to be added
     */
    public void addWatcher(final @NotNull StatusWatcher watcher) {
        this.watcherList.add(watcher);

        if (!this.lowStatusSet.isEmpty()) {
            final var completed = new ArrayList<PluginStatus>();

            for (final var status : this.lowStatusSet) {
                if (status instanceof final SuccessStatus success) {
                    if (watcher.runSuccess(success)) {
                        completed.add(success);
                    }
                } else if (status instanceof final FailureStatus failure) {
                    if (watcher.runFailure(failure)) {
                        completed.add(failure);
                    }
                }
            }

            for (final var status : completed) {
                this.lowStatusSet.remove(status);
            }
        }
    }

    /**
     * Removes the specified watcher
     *
     * @param watcher Watcher to be removed
     */
    public void removeWatcher(final @NotNull StatusWatcher watcher) {
        this.watcherList.remove(watcher);
    }

    /**
     * @return An optional containing the high-priority status if present,
     *         otherwise an empty optional
     */
    public @NotNull Optional<PluginStatus> get() {
        return Optional.ofNullable(this.highStatus.get());
    }

    /**
     * Sets the specified status and runs all the registered watchers with
     * this status
     *
     * @param status Status to be set
     */
    public void set(final @NotNull PluginStatus status) {
        if (status.getPriority() == PluginStatus.Priority.HIGH) {
            this.highStatus.set(status);
        } else {
            this.lowStatusSet.add(status);
        }

        if (!this.watcherList.isEmpty()) {
            final var completed = new ArrayList<StatusWatcher>();

            for (final var watcher : this.watcherList) {
                if (status instanceof final SuccessStatus success) {
                    if (watcher.runSuccess(success)) {
                        completed.add(watcher);
                    }
                } else if (status instanceof final FailureStatus failure) {
                    if (watcher.runFailure(failure)) {
                        completed.add(watcher);
                    }
                }
            }

            this.watcherList.removeAll(completed);
        }
    }

    /**
     * @param status Status to be checked
     * @return True if the status is present, false otherwise
     */
    public boolean contains(final @NotNull PluginStatus status) {
        return status.isHighPriority()
                ? status.equals(this.highStatus.get())
                : this.lowStatusSet.contains(status);
    }

    /**
     * @param first First status to be checked
     * @param rest  Rest of the statuses to be checked
     * @return True if any of the statuses is present, false otherwise
     */
    public boolean containsAny(
            final @NotNull PluginStatus first,
            final PluginStatus @NotNull ... rest
    ) {
        if (this.contains(first)) {
            return true;
        }

        for (final var status : rest) {
            if (this.contains(status)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @param first First status to be checked
     * @param rest  Rest of the statuses to be checked
     * @return True if all the statuses are present, false otherwise
     * @throws IllegalArgumentException If there are multiple high-priority
     *                                  statuses specified
     */
    public boolean containsAll(
            final @NotNull PluginStatus first,
            final PluginStatus @NotNull ... rest
    ) throws IllegalArgumentException {
        final PluginStatus highStatus = this.highStatus.get();
        boolean hasHighPriority = first.isHighPriority();

        if (
                hasHighPriority
                && !first.equals(highStatus)
        ) {
            return false;
        }

        for (final var status : rest) {
            if (status.isHighPriority()) {
                if (hasHighPriority) {
                    throw new IllegalArgumentException("Cannot contain multiple high-priority statuses");
                }

                hasHighPriority = true;

                if (!status.equals(highStatus)) {
                    return false;
                }
            } else if (!this.lowStatusSet.contains(status)) {
                return false;
            }
        }

        return true;
    }
}
