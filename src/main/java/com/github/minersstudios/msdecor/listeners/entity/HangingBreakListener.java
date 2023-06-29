package com.github.minersstudios.msdecor.listeners.entity;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.mscore.utils.MSDecorUtils;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class HangingBreakListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onHangingBreak(@NotNull HangingBreakEvent event) {
		if (
				event.getEntity() instanceof ItemFrame itemFrame
				&& MSDecorUtils.isCustomDecorEntity(itemFrame)
		) {
			event.setCancelled(true);
		}
	}
}
