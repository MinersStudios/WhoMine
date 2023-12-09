package com.minersstudios.msblock.listeners.packet.player;

import com.minersstudios.msblock.MSBlock;
import com.minersstudios.msblock.collection.DiggingMap;
import com.minersstudios.msblock.api.CustomBlock;
import com.minersstudios.msblock.api.CustomBlockData;
import com.minersstudios.msblock.api.CustomBlockRegistry;
import com.minersstudios.mscore.sound.SoundGroup;
import com.minersstudios.mscore.listener.packet.AbstractMSPacketListener;
import com.minersstudios.mscore.listener.packet.MSPacketListener;
import com.minersstudios.mscore.packet.PacketContainer;
import com.minersstudios.mscore.packet.PacketEvent;
import com.minersstudios.mscore.packet.PacketType;
import com.minersstudios.mscore.util.BlockUtils;
import com.minersstudios.mscore.util.PlayerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action.*;

@MSPacketListener
public final class PlayerActionListener extends AbstractMSPacketListener<MSBlock> {

    public PlayerActionListener() {
        super(PacketType.Play.Server.PLAYER_ACTION);
    }

    @Override
    public void onPacketReceive(final @NotNull PacketEvent event) {
        final ServerPlayer serverPlayer = event.getConnection().getPlayer();
        final PacketContainer container = event.getPacketContainer();

        if (
            serverPlayer.gameMode.getGameModeForPlayer() != GameType.SURVIVAL
            || !(container.getPacket() instanceof final ServerboundPlayerActionPacket packet)
        ) return;

        final ServerboundPlayerActionPacket.Action action = packet.getAction();

        if (
                action != START_DESTROY_BLOCK
                && action != ABORT_DESTROY_BLOCK
                && action != STOP_DESTROY_BLOCK
        ) return;

        final DiggingMap diggingMap = MSBlock.cache().getDiggingMap();
        final Player player = serverPlayer.getBukkitEntity();
        final ServerLevel serverLevel = serverPlayer.serverLevel();
        final BlockPos blockPos = packet.getPos();
        final Location blockLocation = new Location(serverLevel.getWorld(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
        final Block block = blockLocation.getBlock();
        final boolean hasSlowDigging = player.hasPotionEffect(PotionEffectType.SLOW_DIGGING);

        switch (action) {
            case START_DESTROY_BLOCK -> {
                if (block.getBlockData() instanceof final NoteBlock noteBlock) {
                    diggingMap.removeAll(player);

                    if (!hasSlowDigging) {
                        this.getPlugin().runTask(() -> player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 108000, -1, true, false, false)));
                    }

                    final CustomBlockData customBlockData = CustomBlockRegistry.fromNoteBlock(noteBlock).orElse(CustomBlockData.getDefault());
                    final float digSpeed = customBlockData.getBlockSettings().calculateDigSpeed(player);
                    final DiggingMap.Entry entry = DiggingMap.Entry.create(player);

                    diggingMap.put(block, entry.taskId(
                            this.getPlugin().runTaskTimer(new Runnable() {
                                float ticks = 0.0f;
                                float progress = 0.0f;

                                @Override
                                public void run() {
                                    if (!block.equals(blockLocation.getBlock())) {
                                        diggingMap.remove(block, entry);
                                    }

                                    final Block targetBlock = PlayerUtils.getTargetBlock(player);
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
                    final DiggingMap.Entry entry = diggingMap.getBiggestStageEntry(block);

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

                    final DiggingMap.Entry entry = DiggingMap.Entry.create(player);

                    diggingMap.put(block, entry.taskId(
                            this.getPlugin().runTaskTimer(new Runnable() {
                                float ticks = 0.0f;

                                @Override
                                public void run() {
                                    if (!block.equals(blockLocation.getBlock())) {
                                        diggingMap.remove(block, entry);
                                    }

                                    final Block targetBlock = PlayerUtils.getTargetBlock(player);
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
                                        SoundGroup.WOOD.playHitSound(block.getLocation().toCenterLocation());
                                    }
                                }
                            }, 0L, 1L).getTaskId())
                    );
                }
            }
            case ABORT_DESTROY_BLOCK -> {
                final DiggingMap.Entry entry = diggingMap.getEntry(block, player);

                if (
                        entry != null
                        && !entry.farAway()
                ) {
                    final Block targetBlock = PlayerUtils.getTargetBlock(player);

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
