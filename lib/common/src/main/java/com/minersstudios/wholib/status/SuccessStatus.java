package com.minersstudios.wholib.status;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents a successful status.
 * <br>
 * Factory methods for creating a successful status:
 * <ul>
 *     <li>{@link #success(String, StatusPriority)}</li>
 *     <li>{@link #success(String, StatusPriority, FailureStatus)}</li>
 *     <li>{@link #successLow(String)}
 *     <li>{@link #successLow(String, FailureStatus)}</li>
 *     <li>{@link #successHigh(String)}</li>
 *     <li>{@link #successHigh(String, FailureStatus)}</li>
 * </ul>
 *
 * @see Status
 */
@SuppressWarnings("unused")
@Immutable
public class SuccessStatus extends ImplStatus {

    private final FailureStatus failureStatus;

    protected SuccessStatus(
            final @StatusKey @NotNull String key,
            final @NotNull StatusPriority priority,
            final @Nullable FailureStatus failureStatus
    ) {
        super(key, priority);

        this.failureStatus = failureStatus;
    }

    /**
     * Returns the failure status for this successful status
     *
     * @return The failure status, or null if there is no failure status present
     */
    public final @Nullable FailureStatus getFailureStatus() {
        return this.failureStatus;
    }

    @Override
    public int hashCode() {
        return this.failureStatus == null
               ? super.hashCode()
               : 31 * super.hashCode() + this.failureStatus.hashCode();
    }

    @Contract("null -> false")
    @Override
    public boolean equals(final @Nullable Object obj) {
        return this == obj
                || (
                        obj instanceof SuccessStatus that
                        && this.getKey().equals(that.getKey())
                        && this.getPriority() == that.getPriority()
                        && Objects.equals(this.failureStatus, that.failureStatus)
                );
    }

    @Override
    public @NotNull String toString() {
        return this.getKey() +
                "{priority=" + this.getPriority() +
                ", failureStatus=" + this.failureStatus +
                '}';
    }

    @ApiStatus.OverrideOnly
    @Override
    public void accept(
            final @NotNull Consumer<SuccessStatus> onSuccess,
            final @Nullable Consumer<FailureStatus> onFailure
    ) {
        onSuccess.accept(this);
    }

    @ApiStatus.OverrideOnly
    @Override
    public <U> @NotNull U apply(
            final @NotNull Function<SuccessStatus, U> onSuccess,
            final @Nullable Function<FailureStatus, U> onFailure
    ) {
        return onSuccess.apply(this);
    }
}
