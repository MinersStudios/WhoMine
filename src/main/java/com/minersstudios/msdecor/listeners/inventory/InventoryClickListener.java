package com.minersstudios.msdecor.listeners.inventory;

import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.utils.MSDecorUtils;
import org.bukkit.event.EventHandler;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

@MSListener
public class InventoryClickListener extends AbstractMSListener {

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        ItemStack cursor = event.getCursor();
        ItemStack currentItem = event.getCurrentItem();

        event.setCancelled(
                event.getInventory() instanceof HorseInventory
                && (cursor != null
                && !(event.getClickedInventory() instanceof PlayerInventory)
                && cursor.getItemMeta() != null
                && cursor.getItemMeta().hasCustomModelData()
                && MSDecorUtils.isCustomDecor(cursor)
                && MSDecorUtils.getCustomDecorData(cursor) != null
                || event.getClick().isShiftClick()
                && currentItem != null
                && currentItem.getItemMeta() != null
                && currentItem.getItemMeta().hasCustomModelData()
                && MSDecorUtils.isCustomDecor(currentItem)
                && MSDecorUtils.getCustomDecorData(currentItem) != null)
        );
    }
}
