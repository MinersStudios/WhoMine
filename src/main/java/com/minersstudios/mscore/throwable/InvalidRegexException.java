package com.minersstudios.mscore.throwable;

import org.jetbrains.annotations.Nullable;

/**
 * Signals that when checking a string against a regex pattern, the string was
 * invalid
 */
public class InvalidRegexException extends RuntimeException {

    /**
     * Constructs a new exception with no detail message
     */
    public InvalidRegexException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message
     *
     * @param message The detail message
     */
    public InvalidRegexException(final @Nullable String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause
     *
     * @param message The detail message
     * @param cause   The cause
     */
    public InvalidRegexException(
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
    public InvalidRegexException(final @Nullable Throwable cause) {
        super(cause);
    }
}
