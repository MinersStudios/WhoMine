package com.minersstudios.msessentials.listeners.event.entity;

import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.msessentials.MSEssentials;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public final class HangingBreakByEntityListener extends AbstractMSListener<MSEssentials> {

    @EventHandler
    public void onHangingBreakByEntity(final @NotNull HangingBreakByEntityEvent event) {
        if (
                event.getEntity() instanceof ItemFrame itemFrame
                && itemFrame.getScoreboardTags().contains("invisibleItemFrame")
                && itemFrame.isVisible()
        ) {
            itemFrame.removeScoreboardTag("invisibleItemFrame");
        }
    }
}
