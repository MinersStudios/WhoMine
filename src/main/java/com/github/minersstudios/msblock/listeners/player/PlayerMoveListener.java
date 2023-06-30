package com.github.minersstudios.msblock.listeners.player;

import com.github.minersstudios.msblock.customblock.CustomBlockData;
import com.github.minersstudios.msblock.utils.PlayerUtils;
import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.mscore.utils.BlockUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Block bottomBlock = player.getLocation().subtract(0.0d, 0.5d, 0.0d).getBlock();
        Location bottomBlockLocation = bottomBlock.getLocation().toCenterLocation();

        if (
                bottomBlock.getType().isSolid()
                && player.getGameMode() != GameMode.SPECTATOR
                && !player.isFlying()
                && !player.isSneaking()
                && (bottomBlock.getType() == Material.NOTE_BLOCK || BlockUtils.isWoodenSound(bottomBlock.getBlockData()))
        ) {
            Location from = event.getFrom().clone();
            Location to = event.getTo().clone();

            from.setY(0.0d);
            to.setY(0.0d);

            double distance = from.distance(to);
            if (distance == 0.0d) return;
            double fullDistance = PlayerUtils.containsSteps(player) ? PlayerUtils.getStepDistance(player) + distance : 1.0d;

            PlayerUtils.addSteps(player, fullDistance > 1.25d ? 0.0d : fullDistance);

            if (fullDistance > 1.25d) {
                if (bottomBlock.getBlockData() instanceof NoteBlock noteBlock) {
                    CustomBlockData.fromNoteBlock(noteBlock).getSoundGroup().playStepSound(bottomBlockLocation);
                } else {
                    CustomBlockData.DEFAULT.getSoundGroup().playStepSound(bottomBlockLocation);
                }
            }
        } else {
            PlayerUtils.removeSteps(player);
        }
    }
}
