package com.minersstudios.mscustoms.listener.event.player;

import com.minersstudios.mscore.utility.MSDecorUtils;
import com.minersstudios.mscustoms.MSCustoms;
import com.minersstudios.mscustoms.custom.block.CustomBlockRegistry;
import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class PlayerBucketEmptyListener extends AbstractEventListener<MSCustoms> {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerBucketEmpty(final @NotNull PlayerBucketEmptyEvent event) {
        final Block block = event.getBlock();

        if (
                block.getType() == Material.NOTE_BLOCK
                || CustomBlockRegistry.isCustomBlock(event.getPlayer().getInventory().getItemInMainHand())
                || (
                        event.getBucket() == Material.LAVA_BUCKET
                        && MSDecorUtils.isCustomDecor(block)
                )
        ) {
            event.setCancelled(true);
        }
    }
}
