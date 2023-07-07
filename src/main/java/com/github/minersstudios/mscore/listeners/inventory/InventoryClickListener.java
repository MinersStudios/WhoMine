package com.github.minersstudios.mscore.listeners.inventory;

import com.github.minersstudios.mscore.inventory.CustomInventory;
import com.github.minersstudios.mscore.inventory.InventoryButton;
import com.github.minersstudios.mscore.listener.MSListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

@MSListener
public class InventoryClickListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        ClickType clickType = event.getClick();

        if (
                clickedInventory == null
                || !(event.getView().getTopInventory() instanceof CustomInventory customInventory)
        ) return;

        if (
                clickedInventory.getType() == InventoryType.PLAYER
                && (clickType.isShiftClick() || clickType == ClickType.DOUBLE_CLICK)
        ) {
            event.setCancelled(true);
        }

        if (clickedInventory instanceof CustomInventory) {
            InventoryButton inventoryButton = customInventory.buttonAt(event.getSlot());

            if (inventoryButton != null) {
                inventoryButton.doClickAction(event, customInventory);
            }

            customInventory.doClickAction(event);

            if (customInventory.clickAction() == null) {
                event.setCancelled(true);
            }
        } else if (clickedInventory instanceof PlayerInventory) {
            customInventory.doBottomClickAction(event);
        }
    }
}
