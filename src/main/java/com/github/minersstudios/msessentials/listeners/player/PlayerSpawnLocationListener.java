package com.github.minersstudios.msessentials.listeners.player;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.msessentials.world.WorldDark;
import org.bukkit.event.EventHandler;
import com.github.minersstudios.mscore.listener.AbstractMSListener;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

@MSListener
public class PlayerSpawnLocationListener extends AbstractMSListener {

    @EventHandler
    public void onPlayerSpawnLocation(@NotNull PlayerSpawnLocationEvent event) {
        if (!event.getPlayer().isDead()) {
            event.setSpawnLocation(WorldDark.getInstance().getSpawnLocation());
        }
    }
}
