package com.minersstudios.msdecor.listener.event.inventory;

import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.mscore.utility.MSDecorUtils;
import com.minersstudios.msdecor.MSDecor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.HorseInventory;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class InventoryDragListener extends AbstractEventListener<MSDecor> {

    @EventHandler
    public void onInventoryDrag(final @NotNull InventoryDragEvent event) {
        if (
                event.getInventory() instanceof HorseInventory
                && event.getRawSlots().contains(1)
                && MSDecorUtils.isCustomDecor(event.getNewItems().get(1))
        ) {
            event.setCancelled(true);
        }
    }
}
