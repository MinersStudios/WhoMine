package com.minersstudios.msblock.listeners.block;

import com.minersstudios.msblock.customblock.CustomBlockRegistry;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import org.bukkit.Material;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockExplodeEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class BlockExplodeListener extends AbstractMSListener {

    @EventHandler
    public void onBlockExplode(@NotNull BlockExplodeEvent event) {
        event.blockList().stream()
        .filter(block -> block.getType() == Material.NOTE_BLOCK)
        .forEach(block -> {
            block.setType(Material.AIR);
            block.getWorld().dropItemNaturally(
                    block.getLocation(),
                    CustomBlockRegistry.fromNoteBlock((NoteBlock) block.getBlockData()).orElse(CustomBlockRegistry.DEFAULT).craftItemStack()
            );
        });
    }
}
