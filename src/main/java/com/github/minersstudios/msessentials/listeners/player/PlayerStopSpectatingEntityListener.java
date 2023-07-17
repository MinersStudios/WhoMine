package com.github.minersstudios.msessentials.listeners.player;

import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;
import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.msessentials.world.WorldDark;
import org.bukkit.event.EventHandler;
import com.github.minersstudios.mscore.listener.AbstractMSListener;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerStopSpectatingEntityListener extends AbstractMSListener {

    @EventHandler
    public void onPlayerStopSpectatingEntity(@NotNull PlayerStopSpectatingEntityEvent event) {
        if (WorldDark.isInWorldDark(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
