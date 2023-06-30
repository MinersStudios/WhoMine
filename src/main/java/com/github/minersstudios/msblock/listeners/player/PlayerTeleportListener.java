package com.github.minersstudios.msblock.listeners.player;

import com.github.minersstudios.msblock.MSBlock;
import com.github.minersstudios.msblock.utils.CustomBlockUtils;
import com.github.minersstudios.msblock.utils.PlayerUtils;
import com.github.minersstudios.mscore.listener.MSListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerTeleportListener implements Listener {

    @EventHandler
    public void onPlayerTeleport(@NotNull PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTask(MSBlock.getInstance(), () -> {
            CustomBlockUtils.cancelAllTasksWithThisPlayer(player);
            PlayerUtils.removeSteps(player);
        });
    }
}
