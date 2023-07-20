package com.minersstudios.msblock.listeners.block;

import com.minersstudios.msblock.customblock.CustomBlockData;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class EntityExplodeListener extends AbstractMSListener {

    @EventHandler
    public void onEntityExplode(@NotNull EntityExplodeEvent event) {
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
