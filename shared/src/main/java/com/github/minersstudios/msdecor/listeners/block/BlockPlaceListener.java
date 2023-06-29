package com.github.minersstudios.msdecor.listeners.block;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.mscore.utils.MSDecorUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class BlockPlaceListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(@NotNull BlockPlaceEvent event) {
		if (
				MSDecorUtils.isCustomDecorMaterial(event.getBlockReplacedState().getType())
				|| MSDecorUtils.isCustomDecor(event.getPlayer().getInventory().getItemInMainHand())
		) {
			event.setCancelled(true);
		}
	}
}
