package com.github.minersstudios.msdecor.listeners.entity;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.mscore.utils.MSDecorUtils;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import com.github.minersstudios.mscore.listener.AbstractMSListener;
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
