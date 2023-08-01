package com.minersstudios.mscore.util;

import com.minersstudios.msblock.customblock.CustomBlockData;
import com.minersstudios.msblock.customblock.CustomBlockRegistry;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Utility class for {@link CustomBlockData}
 */
public final class MSBlockUtils {
    public static final String NAMESPACED_KEY_REGEX = "(msblock):(\\w+)";
    public static final Pattern NAMESPACED_KEY_PATTERN = Pattern.compile(NAMESPACED_KEY_REGEX);

    @Contract(value = " -> fail")
    private MSBlockUtils() {
        throw new AssertionError("Utility class");
    }

    /**
     * Gets {@link CustomBlockData} item stack from key from
     * {@link CustomBlockRegistry#keySet()} by using
     * {@link CustomBlockRegistry#fromKey(String)} method
     *
     * @param key {@link CustomBlockData} key string
     * @return Optional of {@link ItemStack} object
     *         or empty optional if not found
     * @see CustomBlockRegistry#fromKey(String)
     * @see CustomBlockData#craftItemStack()
     */
    public static @NotNull Optional<ItemStack> getCustomBlockItem(@Nullable String key) {
        return CustomBlockRegistry.fromKey(key).map(CustomBlockData::craftItemStack);
    }

    /**
     * @param string String to be checked
     * @return True if string matches {@link #NAMESPACED_KEY_REGEX}
     */
    @Contract(value = "null -> false")
    public static boolean matchesNamespacedKey(@Nullable String string) {
        return string != null && NAMESPACED_KEY_PATTERN.matcher(string).matches();
    }
}
