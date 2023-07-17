package com.github.minersstudios.msblock.listeners.player;

import com.github.minersstudios.msblock.utils.CustomBlockUtils;
import com.github.minersstudios.msblock.utils.PlayerUtils;
import com.github.minersstudios.mscore.listener.AbstractMSListener;
import com.github.minersstudios.mscore.listener.MSListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerDeathListener extends AbstractMSListener {

    @EventHandler
    public void onPlayerDeath(@NotNull PlayerDeathEvent event) {
        Player player = event.getEntity();
        this.getPlugin().runTask(() -> {
            CustomBlockUtils.cancelAllTasksWithThisPlayer(player);
            PlayerUtils.removeSteps(player);
        });
    }
}
