package com.minersstudios.msessentials.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class for IDs
 */
public final class IDUtils {
    public static final @NotNull String ID_REGEX = "-?\\d+";

    private IDUtils() {
        throw new IllegalStateException("Utility class");
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
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * @param string String to be checked
     * @return True if string matches {@link #ID_REGEX}
     */
    @Contract(value = "null -> false")
    public static boolean matchesIDRegex(final @Nullable String string) {
        return string != null && string.matches(ID_REGEX);
    }
}
