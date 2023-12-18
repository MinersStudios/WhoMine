package com.minersstudios.msblock.listener.event.player;

import com.minersstudios.msblock.MSBlock;
import com.minersstudios.msblock.api.CustomBlockData;
import com.minersstudios.msblock.api.CustomBlockRegistry;
import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.mscore.utility.BlockUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class PlayerMoveListener extends AbstractEventListener<MSBlock> {

    @EventHandler
    public void onPlayerMove(final @NotNull PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final Block block = player.getLocation().subtract(0.0d, 0.15d, 0.0d).getBlock();

        if (
                player.getGameMode() != GameMode.SPECTATOR
                && !player.isFlying()
                && !player.isSneaking()
        ) {
            final double distance = event.getFrom().distance(event.getTo());

            if (
                    distance != 0.0d
                    && this.getPlugin().getCache().getStepMap().addDistance(player, distance)
                    && BlockUtils.isWoodenSound(block.getType())
            ) {
                final Location stepLocation = block.getLocation().toCenterLocation();

                CustomBlockRegistry.fromBlockData(block.getBlockData())
                .orElse(CustomBlockData.getDefault())
                .getSoundGroup().playStepSound(stepLocation);
            }
        }
    }
}
