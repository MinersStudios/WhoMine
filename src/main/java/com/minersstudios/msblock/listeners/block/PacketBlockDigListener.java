package com.minersstudios.msblock.listeners.block;

import com.minersstudios.msblock.MSBlock;
import com.minersstudios.msblock.customblock.CustomBlock;
import com.minersstudios.msblock.customblock.CustomBlockData;
import com.minersstudios.msblock.utils.CustomBlockUtils;
import com.minersstudios.msblock.utils.PlayerUtils;
import com.minersstudios.mscore.listener.packet.AbstractMSPacketListener;
import com.minersstudios.mscore.listener.packet.MSPacketListener;
import com.minersstudios.mscore.packet.PacketContainer;
import com.minersstudios.mscore.packet.PacketEvent;
import com.minersstudios.mscore.packet.PacketType;
import com.minersstudios.mscore.utils.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConnectionListener;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action.*;

@MSPacketListener
public class PacketBlockDigListener extends AbstractMSPacketListener {

    public PacketBlockDigListener() {
        super(PacketType.Play.Server.PLAYER_ACTION);
    }

    @Override
    public void onPacketReceive(@NotNull PacketEvent event) {
        Player player = event.getPlayer();
        PacketContainer container = event.getPacketContainer();

        if (
                player.getGameMode() != GameMode.SURVIVAL
                || !(container.getPacket() instanceof ServerboundPlayerActionPacket packet)
        ) return;

        ServerboundPlayerActionPacket.Action action = packet.getAction();
        BlockPos blockPos = packet.getPos();
        Location location = new Location(player.getWorld(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
        Block block = location.getBlock();
        boolean hasSlowDigging = player.hasPotionEffect(PotionEffectType.SLOW_DIGGING);

        MSBlock.getInstance().runTask(() -> {
            if (action == START_DESTROY_BLOCK) {
                if (block.getBlockData() instanceof NoteBlock noteBlock) {
                    if (CustomBlockUtils.hasPlayer(player)) {
                        CustomBlockUtils.cancelAllTasksWithThisPlayer(player);
                    }

                    if (!hasSlowDigging) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 108000, -1, true, false, false));
                    }

                    CustomBlockData customBlockData = CustomBlockData.fromNoteBlock(noteBlock);
                    float digSpeed = customBlockData.getCalculatedDigSpeed(player);
                    MSBlock.getCache().blocks.put(block, player, Bukkit.getScheduler().scheduleSyncRepeatingTask(MSBlock.getInstance(), new Runnable() {
                        float ticks = 0.0f;
                        float progress = 0.0f;
                        int currentStage = 0;

                        @Override
                        public void run() {
                            Block targetBlock = PlayerUtils.getTargetBlock(player);
                            boolean wasFarAway = false;

                            if (PlayerUtils.getTargetEntity(player, targetBlock) != null || targetBlock == null) {
                                MSBlock.getCache().farAway.add(player);
                                return;
                            } else if (MSBlock.getCache().farAway.contains(player)) {
                                MSBlock.getCache().farAway.remove(player);
                                wasFarAway = true;
                            }

                            if (!targetBlock.equals(block)) return;
                            if (
                                    !(!MSBlock.getCache().farAway.contains(player)
                                    && (((CraftPlayer) player).getHandle().swinging || wasFarAway))
                            ) {
                                PacketBlockDigListener.this.playBreakStage(blockPos, -1);
                                CustomBlockUtils.cancelAllTasksWithThisPlayer(player);
                            }

                            this.ticks++;
                            this.progress += digSpeed;
                            if (this.ticks % 4.0f == 0.0f && !MSBlock.getCache().farAway.contains(player)) {
                                customBlockData.getSoundGroup().playHitSound(block.getLocation().toCenterLocation());
                            }

                            if (this.progress > this.currentStage++ * 0.1f) {
                                this.currentStage = (int) Math.floor(this.progress * 10.0f);
                                if (this.currentStage <= 9) {
                                    PacketBlockDigListener.this.playBreakStage(blockPos, this.currentStage);
                                }
                            }

                            if (this.progress > 1.0f) {
                                PacketBlockDigListener.this.playBreakStage(blockPos, -1);
                                new CustomBlock(block, player, customBlockData).breakCustomBlock();
                            }
                        }
                    }, 0L, 1L));
                } else {
                    if (
                            CustomBlockUtils.hasPlayer(player)
                            && !BlockUtils.isWoodenSound(block.getType())
                    ) {
                        CustomBlockUtils.cancelAllTasksWithThisPlayer(player);
                    }

                    if (hasSlowDigging) {
                        player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
                    }
                }

                if (
                        block.getType() != Material.NOTE_BLOCK
                        && BlockUtils.isWoodenSound(block.getType())
                ) {
                    if (CustomBlockUtils.hasPlayer(player)) {
                        CustomBlockUtils.cancelAllTasksWithThisPlayer(player);
                    }
                    MSBlock.getCache().blocks.put(block, player, Bukkit.getScheduler().scheduleSyncRepeatingTask(MSBlock.getInstance(), new Runnable() {
                        float ticks = 0.0f;

                        @Override
                        public void run() {
                            Block targetBlock = PlayerUtils.getTargetBlock(player);
                            boolean wasFarAway = false;

                            if (PlayerUtils.getTargetEntity(player, targetBlock) != null || targetBlock == null) {
                                MSBlock.getCache().farAway.add(player);
                                return;
                            } else if (MSBlock.getCache().farAway.contains(player)) {
                                MSBlock.getCache().farAway.remove(player);
                                wasFarAway = true;
                            }

                            if (!targetBlock.equals(block)) return;
                            if (
                                    !(!MSBlock.getCache().farAway.contains(player)
                                    && (((CraftPlayer) player).getHandle().swinging || wasFarAway))
                            ) {
                                PacketBlockDigListener.this.playBreakStage(blockPos, -1);
                                CustomBlockUtils.cancelAllTasksWithThisPlayer(player);
                            }

                            this.ticks++;
                            if (this.ticks % 4.0f == 0.0f) {
                                CustomBlockData.DEFAULT.getSoundGroup().playHitSound(block.getLocation().toCenterLocation());
                            }
                        }
                    }, 0L, 1L));
                }
            } else if (action == STOP_DESTROY_BLOCK && CustomBlockUtils.hasBlock(block)) {
                this.playBreakStage(blockPos, -1);
                CustomBlockUtils.cancelAllTasksWithThisBlock(block);
            } else if (
                    action == ABORT_DESTROY_BLOCK
                    && CustomBlockUtils.hasBlock(block)
                    && !MSBlock.getCache().farAway.contains(player)
            ) {
                Block targetBlock = PlayerUtils.getTargetBlock(player);
                if (PlayerUtils.getTargetEntity(player, targetBlock) == null && targetBlock != null) {
                    this.playBreakStage(blockPos, -1);
                    CustomBlockUtils.cancelAllTasksWithThisPlayer(player);
                }
            }
        });
    }

    private void playBreakStage(
            @NotNull BlockPos blockPos,
            int stage
    ) {
        ServerConnectionListener connectionListener = MinecraftServer.getServer().getConnection();
        ClientboundBlockDestructionPacket packet = new ClientboundBlockDestructionPacket(0, blockPos, stage);

        if (connectionListener != null) {
            connectionListener.getConnections().forEach(connection -> connection.send(packet));
        }
    }
}
