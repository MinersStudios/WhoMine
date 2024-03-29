package com.minersstudios.mscustoms.listener.event.block;

import com.minersstudios.mscustoms.MSCustoms;
import com.minersstudios.mscustoms.custom.block.CustomBlockData;
import com.minersstudios.mscustoms.custom.block.CustomBlockRegistry;
import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockExplodeEvent;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class BlockExplodeListener extends AbstractEventListener<MSCustoms> {

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
                        .orElse(CustomBlockData.defaultData())
                        .craftItemStack()
                );
            }
        }
    }
}
