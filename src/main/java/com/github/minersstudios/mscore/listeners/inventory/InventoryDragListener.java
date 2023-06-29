package com.github.minersstudios.mscore.listeners.inventory;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.mscore.inventory.CustomInventory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class InventoryDragListener implements Listener {

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
