package com.minersstudios.mscore.util;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import io.papermc.paper.math.FinePosition;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import org.bukkit.Material;
import org.bukkit.SoundGroup;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_20_R2.CraftSoundGroup;
import org.bukkit.craftbukkit.v1_20_R2.block.CraftBlock;
import org.bukkit.entity.EntityType;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Utility class for blocks
 */
public final class BlockUtils {
    public static final Set<Material> REPLACEABLE_BLOCKS = Sets.immutableEnumSet(
            //<editor-fold desc="Replace materials" defaultstate="collapsed">
            Material.AIR,
            Material.CAVE_AIR,
            Material.VOID_AIR,
            Material.WATER,
            Material.LAVA,
            Material.GRASS,
            Material.FERN,
            Material.SEAGRASS,
            Material.TALL_GRASS,
            Material.LARGE_FERN,
            Material.TALL_SEAGRASS,
            Material.VINE,
            Material.SNOW,
            Material.FIRE
            //</editor-fold>
    );
    public static final Set<net.minecraft.world.level.block.Block> NMS_REPLACEABLE_BLOCKS = ImmutableSet.of(
            //<editor-fold desc="Replace NMS blocks" defaultstate="collapsed">
            Blocks.AIR,
            Blocks.CAVE_AIR,
            Blocks.VOID_AIR,
            Blocks.WATER,
            Blocks.LAVA,
            Blocks.GRASS,
            Blocks.FERN,
            Blocks.SEAGRASS,
            Blocks.TALL_GRASS,
            Blocks.LARGE_FERN,
            Blocks.TALL_SEAGRASS,
            Blocks.VINE,
            Blocks.SNOW,
            Blocks.FIRE
            //</editor-fold>
    );
    public static final Set<EntityType> IGNORABLE_ENTITIES = Sets.immutableEnumSet(
            //<editor-fold desc="Entities to be ignored when placing a block on their location" defaultstate="collapsed">
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
    public static final Set<net.minecraft.world.entity.EntityType<?>> NMS_IGNORABLE_ENTITIES = ImmutableSet.of(
            //<editor-fold desc="NMS entities to be ignored when placing a block on their location" defaultstate="collapsed">
            net.minecraft.world.entity.EntityType.ITEM,
            net.minecraft.world.entity.EntityType.ITEM_FRAME,
            net.minecraft.world.entity.EntityType.GLOW_ITEM_FRAME,
            net.minecraft.world.entity.EntityType.LIGHTNING_BOLT,
            net.minecraft.world.entity.EntityType.LLAMA_SPIT,
            net.minecraft.world.entity.EntityType.EXPERIENCE_ORB,
            net.minecraft.world.entity.EntityType.EXPERIENCE_BOTTLE,
            net.minecraft.world.entity.EntityType.EGG,
            net.minecraft.world.entity.EntityType.POTION,
            net.minecraft.world.entity.EntityType.FIREWORK_ROCKET,
            net.minecraft.world.entity.EntityType.FIREBALL,
            net.minecraft.world.entity.EntityType.FISHING_BOBBER,
            net.minecraft.world.entity.EntityType.SMALL_FIREBALL,
            net.minecraft.world.entity.EntityType.SNOWBALL,
            net.minecraft.world.entity.EntityType.TRIDENT,
            net.minecraft.world.entity.EntityType.WITHER_SKULL,
            net.minecraft.world.entity.EntityType.DRAGON_FIREBALL,
            net.minecraft.world.entity.EntityType.AREA_EFFECT_CLOUD,
            net.minecraft.world.entity.EntityType.ARROW,
            net.minecraft.world.entity.EntityType.SPECTRAL_ARROW,
            net.minecraft.world.entity.EntityType.ENDER_PEARL,
            net.minecraft.world.entity.EntityType.EVOKER_FANGS,
            net.minecraft.world.entity.EntityType.LEASH_KNOT
            //</editor-fold>
    );
    public static final Set<Material> BREAK_ON_BLOCK_PLACE = Sets.immutableEnumSet(
            //<editor-fold desc="Materials that will break on block place" defaultstate="collapsed">
            Material.TALL_GRASS,
            Material.LARGE_FERN,
            Material.TALL_SEAGRASS
            //</editor-fold>
    );

    public static final Set<Material> WOOD_SOUND_MATERIAL_SET;

    static {
        final SoundGroup woodSoundGroup = CraftSoundGroup.getSoundGroup(SoundType.WOOD);
        final var materialSet = new ImmutableSet.Builder<Material>();

        for (final var material : Material.values()) {
            if (
                    !material.isLegacy()
                    && material.isBlock()
                    && material.createBlockData().getSoundGroup() == woodSoundGroup
            ) {
                materialSet.add(material);
            }
        }

        WOOD_SOUND_MATERIAL_SET = materialSet.build();
    }

    @Contract(value = " -> fail")
    private BlockUtils() {
        throw new AssertionError("Utility class");
    }

    public static Block @NotNull [] getBlocks(
            final @NotNull World world,
            final @NotNull BoundingBox boundingBox
    ) {
        return getBlocks(
                world,
                (int) boundingBox.getMinX(),
                (int) boundingBox.getMinY(),
                (int) boundingBox.getMinZ(),
                (int) boundingBox.getMaxX(),
                (int) boundingBox.getMaxY(),
                (int) boundingBox.getMaxZ()
        );
    }

    @SuppressWarnings("UnstableApiUsage")
    public static Block @NotNull [] getBlocks(
            final @NotNull World world,
            final @NotNull FinePosition pos1,
            final @NotNull FinePosition pos2
    ) {
        return getBlocks(
                world,
                (int) pos1.x(),
                (int) pos1.y(),
                (int) pos1.z(),
                (int) pos2.x(),
                (int) pos2.y(),
                (int) pos2.z()
        );
    }

    public static Block @NotNull [] getBlocks(
            final @NotNull World world,
            final int x1,
            final int y1,
            final int z1,
            final int x2,
            final int y2,
            final int z2
    ) {
        final int minX = Math.min(x1, x2);
        final int minY = Math.min(y1, y2);
        final int minZ = Math.min(z1, z2);
        final int offsetX = Math.abs(x1 - x2) + 1;
        final int offsetY = Math.abs(y1 - y2) + 1;
        final int offsetZ = Math.abs(z1 - z2) + 1;
        final Block[] blocks = new Block[offsetX * offsetY * offsetZ];

        int i = 0;

        for (int x = 0; x < offsetX; ++x) {
            for (int y = 0; y < offsetY; ++y) {
                for (int z = 0; z < offsetZ; ++z) {
                    blocks[i++] = world.getBlockAt(
                            minX + x,
                            minY + y,
                            minZ + z
                    );
                }
            }
        }

        return blocks;
    }

    /**
     * Breaks top / bottom block
     *
     * @param centreBlock Block around which the blocks break
     */
    public static void removeBlocksAround(final @NotNull Block centreBlock) {
        final CraftBlock topBlock = (CraftBlock) centreBlock.getRelative(BlockFace.UP);
        final CraftBlock bottomBlock = (CraftBlock) centreBlock.getRelative(BlockFace.DOWN);

        if (BREAK_ON_BLOCK_PLACE.contains(topBlock.getType())) {
            topBlock.getHandle().destroyBlock(topBlock.getPosition(), true);
        }

        if (BREAK_ON_BLOCK_PLACE.contains(bottomBlock.getType())) {
            bottomBlock.getHandle().destroyBlock(bottomBlock.getPosition(), true);
        }
    }

    /**
     * @param material Material that will be checked
     * @return True if material has wood sound
     */
    public static boolean isWoodenSound(final @NotNull Material material) {
        return WOOD_SOUND_MATERIAL_SET.contains(material);
    }

    /**
     * @param block Block that will be checked
     * @return True if block is replaceable
     * @see #REPLACEABLE_BLOCKS
     */
    public static boolean isReplaceable(final @NotNull Block block) {
        return REPLACEABLE_BLOCKS.contains(block.getType());
    }

    /**
     * @param material Material that will be checked
     * @return True if material is replaceable
     * @see #REPLACEABLE_BLOCKS
     */
    public static boolean isReplaceable(final @NotNull Material material) {
        return REPLACEABLE_BLOCKS.contains(material);
    }

    /**
     * @param block Block that will be checked
     * @return True if block is replaceable
     * @see #NMS_REPLACEABLE_BLOCKS
     */
    public static boolean isReplaceable(final @NotNull net.minecraft.world.level.block.Block block) {
        return NMS_REPLACEABLE_BLOCKS.contains(block);
    }

    /**
     * @param entityType Entity type that will be checked
     * @return True if entity type is ignorable
     * @see #IGNORABLE_ENTITIES
     */
    public static boolean isIgnorableEntity(final @NotNull EntityType entityType) {
        return IGNORABLE_ENTITIES.contains(entityType);
    }

    /**
     * @param entityType Entity type that will be checked
     * @return True if entity type is ignorable
     * @see #NMS_IGNORABLE_ENTITIES
     */
    public static boolean isIgnorableEntity(final @NotNull net.minecraft.world.entity.EntityType<?> entityType) {
        return NMS_IGNORABLE_ENTITIES.contains(entityType);
    }
}
