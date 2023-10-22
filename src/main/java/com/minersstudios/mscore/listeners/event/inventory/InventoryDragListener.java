package com.minersstudios.mscore.listeners.event.inventory;

import com.minersstudios.mscore.MSCore;
import com.minersstudios.mscore.inventory.CustomInventory;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class InventoryDragListener extends AbstractMSListener<MSCore> {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryDrag(final @NotNull InventoryDragEvent event) {
        if (!(event.getInventory() instanceof final CustomInventory customInventory)) return;

        for (final int slot : event.getRawSlots()) {
            if (slot >= 0 && slot < customInventory.getSize()) {
                event.setCancelled(true);
                break;
            }
        }
    }
}
