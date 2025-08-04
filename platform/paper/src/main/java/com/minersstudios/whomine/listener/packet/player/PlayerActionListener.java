package com.minersstudios.whomine.listener.packet.player;

import com.minersstudios.wholib.paper.WhoMine;
import com.minersstudios.wholib.event.handle.CancellableHandler;
import com.minersstudios.wholib.paper.custom.block.CustomBlock;
import com.minersstudios.wholib.paper.custom.block.CustomBlockData;
import com.minersstudios.wholib.paper.custom.block.CustomBlockRegistry;
import com.minersstudios.wholib.paper.collection.DiggingMap;
import com.minersstudios.wholib.packet.registry.PlayPackets;
import com.minersstudios.wholib.paper.packet.PaperPacketContainer;
import com.minersstudios.wholib.paper.packet.PaperPacketEvent;
import com.minersstudios.wholib.paper.packet.PaperPacketListener;
import com.minersstudios.wholib.paper.utility.ApiConverter;
import com.minersstudios.wholib.paper.world.location.MSPosition;
import com.minersstudios.wholib.paper.world.sound.SoundGroup;
import com.minersstudios.wholib.paper.utility.BlockUtils;
import com.minersstudios.wholib.paper.utility.PlayerUtils;
import com.minersstudios.wholib.key.ResourceKey;
import com.minersstudios.wholib.utility.SharedConstants;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerActionListener extends PaperPacketListener {

    private final Map<String, Handler> handlerMap;
    final Map<String, CompletableFuture<Block>> clickRequestMap;

    public PlayerActionListener() {
        super(PlayPackets.SERVER_PLAYER_ACTION);

        this.handlerMap = new Object2ObjectOpenHashMap<>();
        this.clickRequestMap = new ConcurrentHashMap<>();
    }

    @CancellableHandler
    public void onEvent(final @NotNull PaperPacketContainer container) {
        final PaperPacketEvent event = container.getEvent();
        final ServerPlayer player = event.getConnection().getPlayer();

        if (player.gameMode.getGameModeForPlayer() == GameType.SURVIVAL) {
            final var packet = (ServerboundPlayerActionPacket) event.getPacket();
            final MSPosition position = MSPosition.of(player.level().getWorld(), packet.getPos());

            this.getHandler(container.getModule(), player, position).ifPresent(handler -> {
                switch (packet.getAction()) {
                    case START_DESTROY_BLOCK -> handler.start();
                    case ABORT_DESTROY_BLOCK -> handler.abort();
                    case STOP_DESTROY_BLOCK  -> handler.finish();
                    default -> {} // Do nothing
                }
            });
        }
    }

    private @NotNull Optional<Handler> getHandler(
            final @NotNull WhoMine module,
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

                final Handler newHandler = new Handler(module, serverPlayer, position);

                this.handlerMap.put(
                        serverPlayer.getStringUUID(),
                        newHandler
                );

                return Optional.of(newHandler);
            } else if (handler != null) {
                handler.stop();
                this.handlerMap.remove(serverPlayer.getStringUUID());
            }

            return Optional.empty();
        }
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
        @SuppressWarnings("UnstableApiUsage")
        private static final AttributeModifier BREAK_SPEED_MODIFIER =
                new AttributeModifier(
                        ApiConverter.apiToBukkit(ResourceKey.whomine("custom_break_speed")),
                        -1,
                        AttributeModifier.Operation.MULTIPLY_SCALAR_1,
                        EquipmentSlotGroup.HAND
                );

        private final WhoMine module;
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
                final @NotNull WhoMine module,
                final @NotNull ServerPlayer serverPlayer,
                final @NotNull MSPosition position
        ) {
            this.module = module;
            this.serverPlayer = serverPlayer;
            this.position = position;
            this.block = position.getBlock();
        }

        /**
         * Starts the block-breaking process
         */
        public void start() {
            this.stop();

            if (this.block.getBlockData() instanceof final NoteBlock noteBlock) {
                this.addSpeedModifier();
                this.handleNoteBlock(noteBlock);
            } else {
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
            final DiggingMap diggingMap = this.module.getCache().getDiggingMap();
            final DiggingMap.Entry entry = diggingMap.getEntry(
                    this.block,
                    this.serverPlayer.getBukkitEntity()
            );

            if (entry != null) {
                final String uuid = this.serverPlayer.getStringUUID();

                if (PlayerActionListener.this.clickRequestMap.containsKey(uuid)) {
                    return;
                }

                this.module.runTask(() -> {
                    if (fromPacket) {
                        if (this.block.equals(getTargetBlock(this.serverPlayer))) {
                            this.stop(entry);

                            return;
                        }

                        entry.setStage(-1);

                        if (diggingMap.getDiggingEntries(this.block).size() == 1) {
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
            this.removeSpeedModifier();

            final DiggingMap diggingMap = this.module.getCache().getDiggingMap();

            if (entry == null) {
                final var removed = diggingMap.removeAll(this.serverPlayer.getBukkitEntity());

                if (!removed.isEmpty()) {
                    this.module.runTask(() -> {
                        for (final var removedEntry : removed) {
                            broadcastBiggestStage(removedEntry.getKey());
                        }
                    });
                }
            } else {
                diggingMap.remove(this.block, entry);
                this.module.runTask(() -> broadcastBiggestStage(this.block));
            }

            PlayerActionListener.this.clickRequestMap.remove(this.serverPlayer.getStringUUID());
        }

        /**
         * Finishes the block-breaking process, removes all entries associated
         * with the block
         */
        public void finish() {
            final DiggingMap diggingMap = this.module.getCache().getDiggingMap();

            if (diggingMap.containsBlock(this.block)) {
                diggingMap.removeAll(this.block);
                PlayerActionListener.this.clickRequestMap.remove(
                        this.serverPlayer.getStringUUID()
                );
                this.module.runTask(
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
            final DiggingMap.Entry entry = this.module.getCache().getDiggingMap().getBiggestStageEntry(block);

            this.broadcastStage(
                    block,
                    entry == null ? -1 : entry.getStage()
            );
        }

        private void addSpeedModifier() {
            final AttributeInstance attributeInstance =
                    this.serverPlayer.getBukkitEntity().getAttribute(Attribute.GENERIC_ATTACK_SPEED);

            if (attributeInstance != null) {
                attributeInstance.removeModifier(BREAK_SPEED_MODIFIER);
                attributeInstance.addTransientModifier(BREAK_SPEED_MODIFIER);
            }
        }

        private void removeSpeedModifier() {
            final AttributeInstance attributeInstance =
                    this.serverPlayer.getBukkitEntity().getAttribute(Attribute.GENERIC_ATTACK_SPEED);

            if (attributeInstance != null) {
                attributeInstance.removeModifier(BREAK_SPEED_MODIFIER);
            }
        }

        private void handleNoteBlock(final @NotNull NoteBlock noteBlock) {
            final Player player = this.serverPlayer.getBukkitEntity();
            final Location center = this.position.center().toLocation();
            final DiggingMap.Entry entry = DiggingMap.Entry.create(player);

            final CustomBlockData customBlockData =
                    CustomBlockRegistry
                    .fromNoteBlock(noteBlock)
                    .orElse(CustomBlockData.defaultData());
            final SoundGroup soundGroup = customBlockData.getSoundGroup();
            final float digSpeed = customBlockData.getBlockSettings().calculateDigSpeed(player);

            this.module.getCache().getDiggingMap().put(this.block, entry.setTaskId(
                    this.module.runTaskTimer(new Runnable() {
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

                                if (progressInStage > SharedConstants.FINAL_DESTROY_STAGE) {
                                    Handler.this.finish();
                                    new CustomBlock(block, customBlockData)
                                            .destroy(module, player);
                                } else if (entry.isStageTheBiggest(module, block)) {
                                    Handler.this.broadcastStage(block, progressInStage);
                                }
                            }
                        }
                    }, 0L, 1L).getTaskId())
            );
        }

        private void handleWoodenBlock() {
            final DiggingMap.Entry entry = DiggingMap.Entry.create(this.serverPlayer.getBukkitEntity());
            final Location center = this.position.center().toLocation();

            this.module.getCache().getDiggingMap().put(this.block, entry.setTaskId(
                    this.module.runTaskTimer(new Runnable() {
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
