package com.minersstudios.mscustoms.listener.packet.player;

import com.minersstudios.mscustoms.MSCustoms;
import com.minersstudios.mscustoms.custom.block.CustomBlock;
import com.minersstudios.mscustoms.custom.block.CustomBlockData;
import com.minersstudios.mscustoms.custom.block.CustomBlockRegistry;
import com.minersstudios.mscustoms.collection.DiggingMap;
import com.minersstudios.mscore.listener.api.packet.AbstractPacketListener;
import com.minersstudios.mscore.listener.api.packet.PacketListener;
import com.minersstudios.mscore.location.MSPosition;
import com.minersstudios.mscore.packet.PacketContainer;
import com.minersstudios.mscore.packet.PacketEvent;
import com.minersstudios.mscore.packet.PacketType;
import com.minersstudios.mscore.sound.SoundGroup;
import com.minersstudios.mscore.utility.BlockUtils;
import com.minersstudios.mscore.utility.PlayerUtils;
import com.minersstudios.mscore.utility.SharedConstants;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.GameType;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static net.minecraft.world.effect.MobEffectInstance.INFINITE_DURATION;
import static net.minecraft.world.effect.MobEffects.DIG_SLOWDOWN;
import static org.bukkit.event.entity.EntityPotionEffectEvent.Cause.PLUGIN;

@PacketListener
public final class PlayerActionListener extends AbstractPacketListener<MSCustoms> {
    private final Map<String, Handler> handlerMap;
    private final Map<String, MobEffectInstance> effectMap;
    final Map<String, CompletableFuture<Block>> clickRequestMap;

    public PlayerActionListener() {
        super(PacketType.Play.Server.PLAYER_ACTION);

        this.handlerMap = new Object2ObjectOpenHashMap<>();
        this.effectMap = new Object2ObjectOpenHashMap<>();
        this.clickRequestMap = new ConcurrentHashMap<>();
    }

    @Override
    public void onPacketReceive(final @NotNull PacketEvent event) {
        final ServerPlayer player = event.getConnection().getPlayer();
        final PacketContainer container = event.getPacketContainer();

        if (player.gameMode.getGameModeForPlayer() == GameType.SURVIVAL) {
            final var packet = (ServerboundPlayerActionPacket) container.getPacket();
            final MSPosition position = MSPosition.of(
                    player.level().getWorld(),
                    packet.getPos()
            );

            switch (packet.getAction()) {
                case START_DESTROY_BLOCK -> this.getHandler(player, position).ifPresent(Handler::start);
                case ABORT_DESTROY_BLOCK -> this.getHandler(player, position).ifPresent(Handler::abort);
                case STOP_DESTROY_BLOCK ->  this.getHandler(player, position).ifPresent(Handler::finish);
            }
        }
    }

    private @NotNull Optional<Handler> getHandler(
            final @NotNull ServerPlayer serverPlayer,
            final @NotNull MSPosition position
    ) {
        synchronized (this.handlerMap) {
            final Handler handler = this.handlerMap.get(serverPlayer.getStringUUID());

            if (BlockUtils.isWoodenSound(position.getBlock().getType())) {
                if (
                        handler != null
                        && handler.position.equals(position)
                ) {
                    return Optional.of(handler);
                }

                final Handler newHandler = new Handler(serverPlayer, position);

                this.handlerMap.put(
                        serverPlayer.getStringUUID(),
                        newHandler
                );

                return Optional.of(newHandler);
            } else if (handler != null) {
                handler.stop();
                this.handlerMap.remove(serverPlayer.getStringUUID());
            }

            this.getPlugin().runTask(
                    () -> this.removeSlowDigging(serverPlayer)
            );

            return Optional.empty();
        }
    }

    private boolean addSlowDigging(final @NotNull ServerPlayer serverPlayer) {
        final MobEffectInstance effect = serverPlayer.getEffect(DIG_SLOWDOWN);
        final boolean hasSlowDigging = effect != null;

        if (hasSlowDigging) {
            if (!effect.isVisible()) {
                return false;
            }

            this.effectMap.put(
                    serverPlayer.getStringUUID(),
                    new MobEffectInstance(effect)
            );
        }

        serverPlayer.addEffect(
                new MobEffectInstance(
                        DIG_SLOWDOWN,
                        INFINITE_DURATION,
                        Integer.MAX_VALUE,
                        false,
                        hasSlowDigging,
                        hasSlowDigging
                ),
                PLUGIN
        );

        return true;
    }

    private boolean removeSlowDigging(final @NotNull ServerPlayer serverPlayer) {
        final MobEffectInstance effect = serverPlayer.getEffect(DIG_SLOWDOWN);

        if (
                effect != null
                && !effect.isVisible()
        ) {
            serverPlayer.removeEffect(DIG_SLOWDOWN, PLUGIN);

            final MobEffectInstance oldEffect = this.effectMap.remove(serverPlayer.getStringUUID());

            if (oldEffect != null) {
                serverPlayer.addEffect(oldEffect);
            }

            return true;
        }

        return false;
    }

    static @Nullable Block getTargetBlock(final @NotNull ServerPlayer serverPlayer) {
        final Player player = serverPlayer.getBukkitEntity();
        final Block targetBlock = PlayerUtils.getTargetBlock(player);

        return PlayerUtils.getTargetEntity(player, targetBlock) == null
                ? targetBlock
                : null;
    }

    /**
     * Handles the block-breaking process
     */
    private class Handler {
        private final DiggingMap diggingMap;
        private final ServerPlayer serverPlayer;
        private final MSPosition position;
        private final Block block;

        /**
         * Constructs a BreakHandler with the given player and position
         *
         * @param serverPlayer The player who is breaking the block
         * @param position     The position of the block
         */
        Handler(
                final @NotNull ServerPlayer serverPlayer,
                final @NotNull MSPosition position
        ) {
            this.diggingMap = PlayerActionListener.this.getPlugin().getCache().getDiggingMap();
            this.serverPlayer = serverPlayer;
            this.position = position;
            this.block = position.getBlock();
        }

        /**
         * Starts the block-breaking process
         */
        public void start() {
            final MSCustoms plugin = PlayerActionListener.this.getPlugin();

            this.stop();

            if (this.block.getBlockData() instanceof final NoteBlock noteBlock) {
                plugin.runTask(
                        () -> PlayerActionListener.this.addSlowDigging(this.serverPlayer)
                );
                this.handleNoteBlock(noteBlock);
            } else {
                plugin.runTask(
                        () -> PlayerActionListener.this.removeSlowDigging(this.serverPlayer)
                );
                this.handleWoodenBlock();
            }
        }

        /**
         * Handles the block-breaking aborting process
         *
         * @see #abort(boolean)
         */
        public void abort() {
            this.abort(true);
        }

        /**
         * Handles the block-breaking aborting process
         *
         * @param fromPacket Whether the aborting process is called from the
         *                   packet sent by the client
         */
        public void abort(final boolean fromPacket) {
            final DiggingMap.Entry entry = this.diggingMap.getEntry(
                    this.block,
                    this.serverPlayer.getBukkitEntity()
            );

            if (entry != null) {
                final MSCustoms plugin = PlayerActionListener.this.getPlugin();
                final String uuid = this.serverPlayer.getStringUUID();

                if (PlayerActionListener.this.clickRequestMap.containsKey(uuid)) {
                    return;
                }

                plugin.runTask(() -> {
                    if (fromPacket) {
                        if (this.block.equals(getTargetBlock(this.serverPlayer))) {
                            this.stop(entry);
                            return;
                        }

                        entry.setStage(-1);

                        if (this.diggingMap.getDiggingEntries(this.block).size() == 1) {
                            this.broadcastStage(this.block, -1);
                        }
                    }

                    final var future = new CompletableFuture<Block>();

                    entry.setAborting(true);
                    future.thenAccept(
                            block -> {
                                if (
                                        block != null
                                        && !block.equals(this.block)
                                ) {
                                    this.stop(entry);
                                } else {
                                    entry.setAborting(false);
                                }
                            }
                    );
                    PlayerActionListener.this.clickRequestMap
                    .put(
                            this.serverPlayer.getStringUUID(),
                            future
                    );
                });
            }
        }

        /**
         * Stops the block-breaking process for all entries associated with
         * the player
         *
         * @see #stop(DiggingMap.Entry)
         */
        public void stop() {
            this.stop(null);
        }

        /**
         * Stops the block-breaking process
         *
         * @param entry The entry to stop, if null, all entries associated with
         *              the player will be stopped
         */
        public void stop(final @Nullable DiggingMap.Entry entry) {
            final MSCustoms plugin = PlayerActionListener.this.getPlugin();

            if (entry == null) {
                final var removed = this.diggingMap.removeAll(this.serverPlayer.getBukkitEntity());

                if (!removed.isEmpty()) {
                    plugin.runTask(() -> {
                        for (final var removedEntry : removed) {
                            broadcastBiggestStage(removedEntry.getKey());
                        }
                    });
                }
            } else {
                this.diggingMap.remove(this.block, entry);
                plugin.runTask(() -> broadcastBiggestStage(this.block));
            }

            PlayerActionListener.this.clickRequestMap.remove(this.serverPlayer.getStringUUID());
        }

        /**
         * Finishes the block-breaking process, removes all entries associated
         * with the block
         */
        public void finish() {
            if (this.diggingMap.containsBlock(this.block)) {
                this.diggingMap.removeAll(this.block);
                PlayerActionListener.this.clickRequestMap.remove(
                        this.serverPlayer.getStringUUID()
                );
                PlayerActionListener.this.getPlugin().runTask(
                        () -> this.broadcastStage(this.block, -1)
                );
            }
        }

        /**
         * Broadcasts the block-breaking progress with the specified stage
         *
         * @param block The block to set the progress for
         * @param stage The destroying stage of the block
         */
        public void broadcastStage(
                final @NotNull Block block,
                final int stage
        ) {
            final BlockPos blockPos = new BlockPos(
                    block.getX(),
                    block.getY(),
                    block.getZ()
            );

            this.serverPlayer.level().destroyBlockProgress(
                    blockPos.hashCode(),
                    blockPos,
                    stage
            );
        }

        /**
         * Broadcasts the block-breaking progress with the biggest stage in the
         * digging map
         *
         * @param block The block to get the biggest stage entry from
         */
        public void broadcastBiggestStage(final @NotNull Block block) {
            final DiggingMap.Entry entry = this.diggingMap.getBiggestStageEntry(block);

            this.broadcastStage(
                    block,
                    entry == null ? -1 : entry.getStage()
            );
        }

        private int getSlowDiggingAmplifier() {
            MobEffectInstance slowDigging =
                    PlayerActionListener.this.effectMap.get(this.serverPlayer.getStringUUID());

            if (slowDigging == null) {
                slowDigging = this.serverPlayer.getEffect(DIG_SLOWDOWN);
            }

            return slowDigging == null
                    || slowDigging.isInfiniteDuration()
                    ? -1
                    : slowDigging.getAmplifier();
        }

        private void handleNoteBlock(final NoteBlock noteBlock) {
            final MSCustoms plugin = PlayerActionListener.this.getPlugin();
            final Player player = this.serverPlayer.getBukkitEntity();
            final Location center = this.position.center().toLocation();
            final DiggingMap.Entry entry = DiggingMap.Entry.create(player);

            final CustomBlockData customBlockData =
                    CustomBlockRegistry
                    .fromNoteBlock(noteBlock)
                    .orElse(CustomBlockData.defaultData());
            final SoundGroup soundGroup = customBlockData.getSoundGroup();
            final float digSpeed = customBlockData.getBlockSettings().calculateDigSpeed(
                    player,
                    this.getSlowDiggingAmplifier()
            );

            this.diggingMap.put(this.block, entry.setTaskId(
                    plugin.runTaskTimer(new Runnable() {
                        float ticks = 0.0f;
                        float progress = 0.0f;
                        boolean isAlreadyAborted = false;

                        @Override
                        public void run() {
                            if (
                                    this.isAlreadyAborted
                                    || !block.equals(getTargetBlock(serverPlayer))
                            ) {
                                Handler.this.abort(false);
                            }

                            if (entry.isAborting()) {
                                this.isAlreadyAborted = true;

                                return;
                            }

                            final int stage = entry.getStage();

                            if (stage == -1) {
                                this.progress = 0.0f;
                            }

                            this.ticks++;
                            this.progress += digSpeed;
                            final int progressInStage = (int) Math.floor(this.progress * 10.0f);

                            if (this.ticks % 4.0f == 0.0f) {
                                soundGroup.playHitSound(center);
                            }

                            if (progressInStage > stage) {
                                entry.setStage(progressInStage);

                                if (stage >= SharedConstants.FINAL_BREAK_STAGE) {
                                    Handler.this.finish();
                                    new CustomBlock(block, customBlockData)
                                            .destroy(plugin, player);
                                } else if (entry.isStageTheBiggest(plugin, block)) {
                                    Handler.this.broadcastStage(block, stage);
                                }
                            }
                        }
                    }, 0L, 1L).getTaskId())
            );
        }

        private void handleWoodenBlock() {
            final DiggingMap.Entry entry = DiggingMap.Entry.create(this.serverPlayer.getBukkitEntity());
            final Location center = this.position.center().toLocation();

            this.diggingMap.put(this.block, entry.setTaskId(
                    PlayerActionListener.this.getPlugin().runTaskTimer(new Runnable() {
                        float ticks = 0.0f;
                        boolean isAlreadyAborted = false;

                        @Override
                        public void run() {
                            if (
                                    this.isAlreadyAborted
                                    || !block.equals(getTargetBlock(serverPlayer))
                            ) {
                                abort(false);
                            }

                            if (entry.isAborting()) {
                                this.isAlreadyAborted = true;

                                return;
                            }

                            this.ticks++;

                            if (this.ticks % 4.0f == 0.0f) {
                                SoundGroup.WOOD.playHitSound(center);
                            }
                        }
                    }, 0L, 1L).getTaskId())
            );
        }
    }
}
