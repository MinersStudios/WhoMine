package com.github.minersstudios.msessentials.listeners.player;

import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;
import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.msessentials.MSEssentials;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerStopSpectatingEntityListener implements Listener {

    @EventHandler
    public void onPlayerStopSpectatingEntity(@NotNull PlayerStopSpectatingEntityEvent event) {
        if (event.getPlayer().getWorld().equals(MSEssentials.getWorldDark())) {
            event.setCancelled(true);
        }
    }
}
