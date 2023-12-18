package com.minersstudios.msblock.listener.event.player;

import com.minersstudios.msblock.Cache;
import com.minersstudios.msblock.MSBlock;
import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class PlayerQuitListener extends AbstractEventListener<MSBlock> {

    @EventHandler
    public void onPlayerQuit(final @NotNull PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final Cache cache = this.getPlugin().getCache();

        if (cache != null) {
            cache.getDiggingMap().removeAll(player);
            cache.getStepMap().remove(player);
        }
    }
}
