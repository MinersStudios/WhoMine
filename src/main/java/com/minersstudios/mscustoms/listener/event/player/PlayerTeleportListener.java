package com.minersstudios.mscustoms.listener.event.player;

import com.minersstudios.mscustoms.Cache;
import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.mscustoms.MSCustoms;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class PlayerTeleportListener extends AbstractEventListener<MSCustoms> {

    @EventHandler
    public void onPlayerTeleport(final @NotNull PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Cache cache = this.getPlugin().getCache();

        cache.getDiggingMap().removeAll(player);
        cache.getStepMap().put(player, 0.0d);
    }
}
