package com.minersstudios.wholib.key;

import com.minersstudios.wholib.utility.ChatUtils;
import io.netty.buffer.ByteBufUtil;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.RegExp;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.minersstudios.wholib.key.ResourceKey.SEPARATOR;
import static java.lang.annotation.ElementType.*;

/**
 * Annotation used to mark the string as {@link ResourceKey resource key}.
 * <p>
 * The resource key must match the {@link #REGEX regex} pattern.
 * <p>
 * Example of usage:
 * <pre>
 *     {@code @ResKey String resourceKey = "resource:key";}
 *     {@code @ResKey String resourceKey = "key";}
 * </pre>
 *
 * @see ResKey.Validator
 * @see Resource
 * @see Key
 */
@SuppressWarnings("unused")
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({
        FIELD,
        LOCAL_VARIABLE,
        METHOD,
        PARAMETER
})
@Pattern(ResKey.REGEX)
public @interface ResKey {
    /** The regex pattern that a valid resource key must match */
    @RegExp String REGEX = "(" + Resource.REGEX + ")(" + SEPARATOR + "(" + Key.REGEX + "))?";
    /** Number of characters that a resource key can have */
    int MAX_LENGTH = Short.MAX_VALUE;
    /** Number of bytes that a resource key can have */
    int MAX_BYTES = 2 * Short.MAX_VALUE + 1;

    /**
     * Validator class for the {@link ResKey} annotation to check whether
     * the resource key matches the {@link #REGEX regex}
     *
     * @see #matchesPattern(String)
     * @see #matchesLength(String)
     * @see #validatePattern(String)
     * @see #validateLength(String)
     */
    final class Validator {

        @Contract(" -> fail")
        private Validator() throws AssertionError {
            throw new AssertionError("Utility class");
        }

        /**
         * Checks whether the resource key matches the {@link #REGEX regex}.
         * <p>
         * If the resource key is {@code null}, then it is considered valid.
         *
         * @param resourceKey The resource key
         * @return Whether the resource key matches the {@link #REGEX regex}
         */
        @Contract("null -> true")
        public static boolean matchesPattern(final @Subst("resource:key") @ResKey @Nullable String resourceKey) {
            if (resourceKey == null) {
                return true;
            }

            if (!matchesLength(resourceKey)) {
                return false;
            }

            final int colonIndex = resourceKey.indexOf(SEPARATOR);

            @Subst("resource") String resource = "";
            @Subst("key") String key = resourceKey;

            if (colonIndex >= 0) {
                key = resourceKey.substring(colonIndex + 1);

                if (colonIndex >= 1) {
                    resource = resourceKey.substring(0, colonIndex);
                }
            }

            return Resource.Validator.matchesPattern(resource)
                && Key.Validator.matchesPattern(key);
        }

        /**
         * Checks whether the resource key matches the length requirements
         *
         * @param resourceKey The resource key
         * @return Whether the resource key matches the length requirements
         */
        @Contract("null -> true")
        public static boolean matchesLength(final @ResKey @Nullable String resourceKey) {
            return resourceKey == null
                    || (
                            resourceKey.length() <= MAX_LENGTH
                            && ByteBufUtil.utf8MaxBytes(resourceKey) <= MAX_BYTES
                    );
        }

        /**
         * Validates the resource key.
         * <p>
         * If the resource key is {@code null}, then it is considered valid.
         *
         * @param resourceKey The resource key
         * @throws ResourceKeyException If the resource key does not match
         *                              the {@link #REGEX regex}
         * @see #matchesPattern(String)
         */
        public static void validatePattern(final @ResKey @Nullable String resourceKey) throws ResourceKeyException {
            if (!matchesPattern(resourceKey)) {
                throw new ResourceKeyException(resourceKey, ResourceKeyException.Reason.PATTERN);
            }
        }

        /**
         * Validates the length of the resource key.
         * <p>
         * If the resource key is {@code null}, then it is considered valid.
         *
         * @param resourceKey The resource key
         * @throws ResourceKeyException If the resource key is too long
         */
        public static void validateLength(final @ResKey @Nullable String resourceKey) throws ResourceKeyException {
            if (!matchesLength(resourceKey)) {
                throw new ResourceKeyException(resourceKey, ResourceKeyException.Reason.LENGTH);
            }
        }

        /**
         * Validates the resource key to ensure it is not empty
         *
         * @param resourceKey The resource key
         * @throws ResourceKeyException If the resource key is {@code null} or
         *                              empty
         */
        @Contract("null -> fail")
        public static void validateNotEmpty(final @ResKey @Nullable String resourceKey) throws ResourceKeyException {
            if (ChatUtils.isBlank(resourceKey)) {
                throw new ResourceKeyException(resourceKey, ResourceKeyException.Reason.EMPTY);
            }
        }
    }
}
