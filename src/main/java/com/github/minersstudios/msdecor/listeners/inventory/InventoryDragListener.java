package com.github.minersstudios.msdecor.listeners.inventory;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.mscore.utils.MSDecorUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@MSListener
public class InventoryDragListener implements Listener {

	@EventHandler
	public void onInventoryDrag(@NotNull InventoryDragEvent event) {
		ItemStack itemStack = event.getNewItems().get(1);
		if (
				event.getInventory() instanceof HorseInventory
				&& event.getRawSlots().contains(1)
				&& MSDecorUtils.isCustomDecor(itemStack)
		) {
			event.setCancelled(true);
		}
	}
}
