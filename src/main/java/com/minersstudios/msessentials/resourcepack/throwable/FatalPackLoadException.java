package com.minersstudios.msessentials.resourcepack.throwable;

import org.jetbrains.annotations.Nullable;

/**
 * Signals that a fatal error occurred while loading the resource pack and
 * the resource pack object cannot be created
 *
 * @see PackLoadException
 */
public class FatalPackLoadException extends Exception {

    /**
     * Constructs a FatalPackLoadException with no detail message
     */
    public FatalPackLoadException() {
        super();
    }

    /**
     * Constructs a FatalPackLoadException with the specified detail message
     *
     * @param message The detail message
     */
    public FatalPackLoadException(final @Nullable String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause
     *
     * @param message The detail message
     * @param cause   The cause
     */
    public FatalPackLoadException(
            final @Nullable String message,
            final @Nullable Throwable cause
    ) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause and a detail message
     * of cause
     *
     * @param cause The cause
     */
    public FatalPackLoadException(final @Nullable Throwable cause) {
        super(cause);
    }
}
