package com.minersstudios.mscore.status;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Handles statuses and runs registered watchers when a status is set
 *
 * @see Status
 * @see StatusWatcher
 */
@ThreadSafe
public final class StatusHandler {
    private final AtomicReference<Status> highStatus;
    private final Set<Status> lowStatusSet;
    private final List<StatusWatcher> watcherList;

    /**
     * Constructs a new status handler
     */
    public StatusHandler() {
        this.highStatus = new AtomicReference<>(null);
        this.lowStatusSet = new CopyOnWriteArraySet<>();
        this.watcherList = new CopyOnWriteArrayList<>();
    }

    /**
     * Returns the high-priority status
     *
     * @return An optional containing the high-priority status if present,
     *         otherwise an empty optional
     */
    public @NotNull Optional<Status> getHighStatus() {
        return Optional.ofNullable(this.highStatus.get());
    }

    /**
     * Returns an unmodifiable set view of all high-priority statuses
     *
     * @return An unmodifiable set view of all high-priority statuses
     */
    public @NotNull @UnmodifiableView Set<Status> lowStatusSet() {
        return Collections.unmodifiableSet(this.lowStatusSet);
    }

    /**
     * Returns an unmodifiable view of the watcher list
     *
     * @return An unmodifiable view of the watcher list
     */
    public @NotNull @UnmodifiableView List<StatusWatcher> watcherList() {
        return Collections.unmodifiableList(this.watcherList);
    }

    /**
     * Gets all the watchers that contain the specified status
     *
     * @param status Status to be checked
     * @return A new list of watchers that contain the specified status
     * @throws UnsupportedOperationException If the status type is not supported
     */
    @Contract("_ -> new")
    public @NotNull List<StatusWatcher> getWatchers(final @NotNull Status status) throws UnsupportedOperationException {
        final var list = new ObjectArrayList<StatusWatcher>();

        for (final var watcher : this.watcherList) {
            if (watcher.contains(status)) {
                list.add(watcher);
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
        for (final var status : this.lowStatusSet) {
            if (watcher.tryRun(status)) {
                return;
            }
        }

        this.watcherList.add(watcher);
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
     * Assigns the specified status and runs all the registered watchers with
     * this status
     *
     * @param status Status to be assigned
     */
    public void assignStatus(final @NotNull Status status) {
        if (status.getPriority() == Status.Priority.HIGH) {
            this.highStatus.set(status);
        } else {
            this.lowStatusSet.add(status);
        }

        if (!this.watcherList.isEmpty()) {
            final var completed = new ObjectArrayList<StatusWatcher>();

            for (final var watcher : this.watcherList) {
                if (watcher.tryRun(status)) {
                    completed.add(watcher);
                }
            }

            this.watcherList.removeAll(completed);
        }
    }

    /**
     * Returns whether all the specified statuses are present
     *
     * @param first First status to be checked
     * @param rest  Rest of the statuses to be checked
     * @return True if all the statuses are present, false otherwise
     * @throws IllegalArgumentException If there are multiple high-priority
     *                                  statuses specified
     */
    public boolean containsAll(
            final @NotNull Status first,
            final Status @NotNull ... rest
    ) throws IllegalArgumentException {
        final Status highStatus = this.highStatus.get();
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

    /**
     * Returns whether any of the specified statuses are present
     *
     * @param first First status to be checked
     * @param rest  Rest of the statuses to be checked
     * @return True if any of the statuses is present, false otherwise
     */
    public boolean containsAny(
            final @NotNull Status first,
            final Status @NotNull ... rest
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
     * Returns whether the specified status is present
     *
     * @param status Status to be checked
     * @return True if the status is present, false otherwise
     */
    public boolean contains(final @NotNull Status status) {
        return status.isHighPriority()
               ? status.equals(this.highStatus.get())
               : this.lowStatusSet.contains(status);
    }
}
