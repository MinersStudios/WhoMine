package com.github.minersstudios.msblock.listeners.inventory;

import com.github.minersstudios.msblock.MSBlock;
import com.github.minersstudios.msblock.customblock.CustomBlockData;
import com.github.minersstudios.msblock.utils.PlayerUtils;
import com.github.minersstudios.mscore.listener.MSListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class InventoryCreativeListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onInventoryCreative(@NotNull InventoryCreativeEvent event) {
		if (!event.getClick().isCreativeAction()) return;
		Player player = (Player) event.getWhoClicked();
		Block targetBlock = PlayerUtils.getTargetBlock(player);
		if (
				targetBlock == null
				|| event.getCursor().getType() != Material.NOTE_BLOCK
				|| !(targetBlock.getBlockData() instanceof NoteBlock noteBlock)
		) return;
		event.setCancelled(true);
		Bukkit.getScheduler().runTask(MSBlock.getInstance(), () -> player.getInventory().setItem(
				event.getSlot(),
				CustomBlockData.fromNoteBlock(noteBlock).craftItemStack()
		));
	}
}
