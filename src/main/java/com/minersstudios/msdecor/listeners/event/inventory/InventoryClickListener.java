package com.minersstudios.msdecor.listeners.event.inventory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.util.MSDecorUtils;
import com.minersstudios.msdecor.MSDecor;
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
public class InventoryClickListener extends AbstractMSListener<MSDecor> {
    private static final ImmutableSet<InventoryType> IGNORABLE_INVENTORY_TYPES = Sets.immutableEnumSet(
            //<editor-fold desc="Ignorable inventory types" defaultstate="collapsed">
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
    public void onInventoryClick(final @NotNull InventoryClickEvent event) {
        final Inventory clickedInventory = event.getClickedInventory();
        final ItemStack itemInCursor = event.getCursor();
        final ItemStack currentItem = event.getCurrentItem();
        final boolean isCustomDecorInCursor = MSDecorUtils.isCustomDecor(itemInCursor);

        if (
                clickedInventory != null
                && IGNORABLE_INVENTORY_TYPES.contains(clickedInventory.getType())
                && isCustomDecorInCursor
        ) {
            event.setCancelled(true);
        }

        // WTF DID I WRITE HERE
        if (
                event.getInventory() instanceof HorseInventory
                && (!(clickedInventory instanceof PlayerInventory)
                && itemInCursor.getItemMeta() != null
                && itemInCursor.getItemMeta().hasCustomModelData()
                && isCustomDecorInCursor
                || event.getClick().isShiftClick()
                && currentItem != null
                && currentItem.getItemMeta() != null
                && currentItem.getItemMeta().hasCustomModelData()
                && MSDecorUtils.isCustomDecor(currentItem))
        ) {
            event.setCancelled(true);
        }
    }
}
