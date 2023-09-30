package com.minersstudios.mscore.listeners.event.player;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.packet.ChannelHandler;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerJoinListener extends AbstractMSListener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(final @NotNull PlayerJoinEvent event) {
        this.plugin.runTask(() ->
            ChannelHandler.injectConnection(
                ((CraftPlayer) event.getPlayer()).getHandle().connection.connection,
                this.plugin
            )
        );
    }
}
