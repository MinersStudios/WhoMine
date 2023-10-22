package com.minersstudios.msblock.listeners.event.block;

import com.minersstudios.msblock.MSBlock;
import com.minersstudios.msblock.customblock.CustomBlockData;
import com.minersstudios.msblock.customblock.CustomBlockRegistry;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class EntityExplodeListener extends AbstractMSListener<MSBlock> {

    @EventHandler
    public void onEntityExplode(final @NotNull EntityExplodeEvent event) {
        final World world = event.getLocation().getWorld();

        for (final var block : event.blockList()) {
            if (block.getType() != Material.NOTE_BLOCK) continue;

            block.setType(Material.AIR);
            world.dropItemNaturally(
                    block.getLocation(),
                    CustomBlockRegistry.fromBlockData(block.getBlockData())
                    .orElse(CustomBlockData.getDefault())
                    .craftItemStack()
            );
        }
    }
}
