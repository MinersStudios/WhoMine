package com.minersstudios.msblock.listeners.event.inventory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.minersstudios.msblock.MSBlock;
import com.minersstudios.msblock.api.CustomBlockRegistry;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSEventListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

@MSEventListener
public final class InventoryClickListener extends AbstractMSListener<MSBlock> {
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
        if (
                IGNORABLE_INVENTORY_TYPES.contains(event.getInventory().getType())
                && event.isShiftClick()
                && CustomBlockRegistry.isCustomBlock(event.getCurrentItem())
        ) {
            event.setCancelled(true);
        }
    }
}
