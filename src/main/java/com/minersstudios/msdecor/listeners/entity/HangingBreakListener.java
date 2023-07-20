package com.minersstudios.msdecor.listeners.entity;

import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.utils.MSDecorUtils;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class HangingBreakListener extends AbstractMSListener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHangingBreak(@NotNull HangingBreakEvent event) {
        if (
                event.getEntity() instanceof ItemFrame itemFrame
                && MSDecorUtils.isCustomDecorEntity(itemFrame)
        ) {
            event.setCancelled(true);
        }
    }
}
