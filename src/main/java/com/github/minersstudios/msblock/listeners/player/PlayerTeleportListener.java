package com.github.minersstudios.msblock.listeners.player;

import com.github.minersstudios.msblock.utils.CustomBlockUtils;
import com.github.minersstudios.msblock.utils.PlayerUtils;
import com.github.minersstudios.mscore.listener.AbstractMSListener;
import com.github.minersstudios.mscore.listener.MSListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerTeleportListener extends AbstractMSListener {

    @EventHandler
    public void onPlayerTeleport(@NotNull PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        this.getPlugin().runTask(() -> {
            CustomBlockUtils.cancelAllTasksWithThisPlayer(player);
            PlayerUtils.removeSteps(player);
        });
    }
}
