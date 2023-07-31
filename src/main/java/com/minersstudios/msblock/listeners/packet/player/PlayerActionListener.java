package com.minersstudios.msblock.listeners.packet.player;

import com.minersstudios.msblock.MSBlock;
import com.minersstudios.msblock.collection.DiggingMap;
import com.minersstudios.msblock.customblock.CustomBlock;
import com.minersstudios.msblock.customblock.CustomBlockData;
import com.minersstudios.msblock.customblock.CustomBlockRegistry;
import com.minersstudios.msblock.customblock.file.SoundGroup;
import com.minersstudios.mscore.util.PlayerUtils;
import com.minersstudios.mscore.listener.packet.AbstractMSPacketListener;
import com.minersstudios.mscore.listener.packet.MSPacketListener;
import com.minersstudios.mscore.packet.PacketContainer;
import com.minersstudios.mscore.packet.PacketEvent;
import com.minersstudios.mscore.packet.PacketType;
import com.minersstudios.mscore.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
public class PlayerActionListener extends AbstractMSPacketListener {

    public PlayerActionListener() {
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
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        ServerLevel serverLevel = serverPlayer.serverLevel();
        BlockPos blockPos = packet.getPos();
        Location blockLocation = new Location(player.getWorld(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
        Block block = blockLocation.getBlock();
        boolean hasSlowDigging = player.hasPotionEffect(PotionEffectType.SLOW_DIGGING);

        switch (action) {
            case START_DESTROY_BLOCK -> {
                if (block.getBlockData() instanceof NoteBlock noteBlock) {
                    diggingMap.removeAll(player);

                    if (!hasSlowDigging) {
                        this.getPlugin().runTask(() -> player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 108000, -1, true, false, false)));
                    }

                    CustomBlockData customBlockData = CustomBlockRegistry.fromNoteBlock(noteBlock).orElse(CustomBlockData.getDefault());
                    float digSpeed = customBlockData.calculateDigSpeed(player);

                    DiggingMap.Entry entry = DiggingMap.Entry.create(player);

                    diggingMap.put(block, entry.taskId(
                            this.getPlugin().runTaskTimer(new Runnable() {
                                float ticks = 0.0f;
                                float progress = 0.0f;

                                @Override
                                public void run() {
                                    if (!block.equals(blockLocation.getBlock())) {
                                        diggingMap.remove(block, entry);
                                    }

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
                                            && (serverPlayer.swinging || wasFarAway))
                                    ) {
                                        if (entry.isStageTheBiggest(block)) {
                                            serverLevel.destroyBlockProgress(blockPos.hashCode(), blockPos, -1);
                                        }

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
                                            serverLevel.destroyBlockProgress(blockPos.hashCode(), blockPos, entry.stage());
                                        }
                                    }

                                    if (this.progress > 1.0f) {
                                        serverLevel.destroyBlockProgress(blockPos.hashCode(), blockPos, -1);
                                        new CustomBlock(block, customBlockData)
                                                .destroy(player);
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
                        this.getPlugin().runTask(() -> player.removePotionEffect(PotionEffectType.SLOW_DIGGING));
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
                                    if (!block.equals(blockLocation.getBlock())) {
                                        diggingMap.remove(block, entry);
                                    }

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
                                            && (serverPlayer.swinging || wasFarAway))
                                    ) {
                                        diggingMap.removeAll(player);
                                    }

                                    this.ticks++;

                                    if (this.ticks % 4.0f == 0.0f) {
                                        SoundGroup.wood().playHitSound(block.getLocation().toCenterLocation());
                                    }
                                }
                            }, 0L, 1L).getTaskId())
                    );
                }
            }
            case ABORT_DESTROY_BLOCK -> {
                DiggingMap.Entry entry = diggingMap.getEntry(block, player);

                if (
                        entry != null
                        && !entry.farAway()
                ) {
                    Block targetBlock = PlayerUtils.getTargetBlock(player);

                    this.getPlugin().runTask(() -> {
                        if (
                                PlayerUtils.getTargetEntity(player, targetBlock) == null
                                && targetBlock != null
                        ) {
                            serverLevel.destroyBlockProgress(blockPos.hashCode(), blockPos, -1);
                            diggingMap.remove(block, entry);
                        }
                    });
                }
            }
            case STOP_DESTROY_BLOCK -> {
                if (diggingMap.containsBlock(block)) {
                    this.getPlugin().runTask(() -> serverLevel.destroyBlockProgress(blockPos.hashCode(), blockPos, -1));
                    diggingMap.removeAll(block);
                }
            }
        }
    }
}