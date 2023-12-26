package com.minersstudios.msessentials.listener.impl.event.player;

import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.msessentials.MSEssentials;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class PlayerDropItemListener extends AbstractEventListener<MSEssentials> {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDropItem(final @NotNull PlayerDropItemEvent event) {
        if (this.getPlugin().getCache().getWorldDark().isInWorldDark(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
