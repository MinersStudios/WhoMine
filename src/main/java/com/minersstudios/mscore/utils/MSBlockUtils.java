package com.minersstudios.mscore.utils;

import com.minersstudios.msblock.customblock.CustomBlockData;
import com.minersstudios.mscore.plugin.MSPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MSBlockUtils {
    public static final NamespacedKey CUSTOM_BLOCK_TYPE_NAMESPACED_KEY = new NamespacedKey("msblock", "type");
    public static final String NAMESPACED_KEY_REGEX = "msblock:(\\w+)";

    @Contract(value = " -> fail")
    private MSBlockUtils() {
        throw new AssertionError("Utility class");
    }

    /**
     * @param itemStack Item
     * @return True if item is {@link CustomBlockData}
     */
    @Contract("null -> false")
    public static boolean isCustomBlock(@Nullable ItemStack itemStack) {
        if (itemStack == null) return false;
        ItemMeta itemMeta = itemStack.getItemMeta();
        return itemMeta != null && itemMeta.getPersistentDataContainer().has(CUSTOM_BLOCK_TYPE_NAMESPACED_KEY);
    }

    /**
     * Gets {@link CustomBlockData} item stack from key
     *
     * @param key {@link CustomBlockData} key string
     * @return {@link CustomBlockData} item stack
     * @throws MSCustomNotFoundException if {@link CustomBlockData} is not found
     */
    public static @NotNull ItemStack getCustomBlockItem(@NotNull String key) throws MSCustomNotFoundException {
        return getCustomBlockData(key).craftItemStack();
    }

    /**
     * Gets {@link CustomBlockData} from {@link ItemStack}
     *
     * @param itemStack {@link ItemStack}
     * @return {@link CustomBlockData} object
     * @throws MSCustomNotFoundException If {@link CustomBlockData} is not found
     */
    @Contract("null -> null")
    public static @Nullable CustomBlockData getCustomBlockData(@Nullable ItemStack itemStack) throws MSCustomNotFoundException {
        if (itemStack == null) return null;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return null;
        String key = itemMeta.getPersistentDataContainer().get(CUSTOM_BLOCK_TYPE_NAMESPACED_KEY, PersistentDataType.STRING);
        return key == null ? null : getCustomBlockData(key);
    }

    /**
     * Gets {@link CustomBlockData} from key
     *
     * @param key {@link CustomBlockData} key string
     * @return {@link CustomBlockData} object
     * @throws MSCustomNotFoundException If {@link CustomBlockData} is not found
     */
    public static @NotNull CustomBlockData getCustomBlockData(@NotNull String key) throws MSCustomNotFoundException {
        CustomBlockData customBlockData = MSPlugin.getGlobalCache().customBlockMap.getByPrimaryKey(key);
        if (customBlockData == null) {
            throw new MSCustomNotFoundException("Custom block is not found : " + key);
        }
        return customBlockData;
    }

    /**
     * @param string String to be checked
     * @return True if string matches {@link MSBlockUtils#NAMESPACED_KEY_REGEX} regex
     */
    @Contract(value = "null -> false", pure = true)
    public static boolean matchesNamespacedKey(@Nullable String string) {
        return string != null && string.matches(NAMESPACED_KEY_REGEX);
    }
}
