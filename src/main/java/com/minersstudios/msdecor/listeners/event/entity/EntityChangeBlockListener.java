package com.minersstudios.msdecor.listeners.event.entity;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.util.MSDecorUtils;
import com.minersstudios.msdecor.MSDecor;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@MSListener
public final class EntityChangeBlockListener extends AbstractMSListener<MSDecor> {

    @EventHandler
    public void onEntityChangeBlock(final @NotNull EntityChangeBlockEvent event) {
        final Block block = event.getBlock();

        if (
                event.getEntity() instanceof FallingBlock
                && MSDecorUtils.isCustomDecor(block)
        ) {
            event.setCancelled(true);
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(event.getTo()));
        }
    }
}
