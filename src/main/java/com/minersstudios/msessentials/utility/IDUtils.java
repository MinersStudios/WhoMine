package com.minersstudios.msessentials.utility;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

/**
 * Utility class for IDs
 */
public final class IDUtils {
    public static final String ID_REGEX = "-?\\d+";
    public static final Pattern ID_PATTERN = Pattern.compile(ID_REGEX);

    @Contract(" -> fail")
    private IDUtils() {
        throw new AssertionError("Utility class");
    }

    /**
     * Parses ID from the string
     *
     * @param stringId ID string
     * @return int ID from string or -1 if
     */
    public static int parseID(final @NotNull String stringId) {
        try {
            return Integer.parseInt(stringId);
        } catch (final NumberFormatException ignored) {
            return -1;
        }
    }

    /**
     * @param string String to be checked
     * @return True if string matches {@link #ID_REGEX}
     */
    @Contract("null -> false")
    public static boolean matchesIDRegex(final @Nullable String string) {
        return StringUtils.isNotBlank(string)
                && ID_PATTERN.matcher(string).matches();
    }
}
