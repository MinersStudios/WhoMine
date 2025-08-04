package com.minersstudios.wholib.key;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An exception that is thrown when a resource key is invalid by any of the
 * available {@link Reason reasons}.
 */
@SuppressWarnings("unused")
public class ResourceKeyException extends RuntimeException {

    private final @ResKey String resourceKey;
    private final Reason reason;

    /**
     * Constructs a new {@link ResourceKeyException} with the given resource key
     * and reason
     *
     * @param resourceKey The resource key that caused the exception
     * @param reason      The reason why the resource key is invalid
     */
    public ResourceKeyException(
            final @Nullable ResourceKey resourceKey,
            final @NotNull Reason reason
    ) {
        this(resourceKey == null ? null : resourceKey.toString(), reason);
    }

    /**
     * Constructs a new {@link ResourceKeyException} with the given resource key
     * and reason
     *
     * @param resourceKey The resource key that caused the exception
     * @param reason      The reason why the resource key is invalid
     */
    public ResourceKeyException(
            final @ResKey @Nullable String resourceKey,
            final @NotNull Reason reason
    ) {
        super(constructMessage(resourceKey, reason));

        this.resourceKey = resourceKey;
        this.reason = reason;
    }

    /**
     * Constructs a new {@link ResourceKeyException} with the given resource key,
     * reason and cause
     *
     * @param resourceKey The resource key that caused the exception
     * @param reason      The reason why the resource key is invalid
     * @param cause       The cause of the exception
     */
    public ResourceKeyException(
            final @Nullable ResourceKey resourceKey,
            final @NotNull Reason reason,
            final @NotNull Throwable cause
    ) {
        this(resourceKey == null ? null : resourceKey.toString(), reason, cause);
    }

    /**
     * Constructs a new {@link ResourceKeyException} with the given resource key,
     * reason and cause
     *
     * @param resourceKey The resource key that caused the exception
     * @param reason      The reason why the resource key is invalid
     * @param cause       The cause of the exception
     */
    public ResourceKeyException(
            final @ResKey @Nullable String resourceKey,
            final @NotNull Reason reason,
            final @NotNull Throwable cause
    ) {
        super(constructMessage(resourceKey, reason), cause);

        this.resourceKey = resourceKey;
        this.reason = reason;
    }

    /**
     * Returns the resource key that caused the exception
     *
     * @return The resource key that caused the exception
     */
    public @ResKey @Nullable String getResourceKey() {
        return this.resourceKey;
    }

    /**
     * Returns the reason why the resource key is invalid
     *
     * @return The reason why the resource key is invalid
     */
    public @NotNull Reason getReason() {
        return this.reason;
    }

    private static @NotNull String constructMessage(
            final @Nullable String resourceKey,
            final @NotNull Reason reason
    ) {
        return String.format(reason.getMessage(), resourceKey);
    }

    /**
     * The reasons why a resource key is invalid.
     * <table>
     *     <caption>Available Reasons</caption>
     *     <tr>
     *         <th>Reason</th>
     *         <th>Description</th>
     *     </tr>
     *     <tr>
     *         <td>{@link Reason#PATTERN}</td>
     *         <td>The resource key does not match the
     *             {@link ResKey#REGEX pattern}</td>
     *     </tr>
     *     <tr>
     *         <td>{@link Reason#LENGTH}</td>
     *         <td>The resource key is longer than
     *         {@value ResKey#MAX_LENGTH} characters or
     *         {@value ResKey#MAX_BYTES} bytes</td>
     *     </tr>
     *     <tr>
     *         <td>{@link Reason#EMPTY}</td>
     *         <td>The resource key is null or empty</td>
     *     </tr>
     * </table>
     */
    public enum Reason {
        /**
         * The resource key does not match the
         * {@link ResKey#REGEX pattern}
         */
        PATTERN(
                "The resource key '%s' is not valid because it does not match the pattern '" +
                ResKey.REGEX + '\''
        ),
        /**
         * The resource key is longer than {@value ResKey#MAX_LENGTH}
         * characters or {@value ResKey#MAX_BYTES} bytes
         */
        LENGTH(
                "The resource key '%s' is too long, it must be at most " + ResKey.MAX_LENGTH +
                " characters and " + ResKey.MAX_BYTES + " bytes long"
        ),
        /** The resource key is null or empty */
        EMPTY("The resource key '%s' is empty");

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
