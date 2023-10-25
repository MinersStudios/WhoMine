package com.minersstudios.msdecor.listeners.event.inventory;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.util.MSDecorUtils;
import com.minersstudios.msdecor.MSDecor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@MSListener
public final class InventoryDragListener extends AbstractMSListener<MSDecor> {

    @EventHandler
    public void onInventoryDrag(final @NotNull InventoryDragEvent event) {
        final ItemStack itemStack = event.getNewItems().get(1);

        if (
                event.getInventory() instanceof HorseInventory
                && event.getRawSlots().contains(1)
                && MSDecorUtils.isCustomDecor(itemStack)
        ) {
            event.setCancelled(true);
        }
    }
}
