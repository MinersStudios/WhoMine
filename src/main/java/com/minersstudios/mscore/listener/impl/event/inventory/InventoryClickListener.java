package com.minersstudios.mscore.listener.impl.event.inventory;

import com.minersstudios.mscore.MSCore;
import com.minersstudios.mscore.inventory.CustomInventory;
import com.minersstudios.mscore.inventory.InventoryButton;
import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class InventoryClickListener extends AbstractEventListener<MSCore> {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(final @NotNull InventoryClickEvent event) {
        final Inventory clickedInventory = event.getClickedInventory();
        final ClickType clickType = event.getClick();

        if (
                clickedInventory == null
                || !(event.getView().getTopInventory() instanceof final CustomInventory customInventory)
        ) {
            return;
        }

        if (
                clickedInventory.getType() == InventoryType.PLAYER
                && (clickType.isShiftClick() || clickType == ClickType.DOUBLE_CLICK)
        ) {
            event.setCancelled(true);
        }

        if (clickedInventory instanceof CustomInventory) {
            final InventoryButton inventoryButton = customInventory.buttonAt(event.getSlot());

            if (inventoryButton != null) {
                inventoryButton.doClickAction(event, customInventory);
            }

            customInventory.doClickAction(event);

            if (
                    customInventory.clickAction() == null
                    && !clickType.isCreativeAction()
            ) {
                event.setCancelled(true);
            }
        } else if (clickedInventory instanceof PlayerInventory) {
            customInventory.doBottomClickAction(event);
        }
    }
}
