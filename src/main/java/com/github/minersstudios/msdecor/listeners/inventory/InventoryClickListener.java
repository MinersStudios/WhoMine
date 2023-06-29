package com.github.minersstudios.msdecor.listeners.inventory;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.mscore.utils.MSDecorUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

@MSListener
public class InventoryClickListener implements Listener {

	@EventHandler
	public void onInventoryClick(@NotNull InventoryClickEvent event) {
		ItemStack cursor = event.getCursor(),
				currentItem = event.getCurrentItem();
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
