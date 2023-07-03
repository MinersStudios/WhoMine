package com.github.minersstudios.msessentials.anomalies;

import com.github.minersstudios.mscore.utils.ItemUtils;
import com.github.minersstudios.msessentials.MSEssentials;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class is used to store the items
 * that are ignored by the anomaly and protect the player.
 * All items that are ignored by the anomaly will be damaged
 * with the specified amount of damage when the anomaly action is performed.
 */
public class AnomalyIgnorableItems {
    private final @NotNull Map<EquipmentSlot, ItemStack> includedItems;
    private final int breakingPerAction;

    /**
     * @param includedItems     Ignorable items that will protect the player
     * @param breakingPerAction The amount of damage that will be dealt to the item
     *                          when the action is performed
     */
    public AnomalyIgnorableItems(
            @NotNull Map<EquipmentSlot, ItemStack> includedItems,
            int breakingPerAction
    ) {
        this.includedItems = includedItems;
        this.breakingPerAction = breakingPerAction;
    }

    /**
     * @param equipmentSlot The equipment slot of the item
     * @param item          The item to check
     * @return True if the item is ignorable, false otherwise
     */
    @Contract("null, null -> false")
    public boolean isIgnorableItem(
            @Nullable EquipmentSlot equipmentSlot,
            @Nullable ItemStack item
    ) {
        if (equipmentSlot == null || item == null) return false;
        ItemStack ignorableItem = this.includedItems.get(equipmentSlot);
        return ignorableItem == null
                || item.getType() == ignorableItem.getType()
                && item.getItemMeta().getCustomModelData() == ignorableItem.getItemMeta().getCustomModelData();
    }

    /**
     * @param inventory The player inventory to check
     * @return True if the player has all ignorable items, false otherwise
     */
    public boolean hasIgnorableItems(@NotNull PlayerInventory inventory) {
        for (var entry : getEquippedItems(inventory).entrySet()) {
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
    public void damageIgnorableItems(@NotNull PlayerInventory inventory) {
        for (var entry : getEquippedItems(inventory).entrySet()) {
            EquipmentSlot equipmentSlot = entry.getKey();
            ItemStack item = entry.getValue();

            if (
                    this.includedItems.containsKey(equipmentSlot)
                    && this.isIgnorableItem(equipmentSlot, item)
            ) {
                Bukkit.getScheduler().runTask(
                        MSEssentials.getInstance(),
                        () -> ItemUtils.damageItem(
                                (Player) Objects.requireNonNull(inventory.getHolder()),
                                equipmentSlot,
                                item,
                                this.breakingPerAction
                        )
                );
            }
        }
    }

    /**
     * @return A map of ignorable items and their equipment slots,
     *         that will protect the player
     */
    public @NotNull Map<EquipmentSlot, ItemStack> getIncludedItems() {
        return Map.copyOf(this.includedItems);
    }

    /**
     * @return The amount of damage that will be dealt to the item,
     *         when the action is performed
     */
    public int getBreakingValue() {
        return this.breakingPerAction;
    }

    /**
     * @param inventory The player inventory to get the equipped items from
     * @return A map of equipped items and their equipment slots of the player
     *         (HEAD, CHEST, LEGS, FEET)
     */
    private static @NotNull Map<@NotNull EquipmentSlot, @Nullable ItemStack> getEquippedItems(@NotNull PlayerInventory inventory) {
        var playerEquippedItems = new HashMap<EquipmentSlot, ItemStack>();

        playerEquippedItems.put(EquipmentSlot.HEAD, inventory.getHelmet());
        playerEquippedItems.put(EquipmentSlot.CHEST, inventory.getChestplate());
        playerEquippedItems.put(EquipmentSlot.LEGS, inventory.getLeggings());
        playerEquippedItems.put(EquipmentSlot.FEET, inventory.getBoots());

        return playerEquippedItems;
    }
}
