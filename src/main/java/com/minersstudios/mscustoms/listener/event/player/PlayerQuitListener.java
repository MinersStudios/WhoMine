package com.minersstudios.mscustoms.listener.event.player;

import com.minersstudios.mscustoms.CustomsCache;
import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.mscustoms.MSCustoms;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class PlayerQuitListener extends AbstractEventListener<MSCustoms> {

    @EventHandler
    public void onPlayerQuit(final @NotNull PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final CustomsCache cache = this.getPlugin().getCache();

        if (cache != null) {
            cache.getDiggingMap().removeAll(player);
            cache.getStepMap().remove(player);
        }
    }
}
