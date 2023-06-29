package com.github.minersstudios.msessentials.listeners.entity;

import com.github.minersstudios.mscore.listener.MSListener;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class HangingBreakByEntityListener implements Listener {

    @EventHandler
    public void onHangingBreakByEntity(@NotNull HangingBreakByEntityEvent event) {
        if (
                event.getEntity() instanceof ItemFrame itemFrame
                && itemFrame.getScoreboardTags().contains("invisibleItemFrame")
                && itemFrame.isVisible()
        ) {
            itemFrame.removeScoreboardTag("invisibleItemFrame");
        }
    }
}
