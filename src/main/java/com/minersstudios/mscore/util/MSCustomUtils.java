package com.minersstudios.mscore.util;

import com.minersstudios.msblock.api.CustomBlockData;
import com.minersstudios.msblock.api.CustomBlockRegistry;
import com.minersstudios.msdecor.api.CustomDecorData;
import com.minersstudios.msdecor.api.CustomDecorType;
import com.minersstudios.msitem.api.CustomItem;
import com.minersstudios.msitem.api.CustomItemType;
import com.minersstudios.msitem.api.renameable.RenameableItemRegistry;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Utility class for custom items / blocks / decor.
 * Allowed namespaces: msitems, msblock, msdecor.
 *
 * @see CustomItem
 * @see CustomBlockData
 * @see CustomDecorData
 */
public final class MSCustomUtils {

    @Contract(value = " -> fail")
    private MSCustomUtils() {
        throw new AssertionError("Utility class");
    }

    /**
     * Gets an {@link ItemStack} of custom item / block / decor
     * from NamespacedKey string
     *
     * @param namespacedKeyStr NamespacedKey string,
     *                         example - (msitems:example)
     * @return Optional of {@link ItemStack} object
     *         or empty optional if not found
     * @see #getItemStack(String, String)
     */
    public static @NotNull Optional<ItemStack> getItemStack(final @Nullable String namespacedKeyStr) {
        if (StringUtils.isBlank(namespacedKeyStr)) return Optional.empty();

        final int index = namespacedKeyStr.indexOf(":");

        return getItemStack(
                namespacedKeyStr.substring(0, index),
                namespacedKeyStr.substring(index + 1)
        );
    }

    /**
     * Gets an {@link ItemStack} of custom item / block / decor
     * from {@link NamespacedKey}
     *
     * @param namespacedKey NamespacedKey of custom item / block / decor,
     *                      example - (msitems:example)
     * @return Optional of {@link ItemStack} object
     *         or empty optional if not found
     * @see #getItemStack(String, String)
     */
    public static @NotNull Optional<ItemStack> getItemStack(final @Nullable NamespacedKey namespacedKey) {
        return namespacedKey == null
                ? Optional.empty()
                : getItemStack(namespacedKey.getNamespace(), namespacedKey.getKey());
    }

    /**
     * Gets an {@link ItemStack} of custom item / block / decor
     * from namespace and key
     *
     * @param namespace The namespace of the plugin,
     *                  example - (msitems, msblock, msdecor)
     * @param key       The key of the custom item / block / decor,
     *                  example - (example)
     * @return Optional of {@link ItemStack} object
     *         or empty optional if not found
     * @see MSBlockUtils#getItemStack(String)
     * @see MSDecorUtils#getItemStack(String)
     * @see MSItemUtils#getItemStack(String)
     */
    public static @NotNull Optional<ItemStack> getItemStack(
            final @Nullable String namespace,
            final @Nullable String key
    ) {
        return namespace == null || key == null
                ? Optional.empty()
                : switch (namespace) {
                    case CustomBlockRegistry.NAMESPACE -> MSBlockUtils.getItemStack(key);
                    case CustomDecorType.NAMESPACE -> MSDecorUtils.getItemStack(key);
                    case CustomItemType.NAMESPACE -> MSItemUtils.getItemStack(key);
                    default -> Optional.empty();
                };
    }

    /**
     * Gets {@link CustomBlockData}
     * or {@link CustomDecorData}
     * or {@link CustomItem}
     * from {@link ItemStack}
     *
     * @param itemStack {@link ItemStack} of custom item / block / decor
     * @return Optional of {@link CustomBlockData}
     *         or {@link CustomDecorData}
     *         or {@link CustomItem}
     *         or empty optional if not found
     * @see #getCustom(NamespacedKey)
     */
    public static @NotNull Optional<?> getCustom(final @Nullable ItemStack itemStack) {
        if (itemStack == null) return Optional.empty();
        final ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return Optional.empty();
        final PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        for (final var namespacedKey : container.getKeys()) {
            if (!namespacedKey.equals(RenameableItemRegistry.RENAMEABLE_NAMESPACED_KEY)) {
                return getCustom(namespacedKey.getNamespace(), container.get(namespacedKey, PersistentDataType.STRING));
            }
        }

        return Optional.empty();
    }

    /**
     * Gets {@link CustomBlockData}
     * or {@link CustomDecorData}
     * or {@link CustomItem}
     * from namespaced key string
     *
     * @param namespacedKeyStr Namespaced key string,
     *                         example - (msitems:example)
     * @return Optional of {@link CustomBlockData}
     *         or {@link CustomDecorData}
     *         or {@link CustomItem}
     *         or empty optional if not found
     * @see #getCustom(String, String)
     */
    public static @NotNull Optional<?> getCustom(final @Nullable String namespacedKeyStr) {
        if (StringUtils.isBlank(namespacedKeyStr)) return Optional.empty();

        final int index = namespacedKeyStr.indexOf(":");

        return getCustom(
                namespacedKeyStr.substring(0, index),
                namespacedKeyStr.substring(index + 1)
        );
    }

    /**
     * Gets {@link CustomBlockData}
     * or {@link CustomDecorData}
     * or {@link CustomItem}
     * from {@link NamespacedKey}
     *
     * @param namespacedKey NamespacedKey of custom item / block / decor,
     *                      example - (msitems:example)
     * @return Optional of {@link CustomBlockData}
     *         or {@link CustomDecorData}
     *         or {@link CustomItem}
     *         or empty optional if not found
     * @see #getCustom(String, String)
     */
    public static @NotNull Optional<?> getCustom(final @Nullable NamespacedKey namespacedKey) {
        return namespacedKey == null
                ? Optional.empty()
                : getCustom(namespacedKey.getNamespace(), namespacedKey.getKey());
    }

    /**
     * Gets {@link CustomBlockData}
     * or {@link CustomDecorData}
     * or {@link CustomItem}
     * from namespace and key
     *
     * @param namespace The namespace of the plugin,
     *                  example - (msitems, msblock, msdecor)
     * @param key       The key of the custom item / block / decor,
     *                  example - (example)
     * @return Optional of {@link CustomBlockData}
     *         or {@link CustomDecorData}
     *         or {@link CustomItem}
     *         or empty optional if not found
     * @see CustomBlockRegistry#fromKey(String)
     * @see CustomDecorType#fromKey(String)
     * @see CustomItemType#fromKey(String)
     */
    public static @NotNull Optional<?> getCustom(
            final @Nullable String namespace,
            final @Nullable String key
    ) {
        return StringUtils.isBlank(namespace) || StringUtils.isBlank(key)
                ? Optional.empty()
                : switch (namespace) {
                    case CustomBlockRegistry.NAMESPACE,
                            CustomBlockRegistry.NAMESPACE + ":type" -> CustomBlockRegistry.fromKey(key);
                    case CustomDecorType.NAMESPACE,
                            CustomDecorType.NAMESPACE + ":type" -> CustomDecorData.fromKey(key);
                    case CustomItemType.NAMESPACE,
                            CustomItemType.NAMESPACE + ":type" -> CustomItem.fromKey(key);
                    default -> Optional.empty();
                };
    }
}
