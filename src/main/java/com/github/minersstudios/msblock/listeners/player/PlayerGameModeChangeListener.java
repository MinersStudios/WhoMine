package com.github.minersstudios.msblock.listeners.player;

import com.github.minersstudios.msblock.MSBlock;
import com.github.minersstudios.msblock.utils.CustomBlockUtils;
import com.github.minersstudios.msblock.utils.PlayerUtils;
import com.github.minersstudios.mscore.listener.MSListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerGameModeChangeListener implements Listener {

    @EventHandler
    public void onPlayerGameModeChange(@NotNull PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        MSBlock.getInstance().runTask(() -> {
            CustomBlockUtils.cancelAllTasksWithThisPlayer(player);
            PlayerUtils.removeSteps(player);
        });
    }
}
