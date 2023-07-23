package com.minersstudios.mscore.utils;

import com.google.common.collect.Sets;
import net.minecraft.world.level.block.SoundType;
import org.bukkit.Material;
import org.bukkit.SoundGroup;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_20_R1.CraftSoundGroup;
import org.bukkit.craftbukkit.v1_20_R1.block.CraftBlock;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for blocks
 */
public final class BlockUtils {
    public static final Set<Material> REPLACE = Sets.newHashSet(
            //<editor-fold desc="Replace materials">
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

    public static final Set<Material> BREAK_ON_BLOCK_PLACE = Sets.newHashSet(
            //<editor-fold desc="Materials that will break on block place">
            Material.TALL_GRASS,
            Material.LARGE_FERN,
            Material.TALL_SEAGRASS
            //</editor-fold>
    );

    public static final Set<Material> WOOD_SOUND_MATERIAL_SET;

    static {
        SoundGroup woodSoundGroup = CraftSoundGroup.getSoundGroup(SoundType.WOOD);
        WOOD_SOUND_MATERIAL_SET =
                Arrays.stream(Material.values())
                .filter(material ->
                        !material.isLegacy()
                        && material.isBlock()
                        && material.createBlockData().getSoundGroup() == woodSoundGroup
                )
                .collect(Collectors.toUnmodifiableSet());
    }

    @Contract(value = " -> fail")
    private BlockUtils() {
        throw new AssertionError("Utility class");
    }

    /**
     * Breaks top / bottom block
     *
     * @param centreBlock Block around which the blocks break
     */
    public static void removeBlocksAround(@NotNull Block centreBlock) {
        CraftBlock topBlock = (CraftBlock) centreBlock.getRelative(BlockFace.UP);
        CraftBlock bottomBlock = (CraftBlock) centreBlock.getRelative(BlockFace.DOWN);

        if (BREAK_ON_BLOCK_PLACE.contains(topBlock.getType())) {
            topBlock.getHandle().destroyBlock(topBlock.getPosition(), true);
        }

        if (BREAK_ON_BLOCK_PLACE.contains(bottomBlock.getType())) {
            bottomBlock.getHandle().destroyBlock(bottomBlock.getPosition(), true);
        }
    }

    /**
     * @param material Material that will be used
     *                 to get the {@link BlockData}
     * @return {@link BlockData} from {@link Material}
     */
    public static @Nullable BlockData getBlockDataByMaterial(@NotNull Material material) {
        return switch (material) {
            case REDSTONE -> Material.REDSTONE_WIRE.createBlockData();
            case STRING -> Material.TRIPWIRE.createBlockData();
            default -> material.isBlock() ? material.createBlockData() : null;
        };
    }

    /**
     * @param material Material that will be checked
     * @return True if material has wood sound
     */
    public static boolean isWoodenSound(@NotNull Material material) {
        return WOOD_SOUND_MATERIAL_SET.contains(material);
    }
}
