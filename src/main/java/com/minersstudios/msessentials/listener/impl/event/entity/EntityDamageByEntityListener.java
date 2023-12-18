package com.minersstudios.msessentials.listener.impl.event.entity;

import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.utility.SharedConstants;
import com.minersstudios.msessentials.MSEssentials;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class EntityDamageByEntityListener extends AbstractEventListener<MSEssentials> {

    @EventHandler
    public void onEntityDamageByEntity(final @NotNull EntityDamageByEntityEvent event) {
        if (
                event.getEntity() instanceof final ItemFrame itemFrame
                && itemFrame.getScoreboardTags().contains(SharedConstants.INVISIBLE_ITEM_FRAME_TAG)
                && !itemFrame.isVisible()
        ) {
            itemFrame.setVisible(true);
        }
    }
}
