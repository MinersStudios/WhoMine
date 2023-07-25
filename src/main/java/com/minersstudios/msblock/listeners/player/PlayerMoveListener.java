package com.minersstudios.msblock.listeners.player;

import com.minersstudios.msblock.MSBlock;
import com.minersstudios.msblock.collection.StepMap;
import com.minersstudios.msblock.customblock.CustomBlockData;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.utils.BlockUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerMoveListener extends AbstractMSListener {

    @EventHandler
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Block bottomBlock = player.getLocation().subtract(0.0d, 0.5d, 0.0d).getBlock();
        Location bottomBlockLocation = bottomBlock.getLocation().toCenterLocation();
        StepMap stepMap = MSBlock.getCache().stepMap;

        if (
                player.getGameMode() != GameMode.SPECTATOR
                && !player.isFlying()
                && !player.isSneaking()
                && BlockUtils.isWoodenSound(bottomBlock.getType())
        ) {
            Location from = event.getFrom().clone();
            Location to = event.getTo().clone();

            from.setY(0.0d);
            to.setY(0.0d);

            double distance = from.distance(to);

            if (
                    distance != 0.0d
                    && stepMap.addDistance(player, distance)
            ) {
                if (bottomBlock.getBlockData() instanceof NoteBlock noteBlock) {
                    CustomBlockData.fromNoteBlock(noteBlock).getSoundGroup().playStepSound(bottomBlockLocation);
                } else {
                    CustomBlockData.DEFAULT.getSoundGroup().playStepSound(bottomBlockLocation);
                }
            }
        } else {
            stepMap.remove(player);
        }
    }
}
