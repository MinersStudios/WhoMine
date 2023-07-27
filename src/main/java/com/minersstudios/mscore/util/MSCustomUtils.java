package com.minersstudios.mscore.util;

import com.minersstudios.msblock.customblock.CustomBlockData;
import com.minersstudios.msdecor.customdecor.CustomDecorData;
import com.minersstudios.msitem.items.CustomItem;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
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
    public static @NotNull Optional<ItemStack> getItemStack(@Nullable String namespacedKeyStr) {
        if (namespacedKeyStr == null) return Optional.empty();

        String namespace = namespacedKeyStr.substring(0, namespacedKeyStr.indexOf(":"));
        String key = namespacedKeyStr.substring(namespacedKeyStr.indexOf(":") + 1);

        return getItemStack(namespace, key);
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
    public static @NotNull Optional<ItemStack> getItemStack(@Nullable NamespacedKey namespacedKey) {
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
     * @see MSBlockUtils#getCustomBlockItem(String)
     * @see MSDecorUtils#getCustomDecorItem(String)
     * @see MSItemUtils#getCustomItemItemStack(String)
     */
    public static @NotNull Optional<ItemStack> getItemStack(
            @Nullable String namespace,
            @Nullable String key
    ) {
        return namespace == null || key == null
                ? Optional.empty()
                : switch (namespace) {
                    case "msblock" -> MSBlockUtils.getCustomBlockItem(key);
                    case "msdecor" -> MSDecorUtils.getCustomDecorItem(key);
                    case "msitems" -> MSItemUtils.getCustomItemItemStack(key);
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
    public static @NotNull Optional<?> getCustom(@Nullable ItemStack itemStack) {
        if (itemStack == null) return Optional.empty();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return Optional.empty();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        for (var namespacedKey : container.getKeys()) {
            return getCustom(namespacedKey);
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
    public static @NotNull Optional<?> getCustom(@Nullable String namespacedKeyStr) {
        if (namespacedKeyStr == null) return Optional.empty();

        String namespace = namespacedKeyStr.substring(0, namespacedKeyStr.indexOf(":"));
        String key = namespacedKeyStr.substring(namespacedKeyStr.indexOf(":") + 1);

        return getCustom(namespace, key);
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
    public static @NotNull Optional<?> getCustom(@Nullable NamespacedKey namespacedKey) {
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
     * @see MSBlockUtils#getCustomBlockData(String)
     * @see MSDecorUtils#getCustomDecorData(String)
     * @see MSItemUtils#getCustomItem(String)
     */
    public static @NotNull Optional<?> getCustom(
            @Nullable String namespace,
            @Nullable String key
    ) {
        return namespace == null || key == null
                ? Optional.empty()
                : switch (namespace) {
                    case "msblock" -> MSBlockUtils.getCustomBlockData(key);
                    case "msdecor" -> MSDecorUtils.getCustomDecorData(key);
                    case "msitems" -> MSItemUtils.getCustomItem(key);
                    default -> Optional.empty();
                };
    }
}
