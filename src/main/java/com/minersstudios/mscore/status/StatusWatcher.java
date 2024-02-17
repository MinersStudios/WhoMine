package com.minersstudios.mscore.status;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.*;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * Status watcher for handling statuses.
 * <br>
 * The watcher will run the status runnables when all statuses have been
 * triggered successfully, or if {@link #isAnySuccess()}/{@link #isAnyFailure()}
 * is true, when any of the statuses have been triggered successfully.
 * <br>
 * You can use the {@link #builder()} to create a new watcher.
 *
 * @see StatusHandler
 */
@ThreadSafe
public final class StatusWatcher {
    private final Set<SuccessStatus> successStatusSet;
    private final Set<FailureStatus> failureStatusSet;
    private final Runnable successRunnable;
    private final Runnable failureRunnable;
    private final boolean isAnySuccess;
    private final boolean isAnyFailure;

    private static final String FORMAT = "%s{" +
            "successStatusSet=[%s], " +
            "failureStatusSet=[%s], " +
            "successRunnable=%s, " +
            "failureRunnable=%s, " +
            "isAnySuccess=%s, " +
            "isAnyFailure=%s" +
            '}';

    private StatusWatcher(final @NotNull Builder builder) {
        this.successStatusSet = builder.successStatusSet;
        this.failureStatusSet = builder.failureStatusSet;
        this.successRunnable = builder.successRunnable;
        this.failureRunnable = builder.failureRunnable;
        this.isAnySuccess = builder.anySuccess;
        this.isAnyFailure = builder.anyFailure;
    }

    /**
     * Returns an unmodifiable set view of all statuses
     *
     * @return An unmodifiable set view of all statuses
     */
    public @NotNull @UnmodifiableView Set<Status> statuses() {
        return Sets.union(this.successStatusSet, this.failureStatusSet);
    }

    /**
     * Returns an unmodifiable set view of success statuses
     *
     * @return An unmodifiable set view of success statuses
     */
    public @NotNull @UnmodifiableView Set<SuccessStatus> successStatusSet() {
        return Collections.unmodifiableSet(this.successStatusSet);
    }

    /**
     * Returns an unmodifiable set view of failure statuses
     *
     * @return An unmodifiable set view of failure statuses
     */
    public @NotNull @UnmodifiableView Set<FailureStatus> failureStatusSet() {
        return Collections.unmodifiableSet(this.failureStatusSet);
    }

    /**
     * Returns the success runnable
     *
     * @return The success runnable
     */
    public @Nullable Runnable getSuccessRunnable() {
        return this.successRunnable;
    }

    /**
     * Returns the failure runnable
     *
     * @return The failure runnable
     */
    public @Nullable Runnable getFailureRunnable() {
        return this.failureRunnable;
    }

    /**
     * Returns whether any success status will trigger the success-runnable,
     * even if there are other success statuses that have not been triggered
     * yet.
     * <br>
     * Otherwise, the success-runnable will only be triggered if all success
     * statuses have been triggered successfully.
     *
     * @return Whether any success status will trigger the success-runnable
     */
    public boolean isAnySuccess() {
        return this.isAnySuccess;
    }

    /**
     * Returns whether any failure status will trigger the failure-runnable,
     * even if there are other failure statuses that have not been triggered
     * yet.
     * <br>
     * Otherwise, the failure-runnable will only be triggered if all failure
     * statuses have been triggered successfully.
     *
     * @return Whether any failure status will trigger the failure-runnable
     */
    public boolean isAnyFailure() {
        return this.isAnyFailure;
    }

    /**
     * Returns whether all the specified statuses are present
     *
     * @param first First status to be checked
     * @param rest  Rest of the statuses to be checked
     * @return True if all the statuses are present, false otherwise
     * @throws UnsupportedOperationException If the status is not supported
     * @see #contains(Status)
     */
    public boolean containsAll(
            final @NotNull Status first,
            final Status @NotNull ... rest
    ) throws UnsupportedOperationException {
        if (!this.contains(first)) {
            return false;
        }

        for (final var status : rest) {
            if (!this.contains(status)) {
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
     * @throws UnsupportedOperationException If the status is not supported
     * @see #contains(Status)
     */
    public boolean containsAny(
            final @NotNull Status first,
            final Status @NotNull ... rest
    ) throws UnsupportedOperationException {
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
     * @throws UnsupportedOperationException If the status is not supported
     * @see #containsSuccess(SuccessStatus)
     * @see #containsFailure(FailureStatus)
     */
    public boolean contains(final @Nullable Status status) throws UnsupportedOperationException {
        return status != null
                && status.apply(
                        this::containsSuccess,
                        this::containsFailure
                );
    }

    /**
     * Returns whether all the specified success statuses are present
     *
     * @param first First status to be checked
     * @param rest  Rest of the statuses to be checked
     * @return True if all the statuses are present, false otherwise
     */
    public boolean containsAllSuccess(
            final @NotNull SuccessStatus first,
            final SuccessStatus @NotNull ... rest
    ) {
        if (!this.containsSuccess(first)) {
            return false;
        }

        for (final var status : rest) {
            if (!this.containsSuccess(status)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns whether any of the specified success statuses are present
     *
     * @param first First status to be checked
     * @param rest  Rest of the statuses to be checked
     * @return True if any of the statuses is present, false otherwise
     */
    public boolean containsAnySuccess(
            final @NotNull SuccessStatus first,
            final SuccessStatus @NotNull ... rest
    ) {
        if (this.containsSuccess(first)) {
            return true;
        }

        for (final var status : rest) {
            if (this.containsSuccess(status)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns whether the specified success status is present
     *
     * @param status Status to be checked
     * @return True if the status is present, false otherwise
     */
    @Contract("null -> false")
    public boolean containsSuccess(final @Nullable SuccessStatus status) {
        return status != null
                && this.successStatusSet.contains(status);
    }

    /**
     * Returns whether all the specified failure statuses are present
     *
     * @param first First status to be checked
     * @param rest  Rest of the statuses to be checked
     * @return True if all the statuses are present, false otherwise
     */
    public boolean containsAllFailure(
            final @NotNull FailureStatus first,
            final FailureStatus @NotNull ... rest
    ) {
        if (!this.containsFailure(first)) {
            return false;
        }

        for (final var status : rest) {
            if (!this.containsFailure(status)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns whether any of the specified failure statuses are present
     *
     * @param first First status to be checked
     * @param rest  Rest of the statuses to be checked
     * @return True if any of the statuses is present, false otherwise
     */
    public boolean containsAnyFailure(
            final @NotNull FailureStatus first,
            final FailureStatus @NotNull ... rest
    ) {
        if (this.containsFailure(first)) {
            return true;
        }

        for (final var status : rest) {
            if (this.containsFailure(status)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns whether the specified failure status is present
     *
     * @param status Status to be checked
     * @return True if the status is present, false otherwise
     */
    public boolean containsFailure(final @Nullable FailureStatus status) {
        return status != null
                && this.failureStatusSet.contains(status);
    }

    /**
     * Tries to run the runnable for the specified status
     *
     * @param status Status to be checked
     * @return True if the status is present and the runnable was run, false
     *         otherwise
     * @throws UnsupportedOperationException If the status type is not supported
     * @see #tryRunSuccess(SuccessStatus)
     * @see #tryRunFailure(FailureStatus)
     */
    public boolean tryRun(final @NotNull Status status) throws UnsupportedOperationException {
        return status.apply(
                this::tryRunSuccess,
                this::tryRunFailure
        );
    }

    /**
     * Removes the specified status from the success status set and runs
     * the success-runnable if all success statuses have been triggered, or
     * if {@link #isAnySuccess} is true
     *
     * @param status Status to be checked
     * @return True if the status is present and the success runnable was
     *         run, false otherwise
     * @see #runSuccessNow()
     */
    public boolean tryRunSuccess(final @NotNull SuccessStatus status) {
        synchronized (this.successStatusSet) {
            if (this.successStatusSet.contains(status)) {
                this.successStatusSet.remove(status);

                if (
                        this.isAnySuccess
                        || this.successStatusSet.isEmpty()
                ) {
                    this.runSuccessNow();

                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Removes the specified status from the failure status set and runs
     * the failure-runnable if all failure statuses have been triggered, or
     * if {@link #isAnyFailure} is true
     *
     * @param status Status to be checked
     * @return True if the status is present and the failure runnable was
     *         run, false otherwise
     * @see #runFailureNow()
     */
    public boolean tryRunFailure(final @NotNull FailureStatus status) {
        synchronized (this.failureStatusSet) {
            if (this.failureStatusSet.contains(status)) {
                this.failureStatusSet.remove(status);

                if (
                        this.isAnyFailure
                        || this.failureStatusSet.isEmpty()
                ) {
                    this.runFailureNow();

                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Runs the success-runnable immediately
     */
    public void runSuccessNow() {
        if (this.successRunnable != null) {
            this.successRunnable.run();
        }
    }

    /**
     * Runs the failure-runnable immediately
     */
    public void runFailureNow() {
        if (this.failureRunnable != null) {
            this.failureRunnable.run();
        }
    }

    /**
     * Returns the hash code of the watcher
     *
     * @return Hash code of the watcher
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + this.successStatusSet.hashCode();
        result = prime * result + this.failureStatusSet.hashCode();
        result = prime * result + (this.successRunnable == null ? 0 : this.successRunnable.hashCode());
        result = prime * result + (this.failureRunnable == null ? 0 : this.failureRunnable.hashCode());
        result = prime * result + Boolean.hashCode(this.isAnySuccess);
        result = prime * result + Boolean.hashCode(this.isAnyFailure);

        return result;
    }

    /**
     * Returns whether the specified object is equal to this watcher
     *
     * @param obj The object to compare
     * @return True if the object is equal to this watcher
     */
    @Contract("null -> false")
    @Override
    public boolean equals(final @Nullable Object obj) {
        return obj == this
                || (
                        obj instanceof StatusWatcher that
                        && this.successStatusSet.containsAll(that.successStatusSet)
                        && this.failureStatusSet.containsAll(that.failureStatusSet)
                        && Objects.equals(this.successRunnable, that.successRunnable)
                        && Objects.equals(this.failureRunnable, that.failureRunnable)
                        && this.isAnySuccess == that.isAnySuccess
                        && this.isAnyFailure == that.isAnyFailure
                );
    }

    /**
     * Returns a string representation of the watcher
     *
     * @return A string representation of the watcher
     */
    @Override
    public @NotNull String toString() {
        return String.format(
                FORMAT,
                this.getClass().getSimpleName(),
                Joiner.on(", ").join(this.successStatusSet),
                Joiner.on(", ").join(this.failureStatusSet),
                this.successRunnable,
                this.failureRunnable,
                this.isAnySuccess,
                this.isAnyFailure
        );
    }

    /**
     * Creates a new watcher builder
     *
     * @return A new builder
     */
    @Contract(" -> new")
    public static @NotNull Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@code StatusWatcher}
     *
     * @see StatusWatcher
     * @see StatusWatcher#builder()
     */
    public static final class Builder {
        private Set<SuccessStatus> successStatusSet;
        private Set<FailureStatus> failureStatusSet;
        private Runnable successRunnable;
        private Runnable failureRunnable;
        private boolean anySuccess;
        private boolean anyFailure;

        private Builder() {
            this.successStatusSet = Collections.emptySet();
            this.failureStatusSet = Collections.emptySet();
        }

        /**
         * Returns an unmodifiable set of statuses present in the builder
         *
         * @return An unmodifiable set of statuses
         * @see #statuses(Status, Status...)
         */
        public @NotNull @Unmodifiable Set<Status> statuses() {
            return Sets.union(this.successStatusSet, this.failureStatusSet);
        }

        /**
         * Adds the specified statuses to the builder
         *
         * @param first First status
         * @param rest  Rest of the statuses
         * @return This builder, for chaining
         * @throws UnsupportedOperationException If any of the statuses is not
         *                                       supported
         */
        @Contract("_, _ -> this")
        public @NotNull Builder statuses(
                final @NotNull Status first,
                final Status @NotNull ... rest
        ) throws UnsupportedOperationException {
            this.successStatusSet = new ObjectOpenHashSet<>();
            this.failureStatusSet = new ObjectOpenHashSet<>();

            this.addStatus(first);

            for (final var status : rest) {
                this.addStatus(status);
            }

            return this;
        }

        /**
         * Returns an unmodifiable set of success statuses
         *
         * @return An unmodifiable set of success statuses
         * @see #successStatuses(SuccessStatus, SuccessStatus...)
         */
        public @NotNull @Unmodifiable Set<SuccessStatus> successStatuses() {
            return Collections.unmodifiableSet(this.successStatusSet);
        }

        /**
         * Sets the success statuses for the watcher. If any of the success
         * statuses provide a failure status, it will be added to the set of
         * failure statuses.
         *
         * @param first First success status
         * @param rest  Rest of the success statuses
         * @return This builder, for chaining
         */
        @Contract("_, _ -> this")
        public @NotNull Builder successStatuses(
                final @NotNull SuccessStatus first,
                final SuccessStatus @NotNull ... rest
        ) {
            this.successStatusSet = new ObjectOpenHashSet<>(rest.length + 1);

            this.successStatusSet.add(first);
            this.addFailureStatus(first.getFailureStatus());

            for (final var status : rest) {
                this.successStatusSet.add(status);
                this.addFailureStatus(status.getFailureStatus());
            }

            return this;
        }

        /**
         * Returns an unmodifiable set of failure statuses
         *
         * @return An unmodifiable set of failure statuses
         * @see #failureStatuses(FailureStatus, FailureStatus...)
         */
        public @NotNull @Unmodifiable Set<FailureStatus> failureStatuses() {
            return Collections.unmodifiableSet(this.failureStatusSet);
        }

        /**
         * Sets the failure statuses for the watcher
         *
         * @param first First failure status
         * @param rest  Rest of the failure statuses
         * @return This builder, for chaining
         */
        @Contract("_, _ -> this")
        public @NotNull Builder failureStatuses(
                final @NotNull FailureStatus first,
                final FailureStatus @NotNull ... rest
        ) {
            this.failureStatusSet = new ObjectOpenHashSet<>(rest.length + 1);

            this.failureStatusSet.add(first);
            this.failureStatusSet.addAll(Arrays.asList(rest));

            return this;
        }

        /**
         * Returns the success runnable
         *
         * @return The success runnable
         * @see #successRunnable(Runnable)
         */
        public @UnknownNullability Runnable successRunnable() {
            return this.successRunnable;
        }

        /**
         * Sets the success-runnable for the watcher, which will be run when
         * all success statuses have been triggered successfully, or if
         * {@link #anySuccess} is true, when any success status has been
         * triggered successfully.
         *
         * @param runnable The runnable
         * @return This builder, for chaining
         */
        @Contract("_ -> this")
        public @NotNull Builder successRunnable(final @Nullable Runnable runnable) {
            this.successRunnable = runnable;

            return this;
        }

        /**
         * Returns the failure runnable
         *
         * @return The failure runnable
         * @see #failureRunnable(Runnable)
         */
        public @UnknownNullability Runnable failureRunnable() {
            return this.failureRunnable;
        }

        /**
         * Sets the failure-runnable for the watcher, which will be run when
         * all failure statuses have been triggered successfully, or if
         * {@link #anyFailure} is true, when any failure status has been
         * triggered successfully.
         *
         * @param runnable The runnable
         * @return This builder, for chaining
         */
        @Contract("_ -> this")
        public @NotNull Builder failureRunnable(final @Nullable Runnable runnable) {
            this.failureRunnable = runnable;

            return this;
        }

        /**
         * Returns whether any success status will trigger the success-runnable,
         * even if there are other success statuses that have not been
         * triggered yet.
         * <br>
         * Otherwise, the success-runnable will only be triggered if all
         * success statuses have been triggered successfully.
         *
         * @return Whether any success status will trigger the success-runnable
         */
        public boolean anySuccess() {
            return this.anySuccess;
        }

        /**
         * Sets whether any success status will trigger the success-runnable,
         * even if there are other success statuses that have not been
         * triggered yet.
         * <br>
         * Otherwise, the success-runnable will only be triggered if all
         * success statuses have been triggered successfully.
         *
         * @param anySuccess Whether any success status will trigger the
         *                   success-runnable
         * @return This builder, for chaining
         */
        @Contract("_ -> this")
        public @NotNull Builder anySuccess(final boolean anySuccess) {
            this.anySuccess = anySuccess;

            return this;
        }

        /**
         * Returns whether any failure status will trigger the failure-runnable,
         * even if there are other failure statuses that have not been
         * triggered yet.
         * <br>
         * Otherwise, the failure-runnable will only be triggered if all
         * failure statuses have been triggered successfully.
         *
         * @return Whether any failure status will trigger the failure-runnable
         */
        public boolean anyFailure() {
            return this.anyFailure;
        }

        /**
         * Sets whether any failure status will trigger the failure-runnable,
         * even if there are other failure statuses that have not been
         * triggered yet.
         * <br>
         * Otherwise, the failure-runnable will only be triggered if all
         * failure statuses have been triggered successfully.
         *
         * @param anyFailure Whether any failure status will trigger the
         *                   failure-runnable
         * @return This builder, for chaining
         */
        @Contract("_ -> this")
        public @NotNull Builder anyFailure(final boolean anyFailure) {
            this.anyFailure = anyFailure;

            return this;
        }

        /**
         * Builds a new watcher from the builder values
         *
         * @return A new status watcher
         * @throws IllegalStateException If there are no statuses, or if there
         *                               are no runnables
         */
        @Contract(" -> new")
        public @NotNull StatusWatcher build() throws IllegalStateException {
            if (
                    this.successStatusSet.isEmpty()
                    && this.failureStatusSet.isEmpty()
            ) {
                throw new IllegalStateException("Provide at least one status");
            }

            if (
                    this.successRunnable == null
                    && this.failureRunnable == null
            ) {
                throw new IllegalStateException("Provide success and/or failure runnables");
            }

            return new StatusWatcher(this);
        }

        /**
         * Returns a string representation of the builder
         *
         * @return A string representation of the builder
         */
        @Override
        public @NotNull String toString() {
            return FORMAT.formatted(
                    "StatusWatcher.Builder",
                    Joiner.on(", ").join(this.successStatusSet),
                    Joiner.on(", ").join(this.failureStatusSet),
                    this.successRunnable,
                    this.failureRunnable,
                    this.anySuccess,
                    this.anyFailure
            );
        }

        private void addStatus(final @Nullable Status status) throws UnsupportedOperationException {
            if (status != null) {
                status.accept(
                        this::addSuccessStatus,
                        this::addFailureStatus
                );
            }
        }

        private void addSuccessStatus(final @Nullable SuccessStatus status) {
            if (status == null) {
                return;
            }

            if (this.successStatusSet.isEmpty()) {
                this.successStatusSet = new ObjectOpenHashSet<>();
            }

            this.successStatusSet.add(status);
            this.addFailureStatus(status.getFailureStatus());
        }

        private void addFailureStatus(final @Nullable FailureStatus status) {
            if (status == null) {
                return;
            }

            if (this.failureStatusSet.isEmpty()) {
                this.failureStatusSet = new ObjectOpenHashSet<>();
            }

            this.failureStatusSet.add(status);
        }
    }
}
