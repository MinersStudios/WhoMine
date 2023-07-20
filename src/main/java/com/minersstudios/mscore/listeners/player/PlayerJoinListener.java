package com.minersstudios.mscore.listeners.player;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.packet.ChannelHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerJoinListener extends AbstractMSListener {

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        ChannelHandler.injectPlayer(event.getPlayer(), this.getPlugin());
    }
}
