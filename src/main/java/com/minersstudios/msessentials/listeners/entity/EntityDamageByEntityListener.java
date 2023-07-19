package com.minersstudios.msessentials.listeners.entity;

import com.minersstudios.mscore.listener.MSListener;
import com.minersstudios.mscore.listener.AbstractMSListener;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class EntityDamageByEntityListener extends AbstractMSListener {

    @EventHandler
    public void onEntityDamageByEntity(@NotNull EntityDamageByEntityEvent event) {
        if (
                event.getEntity() instanceof ItemFrame itemFrame
                && itemFrame.getScoreboardTags().contains("invisibleItemFrame")
                && !itemFrame.isVisible()
        ) {
            itemFrame.setVisible(true);
        }
    }
}
