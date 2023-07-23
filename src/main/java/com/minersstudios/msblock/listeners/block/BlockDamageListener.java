package com.minersstudios.msblock.listeners.block;

import com.minersstudios.msblock.customblock.CustomBlock;
import com.minersstudios.msblock.customblock.CustomBlockData;
import com.minersstudios.msblock.events.CustomBlockDamageEvent;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.utils.BlockUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
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
            CustomBlockData.DEFAULT.getSoundGroup().playHitSound(blockLocation);
        }

        if (block.getBlockData() instanceof NoteBlock noteBlock) {
            Player player = event.getPlayer();
            CustomBlockData customBlockData = CustomBlockData.fromNoteBlock(noteBlock);
            CustomBlockDamageEvent damageEvent = new CustomBlockDamageEvent(new CustomBlock(block, player, customBlockData), player, event.getItemInHand());

            Bukkit.getPluginManager().callEvent(damageEvent);
            if (damageEvent.isCancelled()) return;
            customBlockData.getSoundGroup().playHitSound(blockLocation);
        }
    }
}
