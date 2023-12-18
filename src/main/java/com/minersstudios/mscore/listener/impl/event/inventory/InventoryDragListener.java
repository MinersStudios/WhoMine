package com.minersstudios.mscore.listener.impl.event.inventory;

import com.minersstudios.mscore.MSCore;
import com.minersstudios.mscore.inventory.CustomInventory;
import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class InventoryDragListener extends AbstractEventListener<MSCore> {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryDrag(final @NotNull InventoryDragEvent event) {
        if (!(event.getInventory() instanceof final CustomInventory customInventory)) {
            return;
        }

        for (final int slot : event.getRawSlots()) {
            if (slot >= 0 && slot < customInventory.getSize()) {
                event.setCancelled(true);
                break;
            }
        }
    }
}
