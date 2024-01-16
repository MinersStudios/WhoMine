package com.minersstudios.mscustoms.listener.event.block;

import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.mscustoms.utility.MSDecorUtils;
import com.minersstudios.mscustoms.MSCustoms;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class BlockPistonExtendListener extends AbstractEventListener<MSCustoms> {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPistonExtend(final @NotNull BlockPistonExtendEvent event) {
        for (final var block : event.getBlocks()) {
            if (MSDecorUtils.isCustomDecorMaterial(block.getType())) {
                event.setCancelled(true);
                break;
            }
        }
    }
}
