package com.github.minersstudios.msblock.listeners.block;

import com.github.minersstudios.msblock.customblock.CustomBlockData;
import com.github.minersstudios.msblock.customblock.ToolType;
import com.github.minersstudios.msblock.utils.CustomBlockUtils;
import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.mscore.utils.BlockUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.craftbukkit.v1_20_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

@MSListener
public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
        Player player = event.getPlayer();
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        Block block = event.getBlock();
        CraftBlock craftBlock = (CraftBlock) block;
        Block topBlock = event.getBlock().getRelative(BlockFace.UP);
        Block bottomBlock = event.getBlock().getRelative(BlockFace.DOWN);
        Location blockLocation = block.getLocation().toCenterLocation();

        if (BlockUtils.isWoodenSound(block.getBlockData())) {
            CustomBlockData.DEFAULT.getSoundGroup().playBreakSound(blockLocation);
        }

        if (block.getBlockData() instanceof NoteBlock noteBlock) {
            CustomBlockData customBlockMaterial = CustomBlockData.fromNoteBlock(noteBlock);
            GameMode gameMode = player.getGameMode();

            event.setCancelled(true);

            if (
                    gameMode == GameMode.CREATIVE
                    && CustomBlockUtils.destroyBlock(new ServerPlayerGameMode(serverPlayer), serverPlayer, craftBlock.getPosition())
            ) {
                customBlockMaterial.getSoundGroup().playBreakSound(blockLocation);
            }

            if (
                    customBlockMaterial.getToolType() == ToolType.AXE
                    && gameMode != GameMode.CREATIVE
            ) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 108000, -1, true, false, false));
                block.getWorld().dropItemNaturally(block.getLocation(), customBlockMaterial.craftItemStack());
                CustomBlockUtils.destroyBlock(new ServerPlayerGameMode(serverPlayer), serverPlayer, craftBlock.getPosition());
            }
            return;
        }

        if (
                topBlock.getType() == Material.NOTE_BLOCK
                || bottomBlock.getType() == Material.NOTE_BLOCK
        ) {
            event.setCancelled(true);
            CustomBlockUtils.destroyBlock(new ServerPlayerGameMode(serverPlayer), serverPlayer, craftBlock.getPosition());
        }
    }
}
