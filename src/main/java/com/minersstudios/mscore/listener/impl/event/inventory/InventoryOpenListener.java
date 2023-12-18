package com.minersstudios.mscore.listener.impl.event.inventory;

import com.minersstudios.mscore.MSCore;
import com.minersstudios.mscore.inventory.CustomInventory;
import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class InventoryOpenListener extends AbstractEventListener<MSCore> {

    @EventHandler
    public void onInventoryOpen(final @NotNull InventoryOpenEvent event) {
        if (event.getInventory() instanceof final CustomInventory customInventory) {
            customInventory.doOpenAction(event);
        }
    }
}
