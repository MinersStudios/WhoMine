package com.minersstudios.msblock.listeners.inventory;

import com.minersstudios.msblock.utils.CustomBlockUtils;
import com.minersstudios.mscore.listener.MSListener;
import com.minersstudios.mscore.utils.MSBlockUtils;
import com.minersstudios.mscore.utils.MSDecorUtils;
import com.minersstudios.mscore.listener.AbstractMSListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@MSListener
public class InventoryClickListener extends AbstractMSListener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        ItemStack itemInCursor = event.getCursor();
        ItemStack currentItem = event.getCurrentItem();

        if (
                CustomBlockUtils.IGNORABLE_INVENTORY_TYPES.contains(event.getInventory().getType())
                && event.isShiftClick()
                && MSBlockUtils.isCustomBlock(currentItem)
        ) {
            event.setCancelled(true);
        } else if (
                clickedInventory != null
                && CustomBlockUtils.IGNORABLE_INVENTORY_TYPES.contains(clickedInventory.getType())
                && MSDecorUtils.isCustomDecor(itemInCursor)
        ) {
            event.setCancelled(true);
        }
    }
}
