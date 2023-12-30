package com.minersstudios.mscore.plugin.status;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    private final AtomicReference<PluginStatus> highStatus;
    private final Set<PluginStatus> lowStatusSet;
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
     * @return An unmodifiable set of all low-priority statuses
     */
    public @NotNull @Unmodifiable Set<PluginStatus> lowStatusSet() {
        return Collections.unmodifiableSet(this.lowStatusSet);
    }

    /**
     * @return An unmodifiable set of all statuses
     */
    public @NotNull @Unmodifiable Set<PluginStatus> statusSet() {
        final var set = new ObjectOpenHashSet<>(this.lowStatusSet);
        final PluginStatus highStatus = this.highStatus.get();

        if (highStatus != null) {
            set.add(highStatus);
        }

        return Collections.unmodifiableSet(set);
    }

    /**
     * @return An unmodifiable watcher list
     */
    public @NotNull @Unmodifiable List<StatusWatcher> watcherList() {
        return Collections.unmodifiableList(this.watcherList);
    }

    /**
     * @return An optional containing the high-priority status if present,
     *         otherwise an empty optional
     */
    public @NotNull Optional<PluginStatus> getHighStatus() {
        return Optional.ofNullable(this.highStatus.get());
    }

    /**
     * Sets the specified status and runs all the registered watchers with
     * this status
     *
     * @param status Status to be set
     */
    public void setStatus(final @NotNull PluginStatus status) {
        if (status.getPriority() == PluginStatus.Priority.HIGH) {
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
     * @param status Status to be checked
     * @return A new list of watchers that contain the specified status
     * @throws UnsupportedOperationException If the status type is not supported
     */
    @Contract("_ -> new")
    public @NotNull List<StatusWatcher> getWatchers(final @NotNull PluginStatus status) throws UnsupportedOperationException {
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
