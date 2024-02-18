package com.minersstudios.mscustoms.listener.event.player;

import com.minersstudios.mscustoms.CustomsCache;
import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.mscustoms.MSCustoms;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class PlayerDeathListener extends AbstractEventListener<MSCustoms> {

    @EventHandler
    public void onPlayerDeath(final @NotNull PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final CustomsCache cache = this.getPlugin().getCache();

        cache.getDiggingMap().removeAll(player);
        cache.getStepMap().put(player, 0.0d);
    }
}
