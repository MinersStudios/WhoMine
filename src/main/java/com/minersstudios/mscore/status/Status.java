package com.minersstudios.mscore.status;

import com.minersstudios.mscore.throwable.InvalidRegexException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

import static com.minersstudios.mscore.status.StatusKey.Validator.validate;

/**
 * Represents a status.
 * <br>
 * There are two types of statuses:
 * <ul>
 *     <li>{@link SuccessStatus Success Status}</li>
 *     <li>{@link FailureStatus Failure Status}</li>
 * </ul>
 */
public interface Status {

    /**
     * Returns the key of the status
     *
     * @return The key of the status
     */
    @StatusKey
    @NotNull String getKey();

    /**
     * Returns the priority of the status
     *
     * @return The priority of the status
     */
    @NotNull Priority getPriority();

    /**
     * Returns whether the status is a high priority
     *
     * @return True if the status is a high priority
     */
    boolean isHighPriority();

    /**
     * Returns whether the status is a low priority
     *
     * @return True if the status is a low priority
     */
    boolean isLowPriority();

    /**
     * Returns the hash code of the status
     *
     * @return Hash code of the status
     */
    @Override
    int hashCode();

    /**
     * Returns whether the object is equal to this status
     *
     * @param obj The object to compare
     * @return True if the object is equal to this status
     */
    @Contract("null -> false")
    @Override
    boolean equals(final @Nullable Object obj);

    /**
     * Returns a string representation of the status
     *
     * @return A string representation of the status
     */
    @Override
    @NotNull String toString();

    /**
     * Accepts the consumer to this status
     *
     * @param onSuccess Consumer for the success status
     * @param onFailure Consumer for the failure status
     * @throws UnsupportedOperationException If the status is not supported
     */
    void accept(
            final Consumer<SuccessStatus> onSuccess,
            final Consumer<FailureStatus> onFailure
    ) throws UnsupportedOperationException;

    /**
     * Applies the function to this status
     *
     * @param onSuccess Function for the success status
     * @param onFailure Function for the failure status
     * @param <U>       The type of the result
     * @return The result of the function
     * @throws UnsupportedOperationException If the status is not supported
     */
    <U> U apply(
            final Function<SuccessStatus, U> onSuccess,
            final Function<FailureStatus, U> onFailure
    ) throws UnsupportedOperationException;

    /**
     * Creates a new status with the specified key and low priority
     *
     * @param key Key of the status
     * @return A new status with the specified key and low priority
     * @throws InvalidRegexException If the key does not match the
     *                               {@link StatusKey#REGEX regex}
     * @see #success(String, Priority)
     */
    @Contract("_ -> new")
    static @NotNull SuccessStatus successLow(final @StatusKey @NotNull String key) throws InvalidRegexException {
        return success(key, Priority.LOW);
    }

    /**
     * Creates a new status with the specified key, failure status, and low
     * priority
     *
     * @param key           Key of the status
     * @param failureStatus The status that will be set if this status is not
     *                      successful, or null if there is no failure status
     * @return A new status with the given key and low priority
     * @throws InvalidRegexException    If the key does not match the
     *                                  {@link StatusKey#REGEX regex}
     * @throws IllegalArgumentException If the failure status has a different
     *                                  priority than the specified priority
     * @see #success(String, Priority, FailureStatus)
     */
    @Contract("_, _ -> new")
    static @NotNull SuccessStatus successLow(
            final @StatusKey @NotNull String key,
            final @Nullable FailureStatus failureStatus
    ) throws InvalidRegexException, IllegalArgumentException {
        return success(key, Priority.LOW, failureStatus);
    }

    /**
     * Creates a new status with the specified key and high priority
     *
     * @param key Key of the status
     * @return A new status with the specified key and high priority
     * @throws InvalidRegexException If the key does not match the
     *                               {@link StatusKey#REGEX regex}
     * @see #success(String, Priority)
     */
    @Contract("_ -> new")
    static @NotNull SuccessStatus successHigh(final @StatusKey @NotNull String key) throws InvalidRegexException {
        return success(key, Priority.HIGH);
    }

    /**
     * Creates a new status with the specified key, failure status, and high
     * priority
     *
     * @param key           Key of the status
     * @param failureStatus The status that will be set if this status is not
     *                      successful, or null if there is no failure status
     * @return A new status with the specified key, failure status, and high
     *         priority
     * @throws InvalidRegexException    If the key does not match the
     *                                  {@link StatusKey#REGEX regex}
     * @throws IllegalArgumentException If the failure status has a different
     *                                  priority than the specified priority
     * @see #success(String, Priority, FailureStatus)
     */
    @Contract("_, _ -> new")
    static @NotNull SuccessStatus successHigh(
            final @StatusKey @NotNull String key,
            final @Nullable FailureStatus failureStatus
    ) throws InvalidRegexException, IllegalArgumentException {
        return success(key, Priority.HIGH, failureStatus);
    }

    /**
     * Creates a new status with the specified key and priority
     *
     * @param key      Key of the status
     * @param priority Priority of the status
     * @return A new status with the specified key and priority
     * @throws InvalidRegexException If the key does not match the
     *                               {@link StatusKey#REGEX regex}
     * @see StatusKey.Validator#validate(String)
     */
    @Contract("_, _ -> new")
    static @NotNull SuccessStatus success(
            final @StatusKey @NotNull String key,
            final @NotNull Priority priority
    ) throws InvalidRegexException {
        validate(key);

        return new SuccessStatus(key, priority, null);
    }

    /**
     * Creates a new status with the specified key, priority, and failure status
     *
     * @param key           Key of the status
     * @param priority      Priority of the status
     * @param failureStatus The status that will be set if this status is not
     *                      successful, or null if there is no failure status
     * @return A new status with the specified key, priority, and failure status
     * @throws InvalidRegexException    If the key does not match the
     *                                  {@link StatusKey#REGEX regex}
     * @throws IllegalArgumentException If the failure status has a different
     *                                  priority than the specified priority
     * @see StatusKey.Validator#validate(String)
     */
    @Contract("_, _, _ -> new")
    static @NotNull SuccessStatus success(
            final @StatusKey @NotNull String key,
            final @NotNull Priority priority,
            final @Nullable FailureStatus failureStatus
    ) throws InvalidRegexException, IllegalArgumentException {
        validate(key);

        if (
                failureStatus != null
                && failureStatus.getPriority() != priority
        ) {
            throw new IllegalArgumentException(
                    "Failure status must have the same priority as the main status"
            );
        }

        return new SuccessStatus(key, priority, failureStatus);
    }

    /**
     * Creates a new status failure with the specified key and low priority
     *
     * @param key Key of the failure status
     * @return A new failure status with the specified key and low priority
     * @throws InvalidRegexException If the key does not match the
     *                               {@link StatusKey#REGEX regex}
     * @see #failure(String, Priority)
     */
    @Contract("_ -> new")
    static @NotNull FailureStatus failureLow(final @StatusKey @NotNull String key) throws InvalidRegexException {
        return failure(key, Priority.LOW);
    }

    /**
     * Creates a new status failure with the specified key and medium priority
     *
     * @param key Key of the failure status
     * @return A new failure status with the specified key and medium priority
     * @throws InvalidRegexException If the key does not match the
     *                               {@link StatusKey#REGEX regex}
     * @see #failure(String, Priority)
     */
    @Contract("_ -> new")
    static @NotNull FailureStatus failureHigh(final @StatusKey @NotNull String key) throws InvalidRegexException {
        return failure(key, Priority.HIGH);
    }

    /**
     * Creates a new status failure with the specified key and priority
     *
     * @param key      Key of the failure status
     * @param priority Priority of the failure status
     * @return A new failure status with the specified key and priority
     * @throws InvalidRegexException If the key does not match the
     *                               {@link StatusKey#REGEX regex}
     * @see StatusKey.Validator#validate(String)
     */
    @Contract("_, _ -> new")
    static @NotNull FailureStatus failure(
            final @StatusKey @NotNull String key,
            final @NotNull Priority priority
    ) throws InvalidRegexException {
        validate(key);

        return new FailureStatus(key, priority);
    }

    /**
     * Priority of the status.
     * <br>
     * There are two types of priorities:
     * <ul>
     *     <li>{@link Priority#LOW Low-priority}</li>
     *     <li>{@link Priority#HIGH High-priority}</li>
     * </ul>
     */
    enum Priority {
        LOW, HIGH
    }
}
