package com.minersstudios.mscore.plugin.status;

import com.google.common.base.Joiner;
import org.jetbrains.annotations.*;

import java.util.*;

public class StatusWatcher {
    private final Set<SuccessStatus> successStatusSet;
    private final Set<FailureStatus> failureStatusSet;
    private final Runnable successRunnable;
    private final Runnable failureRunnable;
    private final boolean isAnySuccess;
    private final boolean isAnyFailure;

    /**
     * Constructs a new watcher with the specified builder
     *
     * @param builder Builder to be used
     */
    private StatusWatcher(final @NotNull Builder builder) {
        this.successStatusSet = builder.successStatusSet;
        this.failureStatusSet = builder.failureStatusSet;
        this.successRunnable = builder.successRunnable;
        this.failureRunnable = builder.failureRunnable;
        this.isAnySuccess = builder.anySuccess;
        this.isAnyFailure = builder.anyFailure;
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
     * @return An unmodifiable set of success statuses
     */
    public @NotNull @Unmodifiable Set<SuccessStatus> successStatusSet() {
        return Collections.unmodifiableSet(this.successStatusSet);
    }

    /**
     * @return An unmodifiable set of failure statuses
     */
    public @NotNull @Unmodifiable Set<FailureStatus> failureStatusSet() {
        return Collections.unmodifiableSet(this.failureStatusSet);
    }

    /**
     * @return The success runnable
     */
    public @Nullable Runnable getSuccessRunnable() {
        return this.successRunnable;
    }

    /**
     * @return The failure runnable
     */
    public @Nullable Runnable getFailureRunnable() {
        return this.failureRunnable;
    }

    /**
     * @return Whether any success status will trigger the success-runnable,
     *         even if there are other success statuses that have not been
     *         triggered yet. Otherwise, the success-runnable will only be
     *         triggered if all success statuses have been triggered
     *         successfully.
     */
    public boolean isAnySuccess() {
        return this.isAnySuccess;
    }

    /**
     * @return Whether any failure status will trigger the failure-runnable,
     *         even if there are other failure statuses that have not been
     *         triggered yet. Otherwise, the failure-runnable will only be
     *         triggered if all failure statuses have been triggered
     *         successfully.
     */
    public boolean isAnyFailure() {
        return this.isAnyFailure;
    }

    /**
     * @param first First status to be checked
     * @param rest  Rest of the statuses to be checked
     * @return True if all the statuses are present, false otherwise
     * @throws UnsupportedOperationException If the status type is not supported
     * @see #contains(PluginStatus)
     */
    public boolean containsAll(
            final @NotNull PluginStatus first,
            final PluginStatus @NotNull ... rest
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
     * @param first First status to be checked
     * @param rest  Rest of the statuses to be checked
     * @return True if any of the statuses is present, false otherwise
     * @throws UnsupportedOperationException If the status type is not supported
     * @see #contains(PluginStatus)
     */
    public boolean containsAny(
            final @NotNull PluginStatus first,
            final PluginStatus @NotNull ... rest
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
     * @param status Status to be checked
     * @return True if the status is present, false otherwise
     * @throws UnsupportedOperationException If the status type is not supported
     * @see #containsSuccess(SuccessStatus)
     * @see #containsFailure(FailureStatus)
     */
    public boolean contains(final @Nullable PluginStatus status) throws UnsupportedOperationException {
        if (status == null) {
            return false;
        }

        if (status instanceof final SuccessStatus success) {
            return this.containsSuccess(success);
        } else if (status instanceof final FailureStatus failure) {
            return this.containsFailure(failure);
        }

        throw new UnsupportedOperationException("Unsupported status type: " + status.getClass().getName());
    }

    /**
     * @param status Status to be checked
     * @return True if the status is present, false otherwise
     */
    @Contract("null -> false")
    public boolean containsSuccess(final @Nullable SuccessStatus status) {
        return status != null
                && this.successStatusSet.contains(status);
    }

    /**
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
     * @param status Status to be checked
     * @return True if the status is present, false otherwise
     */
    public boolean containsFailure(final @Nullable FailureStatus status) {
        return status != null
                && this.failureStatusSet.contains(status);
    }

    /**
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
     * @param status Status to be checked
     * @return True if the status is present and the runnable was run, false
     *         otherwise
     */
    public boolean tryRun(final @NotNull PluginStatus status) {
        if (status instanceof final SuccessStatus success) {
            return this.tryRunSuccess(success);
        } else if (status instanceof final FailureStatus failure) {
            return this.tryRunFailure(failure);
        }

        throw new UnsupportedOperationException("Unsupported status type: " + status.getClass().getName());
    }

    /**
     * Removes the specified status from the success status set and runs
     * the success-runnable if all success statuses have been triggered, or
     * if {@link #isAnySuccess} is true
     *
     * @param status Status to be checked
     * @return True if the status is present and the success runnable was
     *         run, false otherwise
     */
    public boolean tryRunSuccess(final @NotNull SuccessStatus status) {
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
     */
    public boolean tryRunFailure(final @NotNull FailureStatus status) {
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
     * @return Hash code of the watcher
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                this.successStatusSet,
                this.failureStatusSet,
                this.successRunnable,
                this.failureRunnable
        );
    }

    /**
     * @param obj The object to compare
     * @return True if the object is equal to this watcher
     */
    @Override
    public boolean equals(final @Nullable Object obj) {
        return obj == this
                || (
                        obj instanceof StatusWatcher that
                        && this.successStatusSet.equals(that.successStatusSet)
                        && this.failureStatusSet.equals(that.failureStatusSet)
                        && Objects.equals(this.successRunnable, that.successRunnable)
                        && Objects.equals(this.failureRunnable, that.failureRunnable)
                        && this.isAnySuccess == that.isAnySuccess
                        && this.isAnyFailure == that.isAnyFailure
                );
    }

    /**
     * @return A string representation of the watcher
     */
    @Override
    public @NotNull String toString() {
        return "Watcher{" +
                "successStatusSet=[" + Joiner.on(", ").join(this.successStatusSet) + ']' +
                ", failureStatusSet=[" + Joiner.on(", ").join(this.failureStatusSet) + ']' +
                ", successRunnable=" + this.successRunnable +
                ", failureRunnable=" + this.failureRunnable +
                '}';
    }

    /**
     * Builder for {@link StatusWatcher}
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
         * Builds a new watcher from the builder values
         *
         * @return A new watcher
         * @throws IllegalStateException If there are no statuses present
         */
        @Contract(" -> new")
        public @NotNull StatusWatcher build() throws IllegalStateException {
            final boolean isEmptySuccess = this.successStatusSet.isEmpty();
            final boolean isEmptyFailure = this.failureStatusSet.isEmpty();

            if (
                    isEmptySuccess
                    && isEmptyFailure
            ) {
                throw new IllegalStateException("Cannot build watcher with no statuses");
            }

            final boolean hasAnyRunnable =
                    this.successRunnable != null
                    || this.failureRunnable != null;

            if (
                    !isEmptySuccess
                    && !hasAnyRunnable
            ) {
                throw new IllegalStateException("Cannot build watcher with success statuses and no runnable");
            }

            if (
                    !isEmptyFailure
                    && !hasAnyRunnable
            ) {
                throw new IllegalStateException("Cannot build watcher with failure statuses and no runnable");
            }

            return new StatusWatcher(this);
        }

        /**
         * @return A set of success statuses, or an empty unmodifiable set
         *         if there are no success statuses
         * @see #successStatuses(SuccessStatus, SuccessStatus...)
         */
        public @NotNull Set<SuccessStatus> successStatuses() {
            return this.successStatusSet;
        }

        /**
         * Sets the success statuses for the watcher. If any of the success
         * statuses provide a failure status, it will be added to the set of
         * failure statuses.
         *
         * @param first    First success status
         * @param rest     Rest of the success statuses
         * @return This builder
         */
        @Contract("_, _ -> this")
        public @NotNull Builder successStatuses(
                final @NotNull SuccessStatus first,
                final SuccessStatus @NotNull ... rest
        ) {
            this.successStatusSet = new HashSet<>(Arrays.asList(rest));

            this.successStatusSet.add(first);
            this.addFailureStatus(first.getFailureStatus());

            for (final var status : rest) {
                this.addFailureStatus(status.getFailureStatus());
            }

            return this;
        }

        /**
         * @return A set of failure statuses, or an empty unmodifiable set
         *         if there are no failure statuses
         * @see #failureStatuses(FailureStatus, FailureStatus...)
         */
        public @NotNull Set<FailureStatus> failureStatuses() {
            return this.failureStatusSet;
        }

        /**
         * Sets the failure statuses for the watcher
         *
         * @param first    First failure status
         * @param rest     Rest of the failure statuses
         * @return This builder
         */
        @Contract("_, _ -> this")
        public @NotNull Builder failureStatuses(
                final @NotNull FailureStatus first,
                final FailureStatus @NotNull ... rest
        ) {
            this.failureStatusSet = new HashSet<>(Arrays.asList(rest));

            this.failureStatusSet.add(first);

            return this;
        }

        /**
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
         * @param successRunnable The success runnable
         * @return This builder
         */
        @Contract("_ -> this")
        public @NotNull Builder successRunnable(final @Nullable Runnable successRunnable) {
            this.successRunnable = successRunnable;

            return this;
        }

        /**
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
         * @param failureRunnable The failure runnable
         * @return This builder
         */
        @Contract("_ -> this")
        public @NotNull Builder failureRunnable(final @Nullable Runnable failureRunnable) {
            this.failureRunnable = failureRunnable;

            return this;
        }

        /**
         * @return Whether any success status will trigger the
         *         success-runnable, even if there are other success
         *         statuses that have not been triggered yet. Otherwise,
         *         the success-runnable will only be triggered if all
         *         success statuses have been triggered successfully.
         */
        public boolean anySuccess() {
            return this.anySuccess;
        }

        /**
         * @param anySuccess Whether any success status will trigger the
         *                   success-runnable, even if there are other
         *                   success statuses that have not been triggered
         *                   yet. Otherwise, the success-runnable will only
         *                   be triggered if all success statuses have been
         *                   triggered successfully.
         * @return This builder
         */
        @Contract("_ -> this")
        public @NotNull Builder anySuccess(final boolean anySuccess) {
            this.anySuccess = anySuccess;

            return this;
        }

        /**
         * @return Whether any failure status will trigger the
         *         failure-runnable, even if there are other failure
         *         statuses that have not been triggered yet. Otherwise,
         *         the failure-runnable will only be triggered if all
         *         failure statuses have been triggered successfully.
         */
        public boolean anyFailure() {
            return this.anyFailure;
        }

        /**
         * @param anyFailure Whether any failure status will trigger the
         *                   failure-runnable, even if there are other
         *                   failure statuses that have not been triggered
         *                   yet. Otherwise, the failure-runnable will only
         *                   be triggered if all failure statuses have been
         *                   triggered successfully.
         * @return This builder
         */
        @Contract("_ -> this")
        public @NotNull Builder anyFailure(final boolean anyFailure) {
            this.anyFailure = anyFailure;

            return this;
        }

        private void addFailureStatus(final @Nullable FailureStatus status) {
            if (status == null) {
                return;
            }

            if (this.failureStatusSet.isEmpty()) {
                this.failureStatusSet = new HashSet<>();
            }

            this.failureStatusSet.add(status);
        }
    }
}
