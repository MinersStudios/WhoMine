package com.minersstudios.mscore.listeners.event.inventory;

import com.minersstudios.mscore.inventory.CustomInventory;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class InventoryDragListener extends AbstractMSListener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryDrag(@NotNull InventoryDragEvent event) {
        if (
                event.getInventory() instanceof CustomInventory customInventory
                && event.getRawSlots().stream().anyMatch(slot -> slot >= 0 && slot < customInventory.getSize())
        ) {
            event.setCancelled(true);
        }
    }
}