package com.minersstudios.mscore.listeners.player;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.packet.ChannelHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerQuitListener extends AbstractMSListener {

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        ChannelHandler.uninjectPlayer(event.getPlayer());
    }
}
