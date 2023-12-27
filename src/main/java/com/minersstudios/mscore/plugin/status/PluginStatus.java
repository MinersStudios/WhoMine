package com.minersstudios.mscore.plugin.status;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.regex.Pattern;

public interface PluginStatus {
    String REGEX = "^[A-Z0-9_]+$";
    Pattern PATTERN = Pattern.compile(REGEX);

    /**
     * @return The name of the status
     */
    @NotNull String getName();

    /**
     * @return The priority of the status
     */
    @NotNull Priority getPriority();

    /**
     * @return True if the status is a high priority
     */
    boolean isHighPriority();

    /**
     * @return True if the status is a low priority
     */
    boolean isLowPriority();

    /**
     * @return Hash code of the status
     */
    @Override
    int hashCode();

    /**
     * @param obj The object to compare
     * @return True if the object is equal to this status
     */
    @Override
    boolean equals(final @Nullable Object obj);

    /**
     * @return A string representation of the status
     */
    @Override
    @NotNull String toString();

    /**
     * @param name Name of the status
     * @return True if the name matches the {@link #REGEX regex}
     */
    static boolean matchesNameRegex(final @NotNull String name) {
        return PATTERN.matcher(name).matches();
    }

    /**
     * Validates the name of the status
     *
     * @param name Name of the status
     * @throws IllegalArgumentException If the name does not match the
     *                                  {@link #REGEX regex}
     */
    static void validateName(final @NotNull String name) throws IllegalArgumentException {
        if (!matchesNameRegex(name)) {
            throw new IllegalArgumentException("Name must match regex: " + REGEX);
        }
    }

    /**
     * Validates the failure status
     *
     * @param mainPriority  The priority of the main status
     * @param failureStatus The failure status, that adds to the main status
     * @throws IllegalArgumentException If the failure status has a different
     *                                  priority than the main status
     */
    static void validateFailureStatus(
            final @NotNull Priority mainPriority,
            final @Nullable FailureStatus failureStatus
    ) throws IllegalArgumentException {
        if (
                failureStatus != null
                && failureStatus.getPriority() != mainPriority
        ) {
            throw new IllegalArgumentException(
                    "Failure status must have the same priority as the main status"
            );
        }
    }

    enum Priority {
        LOW, HIGH
    }
}
