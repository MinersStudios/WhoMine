package com.minersstudios.msessentials.listener.impl.event.player;

import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.world.WorldDark;
import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

@EventListener
public final class PlayerSpawnLocationListener extends AbstractEventListener<MSEssentials> {

    @EventHandler
    public void onPlayerSpawnLocation(final @NotNull PlayerSpawnLocationEvent event) {
        if (!event.getPlayer().isDead()) {
            event.setSpawnLocation(WorldDark.getInstance().getSpawnLocation());
        }
    }
}
