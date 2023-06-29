package com.github.minersstudios.msitem.listeners.mechanic;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.mscore.utils.MSItemUtils;
import com.github.minersstudios.msitem.items.register.items.BanSword;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@MSListener
public class BanSwordMechanic implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamageByEntity(@NotNull EntityDamageByEntityEvent event) {
		if (
				!(event.getDamager() instanceof Player damager)
				|| !(MSItemUtils.getCustomItem(damager.getInventory().getItemInMainHand()) instanceof BanSword)
		) return;
		Entity damagedEntity = event.getEntity();
		event.setCancelled(!damager.isOp() || damagedEntity instanceof Player);
		if (damager.isOp() && damagedEntity instanceof Player damaged) {
			damager.performCommand("ban " + damaged.getName() + " 1000y Вы были поражены великим Бан-Мечём");
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onInventoryClick(@NotNull InventoryClickEvent event) {
		ItemStack currentItem = event.getCurrentItem();
		if (!(MSItemUtils.getCustomItem(currentItem) instanceof BanSword)) return;
		currentItem.setAmount(0);
		event.setCancelled(true);
	}
}
