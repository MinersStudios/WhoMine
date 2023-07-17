package com.github.minersstudios.mscore.listeners.inventory;

import com.github.minersstudios.mscore.inventory.CustomInventory;
import com.github.minersstudios.mscore.listener.AbstractMSListener;
import com.github.minersstudios.mscore.listener.MSListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class InventoryOpenListener extends AbstractMSListener {

    @EventHandler
    public void onInventoryOpen(@NotNull InventoryOpenEvent event) {
        if (event.getInventory() instanceof CustomInventory customInventory) {
            customInventory.doOpenAction(event);
        }
    }
}
