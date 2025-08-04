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
 * Annotation used to mark the resource.
 * <p>
 * The resource must match the {@link #REGEX regex} pattern.
 * <p>
 * Example of usage:
 * <pre>
 *     {@code @Resource String resource = "resource";}
 * </pre>
 * <table>
 *     <caption>Available Constants</caption>
 *     <tr>
 *         <th>Resource</th>
 *         <th>Value</th>
 *     </tr>
 *     <tr>
 *         <td>{@link #EMPTY}</td>
 *         <td>{@value #EMPTY}</td>
 *     </tr>
 *     <tr>
 *         <td>{@link #MINECRAFT}</td>
 *         <td>{@value #MINECRAFT}</td>
 *     </tr>
 *     <tr>
 *         <td>{@link #REALMS}</td>
 *         <td>{@value #REALMS}</td>
 *     </tr>
 *     <tr>
 *         <td>{@link #PAPER}</td>
 *         <td>{@value #PAPER}</td>
 *     </tr>
 *     <tr>
 *         <td>{@link #WHOMINE}</td>
 *         <td>{@value #WHOMINE}</td>
 *     </tr>
 *     <tr>
 *         <td>{@link #WMBLOCK}</td>
 *         <td>{@value #WMBLOCK}</td>
 *     </tr>
 *     <tr>
 *         <td>{@link #WMITEM}</td>
 *         <td>{@value #WMITEM}</td>
 *     </tr>
 *     <tr>
 *         <td>{@link #WMDECOR}</td>
 *         <td>{@value #WMDECOR}</td>
 *     </tr>
 *     <tr>
 *         <td>{@link #WMENTITY}</td>
 *         <td>{@value #WMENTITY}</td>
 *     </tr>
 * </table>
 *
 * @see Resource.Validator
 * @see ResKey
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
@Pattern(Resource.REGEX)
public @interface Resource {
    /** The regex pattern that a valid resource must match */
    @RegExp String REGEX = "[a-z0-9._-]*";

    //<editor-fold desc="Default resources" defaultstate="collapsed">

    /** The empty resource */
    @Resource String EMPTY = "";
    /** The Minecraft resource */
    @Resource String MINECRAFT = "minecraft";
    /** The Realms resource */
    @Resource String REALMS = "realms";
    /** The Paper resource */
    @Resource String PAPER = "paper";
    /** The Velocity resource */
    @Resource String VELOCITY = "velocity";
    /** The WhoMine resource */
    @Resource String WHOMINE = "whomine";
    /** The WhoMine's block resource */
    @Resource String WMBLOCK = "wmblock";
    /** The WhoMine's item resource */
    @Resource String WMITEM = "wmitem";
    /** The WhoMine's decor resource */
    @Resource String WMDECOR = "wmdecor";
    /** The WhoMine's entity resource */
    @Resource String WMENTITY = "wmentity";

    //</editor-fold>

    /**
     * Validator class for the {@link Resource} annotation to check whether the
     * resource matches the {@link #REGEX regex}
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
         * Checks whether the resource matches the {@link #REGEX regex}.
         * <p>
         * If the resource is {@code null}, then it is considered valid.
         *
         * @param resource The resource
         * @return Whether the resource matches the {@link #REGEX regex}
         */
        public static boolean matchesPattern(final @Resource @Nullable String resource) {
            if (resource == null) {
                return true;
            }

            for(int i = 0; i < resource.length(); ++i) {
                final char character = resource.charAt(i);

                switch (character) {
                    case '_', '-', '.' -> {}
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
         * Validates the resource.
         * <p>
         * If the resource is {@code null}, then it is considered valid.
         *
         * @param resource The resource
         * @throws ResourceException If the resource does not match
         *                           the {@link #REGEX regex}
         * @see #matchesPattern(String)
         */
        public static void validatePattern(final @Resource @Nullable String resource) throws ResourceException {
            if (!matchesPattern(resource)) {
                throw new ResourceException(resource, ResourceException.Reason.PATTERN);
            }
        }

        /**
         * Validates that the resource is not empty
         *
         * @param resource The resource
         * @throws ResourceException If the resource is {@code null} or empty
         */
        @Contract("null -> fail")
        public static void validateNotEmpty(final @Resource @Nullable String resource) throws ResourceException {
            if (ChatUtils.isBlank(resource)) {
                throw new ResourceException(resource, ResourceException.Reason.EMPTY);
            }
        }
    }
}
