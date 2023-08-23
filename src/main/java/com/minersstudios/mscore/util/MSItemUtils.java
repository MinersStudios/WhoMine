package com.minersstudios.mscore.util;

import com.minersstudios.msitem.item.CustomItem;
import com.minersstudios.msitem.item.CustomItemType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Utility class for {@link CustomItem}
 */
public final class MSItemUtils {
    public static final String NAMESPACED_KEY_REGEX = "(" + CustomItemType.NAMESPACE + "):([a-z0-9./_-]+)";
    public static final Pattern NAMESPACED_KEY_PATTERN = Pattern.compile(NAMESPACED_KEY_REGEX);

    @Contract(value = " -> fail")
    private MSItemUtils() {
        throw new AssertionError("Utility class");
    }

    /**
     * Gets {@link CustomItem} item stack from key from
     * {@link CustomItemType} by using
     * {@link CustomItemType#fromKey(String)} method
     *
     * @param key {@link CustomItem} key string
     * @return Optional of {@link ItemStack} object
     *         or empty optional if not found
     * @see CustomItemType#fromKey(String)
     * @see CustomItem#getItem()
     */
    public static @NotNull Optional<ItemStack> getItemStack(final @Nullable String key) {
        return CustomItemType.fromKey(key).map(CustomItem::getItem);
    }

    /**
     * Gets {@link CustomItem} item stack from class from
     * {@link CustomItemType} by using
     * {@link CustomItemType#fromClass(Class)} method
     *
     * @param clazz {@link CustomItem} class
     * @return Optional of {@link ItemStack} object
     *         or empty optional if not found
     * @see CustomItemType#fromClass(Class)
     * @see CustomItem#getItem()
     */
    public static @NotNull Optional<ItemStack> getItemStack(final @NotNull Class<? extends CustomItem> clazz) {
       return CustomItemType.fromClass(clazz).map(CustomItem::getItem);
    }

    /**
     * @param string String to be checked
     * @return True if string matches {@link #NAMESPACED_KEY_REGEX}
     */
    @Contract(value = "null -> false")
    public static boolean matchesNamespacedKey(final @Nullable String string) {
        return string != null && NAMESPACED_KEY_PATTERN.matcher(string).matches();
    }
}
