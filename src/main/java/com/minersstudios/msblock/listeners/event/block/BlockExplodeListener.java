package com.minersstudios.msblock.listeners.event.block;

import com.minersstudios.msblock.MSBlock;
import com.minersstudios.msblock.api.CustomBlockData;
import com.minersstudios.msblock.api.CustomBlockRegistry;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSEventListener;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockExplodeEvent;
import org.jetbrains.annotations.NotNull;

@MSEventListener
public final class BlockExplodeListener extends AbstractMSListener<MSBlock> {

    @EventHandler
    public void onBlockExplode(final @NotNull BlockExplodeEvent event) {
        final World world = event.getBlock().getWorld();

        for (final var block : event.blockList()) {
            if (block.getType() == Material.NOTE_BLOCK) {
                block.setType(Material.AIR);
                world.dropItemNaturally(
                        block.getLocation(),
                        CustomBlockRegistry
                        .fromNoteBlock((NoteBlock) block.getBlockData())
                        .orElse(CustomBlockData.getDefault())
                        .craftItemStack()
                );
            }
        }
    }
}
