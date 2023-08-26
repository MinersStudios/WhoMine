package com.minersstudios.mscore.listeners.event.player;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.packet.ChannelHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerLoginListener extends AbstractMSListener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(final @NotNull PlayerLoginEvent event) {
        this.plugin.runTask(() -> ChannelHandler.injectPlayer(event.getPlayer(), this.plugin));
    }
}
