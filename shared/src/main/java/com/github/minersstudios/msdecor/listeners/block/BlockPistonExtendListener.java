package com.github.minersstudios.msdecor.listeners.block;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.mscore.utils.MSDecorUtils;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class BlockPistonExtendListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPistonExtend(@NotNull BlockPistonExtendEvent event) {
		for (Block block : event.getBlocks()) {
			if (MSDecorUtils.isCustomDecorMaterial(block.getType())) {
				event.setCancelled(true);
			}
		}
	}
}
