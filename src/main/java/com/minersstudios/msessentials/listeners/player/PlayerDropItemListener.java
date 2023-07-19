package com.minersstudios.msessentials.listeners.player;

import com.minersstudios.mscore.listener.MSListener;
import com.minersstudios.msessentials.world.WorldDark;
import com.minersstudios.mscore.listener.AbstractMSListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerDropItemListener extends AbstractMSListener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDropItem(@NotNull PlayerDropItemEvent event) {
        if (WorldDark.isInWorldDark(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
