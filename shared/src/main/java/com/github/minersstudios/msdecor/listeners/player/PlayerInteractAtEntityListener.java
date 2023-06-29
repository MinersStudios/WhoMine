package com.github.minersstudios.msdecor.listeners.player;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.mscore.utils.MSDecorUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerInteractAtEntityListener implements Listener {

	@EventHandler
	public void onPlayerInteractAtEntity(@NotNull PlayerInteractAtEntityEvent event) {
		if (MSDecorUtils.isCustomDecorEntity(event.getRightClicked())) {
			event.setCancelled(true);
		}
	}
}
