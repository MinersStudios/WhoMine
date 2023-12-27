package com.minersstudios.mscore.plugin.status;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;
import java.util.Locale;

@Immutable
public class FailureStatus extends ImplPluginStatus {

    private FailureStatus(
            final @NotNull String name,
            final @NotNull Priority priority
    ) {
        super(name, priority);
    }

    /**
     * Creates a new status failure with the specified name and low priority
     *
     * @param name Name of the failure status
     * @return A new failure status with the specified name and low priority
     * @throws IllegalArgumentException If the name does not match the name
     *                                  regex
     * @see #validateName(String)
     */
    @Contract("_ -> new")
    public static @NotNull FailureStatus low(final @NotNull String name) throws IllegalArgumentException {
        final String nameUpper = name.toUpperCase(Locale.ENGLISH);

        PluginStatus.validateName(nameUpper);

        return new FailureStatus(
                nameUpper,
                Priority.LOW
        );
    }

    /**
     * Creates a new status failure with the specified name and medium priority
     *
     * @param name Name of the failure status
     * @return A new failure status with the specified name and medium priority
     * @throws IllegalArgumentException If the name does not match the name
     *                                  regex
     * @see #validateName(String)
     */
    @Contract("_ -> new")
    public static @NotNull FailureStatus high(final @NotNull String name) throws IllegalArgumentException {
        final String nameUpper = name.toUpperCase(Locale.ENGLISH);

        PluginStatus.validateName(nameUpper);

        return new FailureStatus(
                nameUpper,
                Priority.HIGH
        );
    }
}
