package com.minersstudios.msblock.listeners.event.block;

import com.minersstudios.msblock.customblock.CustomBlock;
import com.minersstudios.msblock.customblock.CustomBlockData;
import com.minersstudios.msblock.customblock.CustomBlockRegistry;
import com.minersstudios.msblock.customblock.file.SoundGroup;
import com.minersstudios.msblock.events.CustomBlockDamageEvent;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.util.BlockUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDamageEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class BlockDamageListener extends AbstractMSListener {

    @EventHandler
    public void onBlockDamage(@NotNull BlockDamageEvent event) {
        Block block = event.getBlock();
        Material blockType = block.getType();
        Location blockLocation = block.getLocation().toCenterLocation();

        if (
                blockType != Material.NOTE_BLOCK
                && BlockUtils.isWoodenSound(blockType)
        ) {
            SoundGroup.wood().playHitSound(blockLocation);
        }

        if (block.getBlockData() instanceof NoteBlock noteBlock) {
            Player player = event.getPlayer();
            CustomBlockData customBlockData = CustomBlockRegistry.fromNoteBlock(noteBlock).orElse(CustomBlockData.getDefault());
            CustomBlock customBlock = new CustomBlock(block, customBlockData);
            CustomBlockDamageEvent damageEvent = new CustomBlockDamageEvent(customBlock, player, event.getItemInHand());

            this.getPlugin().getServer().getPluginManager().callEvent(damageEvent);

            if (!damageEvent.isCancelled()) {
                customBlockData.getSoundGroup().playHitSound(blockLocation);
            }
        }
    }
}
