package com.minersstudios.msessentials.anomalies;

import com.minersstudios.mscore.util.ItemUtils;
import com.minersstudios.msessentials.MSEssentials;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.Map;

/**
 * This class is used to store the items
 * that are ignored by the anomaly and protect the player.
 * All items that are ignored by the anomaly will be damaged
 * with the specified amount of damage when the anomaly action is performed.
 */
public final class AnomalyIgnorableItems {
    private final Map<EquipmentSlot, ItemStack> includedItems;
    private final int breakingPerAction;

    private static final ItemStack EMPTY_ITEM = ItemStack.empty();

    /**
     * @param includedItems     Ignorable items that will protect the player
     * @param breakingPerAction The amount of damage that will be dealt to the item
     *                          when the action is performed
     */
    public AnomalyIgnorableItems(
            final @NotNull Map<EquipmentSlot, ItemStack> includedItems,
            final int breakingPerAction
    ) {
        this.includedItems = includedItems;
        this.breakingPerAction = breakingPerAction;
    }

    /**
     * @return A map of ignorable items and their equipment slots,
     *         that will protect the player
     */
    public @NotNull @Unmodifiable Map<EquipmentSlot, ItemStack> getIncludedItems() {
        return Collections.unmodifiableMap(this.includedItems);
    }

    /**
     * @return The amount of damage that will be dealt to the item,
     *         when the action is performed
     */
    public int getBreakingValue() {
        return this.breakingPerAction;
    }

    /**
     * @param equipmentSlot The equipment slot of the item
     * @param item          The item to check
     * @return True if the item is ignorable, false otherwise
     */
    @Contract("null, null -> false")
    public boolean isIgnorableItem(
            final @Nullable EquipmentSlot equipmentSlot,
            final @Nullable ItemStack item
    ) {
        if (
                equipmentSlot == null
                || item == null
        ) return false;

        final ItemStack ignorableItem = this.includedItems.get(equipmentSlot);
        return ignorableItem == null
                || item.getType() == ignorableItem.getType()
                && item.getItemMeta().getCustomModelData() == ignorableItem.getItemMeta().getCustomModelData();
    }

    /**
     * @param inventory The player inventory to check
     * @return True if the player has all ignorable items, false otherwise
     */
    public boolean hasIgnorableItems(final @NotNull PlayerInventory inventory) {
        for (final var entry : getEquippedItems(inventory).entrySet()) {
            if (!this.includedItems.containsKey(entry.getKey())) continue;
            if (!this.isIgnorableItem(entry.getKey(), entry.getValue())) return false;
        }

        return true;
    }

    /**
     * Damages all ignorable items in the player inventory
     *
     * @param inventory The player inventory whose items will be damaged
     */
    public void damageIgnorableItems(final @NotNull PlayerInventory inventory) {
        final Player player = (Player) inventory.getHolder();

        if (player == null) return;

        for (final var entry : getEquippedItems(inventory).entrySet()) {
            final EquipmentSlot equipmentSlot = entry.getKey();
            final ItemStack item = entry.getValue();

            if (
                    this.includedItems.containsKey(equipmentSlot)
                    && this.isIgnorableItem(equipmentSlot, item)
            ) {
                MSEssentials.getInstance().runTask(() ->
                        ItemUtils.damageItem(
                                player,
                                equipmentSlot,
                                item,
                                this.breakingPerAction
                        )
                );
            }
        }
    }

    /**
     * @param inventory The player inventory to get the equipped items from
     * @return A map of equipped items and their equipment slots of the player
     *         (HEAD, CHEST, LEGS, FEET)
     */
    @Contract("_ -> new")
    private static @NotNull @Unmodifiable Map<EquipmentSlot, ItemStack> getEquippedItems(final @NotNull PlayerInventory inventory) {
        final ItemStack helmet = inventory.getHelmet();
        final ItemStack chestplate = inventory.getChestplate();
        final ItemStack leggings = inventory.getLeggings();
        final ItemStack boots = inventory.getBoots();
        return Map.of(
                EquipmentSlot.HEAD, helmet == null ? EMPTY_ITEM : helmet,
                EquipmentSlot.CHEST, chestplate == null ? EMPTY_ITEM : chestplate,
                EquipmentSlot.LEGS, leggings == null ? EMPTY_ITEM : leggings,
                EquipmentSlot.FEET, boots == null ? EMPTY_ITEM : boots
        );
    }
}
