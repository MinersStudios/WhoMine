package com.minersstudios.msdecor.listeners.event.block;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSEventListener;
import com.minersstudios.mscore.util.BlockUtils;
import com.minersstudios.mscore.util.MSDecorUtils;
import com.minersstudios.msdecor.MSDecor;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;

@MSEventListener
public class BlockPlaceListener extends AbstractMSListener<MSDecor> {

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
