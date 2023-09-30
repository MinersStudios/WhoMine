package com.minersstudios.msessentials.listeners.event.player;

import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.msessentials.world.WorldDark;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerMoveListener extends AbstractMSListener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(final @NotNull PlayerMoveEvent event) {
        if (WorldDark.isInWorldDark(event.getFrom())) {
            event.setCancelled(true);
        }
    }
}
