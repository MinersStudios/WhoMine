package com.github.minersstudios.msdecor.listeners.player;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.mscore.utils.BlockUtils;
import com.github.minersstudios.mscore.utils.MSBlockUtils;
import com.github.minersstudios.mscore.utils.MSDecorUtils;
import com.github.minersstudios.msdecor.customdecor.CustomDecor;
import com.github.minersstudios.msdecor.customdecor.CustomDecorData;
import com.github.minersstudios.msdecor.utils.CustomDecorUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerInteractListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlaceArmorStand(@NotNull PlayerInteractEvent event) {
		if (
				event.getAction() != Action.RIGHT_CLICK_BLOCK
				|| event.getClickedBlock() == null
				|| event.getHand() == null
				|| !MSDecorUtils.isCustomDecor(event.getPlayer().getInventory().getItemInMainHand())
		) return;
		event.setUseItemInHand(Event.Result.DENY);
	}

	@EventHandler
	public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
		if (event.getClickedBlock() == null || event.getHand() == null) return;
		Action action = event.getAction();
		BlockFace blockFace = event.getBlockFace();
		Block clickedBlock = event.getClickedBlock();
		Block replaceableBlock =
				BlockUtils.REPLACE.contains(clickedBlock.getType())
				? clickedBlock
				: clickedBlock.getRelative(blockFace);

		Player player = event.getPlayer();
		PlayerInventory inventory = player.getInventory();
		GameMode gameMode = player.getGameMode();
		EquipmentSlot hand = event.getHand();
		ItemStack itemInMainHand = inventory.getItemInMainHand();

		if (
				action == Action.LEFT_CLICK_BLOCK
				&& MSDecorUtils.isCustomDecorMaterial(clickedBlock.getType())
				&& (player.isSneaking() && player.getGameMode() == GameMode.SURVIVAL
				|| gameMode == GameMode.SURVIVAL && clickedBlock.getType() == Material.STRUCTURE_VOID
				|| gameMode == GameMode.CREATIVE)
		) {
			CustomDecorData customDecorData = CustomDecorUtils.getCustomDecorDataByLocation(clickedBlock.getLocation());
			if (customDecorData == null) return;
			new CustomDecor(clickedBlock, player, customDecorData).breakCustomDecor();
		}

		if (
				action == Action.RIGHT_CLICK_BLOCK
				&& Tag.SHULKER_BOXES.isTagged(clickedBlock.getType())
				&& clickedBlock.getBlockData() instanceof Directional directional
				&& MSDecorUtils.isCustomDecorMaterial(clickedBlock.getRelative(directional.getFacing()).getType())
		) {
			event.setCancelled(true);
		}

		if (MSBlockUtils.isCustomBlock(itemInMainHand)) return;
		if (hand != EquipmentSlot.HAND && MSDecorUtils.isCustomDecor(itemInMainHand)) {
			hand = EquipmentSlot.HAND;
		}
		ItemStack itemInHand = inventory.getItem(hand);

		if (
				action == Action.RIGHT_CLICK_BLOCK
				&& MSDecorUtils.isCustomDecor(itemInHand)
				&& (event.getHand() == EquipmentSlot.HAND || hand == EquipmentSlot.OFF_HAND)
				&& gameMode != GameMode.ADVENTURE
				&& gameMode != GameMode.SPECTATOR
				&& (
				(!clickedBlock.getType().isInteractable()
				|| Tag.STAIRS.isTagged(clickedBlock.getType()))
				|| (player.isSneaking() && clickedBlock.getType().isInteractable()
				) || clickedBlock.getType() == Material.NOTE_BLOCK)
				&& BlockUtils.REPLACE.contains(clickedBlock.getRelative(blockFace).getType())
		) {
			CustomDecorData customDecorData = CustomDecorUtils.getCustomDecorDataWithFace(itemInHand, blockFace);
			if (customDecorData == null) return;

			for (Entity nearbyEntity : player.getWorld().getNearbyEntities(replaceableBlock.getLocation().toCenterLocation(), 0.5d, 0.5d, 0.5d)) {
				EntityType entityType = nearbyEntity.getType();
				if (
						entityType != EntityType.DROPPED_ITEM
						&& (customDecorData.getHitBox().isSolidHitBox()
						|| entityType == EntityType.ARMOR_STAND
						|| entityType == EntityType.ITEM_FRAME)
				) return;
			}

			CustomDecor customDecor = new CustomDecor(replaceableBlock, player, customDecorData);
			CustomDecorData.Facing facing = customDecorData.getFacing();
			if (
					facing == null || blockFace != BlockFace.DOWN
					&& replaceableBlock.getRelative(BlockFace.DOWN).getType().isSolid()
					&& facing == CustomDecorData.Facing.FLOOR
			) {
				customDecor.setCustomDecor(BlockFace.UP, hand, null);
			} else if (
					blockFace != BlockFace.UP
					&& replaceableBlock.getRelative(BlockFace.UP).getType().isSolid()
					&& facing == CustomDecorData.Facing.CEILING
			) {
				customDecor.setCustomDecor(BlockFace.DOWN, hand, null);
			} else if (
					blockFace != BlockFace.UP
					&& blockFace != BlockFace.DOWN
					&& facing == CustomDecorData.Facing.WALL
			) {
				customDecor.setCustomDecor(blockFace, hand, null);
			}
		}
	}
}
