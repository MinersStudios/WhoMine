package com.minersstudios.mscore.plugin.status;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.Immutable;
import java.util.Locale;

@Immutable
public final class SuccessStatus extends ImplPluginStatus {
    private final FailureStatus failureStatus;

    private SuccessStatus(
            final @NotNull String name,
            final @NotNull Priority priority,
            final @Nullable FailureStatus failureStatus
    ) {
        super(name, priority);

        this.failureStatus = failureStatus;
    }

    /**
     * Creates a new status with the specified name and low priority
     *
     * @param name Name of the status
     * @return A new status with the specified name and low priority
     * @throws IllegalArgumentException If the name does not match the
     *                                  {@link #REGEX regex}
     * @see #validateName(String)
     */
    public static @NotNull SuccessStatus low(final @NotNull String name) throws IllegalArgumentException {
        final String nameUpper = name.toUpperCase(Locale.ENGLISH);

        PluginStatus.validateName(nameUpper);

        return new SuccessStatus(
                nameUpper,
                Priority.LOW,
                null
        );
    }

    /**
     * Creates a new status with the specified name, failure status, and low
     * priority
     *
     * @param name          Name of the status
     * @param failureStatus The status that will be set if this status is not
     *                      successful, or null if there is no failure status
     * @return A new status with the given name and low priority
     * @throws IllegalArgumentException If the name does not match the
     *                                  {@link #REGEX regex}, or if the failure
     *                                  status has a high priority
     * @see #validateName(String)
     * @see #validateFailureStatus(Priority, FailureStatus)
     */
    @Contract("_, _ -> new")
    public static @NotNull SuccessStatus low(
            final @NotNull String name,
            final @Nullable FailureStatus failureStatus
    ) throws IllegalArgumentException {
        final String nameUpper = name.toUpperCase(Locale.ENGLISH);

        PluginStatus.validateName(nameUpper);
        PluginStatus.validateFailureStatus(Priority.LOW, failureStatus);

        return new SuccessStatus(
                nameUpper,
                Priority.LOW,
                failureStatus
        );
    }

    /**
     * Creates a new status with the specified name and high priority
     *
     * @param name Name of the status
     * @return A new status with the specified name and high priority
     * @throws IllegalArgumentException If the name does not match the
     *                                  {@link #REGEX regex}
     * @see #validateName(String)
     */
    public static @NotNull SuccessStatus high(final @NotNull String name) throws IllegalArgumentException {
        final String nameUpper = name.toUpperCase(Locale.ENGLISH);

        PluginStatus.validateName(nameUpper);

        return new SuccessStatus(
                nameUpper,
                Priority.HIGH,
                null
        );
    }

    /**
     * Creates a new status with the specified name, failure status, and high
     * priority
     *
     * @param name          Name of the status
     * @param failureStatus The status that will be set if this status is not
     *                      successful, or null if there is no failure status
     * @return A new status with the specified name, failure status, and high
     *         priority
     * @throws IllegalArgumentException If the name does not match the
     *                                  {@link #REGEX regex}, or if the failure
     *                                  status has a low priority
     * @see #validateName(String)
     * @see #validateFailureStatus(Priority, FailureStatus)
     */
    @Contract("_, _ -> new")
    public static @NotNull SuccessStatus high(
            final @NotNull String name,
            final @Nullable FailureStatus failureStatus
    ) throws IllegalArgumentException {
        final String nameUpper = name.toUpperCase(Locale.ENGLISH);

        PluginStatus.validateName(nameUpper);
        PluginStatus.validateFailureStatus(Priority.HIGH, failureStatus);

        return new SuccessStatus(
                nameUpper,
                Priority.HIGH,
                failureStatus
        );
    }

    /**
     * @return The failure status, or null if there is no failure status present
     */
    public @Nullable FailureStatus getFailureStatus() {
        return this.failureStatus;
    }

    @Override
    public int hashCode() {
        return this.failureStatus == null
                ? super.hashCode()
                : 31 * super.hashCode() + this.failureStatus.hashCode();
    }
}
