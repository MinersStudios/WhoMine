package com.minersstudios.mscore.status;

import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;
import java.util.regex.Pattern;

import static java.lang.annotation.ElementType.*;

/**
 * Annotation used to mark the key of the status.
 * <br>
 * The status key must match the {@link #REGEX regex} pattern.
 *
 * @see StatusKey.Validator
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({
        FIELD,
        LOCAL_VARIABLE,
        METHOD,
        PARAMETER
})
@org.intellij.lang.annotations.Pattern(StatusKey.REGEX)
public @interface StatusKey {
    /** The regex pattern that a valid status key must match */
    @RegExp String REGEX = "^[A-Z][A-Z0-9_]*$";

    /** The compiled Pattern of the {@link #REGEX regex} string */
    Pattern PATTERN = Pattern.compile(REGEX);

    /**
     * Validator class for the {@link StatusKey} annotation to check whether the
     * key of the status matches the {@link #REGEX regex}
     *
     * @see #matches(String)
     * @see #validate(String)
     */
    final class Validator {

        @Contract(" -> fail")
        private Validator() throws AssertionError {
            throw new AssertionError("Utility class");
        }

        /**
         * Checks whether the key of the status matches the {@link #REGEX regex}
         *
         * @param key Key of the status
         * @return Whether the key matches the {@link #REGEX regex}
         */
        public static boolean matches(final @StatusKey @NotNull String key) {
            return PATTERN.matcher(key).matches();
        }

        /**
         * Validates the key of the status
         *
         * @param key Key of the status
         * @throws IllegalArgumentException If the key does not match the
         *                                  {@link #REGEX regex}
         * @see #matches(String)
         */
        public static void validate(final @StatusKey @NotNull String key) throws IllegalArgumentException {
            if (!matches(key)) {
                throw new IllegalArgumentException("Key must match regex: " + REGEX);
            }
        }
    }
}
