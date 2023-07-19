package com.minersstudios.msblock.listeners.block;

import com.minersstudios.msblock.customblock.CustomBlock;
import com.minersstudios.msblock.customblock.CustomBlockData;
import com.minersstudios.msblock.events.CustomBlockDamageEvent;
import com.minersstudios.mscore.listener.MSListener;
import com.minersstudios.mscore.utils.BlockUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import com.minersstudios.mscore.listener.AbstractMSListener;
import org.bukkit.event.block.BlockDamageEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class BlockDamageListener extends AbstractMSListener {

    @EventHandler
    public void onBlockDamage(@NotNull BlockDamageEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location blockLocation = block.getLocation().toCenterLocation();

        if (BlockUtils.isWoodenSound(block.getBlockData())) {
            CustomBlockData.DEFAULT.getSoundGroup().playHitSound(blockLocation);
        }

        if (block.getBlockData() instanceof NoteBlock noteBlock) {
            CustomBlockData customBlockData = CustomBlockData.fromNoteBlock(noteBlock);
            CustomBlockDamageEvent customBlockDamageEvent = new CustomBlockDamageEvent(new CustomBlock(block, player, customBlockData), player, event.getItemInHand());

            Bukkit.getPluginManager().callEvent(customBlockDamageEvent);
            if (customBlockDamageEvent.isCancelled()) return;
            customBlockData.getSoundGroup().playHitSound(blockLocation);
        }
    }
}
