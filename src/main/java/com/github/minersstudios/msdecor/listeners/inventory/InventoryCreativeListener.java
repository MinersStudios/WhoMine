package com.github.minersstudios.msdecor.listeners.inventory;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.msdecor.customdecor.CustomDecorData;
import com.github.minersstudios.msdecor.utils.CustomDecorUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

@MSListener
public class InventoryCreativeListener implements Listener {

	@EventHandler
	public void onInventoryCreative(@NotNull InventoryCreativeEvent event) {
		if (event.getClick() != ClickType.CREATIVE) return;
		HumanEntity player = event.getWhoClicked();
		Block clickedBlock = player.getTargetBlockExact(5);
		if (
				clickedBlock != null
				&& (event.getCursor().getType() == Material.BARRIER || event.getCursor().getType() == Material.STRUCTURE_VOID)
		) {
			CustomDecorData customDecorData = null;
			ItemStack itemStack = null;
			ItemMeta itemMeta = null;
			for (Entity nearbyEntity : clickedBlock.getWorld().getNearbyEntities(clickedBlock.getLocation().toCenterLocation(), 0.5d, 0.5d, 0.5d)) {
				if (nearbyEntity instanceof ItemFrame itemFrame && itemFrame.getItem().getItemMeta() != null) {
					customDecorData = CustomDecorUtils.getCustomDecorDataByEntity(itemFrame);
					itemStack = itemFrame.getItem();
					itemMeta = itemStack.getItemMeta();
					itemMeta.displayName(nearbyEntity.name());
				}
			}
			if (customDecorData == null) {
				for (Entity nearbyEntity : clickedBlock.getWorld().getNearbyEntities(clickedBlock.getLocation().toCenterLocation(), 0.2d, 0.3d, 0.2d)) {
					if (nearbyEntity instanceof ArmorStand armorStand && armorStand.getEquipment().getHelmet() != null) {
						customDecorData = CustomDecorUtils.getCustomDecorDataByEntity(armorStand);
						itemStack = armorStand.getEquipment().getHelmet();
					}
				}
			}
			if (itemStack != null && customDecorData != null) {
				event.setCancelled(true);
				if (itemMeta == null) {
					itemMeta = itemStack.getItemMeta();
				}
				itemMeta.setCustomModelData(customDecorData.getItemStack().getItemMeta().getCustomModelData());
				itemStack.setItemMeta(itemMeta);
				player.getInventory().setItem(event.getSlot(), itemStack);
			}
		}
	}
}
