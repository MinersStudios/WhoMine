package com.minersstudios.msblock.listeners.block;

import com.minersstudios.msblock.customblock.CustomBlock;
import com.minersstudios.msblock.customblock.CustomBlockData;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.utils.BlockUtils;
import com.minersstudios.mscore.utils.MSBlockUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class BlockPlaceListener extends AbstractMSListener {

    @EventHandler
    public void onBlockPlace(@NotNull BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();

        if (
                block.getType() == Material.NOTE_BLOCK
                || MSBlockUtils.isCustomBlock(player.getInventory().getItemInMainHand())
        ) {
            event.setCancelled(true);
        }

        if (BlockUtils.isWoodenSound(block.getType())) {
            CustomBlockData.DEFAULT.getSoundGroup().playPlaceSound(block.getLocation().toCenterLocation());
        }

        if (block.getType() == Material.NOTE_BLOCK) {
            new CustomBlock(block, player, CustomBlockData.DEFAULT).setCustomBlock(event.getHand());
        }
    }
}
