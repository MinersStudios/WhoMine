package com.minersstudios.mscore.annotation;

import com.minersstudios.mscore.throwable.InvalidRegexException;
import org.intellij.lang.annotations.RegExp;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * Annotation used to mark the key.
 * <br>
 * The key must match the {@link #REGEX regex} pattern.
 *
 * @see Key.Validator
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({
        FIELD,
        LOCAL_VARIABLE,
        METHOD,
        PARAMETER
})
@org.intellij.lang.annotations.Pattern(Key.REGEX)
public @interface Key {
    /** The regex pattern that a valid key must match */
    @RegExp String REGEX = "[a-z0-9/._-]*";

    /**
     * Validator class for the {@link Key} annotation to check whether the
     * key matches the {@link #REGEX regex}
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
         * Checks whether the key matches the {@link #REGEX regex}
         *
         * @param key The key
         * @return Whether the key matches the {@link #REGEX regex}
         */
        public static boolean matches(final @Subst("key") @Key @Nullable String key) {
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
         * Validates the key
         *
         * @param key The key
         * @throws InvalidRegexException If the key does not match the
         *                               {@link #REGEX regex}
         * @see #matches(String)
         */
        public static void validate(final @Subst("key") @Key @Nullable String key) throws InvalidRegexException {
            if (!matches(key)) {
                throw new InvalidRegexException("Key must match regex: " + REGEX);
            }
        }
    }
}
