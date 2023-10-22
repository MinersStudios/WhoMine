package com.minersstudios.msdecor.listeners.event.block;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.util.MSDecorUtils;
import com.minersstudios.msdecor.MSDecor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class BlockBreakListener extends AbstractMSListener<MSDecor> {

    @EventHandler
    public void onBlockBreak(final @NotNull BlockBreakEvent event) {
        if (MSDecorUtils.isCustomDecor(event.getBlock())) {
            event.setCancelled(true);
        }
    }
}