package com.minersstudios.msdecor.listeners.block;

import com.minersstudios.mscore.listener.MSListener;
import com.minersstudios.mscore.utils.MSDecorUtils;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import com.minersstudios.mscore.listener.AbstractMSListener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class BlockBreakListener extends AbstractMSListener {

    @EventHandler
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
        Block block = event.getBlock();

        if (MSDecorUtils.isCustomDecorMaterial(event.getBlock().getType())) {
            for (var nearbyEntity : block.getWorld().getNearbyEntities(block.getLocation().toCenterLocation(), 0.5d, 0.5d, 0.5d)) {
                if (MSDecorUtils.isCustomDecorEntity(nearbyEntity)) {
                    event.setCancelled(true);
                }
            }
        }
    }
}