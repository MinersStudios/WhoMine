package com.minersstudios.mscore.utils;

import com.minersstudios.msblock.customblock.CustomBlockData;
import com.minersstudios.mscore.plugin.GlobalCache;
import com.minersstudios.mscore.plugin.MSPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Utility class for {@link CustomBlockData}
 */
public final class MSBlockUtils {
    public static final NamespacedKey CUSTOM_BLOCK_TYPE_NAMESPACED_KEY = new NamespacedKey("msblock", "type");
    public static final String NAMESPACED_KEY_REGEX = "(msblock):(\\w+)";
    public static final Pattern NAMESPACED_KEY_PATTERN = Pattern.compile(NAMESPACED_KEY_REGEX);

    @Contract(value = " -> fail")
    private MSBlockUtils() {
        throw new AssertionError("Utility class");
    }

    /**
     * Gets {@link CustomBlockData} item stack from key from
     * {@link GlobalCache#customBlockMap} by using
     * {@link #getCustomBlockData(String)} method
     *
     * @param key {@link CustomBlockData} key string
     * @return Optional of {@link ItemStack} object
     *         or empty optional if not found
     * @see #getCustomBlockData(String)
     * @see CustomBlockData#craftItemStack()
     */
    public static @NotNull Optional<ItemStack> getCustomBlockItem(@Nullable String key) {
        return getCustomBlockData(key).map(CustomBlockData::craftItemStack);
    }

    /**
     * Gets {@link CustomBlockData} from {@link ItemStack} from
     * {@link GlobalCache#customBlockMap} by using persistent data
     * container of item and {@link #getCustomBlockData(String)}
     * method
     *
     * @param itemStack {@link ItemStack} to be checked
     * @return Optional of {@link CustomBlockData} object
     *         or empty optional if not found
     * @see #getCustomBlockData(String)
     */
    public static @NotNull Optional<CustomBlockData> getCustomBlockData(@Nullable ItemStack itemStack) {
        if (itemStack == null) return Optional.empty();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return Optional.empty();
        String key = itemMeta.getPersistentDataContainer().get(CUSTOM_BLOCK_TYPE_NAMESPACED_KEY, PersistentDataType.STRING);
        return getCustomBlockData(key);
    }

    /**
     * Gets {@link CustomBlockData} from key from
     * {@link GlobalCache#customBlockMap}
     *
     * @param key {@link CustomBlockData} key string
     * @return Optional of {@link CustomBlockData} object
     *         or empty optional if not found
     */
    public static @NotNull Optional<CustomBlockData> getCustomBlockData(@Nullable String key) {
        return key == null
                ? Optional.empty()
                : Optional.ofNullable(MSPlugin.getGlobalCache().customBlockMap.getByPrimaryKey(key));
    }

    /**
     * Checks if item has {@link #CUSTOM_BLOCK_TYPE_NAMESPACED_KEY}
     * in {@link PersistentDataContainer} of item
     *
     * @param itemStack Item to be checked
     * @return True if item is {@link CustomBlockData} item
     * @see CustomBlockData
     */
    @Contract("null -> false")
    public static boolean isCustomBlock(@Nullable ItemStack itemStack) {
        if (itemStack == null) return false;
        ItemMeta itemMeta = itemStack.getItemMeta();
        return itemMeta != null && itemMeta.getPersistentDataContainer().has(CUSTOM_BLOCK_TYPE_NAMESPACED_KEY);
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
