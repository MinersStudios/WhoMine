package com.minersstudios.msdecor.listeners.block;

import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.utils.MSDecorUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class BlockPistonExtendListener extends AbstractMSListener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPistonExtend(@NotNull BlockPistonExtendEvent event) {
        for (var block : event.getBlocks()) {
            if (MSDecorUtils.isCustomDecorMaterial(block.getType())) {
                event.setCancelled(true);
            }
        }
    }
}
