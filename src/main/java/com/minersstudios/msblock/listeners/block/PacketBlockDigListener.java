package com.minersstudios.msblock.listeners.block;

import com.minersstudios.msblock.MSBlock;
import com.minersstudios.msblock.collection.DiggingMap;
import com.minersstudios.msblock.customblock.CustomBlock;
import com.minersstudios.msblock.customblock.CustomBlockData;
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

        if (
                action != START_DESTROY_BLOCK
                && action != ABORT_DESTROY_BLOCK
                && action != STOP_DESTROY_BLOCK
        ) return;

        DiggingMap diggingMap = MSBlock.getCache().diggingMap;
        BlockPos blockPos = packet.getPos();
        Block block = new Location(player.getWorld(), blockPos.getX(), blockPos.getY(), blockPos.getZ()).getBlock();
        boolean hasSlowDigging = player.hasPotionEffect(PotionEffectType.SLOW_DIGGING);

        this.getPlugin().runTask(() -> {
            switch (action) {
                case START_DESTROY_BLOCK -> {
                    if (block.getBlockData() instanceof NoteBlock noteBlock) {
                        diggingMap.removeAll(player);

                        if (!hasSlowDigging) {
                           player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 108000, -1, true, false, false));
                        }

                        CustomBlockData customBlockData = CustomBlockData.fromNoteBlock(noteBlock);
                        float digSpeed = customBlockData.getCalculatedDigSpeed(player);

                        DiggingMap.Entry entry = DiggingMap.Entry.create(player);

                        diggingMap.put(block, entry.taskId(
                                this.getPlugin().runTaskTimer(new Runnable() {
                                    float ticks = 0.0f;
                                    float progress = 0.0f;

                                    @Override
                                    public void run() {
                                        Block targetBlock = PlayerUtils.getTargetBlock(player);
                                        boolean wasFarAway = false;

                                        if (PlayerUtils.getTargetEntity(player, targetBlock) != null || targetBlock == null) {
                                            entry.farAway(true);
                                            return;
                                        } else if (entry.farAway()) {
                                            entry.farAway(false);
                                            wasFarAway = true;
                                        }

                                        if (!targetBlock.equals(block)) return;
                                        if (
                                                !(!entry.farAway()
                                                && (((CraftPlayer) player).getHandle().swinging || wasFarAway))
                                        ) {
                                            PacketBlockDigListener.this.playBreakStage(blockPos, -1);
                                            diggingMap.removeAll(player);
                                        }

                                        this.ticks++;
                                        this.progress += digSpeed;

                                        if (
                                                this.ticks % 4.0f == 0.0f
                                                && !entry.farAway()
                                        ) {
                                            customBlockData.getSoundGroup().playHitSound(block.getLocation().toCenterLocation());
                                        }

                                        if (this.progress > entry.stage() * 0.1f) {
                                            entry.stage((int) Math.floor(this.progress * 10.0f));

                                            if (
                                                    entry.stage() <= 9
                                                    && entry.isStageTheBiggest(block)
                                            ) {
                                                PacketBlockDigListener.this.playBreakStage(blockPos, entry.stage());
                                            }
                                        }

                                        if (this.progress > 1.0f) {
                                            PacketBlockDigListener.this.playBreakStage(blockPos, -1);
                                            new CustomBlock(block, customBlockData)
                                                    .breakCustomBlock(player);
                                        }
                                    }
                                }, 0L, 1L).getTaskId())
                        );
                    } else {
                        DiggingMap.Entry entry = diggingMap.getBiggestStageEntry(block);

                        if (
                                entry != null
                                && !BlockUtils.isWoodenSound(block.getType())
                        ) {
                            diggingMap.removeAll(entry);
                        }

                        if (hasSlowDigging) {
                           player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
                        }
                    }

                    if (
                            block.getType() != Material.NOTE_BLOCK
                            && BlockUtils.isWoodenSound(block.getType())
                    ) {
                        diggingMap.removeAll(player);

                        DiggingMap.Entry entry = DiggingMap.Entry.create(player);

                        diggingMap.put(block, entry.taskId(
                                this.getPlugin().runTaskTimer(new Runnable() {
                                    float ticks = 0.0f;

                                    @Override
                                    public void run() {
                                        Block targetBlock = PlayerUtils.getTargetBlock(player);
                                        boolean wasFarAway = false;

                                        if (
                                                PlayerUtils.getTargetEntity(player, targetBlock) != null
                                                || targetBlock == null
                                        ) {
                                            entry.farAway(true);
                                            return;
                                        } else if (entry.farAway()) {
                                            entry.farAway(false);
                                            wasFarAway = true;
                                        }

                                        if (!targetBlock.equals(block)) return;
                                        if (
                                                !(!entry.farAway()
                                                && (((CraftPlayer) player).getHandle().swinging || wasFarAway))
                                        ) {
                                            PacketBlockDigListener.this.playBreakStage(blockPos, -1);
                                            diggingMap.removeAll(player);
                                        }

                                        this.ticks++;

                                        if (this.ticks % 4.0f == 0.0f) {
                                            CustomBlockData.DEFAULT.getSoundGroup().playHitSound(block.getLocation().toCenterLocation());
                                        }
                                    }
                                }, 0L, 1L).getTaskId())
                        );
                    }
                }
                case ABORT_DESTROY_BLOCK -> {
                    DiggingMap.Entry entry = diggingMap.getBiggestStageEntry(block);

                    if (
                            entry != null
                            && !entry.farAway()
                    ) {
                        Block targetBlock = PlayerUtils.getTargetBlock(player);

                        if (
                                PlayerUtils.getTargetEntity(player, targetBlock) == null
                                && targetBlock != null
                        ) {
                            this.playBreakStage(blockPos, -1);
                            diggingMap.removeAll(player);
                        }
                    }
                }
                case STOP_DESTROY_BLOCK -> {
                    if (diggingMap.containsBlock(block)) {
                        this.playBreakStage(blockPos, -1);
                        diggingMap.removeAll(block);
                    }
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
