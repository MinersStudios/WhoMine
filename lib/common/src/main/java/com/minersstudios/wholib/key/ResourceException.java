package com.minersstudios.wholib.key;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An exception that is thrown when a resource is invalid by any of the
 * available {@link Reason reasons}.
 */
@SuppressWarnings("unused")
public class ResourceException extends RuntimeException {

    private final @Resource String resource;
    private final Reason reason;

    /**
     * Constructs a new {@link ResourceException} with the given resource
     * and reason
     *
     * @param resource The resource that caused the exception
     * @param reason   The reason why the resource is invalid
     */
    public ResourceException(
            final @Resource @Nullable String resource,
            final @NotNull Reason reason
    ) {
        super(constructMessage(resource, reason));

        this.resource = resource;
        this.reason = reason;
    }

    /**
     * Constructs a new {@link ResourceException} with the given resource,
     * reason and cause
     *
     * @param resource The resource that caused the exception
     * @param reason   The reason why the resource is invalid
     * @param cause    The cause of the exception
     */
    public ResourceException(
            final @Resource @Nullable String resource,
            final @NotNull Reason reason,
            final @NotNull Throwable cause
    ) {
        super(constructMessage(resource, reason), cause);

        this.resource = resource;
        this.reason = reason;
    }

    /**
     * Returns the resource that caused the exception
     *
     * @return The resource that caused the exception
     */
    public @Resource @Nullable String getResource() {
        return this.resource;
    }

    /**
     * Returns the reason why the resource is invalid
     *
     * @return The reason why the resource is invalid
     */
    public @NotNull Reason getReason() {
        return this.reason;
    }

    private static @NotNull String constructMessage(
            final @Resource @Nullable String resource,
            final @NotNull Reason reason
    ) {
        return String.format(reason.getMessage(), resource);
    }

    /**
     * The reasons why a resource is invalid.
     * <table>
     *     <caption>Available Reasons</caption>
     *     <tr>
     *         <th>Reason</th>
     *         <th>Description</th>
     *     </tr>
     *     <tr>
     *         <td>{@link Reason#PATTERN}</td>
     *         <td>The resource does not match the
     *             {@link Resource#REGEX pattern}</td>
     *     </tr>
     *     <tr>
     *         <td>{@link Reason#EMPTY}</td>
     *         <td>The resource is null or empty</td>
     *     </tr>
     * </table>
     */
    public enum Reason {
        /** The resource does not match the {@link Resource#REGEX pattern} */
        PATTERN("The resource '%s' is not valid because it does not match the pattern '" + Resource.REGEX + '\''),
        /** The resource is null or empty */
        EMPTY("The resource '%s' is empty");

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
