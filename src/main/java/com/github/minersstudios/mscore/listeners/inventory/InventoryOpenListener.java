package com.github.minersstudios.mscore.listeners.inventory;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.mscore.inventory.CustomInventory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class InventoryOpenListener implements Listener {

    @EventHandler
    public void onInventoryOpen(@NotNull InventoryOpenEvent event) {
        if (event.getInventory() instanceof CustomInventory customInventory) {
            customInventory.doOpenAction(event);
        }
    }
}
