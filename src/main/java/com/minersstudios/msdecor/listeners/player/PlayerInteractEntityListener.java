package com.minersstudios.msdecor.listeners.player;

import com.minersstudios.mscore.listener.MSListener;
import com.minersstudios.mscore.utils.MSDecorUtils;
import org.bukkit.event.EventHandler;
import com.minersstudios.mscore.listener.AbstractMSListener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerInteractEntityListener extends AbstractMSListener {

    @EventHandler
    public void onPlayerInteractEntity(@NotNull PlayerInteractEntityEvent event) {
        if (MSDecorUtils.isCustomDecorEntity(event.getRightClicked())) {
            event.setCancelled(true);
        }
    }
}
