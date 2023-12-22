package com.minersstudios.msessentials.listener.impl.event.hanging;

import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.utility.SharedConstants;
import com.minersstudios.msessentials.MSEssentials;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class HangingBreakByEntityListener extends AbstractEventListener<MSEssentials> {

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
