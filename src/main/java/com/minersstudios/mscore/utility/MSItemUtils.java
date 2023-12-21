package com.minersstudios.mscore.utility;

import com.minersstudios.msitem.MSItem;
import com.minersstudios.msitem.api.CustomItem;
import com.minersstudios.msitem.api.CustomItemType;
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
    public static final String NAMESPACED_KEY_REGEX = '(' + MSItem.NAMESPACE + "):(" + ChatUtils.KEY_REGEX + ")";
    public static final Pattern NAMESPACED_KEY_PATTERN = Pattern.compile(NAMESPACED_KEY_REGEX);

    @Contract(" -> fail")
    private MSItemUtils() {
        throw new AssertionError("Utility class");
    }

    /**
     * Gets {@link CustomItem} item stack from key from
     * {@link CustomItemType} by using
     * {@link CustomItem#fromKey(String)} method
     *
     * @param key {@link CustomItem} key string
     * @return Optional of {@link ItemStack} object
     *         or empty optional if not found
     * @see CustomItem#fromKey(String)
     * @see CustomItem#getItem()
     */
    public static @NotNull Optional<ItemStack> getItemStack(final @Nullable String key) {
        return CustomItem.fromKey(key).map(CustomItem::getItem);
    }

    /**
     * Gets {@link CustomItem} item stack from class from
     * {@link CustomItemType} by using
     * {@link CustomItem#fromClass(Class)} method
     *
     * @param clazz {@link CustomItem} class
     * @return Optional of {@link ItemStack} object
     *         or empty optional if not found
     * @see CustomItem#fromClass(Class)
     * @see CustomItem#getItem()
     */
    public static @NotNull Optional<ItemStack> getItemStack(final @NotNull Class<? extends CustomItem> clazz) {
       return CustomItem.fromClass(clazz).map(CustomItem::getItem);
    }

    /**
     * Checks if the item stack is a custom item by verifying if it has a valid
     * key associated with it
     *
     * @param itemStack The item stack to check
     * @return True if the item stack is a custom item
     * @see CustomItem#fromItemStack(ItemStack)
     */
    @Contract("null -> false")
    public static boolean isCustomItem(final @Nullable ItemStack itemStack) {
        return CustomItem.fromItemStack(itemStack).isPresent();
    }

    /**
     * @param string String to be checked
     * @return True if string matches {@link #NAMESPACED_KEY_REGEX}
     */
    @Contract("null -> false")
    public static boolean matchesNamespacedKey(final @Nullable String string) {
        return ChatUtils.isNotBlank(string)
                && NAMESPACED_KEY_PATTERN.matcher(string).matches();
    }
}
