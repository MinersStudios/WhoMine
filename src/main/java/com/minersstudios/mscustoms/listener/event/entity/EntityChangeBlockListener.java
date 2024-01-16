package com.minersstudios.mscustoms.listener.event.entity;

import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.mscustoms.utility.MSDecorUtils;
import com.minersstudios.mscustoms.MSCustoms;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class EntityChangeBlockListener extends AbstractEventListener<MSCustoms> {

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
