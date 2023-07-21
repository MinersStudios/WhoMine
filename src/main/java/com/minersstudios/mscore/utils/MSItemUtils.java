package com.minersstudios.mscore.utils;

import com.minersstudios.mscore.GlobalCache;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.msitem.items.CustomItem;
import com.minersstudios.msitem.items.RenameableItem;
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
 * Utility class for {@link CustomItem}
 */
public final class MSItemUtils {
    public static final NamespacedKey CUSTOM_ITEM_TYPE_NAMESPACED_KEY = new NamespacedKey("msitems", "type");
    public static final NamespacedKey CUSTOM_ITEM_RENAMEABLE_NAMESPACED_KEY = new NamespacedKey("msitems", "renameable");
    public static final String NAMESPACED_KEY_REGEX = "(msitems):(\\w+)";
    public static final Pattern NAMESPACED_KEY_PATTERN = Pattern.compile(NAMESPACED_KEY_REGEX);

    @Contract(value = " -> fail")
    private MSItemUtils() {
        throw new AssertionError("Utility class");
    }

    /**
     * Gets {@link CustomItem} item stack from key from
     * {@link GlobalCache#customItemMap} by using
     * {@link #getCustomItem(String)} method
     *
     * @param key {@link CustomItem} key string
     * @return Optional of {@link ItemStack} object
     *         or empty optional if not found
     * @see #getCustomItem(String)
     * @see CustomItem#getItemStack()
     */
    public static @NotNull Optional<ItemStack> getCustomItemItemStack(@Nullable String key) {
        return getCustomItem(key).map(CustomItem::getItemStack);
    }

    /**
     * Gets {@link CustomItem} from {@link ItemStack} from
     * {@link GlobalCache#customItemMap} by using persistent data
     * container of item and {@link #getCustomItem(String)}
     * method
     *
     * @param itemStack {@link ItemStack} to be checked
     * @return Optional of {@link CustomItem} object
     *         or empty optional if not found
     * @see #getCustomItem(String)
     */
    public static @NotNull Optional<CustomItem> getCustomItem(@Nullable ItemStack itemStack) {
        if (itemStack == null) return Optional.empty();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return Optional.empty();
        String key = itemMeta.getPersistentDataContainer().get(CUSTOM_ITEM_TYPE_NAMESPACED_KEY, PersistentDataType.STRING);
        return getCustomItem(key);
    }

    /**
     * Gets {@link CustomItem} from key from
     * {@link GlobalCache#customItemMap}
     *
     * @param key {@link CustomItem} key string
     * @return Optional of {@link CustomItem} object
     *         or empty optional if not found
     */
    public static @NotNull Optional<CustomItem> getCustomItem(@Nullable String key) {
        return key == null
                ? Optional.empty()
                : Optional.ofNullable(MSPlugin.getGlobalCache().customItemMap.getByPrimaryKey(key));
    }

    /**
     * Checks if item has {@link #CUSTOM_ITEM_TYPE_NAMESPACED_KEY}
     * in {@link PersistentDataContainer} of item
     *
     * @param itemStack Item to be checked
     * @return True if item is {@link CustomItem} item
     * @see CustomItem
     */
    @Contract("null -> false")
    public static boolean isCustomItem(@Nullable ItemStack itemStack) {
        if (itemStack == null) return false;
        ItemMeta itemMeta = itemStack.getItemMeta();
        return itemMeta != null && itemMeta.getPersistentDataContainer().has(CUSTOM_ITEM_TYPE_NAMESPACED_KEY);
    }

    /**
     * Checks if item has {@link #CUSTOM_ITEM_RENAMEABLE_NAMESPACED_KEY}
     * in {@link PersistentDataContainer} of item
     *
     * @param itemStack Item to be checked
     * @return True if item is {@link RenameableItem} item
     * @see RenameableItem
     */
    @Contract("null -> false")
    public static boolean isRenameableItem(@Nullable ItemStack itemStack) {
        if (itemStack == null) return false;
        ItemMeta itemMeta = itemStack.getItemMeta();
        return itemMeta != null && itemMeta.getPersistentDataContainer().has(CUSTOM_ITEM_RENAMEABLE_NAMESPACED_KEY);
    }

    /**
     * @param string String to be checked
     * @return True if string matches {@link #NAMESPACED_KEY_REGEX} regex
     */
    @Contract(value = "null -> false", pure = true)
    public static boolean matchesNamespacedKey(@Nullable String string) {
        return string != null && NAMESPACED_KEY_PATTERN.matcher(string).matches();
    }
}
