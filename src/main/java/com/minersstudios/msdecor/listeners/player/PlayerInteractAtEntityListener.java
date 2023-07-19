package com.minersstudios.msdecor.listeners.player;

import com.minersstudios.mscore.listener.MSListener;
import com.minersstudios.mscore.utils.MSDecorUtils;
import org.bukkit.event.EventHandler;
import com.minersstudios.mscore.listener.AbstractMSListener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerInteractAtEntityListener extends AbstractMSListener {

    @EventHandler
    public void onPlayerInteractAtEntity(@NotNull PlayerInteractAtEntityEvent event) {
        if (MSDecorUtils.isCustomDecorEntity(event.getRightClicked())) {
            event.setCancelled(true);
        }
    }
}
