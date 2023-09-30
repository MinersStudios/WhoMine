package com.minersstudios.msdecor.listeners.inventory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.util.MSDecorUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

@MSListener
public class InventoryClickListener extends AbstractMSListener {
    private static final ImmutableSet<InventoryType> IGNORABLE_INVENTORY_TYPES = Sets.immutableEnumSet(
            //<editor-fold desc="Ignorable inventory types">
            InventoryType.CARTOGRAPHY,
            InventoryType.BREWING,
            InventoryType.BEACON,
            InventoryType.BLAST_FURNACE,
            InventoryType.FURNACE,
            InventoryType.SMOKER,
            InventoryType.GRINDSTONE,
            InventoryType.STONECUTTER,
            InventoryType.SMITHING,
            InventoryType.LOOM,
            InventoryType.MERCHANT,
            InventoryType.ENCHANTING
            //</editor-fold>
    );

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        ItemStack itemInCursor = event.getCursor();
        ItemStack currentItem = event.getCurrentItem();

        if (
                clickedInventory != null
                && IGNORABLE_INVENTORY_TYPES.contains(clickedInventory.getType())
                && MSDecorUtils.isCustomDecor(itemInCursor)
        ) {
            event.setCancelled(true);
        }

        // WTF DID I WRITE HERE
        if (
                event.getInventory() instanceof HorseInventory
                && (!(clickedInventory instanceof PlayerInventory)
                && itemInCursor.getItemMeta() != null
                && itemInCursor.getItemMeta().hasCustomModelData()
                && MSDecorUtils.isCustomDecor(itemInCursor)
                && MSDecorUtils.getCustomDecorData(itemInCursor).isPresent()
                || event.getClick().isShiftClick()
                && currentItem != null
                && currentItem.getItemMeta() != null
                && currentItem.getItemMeta().hasCustomModelData()
                && MSDecorUtils.isCustomDecor(currentItem)
                && MSDecorUtils.getCustomDecorData(currentItem).isPresent())
        ) {
            event.setCancelled(true);
        }
    }
}
