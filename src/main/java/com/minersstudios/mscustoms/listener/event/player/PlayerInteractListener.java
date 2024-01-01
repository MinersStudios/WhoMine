package com.minersstudios.mscustoms.listener.event.player;

import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.mscore.location.MSPosition;
import com.minersstudios.mscore.utility.BlockUtils;
import com.minersstudios.mscore.utility.CoreProtectUtils;
import com.minersstudios.mscore.utility.MSDecorUtils;
import com.minersstudios.mscore.utility.PlayerUtils;
import com.minersstudios.mscustoms.MSCustoms;
import com.minersstudios.mscustoms.custom.block.CustomBlock;
import com.minersstudios.mscustoms.custom.block.CustomBlockData;
import com.minersstudios.mscustoms.custom.block.CustomBlockRegistry;
import com.minersstudios.mscustoms.custom.block.params.BlockSettings;
import com.minersstudios.mscustoms.custom.block.params.PlacingType;
import com.minersstudios.mscustoms.custom.block.params.settings.Placing;
import com.minersstudios.mscustoms.custom.decor.CustomDecor;
import com.minersstudios.mscustoms.custom.decor.CustomDecorData;
import com.minersstudios.mscustoms.event.block.CustomBlockRightClickEvent;
import com.minersstudios.mscustoms.event.decor.CustomDecorClickEvent;
import com.minersstudios.mscustoms.utility.UseBucketsAndSpawnableItems;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.data.*;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.block.data.type.Slab;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

@EventListener
public final class PlayerInteractListener extends AbstractEventListener<MSCustoms> {
    private static final BlockFace[] FACES = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
    private static final Set<Material> IGNORABLE_MATERIALS = EnumSet.of(
            //<editor-fold desc="Ignorable materials" defaultstate="collapsed">
            Material.ANVIL,
            Material.CHIPPED_ANVIL,
            Material.DAMAGED_ANVIL,
            Material.LECTERN,
            Material.HOPPER,
            Material.DISPENSER,
            Material.DROPPER,
            Material.OBSERVER,
            Material.PISTON,
            Material.STICKY_PISTON,
            Material.COMPARATOR,
            Material.REPEATER,
            Material.WHITE_GLAZED_TERRACOTTA,
            Material.ORANGE_GLAZED_TERRACOTTA,
            Material.MAGENTA_GLAZED_TERRACOTTA,
            Material.LIGHT_BLUE_GLAZED_TERRACOTTA,
            Material.YELLOW_GLAZED_TERRACOTTA,
            Material.LIME_GLAZED_TERRACOTTA,
            Material.PINK_GLAZED_TERRACOTTA,
            Material.GRAY_GLAZED_TERRACOTTA,
            Material.LIGHT_GRAY_GLAZED_TERRACOTTA,
            Material.CYAN_GLAZED_TERRACOTTA,
            Material.PURPLE_GLAZED_TERRACOTTA,
            Material.BLUE_GLAZED_TERRACOTTA,
            Material.BROWN_GLAZED_TERRACOTTA,
            Material.GREEN_GLAZED_TERRACOTTA,
            Material.RED_GLAZED_TERRACOTTA,
            Material.BLACK_GLAZED_TERRACOTTA,
            Material.WHITE_BED,
            Material.ORANGE_BED,
            Material.MAGENTA_BED,
            Material.LIGHT_BLUE_BED,
            Material.YELLOW_BED,
            Material.LIME_BED,
            Material.PINK_BED,
            Material.GRAY_BED,
            Material.LIGHT_GRAY_BED,
            Material.CYAN_BED,
            Material.PURPLE_BED,
            Material.BLUE_BED,
            Material.BROWN_BED,
            Material.GREEN_BED,
            Material.RED_BED,
            Material.BLACK_BED,
            Material.STONECUTTER,
            Material.CHEST,
            Material.TRAPPED_CHEST,
            Material.ENDER_CHEST,
            Material.BARREL,
            Material.BLAST_FURNACE,
            Material.SMOKER,
            Material.LOOM,
            Material.FURNACE,
            Material.BEEHIVE,
            Material.BEE_NEST,
            Material.END_PORTAL_FRAME
            //</editor-fold>
    );
    private static final Set<Material> SPAWNABLE_ITEMS = EnumSet.of(
            //<editor-fold desc="Non-block buckets and spawnable items" defaultstate="collapsed">
            Material.BUCKET,
            Material.LAVA_BUCKET,
            Material.WATER_BUCKET,
            Material.AXOLOTL_BUCKET,
            Material.TROPICAL_FISH_BUCKET,
            Material.COD_BUCKET,
            Material.SALMON_BUCKET,
            Material.PUFFERFISH_BUCKET,
            Material.TADPOLE_BUCKET,
            Material.PAINTING,
            Material.ITEM_FRAME,
            Material.GLOW_ITEM_FRAME,
            Material.OAK_BOAT,
            Material.SPRUCE_BOAT,
            Material.BIRCH_BOAT,
            Material.JUNGLE_BOAT,
            Material.ACACIA_BOAT,
            Material.DARK_OAK_BOAT,
            Material.MANGROVE_BOAT,
            Material.OAK_CHEST_BOAT,
            Material.SPRUCE_CHEST_BOAT,
            Material.BIRCH_CHEST_BOAT,
            Material.JUNGLE_CHEST_BOAT,
            Material.ACACIA_CHEST_BOAT,
            Material.DARK_OAK_CHEST_BOAT,
            Material.MANGROVE_CHEST_BOAT
            //</editor-fold>
    );
    private static final Set<UUID> HAND_HANDLER = new ObjectOpenHashSet<>();

    @EventHandler(ignoreCancelled = true)
    public void onDecorPlayerInteract(final @NotNull PlayerInteractEvent event) {
        final Block block = event.getClickedBlock();
        final EquipmentSlot hand = event.getHand();

        if (
                block == null
                || hand == null
        ) {
            return;
        }

        final Player player = event.getPlayer();
        final Material blockType = block.getType();
        final GameMode gameMode = player.getGameMode();

        switch (event.getAction()) {
            case LEFT_CLICK_BLOCK -> {
                if (
                        MSDecorUtils.isCustomDecorMaterial(blockType)
                                && gameMode != GameMode.ADVENTURE
                                && gameMode != GameMode.SPECTATOR
                ) {
                    if (
                            (
                                    player.isSneaking()
                                            && gameMode == GameMode.SURVIVAL
                            )
                                    || gameMode == GameMode.CREATIVE
                    ) {
                        CustomDecor.destroyInBlock(player, block);
                    } else {
                        final Location interactedLocation = event.getInteractionPoint();

                        if (interactedLocation != null) {
                            callDecorClickEvent(
                                    player,
                                    block,
                                    hand,
                                    interactedLocation.toVector(),
                                    CustomDecorClickEvent.ClickType.LEFT_CLICK
                            );
                        }
                    }
                }
            }
            case RIGHT_CLICK_BLOCK -> {
                if (
                        Tag.SHULKER_BOXES.isTagged(blockType)
                                && block.getBlockData() instanceof final Directional directional
                                && MSDecorUtils.isCustomDecor(block.getRelative(directional.getFacing()))
                ) {
                    event.setCancelled(true);
                }

                if (
                        gameMode == GameMode.ADVENTURE
                                || gameMode == GameMode.SPECTATOR
                ) {
                    return;
                }

                if (
                        hand == EquipmentSlot.OFF_HAND
                                && isDoneForMainHand(player)
                ) {
                    undoneForMainHand(player);
                    return;
                }

                final BlockFace blockFace = event.getBlockFace();
                final ItemStack itemInHand = player.getInventory().getItem(hand);

                if (!MSDecorUtils.isCustomDecor(itemInHand)) {
                    final Location interactedLocation = event.getInteractionPoint();

                    if (interactedLocation != null) {
                        callDecorClickEvent(
                                player,
                                block,
                                hand,
                                interactedLocation.toVector(),
                                CustomDecorClickEvent.ClickType.RIGHT_CLICK
                        );
                    }

                    return;
                }

                final Block blockAtFace = block.getRelative(blockFace);

                if (
                        BlockUtils.isReplaceable(blockAtFace.getType())
                        && (
                                !BlockUtils.isRightClickBlock(blockType)
                                || player.isSneaking()
                                || blockType == Material.NOTE_BLOCK
                        )
                ) {
                    CustomDecorData.fromItemStack(itemInHand)
                            .ifPresent(data -> {
                                data.place(
                                        MSPosition.of(
                                                BlockUtils.isReplaceable(blockType)
                                                        ? block.getLocation()
                                                        : blockAtFace.getLocation()
                                        ),
                                        player,
                                        blockFace,
                                        hand,
                                        null
                                );
                                doneForMainHand(player);
                            });
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteract(final @NotNull PlayerInteractEvent event) {
        final Block clickedBlock = event.getClickedBlock();
        EquipmentSlot hand = event.getHand();

        if (
                clickedBlock == null
                || hand == null
                || event.getAction() != Action.RIGHT_CLICK_BLOCK
        ) {
            return;
        }

        final BlockFace blockFace = event.getBlockFace();
        final Player player = event.getPlayer();
        final Material blockType = clickedBlock.getType();
        final ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

        if (
                Tag.SHULKER_BOXES.isTagged(blockType)
                && clickedBlock.getRelative(BlockFace.UP).getType() == Material.NOTE_BLOCK
                && clickedBlock.getState() instanceof final ShulkerBox shulkerBox
                && clickedBlock.getBlockData() instanceof final Directional directional
                && BlockUtils.isReplaceable(clickedBlock.getRelative(directional.getFacing()).getType())
        ) {
            event.setCancelled(true);
            PlayerUtils.openShulkerBoxSilent(player, shulkerBox, true);
        }

        if (MSDecorUtils.isCustomDecor(itemInMainHand)) {
            return;
        }

        if (hand != EquipmentSlot.HAND && CustomBlockRegistry.isCustomBlock(itemInMainHand)) {
            hand = EquipmentSlot.HAND;
        }

        final MSCustoms plugin = this.getPlugin();
        final Block blockAtFace = clickedBlock.getRelative(blockFace);
        final ItemStack itemInHand = player.getInventory().getItem(hand);
        final Location interactionPoint = getInteractionPoint(player.getEyeLocation(), 8);
        final boolean validGameMode =
                player.getGameMode() != GameMode.ADVENTURE
                && player.getGameMode() != GameMode.SPECTATOR;

        if (
                clickedBlock.getBlockData() instanceof final NoteBlock noteBlock
                && validGameMode
                && !itemInHand.getType().isAir()
                && (
                        hand == EquipmentSlot.HAND
                        || hand == EquipmentSlot.OFF_HAND
                )
                && !CustomBlockRegistry.isCustomBlock(itemInHand)
                && interactionPoint != null
        ) {
            final CustomBlockData clickedCustomBlockData = CustomBlockRegistry.fromNoteBlock(noteBlock).orElseThrow();

            if (blockType == Material.NOTE_BLOCK) {
                final CustomBlock customBlock = new CustomBlock(clickedBlock, clickedCustomBlockData);
                final CustomBlockRightClickEvent rightClickEvent = new CustomBlockRightClickEvent(customBlock, player, hand, blockFace, interactionPoint);

                plugin.getServer().getPluginManager().callEvent(rightClickEvent);

                if (rightClickEvent.isCancelled()) {
                    return;
                }
            }

            useItemInHand(player, interactionPoint, hand, itemInHand, blockFace, blockAtFace, clickedCustomBlockData);
        }

        if (
                CustomBlockRegistry.isCustomBlock(itemInHand)
                && (event.getHand() == EquipmentSlot.HAND || hand == EquipmentSlot.OFF_HAND)
                && BlockUtils.isReplaceable(blockAtFace.getType())
                && validGameMode
                && interactionPoint != null
        ) {
            if (
                    BlockUtils.isRightClickBlock(clickedBlock.getType())
                    && clickedBlock.getType() != Material.NOTE_BLOCK
                    && !player.isSneaking()
            ) {
                return;
            }

            final Block replaceableBlock =
                    BlockUtils.isReplaceable(clickedBlock.getType())
                    ? clickedBlock
                    : blockAtFace;

            for (
                    final var nearbyEntity
                    : replaceableBlock.getWorld().getNearbyEntities(
                            replaceableBlock.getLocation().toCenterLocation(),
                            0.5d, 0.5d, 0.5d
                    )
            ) {
                if (!BlockUtils.isIgnorableEntity(nearbyEntity.getType())) {
                    return;
                }
            }

            final CustomBlockData customBlockData = CustomBlockRegistry.fromItemStack(itemInHand).orElseThrow();
            final BlockSettings blockSettings = customBlockData.getBlockSettings();
            final Placing placing = blockSettings.getPlacing();
            final PlacingType placingType = placing.getType();
            final CustomBlock customBlock = new CustomBlock(replaceableBlock, customBlockData);

            if (placingType instanceof PlacingType.Default) {
                customBlock.place(plugin, player, hand);
            } else if (placingType instanceof PlacingType.Directional) {
                customBlock.place(plugin, player, hand, blockFace, null);
            } else if (placingType instanceof final PlacingType.Orientable orientable) {
                final Location playerLocation = player.getLocation();
                final float yaw = playerLocation.getYaw();
                final float pitch = playerLocation.getPitch();
                final var blockAxes = orientable.getMap().keySet();

                customBlock.place(plugin, player, hand, null, getAxisByEyes(yaw, pitch, blockAxes));
            }
        }
    }

    private static void useItemInHand(
            final @NotNull Player player,
            final @NotNull Location interactionPoint,
            final @NotNull EquipmentSlot hand,
            final @NotNull ItemStack itemInHand,
            final @NotNull BlockFace blockFace,
            final @NotNull Block blockAtFace,
            final @NotNull CustomBlockData clickedCustomBlockData
    ) {
        final BlockData materialBlockData = getBlockDataByMaterial(itemInHand.getType());

        if (SPAWNABLE_ITEMS.contains(itemInHand.getType())) {
            new UseBucketsAndSpawnableItems(player, blockAtFace, blockFace, itemInHand);
        } else if (Tag.SLABS.isTagged(itemInHand.getType())) {
            final Material itemMaterial = itemInHand.getType();
            boolean placeDouble = true;

            if (blockAtFace.getType() != itemMaterial) {
                useOn(player, hand, itemInHand, blockAtFace);
                placeDouble = false;
            }

            if (!(blockAtFace.getBlockData() instanceof final Slab slab)) {
                return;
            }

            if (
                    placeDouble
                    && blockAtFace.getType() == itemMaterial
            ) {
                final SoundGroup soundGroup = slab.getSoundGroup();

                slab.setType(Slab.Type.DOUBLE);
                blockAtFace.getWorld().playSound(
                        blockAtFace.getLocation(),
                        soundGroup.getPlaceSound(),
                        SoundCategory.BLOCKS,
                        soundGroup.getVolume(),
                        soundGroup.getPitch()
                );

                if (player.getGameMode() == GameMode.SURVIVAL) {
                    itemInHand.setAmount(itemInHand.getAmount() - 1);
                }
            } else if (
                    blockFace == BlockFace.DOWN
                    || interactionPoint.getY() > 0.5d
                    && interactionPoint.getY() < 1.0d
                    && blockAtFace.getType() == itemMaterial
            ) {
                slab.setType(Slab.Type.TOP);
            } else if (
                    blockFace == BlockFace.UP
                    || interactionPoint.getY() < 0.5d
                    && interactionPoint.getY() > 0.0d
                    && blockAtFace.getType() == itemMaterial
            ) {
                slab.setType(Slab.Type.BOTTOM);
            }

            blockAtFace.setBlockData(slab);
        }

        if (!BlockUtils.isReplaceable(blockAtFace.getType())) {
            return;
        }

        if (clickedCustomBlockData.getBlockSettings().getPlacing().isPlaceable(itemInHand.getType())) {
            blockAtFace.setType(itemInHand.getType(), false);
            itemInHand.setAmount(itemInHand.getAmount() - 1);
        }

        if (materialBlockData instanceof FaceAttachable) {
            useOn(player, hand, itemInHand, blockAtFace);

            final FaceAttachable faceAttachable = (FaceAttachable) blockAtFace.getBlockData();

            switch (blockFace) {
                case UP ->   faceAttachable.setAttachedFace(FaceAttachable.AttachedFace.FLOOR);
                case DOWN -> faceAttachable.setAttachedFace(FaceAttachable.AttachedFace.CEILING);
                default ->   faceAttachable.setAttachedFace(FaceAttachable.AttachedFace.WALL);
            }

            if (
                    faceAttachable instanceof final Directional directional
                    && faceAttachable.getAttachedFace() == FaceAttachable.AttachedFace.WALL
            ) {
                directional.setFacing(blockFace);
                blockAtFace.setBlockData(directional);
                return;
            }

            blockAtFace.setBlockData(faceAttachable);
        } else if (materialBlockData instanceof Orientable) {
            useOn(player, hand, itemInHand, blockAtFace);

            if (blockAtFace.getBlockData() instanceof final Orientable orientable) {
                final Location location = player.getLocation();

                orientable.setAxis(getAxis(location.getYaw(), location.getPitch()));
                blockAtFace.setBlockData(orientable);
            }
        } else if (
                materialBlockData instanceof final Directional directionalMaterial
                && (
                        directionalMaterial.getFaces().contains(blockFace)
                        || Tag.STAIRS.isTagged(itemInHand.getType())
                        || Tag.TRAPDOORS.isTagged(itemInHand.getType())
                ) && !IGNORABLE_MATERIALS.contains(itemInHand.getType())
        ) {
            useOn(player, hand, itemInHand, blockAtFace);

            if (!(blockAtFace.getBlockData() instanceof final Directional directional)) {
                return;
            }

            if (!(directional instanceof Bisected bisected)) {
                directional.setFacing(blockFace);
            } else {
                final double y = interactionPoint.getY();

                bisected.setHalf(
                        y == 1.0d
                        || y < 0.5d
                        && y > 0.0d
                                ? Bisected.Half.BOTTOM
                                : Bisected.Half.TOP
                );
                blockAtFace.setBlockData(bisected);
                return;
            }

            blockAtFace.setBlockData(directional);
        } else if (
                !blockAtFace.getType().isSolid()
                && blockAtFace.getType() != itemInHand.getType()
        ) {
            useOn(player, hand, itemInHand, blockAtFace);
        }
    }

    private static void useOn(
            final @NotNull Player player,
            final @NotNull EquipmentSlot hand,
            final @NotNull ItemStack itemInHand,
            final @NotNull Block blockAtFace
    ) {
        final ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        final Material itemType = itemInHand.getType();
        final BlockHitResult blockHitResult = new BlockHitResult(
                serverPlayer.getEyePosition(),
                serverPlayer.getDirection(),
                ((CraftBlock) blockAtFace).getPosition(),
                false
        );
        final UseOnContext useOnContext = new UseOnContext(
                serverPlayer,
                hand == EquipmentSlot.HAND ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND,
                blockHitResult
        );

        if (
                !itemType.isBlock()
                || CraftItemStack.asNMSCopy(itemInHand).useOn(useOnContext) == InteractionResult.FAIL
        ) {
            return;
        }

        final Location blockLocation = blockAtFace.getLocation();
        final BlockData blockData = itemType.createBlockData();
        final SoundGroup soundGroup = blockData.getSoundGroup();

        CoreProtectUtils.logPlacement(player.getName(), blockLocation, itemType, blockData);
        blockAtFace.getWorld().playSound(
                blockLocation,
                soundGroup.getPlaceSound(),
                SoundCategory.BLOCKS,
                soundGroup.getVolume(),
                serverPlayer.getRandom().nextFloat() * 0.1f + soundGroup.getPitch()
        );
    }

    public static @Nullable Location getInteractionPoint(
            final @NotNull Location location,
            final int maxDistance
    ) {
        final World world = location.getWorld();

        if (world == null) {
            return null;
        }

        final RayTraceResult rayTraceResult =
                world.rayTraceBlocks(
                        location,
                        location.getDirection(),
                        maxDistance,
                        FluidCollisionMode.NEVER,
                        true
                );

        return rayTraceResult == null
                || rayTraceResult.getHitBlock() == null
                ? null
                : rayTraceResult.getHitPosition().subtract(
                        rayTraceResult.getHitBlock().getLocation().toVector()
                ).toLocation(world);
    }

    private static @NotNull Axis getAxis(
            final double x,
            final double y
    ) {
        return x == 0.0d || x == 1.0d
                ? Axis.X
                : y == 0.0d || y == 1.0d
                ? Axis.Y
                : Axis.Z;
    }

    private static @NotNull Axis getAxisByEyes(
            final float yaw,
            final float pitch,
            final @NotNull Set<Axis> axes
    ) {
        return !(pitch >= -45.0f && pitch <= 45.0f) && axes.contains(Axis.Y)
                ? Axis.Y
                : switch (getBlockFaceByYaw(yaw)) {
                        case NORTH, SOUTH -> Axis.X;
                        default -> Axis.Z;
                };
    }

    private static @NotNull BlockFace getBlockFaceByYaw(final float yaw) {
        return FACES[Math.round(yaw / 90.0f) & 3];
    }

    private static @Nullable BlockData getBlockDataByMaterial(final @NotNull Material material) {
        return switch (material) {
            case REDSTONE -> Material.REDSTONE_WIRE.createBlockData();
            case STRING ->   Material.TRIPWIRE.createBlockData();
            default ->       material.isBlock() ? material.createBlockData() : null;
        };
    }

    private static void doneForMainHand(final @NotNull Player player) {
        HAND_HANDLER.add(player.getUniqueId());
    }

    private static void undoneForMainHand(final @NotNull Player player) {
        HAND_HANDLER.remove(player.getUniqueId());
    }

    private static boolean isDoneForMainHand(final @NotNull Player player) {
        return HAND_HANDLER.contains(player.getUniqueId());
    }

    private static void callDecorClickEvent(
            final @NotNull Player player,
            final @NotNull Block block,
            final @NotNull EquipmentSlot hand,
            final @NotNull Vector interactedPosition,
            final @NotNull CustomDecorClickEvent.ClickType clickType
    ) {
        final PluginManager pluginManager = player.getServer().getPluginManager();

        for (
                final var interaction :
                MSDecorUtils.getNearbyInteractions(
                        MSPosition.of(block.getLocation().toCenterLocation())
                )
        ) {
            CustomDecor.fromInteraction(interaction)
                    .ifPresent(
                            customDecor -> {
                                final CustomDecorClickEvent clickEvent = new CustomDecorClickEvent(
                                        customDecor,
                                        player,
                                        hand,
                                        interactedPosition,
                                        interaction,
                                        clickType
                                );

                                pluginManager.callEvent(clickEvent);

                                if (!clickEvent.isCancelled()) {
                                    customDecor.getData().doClickAction(clickEvent);
                                    doneForMainHand(player);
                                }
                            }
                    );
        }
    }
}
