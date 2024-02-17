package com.minersstudios.msessentials.resourcepack.throwable;

import org.jetbrains.annotations.Nullable;

/**
 * Signals that a non-fatal error occurred while loading the resource pack and
 * the resource pack object can be created as disabled (without its data)
 *
 * @see FatalPackLoadException
 */
public class PackLoadException extends RuntimeException {

    /**
     * Constructs a new exception with no detail message
     */
    public PackLoadException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message
     *
     * @param message The detail message
     */
    public PackLoadException(final @Nullable String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause
     *
     * @param message The detail message
     * @param cause   The cause
     */
    public PackLoadException(
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
    public PackLoadException(final @Nullable Throwable cause) {
        super(cause);
    }
}
