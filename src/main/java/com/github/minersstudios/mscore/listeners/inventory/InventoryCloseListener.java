package com.github.minersstudios.mscore.listeners.inventory;

import com.github.minersstudios.mscore.inventory.CustomInventory;
import com.github.minersstudios.mscore.listener.MSListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class InventoryCloseListener implements Listener {

    @EventHandler
    public void onInventoryClose(@NotNull InventoryCloseEvent event) {
        if (event.getInventory() instanceof CustomInventory customInventory) {
            customInventory.doCloseAction(event);
        }
    }
}
