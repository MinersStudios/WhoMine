package com.minersstudios.mscore.listeners.event.inventory;

import com.minersstudios.mscore.inventory.CustomInventory;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class InventoryOpenListener extends AbstractMSListener {

    @EventHandler
    public void onInventoryOpen(final @NotNull InventoryOpenEvent event) {
        if (event.getInventory() instanceof final CustomInventory customInventory) {
            customInventory.doOpenAction(event);
        }
    }
}
