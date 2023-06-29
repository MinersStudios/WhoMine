package com.github.minersstudios.msdecor.listeners.mechanic;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.mscore.utils.MSBlockUtils;
import com.github.minersstudios.mscore.utils.MSDecorUtils;
import com.github.minersstudios.msdecor.customdecor.register.other.Poop;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PoopMechanic implements Listener {

	@EventHandler
	public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
		if (
				event.getClickedBlock() == null
				|| event.getHand() == null
				|| event.getAction().isLeftClick()
		) return;
		Block clickedBlock = event.getClickedBlock();
		if (clickedBlock.getType() != Material.COMPOSTER) return;
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
				event.getHand() == EquipmentSlot.HAND
				&& gameMode != GameMode.SPECTATOR
				&& !player.isSneaking()
				&& clickedBlock.getBlockData() instanceof Levelled levelled
				&& (!itemInHand.getType().isBlock() || itemInHand.getType() == Material.AIR)
				&& MSDecorUtils.getCustomDecorData(itemInHand) instanceof Poop
				&& levelled.getLevel() < levelled.getMaximumLevel()
		) {
			levelled.setLevel(levelled.getLevel() + 1);
			clickedBlock.setBlockData(levelled);
			player.swingHand(hand);
			if (gameMode != GameMode.CREATIVE) {
				itemInHand.setAmount(itemInHand.getAmount() - 1);
			}
		}
	}
}
