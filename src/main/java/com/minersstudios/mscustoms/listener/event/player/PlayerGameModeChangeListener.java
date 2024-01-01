package com.minersstudios.mscustoms.listener.event.player;

import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.mscustoms.MSCustoms;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class PlayerGameModeChangeListener extends AbstractEventListener<MSCustoms> {

    @EventHandler
    public void onPlayerGameModeChange(final @NotNull PlayerGameModeChangeEvent event) {
        this.getPlugin().getCache().getDiggingMap().removeAll(event.getPlayer());
    }
}
