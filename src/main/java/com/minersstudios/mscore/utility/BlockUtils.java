package com.minersstudios.mscore.utility;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.block.SoundType;
import org.bukkit.Material;
import org.bukkit.SoundGroup;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_20_R3.CraftSoundGroup;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntityType;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Utility class for blocks
 */
public final class BlockUtils {
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

    @Contract(" -> fail")
    private BlockUtils() throws AssertionError {
        throw new AssertionError("Utility class");
    }

    /**
     * Breaks top / bottom block
     *
     * @param centreBlock Block around which the blocks break
     */
    public static void removeBlocksAround(final @NotNull Block centreBlock) {
        final CraftBlock topBlock = (CraftBlock) centreBlock.getRelative(BlockFace.UP);
        final CraftBlock bottomBlock = (CraftBlock) centreBlock.getRelative(BlockFace.DOWN);

        if (isBreaksOnBlockPlace(topBlock.getType())) {
            topBlock.getHandle().destroyBlock(topBlock.getPosition(), true);
        }

        if (isBreaksOnBlockPlace(bottomBlock.getType())) {
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
     * @return True if block can be interacted with right click
     */
    public static boolean isRightClickBlock(final @NotNull net.minecraft.world.level.block.Block block) {
        return isRightClickBlock(block.defaultBlockState().getBukkitMaterial());
    }

    /**
     * @param block Block that will be checked
     * @return True if block can be interacted with right click
     */
    public static boolean isRightClickBlock(final @NotNull Block block) {
        return isRightClickBlock(block.getType());
    }

    /**
     * @param material Material that will be checked
     * @return True if material can be interacted with right click
     */
    public static boolean isRightClickBlock(final @NotNull Material material) {
        return switch (material) {
            //<editor-fold desc="Materials that can be interacted with right click" defaultstate="collapsed">
            case ACACIA_BUTTON,
                    ACACIA_DOOR,
                    ACACIA_FENCE_GATE,
                    ACACIA_HANGING_SIGN,
                    ACACIA_SIGN,
                    ACACIA_TRAPDOOR,
                    ACACIA_WALL_HANGING_SIGN,
                    ACACIA_WALL_SIGN,
                    ANVIL,
                    BAMBOO_BUTTON,
                    BAMBOO_DOOR,
                    BAMBOO_FENCE_GATE,
                    BAMBOO_HANGING_SIGN,
                    BAMBOO_SIGN,
                    BAMBOO_TRAPDOOR,
                    BAMBOO_WALL_HANGING_SIGN,
                    BAMBOO_WALL_SIGN,
                    BARREL,
                    BEACON,
                    BIRCH_BUTTON,
                    BIRCH_DOOR,
                    BIRCH_FENCE_GATE,
                    BIRCH_HANGING_SIGN,
                    BIRCH_SIGN,
                    BIRCH_TRAPDOOR,
                    BIRCH_WALL_HANGING_SIGN,
                    BIRCH_WALL_SIGN,
                    BLACK_BED,
                    BLACK_CANDLE_CAKE,
                    BLACK_SHULKER_BOX,
                    BLAST_FURNACE,
                    BLUE_BED,
                    BLUE_CANDLE_CAKE,
                    BLUE_SHULKER_BOX,
                    BREWING_STAND,
                    BROWN_BED,
                    BROWN_CANDLE_CAKE,
                    BROWN_SHULKER_BOX,
                    CAKE,
                    CANDLE_CAKE,
                    CARTOGRAPHY_TABLE,
                    CHAIN_COMMAND_BLOCK,
                    CHERRY_BUTTON,
                    CHERRY_DOOR,
                    CHERRY_FENCE_GATE,
                    CHERRY_HANGING_SIGN,
                    CHERRY_SIGN,
                    CHERRY_TRAPDOOR,
                    CHERRY_WALL_HANGING_SIGN,
                    CHERRY_WALL_SIGN,
                    CHEST,
                    CHIPPED_ANVIL,
                    COMMAND_BLOCK,
                    COMPARATOR,
                    CRAFTING_TABLE,
                    CRIMSON_BUTTON,
                    CRIMSON_DOOR,
                    CRIMSON_FENCE_GATE,
                    CRIMSON_HANGING_SIGN,
                    CRIMSON_SIGN,
                    CRIMSON_TRAPDOOR,
                    CRIMSON_WALL_HANGING_SIGN,
                    CRIMSON_WALL_SIGN,
                    CYAN_BED,
                    CYAN_CANDLE_CAKE,
                    CYAN_SHULKER_BOX,
                    DAMAGED_ANVIL,
                    DARK_OAK_BUTTON,
                    DARK_OAK_DOOR,
                    DARK_OAK_FENCE_GATE,
                    DARK_OAK_HANGING_SIGN,
                    DARK_OAK_SIGN,
                    DARK_OAK_TRAPDOOR,
                    DARK_OAK_WALL_HANGING_SIGN,
                    DARK_OAK_WALL_SIGN,
                    DAYLIGHT_DETECTOR,
                    DISPENSER,
                    DROPPER,
                    ENCHANTING_TABLE,
                    ENDER_CHEST,
                    FLETCHING_TABLE,
                    FURNACE,
                    GRAY_BED,
                    GRAY_CANDLE_CAKE,
                    GRAY_SHULKER_BOX,
                    GREEN_BED,
                    GREEN_CANDLE_CAKE,
                    GREEN_SHULKER_BOX,
                    GRINDSTONE,
                    HOPPER,
                    JIGSAW,
                    JUNGLE_BUTTON,
                    JUNGLE_DOOR,
                    JUNGLE_FENCE_GATE,
                    JUNGLE_HANGING_SIGN,
                    JUNGLE_SIGN,
                    JUNGLE_TRAPDOOR,
                    JUNGLE_WALL_HANGING_SIGN,
                    JUNGLE_WALL_SIGN,
                    LECTERN,
                    LEVER,
                    LIGHT_BLUE_BED,
                    LIGHT_BLUE_CANDLE_CAKE,
                    LIGHT_BLUE_SHULKER_BOX,
                    LIGHT_GRAY_BED,
                    LIGHT_GRAY_CANDLE_CAKE,
                    LIGHT_GRAY_SHULKER_BOX,
                    LIME_BED,
                    LIME_CANDLE_CAKE,
                    LIME_SHULKER_BOX,
                    LOOM,
                    MAGENTA_BED,
                    MAGENTA_CANDLE_CAKE,
                    MAGENTA_SHULKER_BOX,
                    MANGROVE_BUTTON,
                    MANGROVE_DOOR,
                    MANGROVE_FENCE_GATE,
                    MANGROVE_HANGING_SIGN,
                    MANGROVE_SIGN,
                    MANGROVE_TRAPDOOR,
                    MANGROVE_WALL_HANGING_SIGN,
                    MANGROVE_WALL_SIGN,
                    NOTE_BLOCK,
                    OAK_BUTTON,
                    OAK_DOOR,
                    OAK_FENCE_GATE,
                    OAK_HANGING_SIGN,
                    OAK_SIGN,
                    OAK_TRAPDOOR,
                    OAK_WALL_HANGING_SIGN,
                    OAK_WALL_SIGN,
                    ORANGE_BED,
                    ORANGE_CANDLE_CAKE,
                    ORANGE_SHULKER_BOX,
                    PINK_BED,
                    PINK_CANDLE_CAKE,
                    PINK_SHULKER_BOX,
                    POLISHED_BLACKSTONE_BUTTON,
                    POTTED_ACACIA_SAPLING,
                    POTTED_ALLIUM,
                    POTTED_AZALEA_BUSH,
                    POTTED_AZURE_BLUET,
                    POTTED_BAMBOO,
                    POTTED_BIRCH_SAPLING,
                    POTTED_BLUE_ORCHID,
                    POTTED_BROWN_MUSHROOM,
                    POTTED_CACTUS,
                    POTTED_CHERRY_SAPLING,
                    POTTED_CORNFLOWER,
                    POTTED_CRIMSON_FUNGUS,
                    POTTED_CRIMSON_ROOTS,
                    POTTED_DANDELION,
                    POTTED_DARK_OAK_SAPLING,
                    POTTED_DEAD_BUSH,
                    POTTED_FERN,
                    POTTED_FLOWERING_AZALEA_BUSH,
                    POTTED_JUNGLE_SAPLING,
                    POTTED_LILY_OF_THE_VALLEY,
                    POTTED_MANGROVE_PROPAGULE,
                    POTTED_OAK_SAPLING,
                    POTTED_ORANGE_TULIP,
                    POTTED_OXEYE_DAISY,
                    POTTED_PINK_TULIP,
                    POTTED_POPPY,
                    POTTED_RED_MUSHROOM,
                    POTTED_RED_TULIP,
                    POTTED_SPRUCE_SAPLING,
                    POTTED_TORCHFLOWER,
                    POTTED_WARPED_FUNGUS,
                    POTTED_WARPED_ROOTS,
                    POTTED_WHITE_TULIP,
                    POTTED_WITHER_ROSE,
                    PURPLE_BED,
                    PURPLE_CANDLE_CAKE,
                    PURPLE_SHULKER_BOX,
                    REDSTONE_WIRE,
                    RED_BED,
                    RED_CANDLE_CAKE,
                    RED_SHULKER_BOX,
                    REPEATER,
                    REPEATING_COMMAND_BLOCK,
                    RESPAWN_ANCHOR,
                    SHULKER_BOX,
                    SMITHING_TABLE,
                    SMOKER,
                    SOUL_CAMPFIRE,
                    SPRUCE_BUTTON,
                    SPRUCE_DOOR,
                    SPRUCE_FENCE_GATE,
                    SPRUCE_HANGING_SIGN,
                    SPRUCE_SIGN,
                    SPRUCE_TRAPDOOR,
                    SPRUCE_WALL_HANGING_SIGN,
                    SPRUCE_WALL_SIGN,
                    STONECUTTER,
                    STONE_BUTTON,
                    STRUCTURE_BLOCK,
                    SWEET_BERRY_BUSH,
                    TRAPPED_CHEST,
                    WARPED_BUTTON,
                    WARPED_DOOR,
                    WARPED_FENCE_GATE,
                    WARPED_HANGING_SIGN,
                    WARPED_SIGN,
                    WARPED_TRAPDOOR,
                    WARPED_WALL_HANGING_SIGN,
                    WARPED_WALL_SIGN,
                    WATER_CAULDRON,
                    WHITE_BED,
                    WHITE_CANDLE,
                    WHITE_CANDLE_CAKE,
                    WHITE_SHULKER_BOX,
                    YELLOW_BED,
                    YELLOW_CANDLE,
                    YELLOW_CANDLE_CAKE,
                    YELLOW_SHULKER_BOX -> true;
            //</editor-fold>
            default -> false;
        };
    }

    /**
     * @param block Block that will be checked
     * @return True if block is replaceable
     */
    public static boolean isReplaceable(final @NotNull net.minecraft.world.level.block.Block block) {
        return isReplaceable(block.defaultBlockState().getBukkitMaterial());
    }

    /**
     * @param block Block that will be checked
     * @return True if block is replaceable
     */
    public static boolean isReplaceable(final @NotNull Block block) {
        return isReplaceable(block.getType());
    }

    /**
     * @param material Material that will be checked
     * @return True if material is replaceable
     */
    public static boolean isReplaceable(final @NotNull Material material) {
        return switch (material) {
            //<editor-fold desc="Materials that will be replaced" defaultstate="collapsed">
            case SEAGRASS,
                    FIRE,
                    WARPED_ROOTS,
                    FERN,
                    LARGE_FERN,
                    NETHER_SPROUTS,
                    VINE,
                    SOUL_FIRE,
                    WATER,
                    DEAD_BUSH,
                    CAVE_AIR,
                    TALL_SEAGRASS,
                    SNOW,
                    BUBBLE_COLUMN,
                    SHORT_GRASS,
                    GLOW_LICHEN,
                    STRUCTURE_VOID,
                    LAVA,
                    CRIMSON_ROOTS,
                    AIR,
                    LIGHT,
                    VOID_AIR,
                    HANGING_ROOTS,
                    TALL_GRASS -> true;
            //</editor-fold>
            default -> false;
        };
    }

    /**
     * @param entityType Entity type that will be checked
     * @return True if an entity type is ignorable
     */
    public static boolean isIgnorableEntity(final @NotNull EntityType entityType) {
        return switch (entityType) {
            //<editor-fold desc="Entities to be ignored when placing a block on their location" defaultstate="collapsed">
            case DROPPED_ITEM,
                    ITEM_FRAME,
                    GLOW_ITEM_FRAME,
                    LIGHTNING,
                    LLAMA_SPIT,
                    EXPERIENCE_ORB,
                    THROWN_EXP_BOTTLE,
                    EGG,
                    SPLASH_POTION,
                    FIREWORK,
                    FIREBALL,
                    FISHING_HOOK,
                    SMALL_FIREBALL,
                    SNOWBALL,
                    TRIDENT,
                    WITHER_SKULL,
                    DRAGON_FIREBALL,
                    AREA_EFFECT_CLOUD,
                    ARROW,
                    SPECTRAL_ARROW,
                    ENDER_PEARL,
                    EVOKER_FANGS,
                    LEASH_HITCH -> true;
            //</editor-fold>
            default -> false;
        };
    }

    /**
     * @param entityType Entity type that will be checked
     * @return True if an entity type is ignorable
     */
    public static boolean isIgnorableEntity(final @NotNull net.minecraft.world.entity.EntityType<?> entityType) {
        return isIgnorableEntity(CraftEntityType.minecraftToBukkit(entityType));
    }

    private static boolean isBreaksOnBlockPlace(final Material material) {
        return material == Material.TALL_GRASS
                || material == Material.LARGE_FERN
                || material == Material.TALL_SEAGRASS;
    }
}
