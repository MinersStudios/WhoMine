package com.minersstudios.msessentials.listeners.player;

import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.msessentials.world.WorldDark;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

@MSListener
public class PlayerSpawnLocationListener extends AbstractMSListener {

    @EventHandler
    public void onPlayerSpawnLocation(final @NotNull PlayerSpawnLocationEvent event) {
        if (!event.getPlayer().isDead()) {
            event.setSpawnLocation(WorldDark.getInstance().getSpawnLocation());
        }
    }
}
