package com.minersstudios.mscustoms.listener.event.block;

import com.minersstudios.mscustoms.MSCustoms;
import com.minersstudios.mscustoms.custom.block.CustomBlock;
import com.minersstudios.mscustoms.custom.block.CustomBlockData;
import com.minersstudios.mscustoms.custom.block.CustomBlockRegistry;
import com.minersstudios.mscore.sound.SoundGroup;
import com.minersstudios.mscustoms.event.block.CustomBlockDamageEvent;
import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.mscore.utility.BlockUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDamageEvent;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class BlockDamageListener extends AbstractEventListener<MSCustoms> {

    @EventHandler
    public void onBlockDamage(final @NotNull BlockDamageEvent event) {
        final Block block = event.getBlock();
        final Material blockType = block.getType();
        final Location blockLocation = block.getLocation().toCenterLocation();

        if (
                blockType != Material.NOTE_BLOCK
                && BlockUtils.isWoodenSound(blockType)
        ) {
            SoundGroup.WOOD.playHitSound(blockLocation);
        }

        if (block.getBlockData() instanceof final NoteBlock noteBlock) {
            final Player player = event.getPlayer();
            final CustomBlockData customBlockData = CustomBlockRegistry.fromNoteBlock(noteBlock).orElse(CustomBlockData.defaultData());
            final CustomBlock customBlock = new CustomBlock(block, customBlockData);
            final CustomBlockDamageEvent damageEvent = new CustomBlockDamageEvent(customBlock, player, event.getItemInHand());

            this.getPlugin().getServer().getPluginManager().callEvent(damageEvent);

            if (!damageEvent.isCancelled()) {
                customBlockData.getSoundGroup().playHitSound(blockLocation);
            }
        }
    }
}
