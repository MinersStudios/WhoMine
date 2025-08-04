package com.minersstudios.wholib.key;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An exception that is thrown when a key is invalid by any of the
 * available {@link Reason reasons}.
 */
@SuppressWarnings("unused")
public class KeyException extends RuntimeException {

    private final @Key String key;
    private final Reason reason;

    /**
     * Constructs a new {@link KeyException} with the given key and reason
     *
     * @param key    The key that caused the exception
     * @param reason The reason why the key is invalid
     */
    public KeyException(
            final @Key @Nullable String key,
            final @NotNull Reason reason
    ) {
        super(constructMessage(key, reason));

        this.key = key;
        this.reason = reason;
    }

    /**
     * Constructs a new {@link KeyException} with the given key, reason and
     * cause
     *
     * @param key    The key that caused the exception
     * @param reason The reason why the key is invalid
     * @param cause  The cause of the exception
     */
    public KeyException(
            final @Key @Nullable String key,
            final @NotNull Reason reason,
            final @NotNull Throwable cause
    ) {
        super(constructMessage(key, reason), cause);

        this.key = key;
        this.reason = reason;
    }

    /**
     * Returns the key that caused the exception
     *
     * @return The key that caused the exception
     */
    public @Key @Nullable String getKey() {
        return this.key;
    }

    /**
     * Returns the reason why the key is invalid
     *
     * @return The reason why the key is invalid
     */
    public @NotNull Reason getReason() {
        return this.reason;
    }

    private static @NotNull String constructMessage(
            final @Key @Nullable String key,
            final @NotNull Reason reason
    ) {
        return "The key '" + key + (
                switch (reason) {
                    case PATTERN -> "' is not valid because it does not match the pattern '" + Key.REGEX + '\'';
                    case EMPTY   -> "' is empty";
                }
        );
    }

    /**
     * The reasons why a key is invalid.
     * <table>
     *     <caption>Available Reasons</caption>
     *     <tr>
     *         <th>Reason</th>
     *         <th>Description</th>
     *     </tr>
     *     <tr>
     *         <td>{@link Reason#PATTERN}</td>
     *         <td>The key does not match the {@link Key#REGEX pattern}</td>
     *     </tr>
     *     <tr>
     *         <td>{@link Reason#EMPTY}</td>
     *         <td>The key is null or empty</td>
     *     </tr>
     * </table>
     */
    public enum Reason {
        /** The key does not match the {@link Key#REGEX pattern */
        PATTERN("The key '%s' is not valid because it does not match the pattern '" + Key.REGEX + '\''),
        /** The key is null or empty */
        EMPTY("The key '%s' is empty");

        private final String message;

        Reason(final @NotNull String message) {
            this.message = message;
        }

        /**
         * Returns the message that describes the reason
         *
         * @return The message that describes the reason
         */
        public @NotNull String getMessage() {
            return this.message;
        }
    }
}
