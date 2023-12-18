package com.minersstudios.mscore.listener.impl.event.player;

import com.minersstudios.mscore.MSCore;
import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.mscore.packet.ChannelHandler;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class PlayerJoinListener extends AbstractEventListener<MSCore> {

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
