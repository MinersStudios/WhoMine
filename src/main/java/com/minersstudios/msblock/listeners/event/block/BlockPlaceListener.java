package com.minersstudios.msblock.listeners.event.block;

import com.minersstudios.msblock.MSBlock;
import com.minersstudios.msblock.api.CustomBlock;
import com.minersstudios.msblock.api.CustomBlockData;
import com.minersstudios.msblock.api.CustomBlockRegistry;
import com.minersstudios.mscore.util.SoundGroup;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.util.BlockUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public final class BlockPlaceListener extends AbstractMSListener<MSBlock> {

    @EventHandler
    public void onBlockPlace(final @NotNull BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlockPlaced();
        final Material blockType = block.getType();

        if (
                blockType == Material.NOTE_BLOCK
                || CustomBlockRegistry.isCustomBlock(player.getInventory().getItemInMainHand())
        ) {
            event.setCancelled(true);
        }

        if (
                blockType != Material.NOTE_BLOCK
                && BlockUtils.isWoodenSound(blockType)
        ) {
            SoundGroup.WOOD.playPlaceSound(block.getLocation().toCenterLocation());
        }

        if (blockType == Material.NOTE_BLOCK) {
            new CustomBlock(block, CustomBlockData.getDefault())
                    .place(player, event.getHand());
        }
    }
}
