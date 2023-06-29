package com.github.minersstudios.msblock.listeners.player;

import com.github.minersstudios.msblock.MSBlock;
import com.github.minersstudios.msblock.utils.CustomBlockUtils;
import com.github.minersstudios.msblock.utils.PlayerUtils;
import com.github.minersstudios.mscore.listener.MSListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerDeathListener implements Listener {

	@EventHandler
	public void onPlayerDeath(@NotNull PlayerDeathEvent event) {
		Player player = event.getEntity();
		Bukkit.getScheduler().runTask(MSBlock.getInstance(), () -> {
			CustomBlockUtils.cancelAllTasksWithThisPlayer(player);
			PlayerUtils.removeSteps(player);
		});
	}
}
