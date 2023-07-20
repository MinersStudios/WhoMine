package com.minersstudios.msessentials.listeners.player;

import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.msessentials.utils.MSPlayerUtils;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerChangedWorldListener extends AbstractMSListener {

    @EventHandler
    public void onPlayerChangedWorld(@NotNull PlayerChangedWorldEvent event) {
        MSPlayerUtils.hideNameTag(event.getPlayer());
    }
}
