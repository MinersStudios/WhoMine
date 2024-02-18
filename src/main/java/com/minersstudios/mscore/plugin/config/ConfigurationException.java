package com.minersstudios.mscore.plugin.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Checked configuration exception.
 * <br>
 * Thrown when a configuration error occurs while working with the
 * {@link Config} class.
 * <br>
 * This exception is used to indicate problems encountered during configuration
 * operations.
 *
 * @see Config
 */
public class ConfigurationException extends Exception {

    /**
     * Constructs a new exception with no detail message
     */
    public ConfigurationException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message
     *
     * @param message The detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method)
     */
    public ConfigurationException(final @NotNull String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause
     *
     * @param message The detail message
     *                (which is saved for later retrieval by
     *                {@link #getMessage()} method)
     * @param cause   The cause
     *                (which is saved for later retrieval by {@link #getCause()}
     *                method)
     *                (A null value is permitted, and indicates that the cause
     *                is nonexistent or unknown)
     */
    public ConfigurationException(
            final @NotNull String message,
            final @Nullable Throwable cause
    ) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause and a detail message
     * of {@code (cause == null ? null : cause.toString())}
     *
     * @param cause The cause
     *              (which is saved for later retrieval by {@link #getCause()}
     *              method)
     *              (A null value is permitted, and indicates that the cause is
     *              nonexistent or unknown)
     */
    public ConfigurationException(final @Nullable Throwable cause) {
        super(cause);
    }
}
