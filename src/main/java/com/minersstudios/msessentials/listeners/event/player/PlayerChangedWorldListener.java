package com.minersstudios.msessentials.listeners.event.player;

import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.util.MSPlayerUtils;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public final class PlayerChangedWorldListener extends AbstractMSListener<MSEssentials> {

    @EventHandler
    public void onPlayerChangedWorld(final @NotNull PlayerChangedWorldEvent event) {
        MSPlayerUtils.hideNameTag(event.getPlayer());
    }
}
