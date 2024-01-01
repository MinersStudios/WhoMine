package com.minersstudios.mscustoms.custom.block.params;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * Represents different tool types.
 * Use {@link #fromMaterial(Material)} to get the tool type of material.
 */
public enum ToolType {
    HAND, SWORD, PICKAXE, AXE, SHOVEL, HOE, SHEARS;

    /**
     * @param material The material to get the tool type from
     * @return The tool type of the specified material
     *         or {@link #HAND} if the material is not a tool
     */
    public static @NotNull ToolType fromMaterial(final @NotNull Material material) {
        return switch (material) {
            //<editor-fold desc="Swords" defaultstate="collapsed">
            case
                    WOODEN_SWORD,
                    STONE_SWORD,
                    IRON_SWORD,
                    GOLDEN_SWORD,
                    DIAMOND_SWORD,
                    NETHERITE_SWORD -> SWORD;
            //</editor-fold>
            //<editor-fold desc="Pickaxes" defaultstate="collapsed">
            case
                    WOODEN_PICKAXE,
                    STONE_PICKAXE,
                    IRON_PICKAXE,
                    GOLDEN_PICKAXE,
                    DIAMOND_PICKAXE,
                    NETHERITE_PICKAXE -> PICKAXE;
            //</editor-fold>
            //<editor-fold desc="Axes" defaultstate="collapsed">
            case
                    WOODEN_AXE,
                    STONE_AXE,
                    IRON_AXE,
                    GOLDEN_AXE,
                    DIAMOND_AXE,
                    NETHERITE_AXE -> AXE;
            //</editor-fold>
            //<editor-fold desc="Shovels" defaultstate="collapsed">
            case
                    WOODEN_SHOVEL,
                    STONE_SHOVEL,
                    IRON_SHOVEL,
                    GOLDEN_SHOVEL,
                    DIAMOND_SHOVEL,
                    NETHERITE_SHOVEL -> SHOVEL;
            //</editor-fold>
            //<editor-fold desc="Hoes" defaultstate="collapsed">
            case
                    WOODEN_HOE,
                    STONE_HOE,
                    IRON_HOE,
                    GOLDEN_HOE,
                    DIAMOND_HOE,
                    NETHERITE_HOE -> HOE;
            //</editor-fold>
            //<editor-fold desc="Shears" defaultstate="collapsed">
            case SHEARS -> SHEARS;
            //</editor-fold>
            default -> HAND;
        };
    }
}
