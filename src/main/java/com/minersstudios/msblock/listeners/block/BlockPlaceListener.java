package com.minersstudios.msblock.listeners.block;

import com.minersstudios.msblock.customblock.CustomBlock;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.utils.BlockUtils;
import com.minersstudios.mscore.utils.MSBlockUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;

import static com.minersstudios.msblock.customblock.CustomBlockData.DEFAULT;

@MSListener
public class BlockPlaceListener extends AbstractMSListener {

    @EventHandler
    public void onBlockPlace(@NotNull BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();
        Material blockType = block.getType();

        if (
                blockType == Material.NOTE_BLOCK
                || MSBlockUtils.isCustomBlock(player.getInventory().getItemInMainHand())
        ) {
            event.setCancelled(true);
        }

        if (
                blockType != Material.NOTE_BLOCK
                && BlockUtils.isWoodenSound(blockType)
        ) {
            DEFAULT.getSoundGroup().playPlaceSound(block.getLocation().toCenterLocation());
        }

        if (blockType == Material.NOTE_BLOCK) {
            new CustomBlock(block, player, DEFAULT)
                    .setCustomBlock(event.getHand());
        }
    }
}
