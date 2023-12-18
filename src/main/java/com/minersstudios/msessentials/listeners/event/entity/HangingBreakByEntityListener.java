package com.minersstudios.msessentials.listeners.event.entity;

import com.minersstudios.mscore.listener.event.MSEventListener;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.util.SharedConstants;
import com.minersstudios.msessentials.MSEssentials;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.jetbrains.annotations.NotNull;

@MSEventListener
public final class HangingBreakByEntityListener extends AbstractMSListener<MSEssentials> {

    @EventHandler
    public void onHangingBreakByEntity(final @NotNull HangingBreakByEntityEvent event) {
        if (
                event.getEntity() instanceof final ItemFrame itemFrame
                && itemFrame.getScoreboardTags().contains(SharedConstants.INVISIBLE_ITEM_FRAME_TAG)
                && itemFrame.isVisible()
        ) {
            itemFrame.removeScoreboardTag(SharedConstants.INVISIBLE_ITEM_FRAME_TAG);
        }
    }
}
