package com.github.minersstudios.msblock.listeners.block;

import com.github.minersstudios.msblock.customblock.CustomBlock;
import com.github.minersstudios.msblock.customblock.CustomBlockData;
import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.mscore.utils.BlockUtils;
import com.github.minersstudios.mscore.utils.MSBlockUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class BlockPlaceListener implements Listener {

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

        if (BlockUtils.isWoodenSound(block.getBlockData())) {
            CustomBlockData.DEFAULT.getSoundGroup().playPlaceSound(block.getLocation().toCenterLocation());
        }

        if (block.getType() == Material.NOTE_BLOCK) {
            new CustomBlock(block, player, CustomBlockData.DEFAULT).setCustomBlock(event.getHand());
        }
    }
}
