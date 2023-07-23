package com.minersstudios.msblock.listeners.block;

import com.minersstudios.msblock.customblock.CustomBlockData;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import org.bukkit.Material;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class EntityExplodeListener extends AbstractMSListener {

    @EventHandler
    public void onEntityExplode(@NotNull EntityExplodeEvent event) {
        event.blockList().stream()
        .filter(block -> block.getType() == Material.NOTE_BLOCK)
        .forEach(block -> {
            block.setType(Material.AIR);
            block.getWorld().dropItemNaturally(
                    block.getLocation(),
                    CustomBlockData.fromNoteBlock((NoteBlock) block.getBlockData()).craftItemStack()
            );
        });
    }
}
