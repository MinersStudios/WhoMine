package com.minersstudios.msdecor.listeners.event.block;

import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.util.MSDecorUtils;
import com.minersstudios.msdecor.MSDecor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public final class BlockPistonExtendListener extends AbstractMSListener<MSDecor> {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPistonExtend(final @NotNull BlockPistonExtendEvent event) {
        for (final var block : event.getBlocks()) {
            if (MSDecorUtils.isCustomDecor(block)) {
                event.setCancelled(true);
            }
        }
    }
}
