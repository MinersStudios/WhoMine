package com.minersstudios.mscore.listeners.event.player;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.packet.ChannelHandler;
import com.minersstudios.mscore.plugin.MSPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerLoginListener extends AbstractMSListener {

    @EventHandler
    public void onPlayerLogin(@NotNull PlayerLoginEvent event) {
        MSPlugin plugin = this.getPlugin();
        plugin.runTask(() -> ChannelHandler.injectPlayer(event.getPlayer(), plugin));
    }
}
