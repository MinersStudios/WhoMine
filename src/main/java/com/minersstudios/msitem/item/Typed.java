package com.minersstudios.msitem.item;

import net.kyori.adventure.text.Component;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * This interface represents a typed custom item,
 * which can have multiple types or variations.
 * It extends the {@link CustomItem} interface and
 * provides methods for interacting with different
 * types of the item. For example, a custom sword
 * could have multiple types, such as a wooden sword,
 * stone sword, iron sword, etc. Each type would have
 * its own custom model data, name, and lore, and
 * will be registered as a separate custom item.
 *
 * @see CustomItem
 */
public interface Typed extends CustomItem {

    /**
     * @return An array of {@link Typed.Type} representing
     *         the different types of this custom item
     */
    Typed.Type @NotNull [] getTypes();

    /**
     * Get the type of given item stack
     *
     * @param itemStack The {@link ItemStack} to check the type of
     *                  (must be an item stack of this custom item)
     * @return The {@link Typed.Type} of the item stack,
     *         or null if the type cannot be determined
     */
    @Contract("null -> null")
    default @Nullable Typed.Type getType(@Nullable ItemStack itemStack) {
        if (itemStack == null) return null;
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (
                itemMeta == null
                || !itemMeta.hasCustomModelData()
        ) return null;

        for (var type : this.getTypes()) {
            if (
                    itemMeta.getCustomModelData() == type.getCustomModelData()
                    && itemStack.getType() == this.getItem().getType()
            ) {
                return type;
            }
        }

        return null;
    }

    /**
     * Creates a new item stack of this custom item type
     *
     * @param type The {@link Typed.Type} representing the desired
     *                                    type of the item stack
     * @return A new {@link ItemStack} of this custom item with
     *         the given type applied to it
     */
    default @NotNull ItemStack createItemStack(@NotNull Typed.Type type) {
        ItemStack itemStack = this.getItem();
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setCustomModelData(type.getCustomModelData());
        itemMeta.displayName(type.getItemName());
        itemMeta.lore(type.getLore());
        itemMeta.getPersistentDataContainer().set(
                CustomItemType.TYPE_NAMESPACED_KEY,
                PersistentDataType.STRING,
                type.getKey().getKey()
        );
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    /**
     * Create a custom item of the specified type
     *
     * @param type The {@link Typed.Type} representing the
     *             desired type of the custom item
     * @return A new {@link CustomItem} instance of this
     *         custom item with the given type applied to it
     * @see #createItemStack(Typed.Type)
     */
    default @NotNull CustomItem createCustomItem(@NotNull Typed.Type type) {
        return CustomItemImpl.create(
                type.getKey().getKey(),
                this.createItemStack(type)
        );
    }

    /**
     * Represents a specific type or variation of the
     * Typed custom item
     */
    interface Type extends Keyed {

        /**
         * @return The {@link NamespacedKey} identifying this type
         */
        @Override
        @NotNull NamespacedKey getKey();

        /**
         * @return The component representing the name of this type
         */
        @NotNull Component getItemName();

        /**
         * @return The custom model data of this type
         */
        int getCustomModelData();

        /**
         * @return The lore of this type
         */
        default @Nullable List<Component> getLore() {
            return null;
        }
    }
}
