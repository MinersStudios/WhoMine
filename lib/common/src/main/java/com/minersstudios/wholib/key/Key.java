package com.minersstudios.wholib.key;

import com.minersstudios.wholib.utility.ChatUtils;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * Annotation used to mark the key.
 * <p>
 * The key must match the {@link #REGEX regex} pattern.
 * <p>
 * Example of usage:
 * <pre>
 *     {@code @Key String key = "key";}
 * </pre>
 *
 * @see Key.Validator
 * @see ResKey
 * @see Resource
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
@Pattern(Key.REGEX)
public @interface Key {
    /** The regex pattern that a valid key must match */
    @RegExp String REGEX = "[a-z0-9/._-]*";

    /**
     * Validator class for the {@link Key} annotation to check whether the
     * key matches the {@link #REGEX regex}
     *
     * @see #matchesPattern(String)
     * @see #validatePattern(String)
     */
    final class Validator {

        @Contract(" -> fail")
        private Validator() throws AssertionError {
            throw new AssertionError("Utility class");
        }

        /**
         * Checks whether the key matches the {@link #REGEX regex}.
         * <p>
         * If the key is {@code null}, then it is considered valid.
         *
         * @param key The key
         * @return Whether the key matches the {@link #REGEX regex}
         */
        @Contract("null -> true")
        public static boolean matchesPattern(final @Key @Nullable String key) {
            if (key == null) {
                return true;
            }

            for(int i = 0; i < key.length(); ++i) {
                final char character = key.charAt(i);

                switch (character) {
                    case '_', '-', '.', '/' -> {}
                    default -> {
                        if (character < 'a' || character > 'z') {
                            if (character < '0' || character > '9') {
                                return false;
                            }
                        }
                    }
                }
            }

            return true;
        }

        /**
         * Validates the key.
         * <p>
         * If the key is {@code null}, then it is considered valid.
         *
         * @param key The key
         * @throws KeyException If the key does not match
         *                      the {@link #REGEX regex}
         * @see #matchesPattern(String)
         */
        public static void validatePattern(final @Key @Nullable String key) throws KeyException {
            if (!matchesPattern(key)) {
                throw new KeyException(key, KeyException.Reason.PATTERN);
            }
        }

        /**
         * Validates the key to ensure it is not empty
         *
         * @param key The key
         * @throws KeyException If the key is {@code null} or empty
         */
        @Contract("null -> fail")
        public static void validateNotEmpty(final @Key @Nullable String key) throws KeyException {
            if (ChatUtils.isBlank(key)) {
                throw new KeyException(key, KeyException.Reason.EMPTY);
            }
        }
    }
}
