package com.github.minersstudios.msdecor.listeners.mechanic;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.mscore.utils.MSDecorUtils;
import com.github.minersstudios.msdecor.customdecor.Typed;
import com.github.minersstudios.msdecor.customdecor.register.decorations.street.Brazier;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;

@MSListener
public class BrazierMechanic implements Listener {

	@EventHandler
	public void onPlayerInteractAtEntity(@NotNull PlayerInteractAtEntityEvent event) {
		if (!(event.getRightClicked() instanceof ArmorStand armorStand)) return;
		Player player = event.getPlayer();
		EquipmentSlot equipmentSlot = event.getHand();
		ItemStack heldItem = player.getInventory().getItem(equipmentSlot);
		ItemStack helmet = armorStand.getEquipment().getHelmet();
		Block block = armorStand.getLocation().getBlock();
		if (
				(heldItem.getType() != Material.FLINT_AND_STEEL
				&& !heldItem.getType().toString().matches(".*_SHOVEL"))
				|| !(MSDecorUtils.getCustomDecorData(helmet) instanceof Brazier brazier)
				|| !(block.getBlockData() instanceof Levelled levelled)
				|| !(heldItem.getItemMeta() instanceof Damageable itemMeta)
		) return;
		Typed.Type type = brazier.getType(helmet);
		if (
				type == Brazier.Type.DEFAULT
				&& heldItem.getType() == Material.FLINT_AND_STEEL
		) {
			armorStand.getEquipment().setHelmet(brazier.createItemStack(Brazier.Type.FIRED));
			block.getWorld().playSound(armorStand.getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 1.0f, 1.0f);
		} else if (
				type == Brazier.Type.FIRED
				&& heldItem.getType().toString().matches(".*_SHOVEL")
		) {
			armorStand.getEquipment().setHelmet(brazier.createItemStack(Brazier.Type.DEFAULT));
			player.getWorld().playSound(armorStand.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 1.0f, 1.0f);
		} else {
			return;
		}
		levelled.setLevel(((Typed.LightableType) type).getSecondLightLevel());
		block.setType(Material.LIGHT);
		block.setBlockData(levelled, true);
		heldItem.setItemMeta(itemMeta);
		player.swingHand(equipmentSlot);
		if (player.getGameMode() == GameMode.SURVIVAL) {
			itemMeta.setDamage(itemMeta.getDamage() + 1);
		}
	}
}
