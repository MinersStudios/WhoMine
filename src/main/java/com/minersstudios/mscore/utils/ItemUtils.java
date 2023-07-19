package com.minersstudios.mscore.utils;

import com.minersstudios.msitem.items.DamageableItem;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class ItemUtils {

    @Contract(value = " -> fail")
    private ItemUtils() {
        throw new AssertionError("Utility class");
    }

    /**
     * @param first  First {@link ItemStack}
     * @param second Second {@link ItemStack}
     * @return True if the CustomModelData and {@link Material} of the two items are the same
     */
    @Contract("null, null -> false")
    public static boolean isSimilarItemStacks(
            @Nullable ItemStack first,
            @Nullable ItemStack second
    ) {
        if (
                first == null
                || second == null
                || first.getType() != second.getType()
        ) return false;

        ItemMeta firstMeta = first.getItemMeta();
        ItemMeta secondMeta = second.getItemMeta();

        return firstMeta.hasCustomModelData()
                && secondMeta.hasCustomModelData()
                && firstMeta.getCustomModelData() == secondMeta.getCustomModelData();
    }

    /**
     * @param list List of items
     * @param item Item that will be checked for its presence in the list
     * @return True if the list contains the item
     * <br>
     * Uses the {@link #isSimilarItemStacks(ItemStack, ItemStack)} method for this
     */
    @Contract("_, null -> false")
    public static boolean isListContainsItem(
            @NotNull List<ItemStack> list,
            @Nullable ItemStack item
    ) {
        if (list.isEmpty() || item == null) return false;
        for (var listItem : list) {
            if (isSimilarItemStacks(listItem, item)) return true;
        }
        return false;
    }

    /**
     * Deals one point of damage to the specified item
     *
     * @param holder Player who is holding the item
     * @param item   The item
     * @return False if the {@link ItemMeta} of the item is not an instance of {@link Damageable}
     */
    @Contract("_, null -> false")
    public static boolean damageItem(
            @NotNull Player holder,
            @Nullable ItemStack item
    ) {
        return damageItem(holder, item, 1);
    }

    /**
     * Damages the specified item with specified damage
     *
     * @param holder         Player who is holding the item
     * @param item           The item
     * @param originalDamage Damage you want to inflict on the item
     * @return False if the {@link ItemMeta} of the item is not an instance of {@link Damageable}
     */
    @Contract("_, null, _ -> false")
    public static boolean damageItem(
            @NotNull Player holder,
            @Nullable ItemStack item,
            int originalDamage
    ) {
        return damageItem(holder, null, item, originalDamage);
    }

    /**
     * Damages the specified item with specified damage
     *
     * @param holder         Player who is holding the item
     * @param slot           Slot where the player is holding the item (used for item break effect)
     * @param item           The item
     * @param originalDamage Damage you want to inflict on the item
     * @return False if the {@link ItemMeta} of the item is not an instance of {@link Damageable}
     */
    @Contract("_, _, null, _ -> false")
    public static boolean damageItem(
            @NotNull Player holder,
            @Nullable EquipmentSlot slot,
            @Nullable ItemStack item,
            int originalDamage
    ) {
        if (item == null || !(item.getItemMeta() instanceof Damageable damageable)) return false;

        int damage = 0;
        DamageableItem damageableItem = DamageableItem.fromItemStack(item);

        if (damageableItem != null) {
            damageableItem.setRealDamage(damageableItem.getRealDamage() + originalDamage);

            if (
                    !damageable.hasEnchant(Enchantment.DURABILITY)
                    || Math.random() < 1.0d / (damageable.getEnchantLevel(Enchantment.DURABILITY) + 1.0d)
            ) {
                damage = originalDamage;

                damageableItem.saveForItemStack(item);
            }
        } else if (
                !damageable.hasEnchant(Enchantment.DURABILITY)
                || Math.random() < 1.0d / (damageable.getEnchantLevel(Enchantment.DURABILITY) + 1.0d)
        ) {
            damage = originalDamage;

            damageable.setDamage(damageable.getDamage() + originalDamage);
            item.setItemMeta(damageable);
        }

        if (damageableItem == null) {
            PlayerItemDamageEvent event = new PlayerItemDamageEvent(holder, item, damage, originalDamage);
            holder.getServer().getPluginManager().callEvent(event);
        }

        if (
                damageableItem != null
                ? damageableItem.getRealDamage() >= damageableItem.getMaxDamage()
                : damageable.getDamage() >= item.getType().getMaxDurability()
        ) {
            item.setAmount(item.getAmount() - 1);

            if (item.getType() == Material.SHIELD) {
                holder.playEffect(EntityEffect.SHIELD_BREAK);
                return true;
            }

            switch (slot == null ? EquipmentSlot.HAND : slot) {
                case HEAD -> holder.playEffect(EntityEffect.BREAK_EQUIPMENT_HELMET);
                case CHEST -> holder.playEffect(EntityEffect.BREAK_EQUIPMENT_CHESTPLATE);
                case LEGS -> holder.playEffect(EntityEffect.BREAK_EQUIPMENT_LEGGINGS);
                case FEET -> holder.playEffect(EntityEffect.BREAK_EQUIPMENT_BOOTS);
                case OFF_HAND -> holder.playEffect(EntityEffect.BREAK_EQUIPMENT_OFF_HAND);
                default -> holder.playEffect(EntityEffect.BREAK_EQUIPMENT_MAIN_HAND);
            }
        }
        return true;
    }
}
