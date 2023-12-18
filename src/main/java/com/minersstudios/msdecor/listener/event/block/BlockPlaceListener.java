package com.minersstudios.msdecor.listener.event.block;

import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.mscore.utility.BlockUtils;
import com.minersstudios.mscore.utility.MSDecorUtils;
import com.minersstudios.msdecor.MSDecor;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;

@EventListener
public class BlockPlaceListener extends AbstractEventListener<MSDecor> {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(final @NotNull BlockPlaceEvent event) {
        final Block block = event.getBlock();

        if (
                (
                        !BlockUtils.isReplaceable(block)
                        || MSDecorUtils.isCustomDecorMaterial(event.getBlockReplacedState().getType())
                )
                && MSDecorUtils.isCustomDecor(block)
        ) {
            event.setCancelled(true);
        }
    }
}
