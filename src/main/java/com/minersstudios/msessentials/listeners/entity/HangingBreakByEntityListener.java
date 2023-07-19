package com.minersstudios.msessentials.listeners.entity;

import com.minersstudios.mscore.listener.MSListener;
import com.minersstudios.mscore.listener.AbstractMSListener;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class HangingBreakByEntityListener extends AbstractMSListener {

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
