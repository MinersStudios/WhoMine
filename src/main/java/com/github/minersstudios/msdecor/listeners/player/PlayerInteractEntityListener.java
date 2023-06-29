package com.github.minersstudios.msdecor.listeners.player;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.mscore.utils.MSDecorUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerInteractEntityListener implements Listener {

	@EventHandler
	public void onPlayerInteractEntity(@NotNull PlayerInteractEntityEvent event) {
		if (MSDecorUtils.isCustomDecorEntity(event.getRightClicked())) {
			event.setCancelled(true);
		}
	}
}
