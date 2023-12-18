package com.minersstudios.msessentials.listener.impl.event.player;

import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.utility.MSPlayerUtils;
import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class PlayerChangedWorldListener extends AbstractEventListener<MSEssentials> {

    @EventHandler
    public void onPlayerChangedWorld(final @NotNull PlayerChangedWorldEvent event) {
        MSPlayerUtils.hideNameTag(
                this.getPlugin(),
                event.getPlayer()
        );
    }
}
