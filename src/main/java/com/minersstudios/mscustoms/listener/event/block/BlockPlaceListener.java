package com.minersstudios.mscustoms.listener.event.block;

import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.mscustoms.sound.SoundGroup;
import com.minersstudios.mscore.utility.BlockUtils;
import com.minersstudios.mscore.utility.MSDecorUtils;
import com.minersstudios.mscustoms.MSCustoms;
import com.minersstudios.mscustoms.custom.block.CustomBlock;
import com.minersstudios.mscustoms.custom.block.CustomBlockData;
import com.minersstudios.mscustoms.custom.block.CustomBlockRegistry;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class BlockPlaceListener extends AbstractEventListener<MSCustoms> {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(final @NotNull BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlockPlaced();
        final Material blockType = block.getType();

        if (
                (
                        !BlockUtils.isReplaceable(block)
                        || MSDecorUtils.isCustomDecorMaterial(event.getBlockReplacedState().getType())
                )
                && MSDecorUtils.isCustomDecor(block)
        ) {
            event.setCancelled(true);
        }

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
            new CustomBlock(block, CustomBlockData.defaultData())
                    .place(this.getPlugin(), player, event.getHand());
        }
    }
}
