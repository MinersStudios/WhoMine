package com.github.minersstudios.msdecor.listeners.mechanic;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.mscore.utils.MSBlockUtils;
import com.github.minersstudios.mscore.utils.MSDecorUtils;
import com.github.minersstudios.mscore.utils.PlayerUtils;
import com.github.minersstudios.msdecor.customdecor.Sittable;
import com.github.minersstudios.msdecor.utils.CustomDecorUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@MSListener
public class SittableMechanic implements Listener {

	@EventHandler
	public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
		if (
				event.getClickedBlock() == null
				|| event.getHand() == null
				|| event.getPlayer().isInsideVehicle()
		) return;
		Block clickedBlock = event.getClickedBlock();
		Player player = event.getPlayer();
		GameMode gameMode = player.getGameMode();
		EquipmentSlot hand = event.getHand();
		ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
		if (MSBlockUtils.isCustomBlock(itemInMainHand)) return;
		if (hand != EquipmentSlot.HAND && MSDecorUtils.isCustomDecor(itemInMainHand)) {
			hand = EquipmentSlot.HAND;
		}
		ItemStack itemInHand = player.getInventory().getItem(hand);
		if (
				event.getAction() == Action.RIGHT_CLICK_BLOCK
				&& !player.isSneaking()
				&& clickedBlock.getType() == Material.BARRIER
				&& (!itemInHand.getType().isBlock() || itemInHand.getType() == Material.AIR)
				&& !MSBlockUtils.isCustomBlock(itemInHand)
				&& !MSDecorUtils.isCustomDecor(itemInHand)
				&& event.getHand() == EquipmentSlot.HAND
				&& gameMode != GameMode.SPECTATOR
				&& !clickedBlock.getRelative(BlockFace.UP).getType().isSolid()
				&& CustomDecorUtils.getCustomDecorDataByLocation(clickedBlock.getLocation()) instanceof Sittable sittable
		) {
			event.setCancelled(true);
			Location sitLocation = clickedBlock.getLocation().clone().add(0.5d, sittable.getHeight(), 0.5d);
			for (Entity entity : player.getWorld().getNearbyEntities(sitLocation, 0.5d, 0.5d, 0.5d)) {
				if (entity.getType() == EntityType.PLAYER && !entity.equals(player)) return;
			}
			sitLocation.getWorld().playSound(sitLocation, Sound.ENTITY_PIG_SADDLE, SoundCategory.PLAYERS, 0.15f, 1.0f);
			PlayerUtils.setSitting(player, sitLocation);
			player.swingHand(hand);
			player.updateInventory();
		}
	}
}
