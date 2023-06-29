package com.github.minersstudios.msitems.listeners.inventory;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.mscore.utils.MSItemUtils;
import com.github.minersstudios.msitems.MSItems;
import com.github.minersstudios.msitems.items.Wearable;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

@MSListener
public class InventoryClickListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onInventoryClick(@NotNull InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		PlayerInventory inventory = player.getInventory();
		Inventory clickedInventory = event.getClickedInventory();
		ItemStack cursorItem = event.getCursor(),
				currentItem = event.getCurrentItem();
		int slot = event.getSlot();

		if (
				slot == 39
				&& event.getSlotType() == InventoryType.SlotType.ARMOR
				&& cursorItem != null
				&& !cursorItem.getType().isAir()
				&& MSItemUtils.getCustomItem(cursorItem) instanceof Wearable
		) {
			if (
					currentItem != null
					&& currentItem.getEnchantments().containsKey(Enchantment.BINDING_CURSE)
			) return;
			Bukkit.getScheduler().runTask(MSItems.getInstance(), () -> {
				inventory.setHelmet(cursorItem);
				player.setItemOnCursor(currentItem);
			});
		}

		if (
				clickedInventory != null
				&& event.isShiftClick()
				&& clickedInventory.getType() == InventoryType.PLAYER
				&& player.getOpenInventory().getType() == InventoryType.CRAFTING
				&& inventory.getHelmet() == null
				&& MSItemUtils.getCustomItem(currentItem) instanceof Wearable
		) {
			event.setCancelled(true);
			Bukkit.getScheduler().runTask(MSItems.getInstance(), () -> {
				inventory.setHelmet(currentItem);
				currentItem.setAmount(0);
			});
		}
	}
}
