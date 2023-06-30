package com.github.minersstudios.msblock.listeners.block;

import com.github.minersstudios.msblock.MSBlock;
import com.github.minersstudios.msblock.customblock.CustomBlockData;
import com.github.minersstudios.msblock.utils.CustomBlockUtils;
import com.github.minersstudios.mscore.listener.MSListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@MSListener
public class BlockPhysicsListener implements Listener {

    @EventHandler
    public void onBlockPhysics(@NotNull BlockPhysicsEvent event) {
        Block block = event.getBlock();
        Block topBlock = block.getRelative(BlockFace.UP);
        Block bottomBlock = block.getRelative(BlockFace.DOWN);

        if (bottomBlock.getBlockData() instanceof NoteBlock noteBlock) {
            Set<Material> placeableMaterials = CustomBlockData.fromNoteBlock(noteBlock).getPlaceableMaterials();
            if (placeableMaterials != null && placeableMaterials.contains(block.getType())) {
                event.setCancelled(true);
            }
        }

        if (
                topBlock.getType() == Material.NOTE_BLOCK
                && block.getState() instanceof ShulkerBox shulkerBox
        ) {
            block.setType(Material.AIR, false);
            Bukkit.getScheduler().runTaskLater(MSBlock.getInstance(), () -> {
                block.setType(block.getType(), false);
                block.setBlockData(event.getChangedBlockData(), false);
                if (block.getState() instanceof ShulkerBox newShulkerBox) {
                    newShulkerBox.getInventory().setContents(shulkerBox.getInventory().getContents());
                }
            }, 1L);
        }

        if (
                topBlock.getType() == Material.NOTE_BLOCK
                || block.getType() == Material.NOTE_BLOCK
        ) {
            event.setCancelled(true);
            CustomBlockUtils.updateNoteBlock(block);

            if (
                    Tag.DOORS.isTagged(block.getType())
                    && block.getBlockData() instanceof Door topDoor
                    && topDoor.getHalf() == Bisected.Half.TOP
                    && bottomBlock.getBlockData() instanceof Door bottomDoor
                    && bottomDoor.getHalf() == Bisected.Half.BOTTOM
            ) {
                bottomDoor.setOpen(topDoor.isOpen());
                bottomBlock.setBlockData(bottomDoor);
            }

            if (
                    bottomBlock.getBlockData() instanceof Door bottomDoor
                    && bottomDoor.getHalf() == Bisected.Half.BOTTOM
                    && !Tag.DOORS.isTagged(block.getType())
            ) {
                bottomBlock.setType(Material.AIR);
            }
        }
    }
}
