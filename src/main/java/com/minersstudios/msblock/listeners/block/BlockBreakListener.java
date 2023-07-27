package com.minersstudios.msblock.listeners.block;

import com.minersstudios.msblock.customblock.CustomBlockData;
import com.minersstudios.msblock.customblock.ToolType;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.util.BlockUtils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.craftbukkit.v1_20_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.event.CraftEventFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@MSListener
public class BlockBreakListener extends AbstractMSListener {

    @EventHandler
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
        Player player = event.getPlayer();
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        Block block = event.getBlock();
        Material blockType = block.getType();
        BlockPos blockPosition = ((CraftBlock) block).getPosition();
        Block topBlock = event.getBlock().getRelative(BlockFace.UP);
        Block bottomBlock = event.getBlock().getRelative(BlockFace.DOWN);
        Location blockLocation = block.getLocation();

        if (
                blockType != Material.NOTE_BLOCK
                && BlockUtils.isWoodenSound(blockType)
        ) {
            CustomBlockData.DEFAULT.getSoundGroup().playBreakSound(blockLocation.toCenterLocation());
        }

        if (block.getBlockData() instanceof NoteBlock noteBlock) {
            CustomBlockData customBlockMaterial = CustomBlockData.fromNoteBlock(noteBlock);
            GameMode gameMode = player.getGameMode();

            event.setCancelled(true);

            if (
                    gameMode == GameMode.CREATIVE
                    && destroyBlock(serverPlayer, blockPosition)
            ) {
                customBlockMaterial.getSoundGroup().playBreakSound(blockLocation.toCenterLocation());
            }

            if (
                    customBlockMaterial.getToolType() == ToolType.AXE
                    && gameMode != GameMode.CREATIVE
            ) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 108000, -1, true, false, false));
                block.getWorld().dropItemNaturally(blockLocation, customBlockMaterial.craftItemStack());
                destroyBlock(serverPlayer, blockPosition);
            }
            return;
        }

        if (
                topBlock.getType() == Material.NOTE_BLOCK
                || bottomBlock.getType() == Material.NOTE_BLOCK
        ) {
            event.setCancelled(true);
            destroyBlock(serverPlayer, blockPosition);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    private static boolean destroyBlock(
            @NotNull ServerPlayer serverPlayer,
            @NotNull BlockPos pos
    ) {
        ServerPlayerGameMode gameMode = serverPlayer.gameMode;
        BlockState iBlockData = gameMode.level.getBlockState(pos);
        Block bblock = CraftBlock.at(gameMode.level, pos);
        BlockBreakEvent event = new BlockBreakEvent(bblock, serverPlayer.getBukkitEntity());
        boolean isSwordNoBreak = !serverPlayer.getMainHandItem().getItem().canAttackBlock(iBlockData, gameMode.level, pos, serverPlayer);

        if (gameMode.level.getBlockEntity(pos) == null && !isSwordNoBreak) {
            serverPlayer.connection.send(new ClientboundBlockUpdatePacket(pos, Blocks.AIR.defaultBlockState()));
        }

        event.setCancelled(isSwordNoBreak);

        BlockState nmsData = gameMode.level.getBlockState(pos);
        net.minecraft.world.level.block.Block nmsBlock = nmsData.getBlock();

        if (
                serverPlayer.isCreative()
                || bblock.getType() == Material.NOTE_BLOCK
        ) {
            event.setDropItems(false);
        }

        if (
                !event.isCancelled()
                && !gameMode.isCreative()
                && serverPlayer.hasCorrectToolForDrops(nmsBlock.defaultBlockState())
        ) {
            event.setExpToDrop(nmsBlock.getExpDrop(nmsData, gameMode.level, pos, serverPlayer.getItemBySlot(EquipmentSlot.MAINHAND), true));
        }

        if (event.isCancelled()) {
            if (isSwordNoBreak) return false;

            serverPlayer.connection.send(new ClientboundBlockUpdatePacket(gameMode.level, pos));

            for (Direction dir : Direction.values()) {
                serverPlayer.connection.send(new ClientboundBlockUpdatePacket(gameMode.level, pos.relative(dir)));
            }

            if (!gameMode.captureSentBlockEntities) {
                BlockEntity tileEntity = gameMode.level.getBlockEntity(pos);
                if (tileEntity != null) {
                    serverPlayer.connection.send(Objects.requireNonNull(tileEntity.getUpdatePacket()));
                }
            } else {
                gameMode.capturedBlockEntity = true;
            }

            return false;
        }

        iBlockData = gameMode.level.getBlockState(pos);
        if (iBlockData.isAir()) {
            return false;
        } else {
            BlockEntity tileEntity = gameMode.level.getBlockEntity(pos);
            net.minecraft.world.level.block.Block block = iBlockData.getBlock();

            if (
                    !(block instanceof GameMasterBlock)
                    || serverPlayer.canUseGameMasterBlocks()
                    || block instanceof CommandBlock && serverPlayer.isCreative()
                    && serverPlayer.getBukkitEntity().hasPermission("minecraft.commandblock")
            ) {
                if (serverPlayer.blockActionRestricted(gameMode.level, pos, gameMode.getGameModeForPlayer())) {
                    return false;
                } else {
                    org.bukkit.block.BlockState state = bblock.getState();
                    gameMode.level.captureDrops = new ArrayList<>();
                    block.playerWillDestroy(gameMode.level, pos, iBlockData, serverPlayer);

                    boolean flag = gameMode.level.removeBlock(pos, false);
                    if (flag) {
                        block.destroy(gameMode.level, pos, iBlockData);
                    }

                    net.minecraft.world.item.ItemStack mainHandStack = null;
                    boolean isCorrectTool = false;

                    if (!gameMode.isCreative()) {
                        net.minecraft.world.item.ItemStack itemStack = serverPlayer.getMainHandItem();
                        net.minecraft.world.item.ItemStack itemStack1 = itemStack.copy();
                        boolean flag1 = serverPlayer.hasCorrectToolForDrops(iBlockData);
                        mainHandStack = itemStack1;
                        isCorrectTool = flag1;

                        itemStack.mineBlock(gameMode.level, iBlockData, pos, serverPlayer);

                        if (flag && flag1 && event.isDropItems()) {
                            block.playerDestroy(gameMode.level, serverPlayer, pos, iBlockData, tileEntity, itemStack1);
                        }
                    }

                    List<ItemEntity> itemsToDrop = gameMode.level.captureDrops;
                    gameMode.level.captureDrops = null;

                    if (event.isDropItems()) {
                        CraftEventFactory.handleBlockDropItemEvent(bblock, state, serverPlayer, itemsToDrop);
                    }

                    if (flag) {
                        iBlockData.getBlock().popExperience(gameMode.level, pos, event.getExpToDrop(), serverPlayer);
                    }

                    if (
                            mainHandStack != null
                            && flag
                            && isCorrectTool
                            && event.isDropItems()
                            && block instanceof BeehiveBlock
                            && tileEntity instanceof BeehiveBlockEntity beehiveBlockEntity
                    ) {
                        CriteriaTriggers.BEE_NEST_DESTROYED.trigger(serverPlayer, iBlockData, mainHandStack, beehiveBlockEntity.getOccupantCount());
                    }
                }
                return true;
            } else {
                gameMode.level.sendBlockUpdated(pos, iBlockData, iBlockData, 3);
                return false;
            }
        }
    }
}
