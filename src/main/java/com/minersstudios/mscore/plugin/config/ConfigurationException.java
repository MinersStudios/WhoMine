package com.minersstudios.mscore.plugin.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Thrown when a configuration error occurs while working
 * with the {@link MSConfig} class. This exception is used
 * to indicate problems encountered during configuration
 * operations.
 *
 * @see MSConfig
 */
public class ConfigurationException extends Exception {

    /**
     * Constructs a new ConfigurationException with no
     * detail message
     */
    public ConfigurationException() {
        super();
    }

    /**
     * Constructs a new ConfigurationException with the
     * specified detail message
     *
     * @param message The detail message
     *                (which is saved for later retrieval
     *                by the {@link #getMessage()} method)
     */
    public ConfigurationException(@NotNull String message) {
        super(message);
    }

    /**
     * Constructs a new ConfigurationException with the
     * specified detail message and cause
     *
     * @param message The detail message
     *                (which is saved for later retrieval
     *                by the {@link #getMessage()} method)
     * @param cause   The cause
     *                (which is saved for later retrieval
     *                by the {@link #getCause()} method)
     *                (A null value is permitted, and indicates
     *                that the cause is nonexistent or unknown)
     */
    public ConfigurationException(
            @NotNull String message,
            @Nullable Throwable cause
    ) {
        super(message, cause);
    }

    /**
     * Constructs a new ConfigurationException with the
     * specified cause and a detail message of
     * {@code (cause == null ? null : cause.toString())}
     *
     * @param cause The cause
     *              (which is saved for later retrieval
     *              by the {@link #getCause()} method)
     *              (A null value is permitted, and indicates
     *              that the cause is nonexistent or unknown)
     */
    public ConfigurationException(@Nullable Throwable cause) {
        super(cause);
    }
}
