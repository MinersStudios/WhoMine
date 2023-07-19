package com.minersstudios.msblock.listeners.player;

import com.minersstudios.msblock.utils.CustomBlockUtils;
import com.minersstudios.msblock.utils.PlayerUtils;
import com.minersstudios.mscore.listener.AbstractMSListener;
import com.minersstudios.mscore.listener.MSListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerGameModeChangeListener extends AbstractMSListener {

    @EventHandler
    public void onPlayerGameModeChange(@NotNull PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        this.getPlugin().runTask(() -> {
            CustomBlockUtils.cancelAllTasksWithThisPlayer(player);
            PlayerUtils.removeSteps(player);
        });
    }
}
