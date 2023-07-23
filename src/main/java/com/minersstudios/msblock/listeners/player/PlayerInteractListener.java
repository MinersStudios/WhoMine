package com.minersstudios.msblock.listeners.player;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.minersstudios.msblock.MSBlock;
import com.minersstudios.msblock.customblock.CustomBlock;
import com.minersstudios.msblock.customblock.CustomBlockData;
import com.minersstudios.msblock.events.CustomBlockRightClickEvent;
import com.minersstudios.msblock.utils.PlayerUtils;
import com.minersstudios.msblock.utils.UseBucketsAndSpawnableItems;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.utils.BlockUtils;
import com.minersstudios.mscore.utils.MSBlockUtils;
import com.minersstudios.mscore.utils.MSDecorUtils;
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
import org.bukkit.craftbukkit.v1_20_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.SecureRandom;
import java.util.Set;

@MSListener
public class PlayerInteractListener extends AbstractMSListener {
    private static final SecureRandom RANDOM = new SecureRandom();

    private static final BlockFace[] FACES = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
    private static final ImmutableSet<EntityType> IGNORABLE_ENTITIES = Sets.immutableEnumSet(
            //<editor-fold desc="Entities to be ignored when placing a block on their location">
            EntityType.DROPPED_ITEM,
            EntityType.ITEM_FRAME,
            EntityType.GLOW_ITEM_FRAME,
            EntityType.LIGHTNING,
            EntityType.LLAMA_SPIT,
            EntityType.EXPERIENCE_ORB,
            EntityType.THROWN_EXP_BOTTLE,
            EntityType.EGG,
            EntityType.SPLASH_POTION,
            EntityType.FIREWORK,
            EntityType.FIREBALL,
            EntityType.FISHING_HOOK,
            EntityType.SMALL_FIREBALL,
            EntityType.SNOWBALL,
            EntityType.TRIDENT,
            EntityType.WITHER_SKULL,
            EntityType.DRAGON_FIREBALL,
            EntityType.AREA_EFFECT_CLOUD,
            EntityType.ARROW,
            EntityType.SPECTRAL_ARROW,
            EntityType.ENDER_PEARL,
            EntityType.EVOKER_FANGS,
            EntityType.LEASH_HITCH
            //</editor-fold>
    );
    private static final ImmutableSet<Material> IGNORABLE_MATERIALS = Sets.immutableEnumSet(
            //<editor-fold desc="Ignorable materials">
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
    private static final ImmutableSet<Material> SPAWNABLE_ITEMS = Sets.immutableEnumSet(
            //<editor-fold desc="Non-block buckets and spawnable items">
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        EquipmentSlot hand = event.getHand();

        if (
                clickedBlock == null
                || hand == null
                || event.getAction() != Action.RIGHT_CLICK_BLOCK
        ) return;

        BlockFace blockFace = event.getBlockFace();
        Player player = event.getPlayer();
        Material blockType = clickedBlock.getType();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

        if (
                event.getAction() == Action.RIGHT_CLICK_BLOCK
                && Tag.SHULKER_BOXES.isTagged(blockType)
                && clickedBlock.getRelative(BlockFace.UP).getType() == Material.NOTE_BLOCK
                && clickedBlock.getState() instanceof ShulkerBox shulkerBox
                && clickedBlock.getBlockData() instanceof Directional directional
                && BlockUtils.REPLACE.contains(clickedBlock.getRelative(directional.getFacing()).getType())
        ) {
            event.setCancelled(true);
            PlayerUtils.openShulkerBoxWithoutAnimation(player, shulkerBox);
        }

        if (MSDecorUtils.isCustomDecor(itemInMainHand)) return;
        if (hand != EquipmentSlot.HAND && MSBlockUtils.isCustomBlock(itemInMainHand)) {
            hand = EquipmentSlot.HAND;
        }

        Block blockAtFace = clickedBlock.getRelative(blockFace);
        ItemStack itemInHand = player.getInventory().getItem(hand);
        Location interactionPoint = getInteractionPoint(player.getEyeLocation(), 8);
        boolean validGameMode = player.getGameMode() != GameMode.ADVENTURE && player.getGameMode() != GameMode.SPECTATOR;

        if (
                clickedBlock.getBlockData() instanceof NoteBlock noteBlock
                && validGameMode
                && !itemInHand.getType().isAir()
                && (hand == EquipmentSlot.HAND || hand == EquipmentSlot.OFF_HAND)
                && !MSBlockUtils.isCustomBlock(itemInHand)
                && interactionPoint != null
        ) {
            CustomBlockData clickedCustomBlockData = CustomBlockData.fromNoteBlock(noteBlock);

            if (blockType == Material.NOTE_BLOCK) {
                CustomBlock customBlock = new CustomBlock(clickedBlock, player, clickedCustomBlockData);
                CustomBlockRightClickEvent rightClickEvent = new CustomBlockRightClickEvent(customBlock, player, hand, blockFace, interactionPoint);

                Bukkit.getPluginManager().callEvent(rightClickEvent);

                if (rightClickEvent.isCancelled()) return;
            }

            useItemInHand(player, interactionPoint, hand, itemInHand, blockFace, blockAtFace, clickedCustomBlockData);
        }

        if (
                MSBlockUtils.isCustomBlock(itemInHand)
                && (event.getHand() == EquipmentSlot.HAND || hand == EquipmentSlot.OFF_HAND)
                && BlockUtils.REPLACE.contains(blockAtFace.getType())
                && validGameMode
                && interactionPoint != null
        ) {
            if (
                    ((clickedBlock.getType().isInteractable()
                    && !Tag.STAIRS.isTagged(clickedBlock.getType()))
                    && clickedBlock.getType() != Material.NOTE_BLOCK)
                    && !player.isSneaking()
            ) return;

            Block replaceableBlock =
                    BlockUtils.REPLACE.contains(clickedBlock.getType())
                    ? clickedBlock
                    : blockAtFace;

            for (var nearbyEntity : replaceableBlock.getWorld().getNearbyEntities(replaceableBlock.getLocation().toCenterLocation(), 0.5d, 0.5d, 0.5d)) {
                if (!IGNORABLE_ENTITIES.contains(nearbyEntity.getType())) return;
            }

            CustomBlockData customBlockData = CustomBlockData.fromCustomModelData(itemInHand.getItemMeta().getCustomModelData());
            CustomBlockData.PlacingType placingType = customBlockData.getPlacingType();
            CustomBlock customBlock = new CustomBlock(replaceableBlock, player, customBlockData);

            if (placingType == null) {
                customBlock.setCustomBlock(hand);
                return;
            }

            Location playerLocation = player.getLocation();
            float yaw = playerLocation.getYaw();
            float pitch = playerLocation.getPitch();
            var blockFaces = customBlockData.getFaces();
            var blockAxes = customBlockData.getAxes();

            if (blockFaces != null) {
                switch (placingType) {
                    case BY_BLOCK_FACE -> customBlock.setCustomBlock(hand, blockFace, null);
                    case BY_EYE_POSITION -> customBlock.setCustomBlock(hand, getBlockFaceByEyes(yaw, pitch, blockFaces), null);
                }
            } else if (blockAxes != null) {
                switch (placingType) {
                    case BY_BLOCK_FACE -> customBlock.setCustomBlock(hand, null, getAxis(interactionPoint.getX(), interactionPoint.getZ()));
                    case BY_EYE_POSITION -> customBlock.setCustomBlock(hand, null, getAxisByEyes(yaw, pitch, blockAxes));
                }
            }
        }
    }

    private static void useItemInHand(
            @NotNull Player player,
            @NotNull Location interactionPoint,
            @NotNull EquipmentSlot hand,
            @NotNull ItemStack itemInHand,
            @NotNull BlockFace blockFace,
            @NotNull Block blockAtFace,
            @NotNull CustomBlockData clickedCustomBlockData
    ) {
        BlockData materialBlockData = BlockUtils.getBlockDataByMaterial(itemInHand.getType());

        if (SPAWNABLE_ITEMS.contains(itemInHand.getType())) {
            new UseBucketsAndSpawnableItems(player, blockAtFace, blockFace, itemInHand);
        } else if (Tag.SLABS.isTagged(itemInHand.getType())) {
            boolean placeDouble = true;
            Material itemMaterial = itemInHand.getType();

            if (blockAtFace.getType() != itemMaterial) {
                useOn(player, hand, itemInHand, blockAtFace);
                placeDouble = false;
            }

            if (!(blockAtFace.getBlockData() instanceof Slab slab)) return;

            if (
                    placeDouble
                    && blockAtFace.getType() == itemMaterial
            ) {
                SoundGroup soundGroup = slab.getSoundGroup();

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

        if (!BlockUtils.REPLACE.contains(blockAtFace.getType())) return;

        var placeableMaterials = clickedCustomBlockData.getPlaceableMaterials();
        if (
                placeableMaterials != null
                && placeableMaterials.contains(itemInHand.getType())
        ) {
            blockAtFace.setType(itemInHand.getType(), false);
            itemInHand.setAmount(itemInHand.getAmount() - 1);
        }

        if (materialBlockData instanceof FaceAttachable) {
            useOn(player, hand, itemInHand, blockAtFace);
            FaceAttachable faceAttachable = (FaceAttachable) blockAtFace.getBlockData();

            switch (blockFace) {
                case UP -> faceAttachable.setAttachedFace(FaceAttachable.AttachedFace.FLOOR);
                case DOWN -> faceAttachable.setAttachedFace(FaceAttachable.AttachedFace.CEILING);
                default -> faceAttachable.setAttachedFace(FaceAttachable.AttachedFace.WALL);
            }

            if (
                    faceAttachable instanceof Directional directional
                    && faceAttachable.getAttachedFace() == FaceAttachable.AttachedFace.WALL
            ) {
                directional.setFacing(blockFace);
                blockAtFace.setBlockData(directional);
                return;
            }

            blockAtFace.setBlockData(faceAttachable);
        } else if (materialBlockData instanceof Orientable) {
            useOn(player, hand, itemInHand, blockAtFace);

            if (blockAtFace.getBlockData() instanceof Orientable orientable) {
                Location location = player.getLocation();

                orientable.setAxis(getAxis(location.getYaw(), location.getPitch()));
                blockAtFace.setBlockData(orientable);
            }
        } else if (
                materialBlockData instanceof Directional directionalMaterial
                && (
                        directionalMaterial.getFaces().contains(blockFace)
                        || Tag.STAIRS.isTagged(itemInHand.getType())
                        || Tag.TRAPDOORS.isTagged(itemInHand.getType())
                ) && !IGNORABLE_MATERIALS.contains(itemInHand.getType())
        ) {
            useOn(player, hand, itemInHand, blockAtFace);

            if (!(blockAtFace.getBlockData() instanceof Directional directional)) return;

            if (!(directional instanceof Bisected bisected)) {
                directional.setFacing(blockFace);
            } else {
                double y = interactionPoint.getY();

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
        } else if (!blockAtFace.getType().isSolid() && blockAtFace.getType() != itemInHand.getType()) {
            useOn(player, hand, itemInHand, blockAtFace);
        }
    }

    private static void useOn(
            @NotNull Player player,
            @NotNull EquipmentSlot hand,
            @NotNull ItemStack itemInHand,
            @NotNull Block blockAtFace
    ) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        Material itemType = itemInHand.getType();
        BlockHitResult blockHitResult = new BlockHitResult(
                serverPlayer.getEyePosition(),
                serverPlayer.getDirection(),
                ((CraftBlock) blockAtFace).getPosition(),
                false
        );
        UseOnContext useOnContext = new UseOnContext(
                serverPlayer,
                hand == EquipmentSlot.HAND ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND,
                blockHitResult
        );

        if (
                !itemType.isBlock()
                || CraftItemStack.asNMSCopy(itemInHand).useOn(useOnContext) == InteractionResult.FAIL
        ) return;

        Location blockLocation = blockAtFace.getLocation();
        BlockData blockData = itemType.createBlockData();
        SoundGroup soundGroup = blockData.getSoundGroup();

        MSBlock.getCoreProtectAPI().logPlacement(player.getName(), blockLocation, itemType, blockData);
        blockAtFace.getWorld().playSound(
                blockLocation,
                soundGroup.getPlaceSound(),
                SoundCategory.BLOCKS,
                soundGroup.getVolume(),
                RANDOM.nextFloat() * 0.1f + soundGroup.getPitch()
        );
    }

    public static @Nullable Location getInteractionPoint(
            @NotNull Location location,
            int maxDistance
    ) {
        World world = location.getWorld();
        if (world == null) return null;
        RayTraceResult rayTraceResult = world.rayTraceBlocks(location, location.getDirection(), maxDistance, FluidCollisionMode.NEVER, true);
        return rayTraceResult == null || rayTraceResult.getHitBlock() == null
                ? null
                : rayTraceResult.getHitPosition().subtract(
                rayTraceResult.getHitBlock().getLocation().toVector()
        ).toLocation(world);
    }

    private static @NotNull Axis getAxis(
            double x,
            double y
    ) {
        return x == 0.0d || x == 1.0d
                ? Axis.X
                : y == 0.0d || y == 1.0d
                ? Axis.Y
                : Axis.Z;
    }

    private static @NotNull Axis getAxisByEyes(
            float yaw,
            float pitch,
            @NotNull Set<Axis> axes
    ) {
        return !(pitch >= -45.0f && pitch <= 45.0f) && axes.contains(Axis.Y)
                ? Axis.Y
                : switch (getBlockFaceByYaw(yaw)) {
                        case NORTH, SOUTH -> Axis.X;
                        default -> Axis.Z;
                };
    }

    private static @NotNull BlockFace getBlockFaceByEyes(
            float yaw,
            float pitch,
            @NotNull Set<BlockFace> blockFaces
    ) {
        return !(pitch >= -45.0f) && blockFaces.contains(BlockFace.DOWN)
                ? BlockFace.DOWN
                : !(pitch <= 45.0f) && blockFaces.contains(BlockFace.UP)
                ? BlockFace.UP
                : getBlockFaceByYaw(yaw);
    }

    private static @NotNull BlockFace getBlockFaceByYaw(float yaw) {
        return FACES[Math.round(yaw / 90f) & 3];
    }
}
