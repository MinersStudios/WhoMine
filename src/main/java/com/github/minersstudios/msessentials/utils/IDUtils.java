package com.github.minersstudios.msessentials.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public final class IDUtils {
    public static final @NotNull String ID_REGEX = "-?\\d+";

    private IDUtils() {
        throw new IllegalStateException("Utility class");
    }

    @Contract(value = "null -> false", pure = true)
    public static boolean matchesIDRegex(@Nullable String string) {
        return string != null && string.matches(ID_REGEX);
    }
}
