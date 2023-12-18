package com.minersstudios.mscore.listeners.event.player;

import com.minersstudios.mscore.MSCore;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSEventListener;
import com.minersstudios.mscore.packet.ChannelHandler;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

@MSEventListener
public final class PlayerJoinListener extends AbstractMSListener<MSCore> {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(final @NotNull PlayerJoinEvent event) {
        final MSCore plugin = this.getPlugin();

        plugin.runTask(() ->
            ChannelHandler.injectConnection(
                ((CraftPlayer) event.getPlayer()).getHandle().connection.connection,
                plugin
            )
        );
    }
}
