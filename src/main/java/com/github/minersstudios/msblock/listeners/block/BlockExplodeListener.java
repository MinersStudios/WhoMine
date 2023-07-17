package com.github.minersstudios.msblock.listeners.block;

import com.github.minersstudios.msblock.customblock.CustomBlockData;
import com.github.minersstudios.mscore.listener.MSListener;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.event.EventHandler;
import com.github.minersstudios.mscore.listener.AbstractMSListener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class BlockExplodeListener extends AbstractMSListener {

    @EventHandler
    public void onBlockExplode(@NotNull BlockExplodeEvent event) {
        for (Block block : event.blockList()) {
            if (block.getBlockData() instanceof NoteBlock noteBlock) {
                block.setType(Material.AIR);
                block.getWorld().dropItemNaturally(
                        block.getLocation(),
                        CustomBlockData.fromNoteBlock(noteBlock).craftItemStack()
                );
            }
        }
    }
}
