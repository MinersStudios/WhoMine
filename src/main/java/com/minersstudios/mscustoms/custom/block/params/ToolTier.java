package com.minersstudios.mscustoms.custom.block.params;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * Represents different tool tiers and their corresponding dig speeds.
 * Use {@link #fromMaterial(Material)} to get the tool tier of a material.
 */
public enum ToolTier {
    HAND(0.1f),
    WOOD(0.35f),
    STONE(0.6f),
    IRON(0.7f),
    GOLD(1.3f),
    DIAMOND(0.95f),
    NETHERITE(1.0f);

    private final float digSpeed;

    /**
     * Constructs a ToolTier with the given dig speed
     *
     * @param digSpeed The dig speed of the tool tier
     */
    ToolTier(final float digSpeed) {
        this.digSpeed = digSpeed;
    }

    /**
     * @param material The material to get the tool tier from
     * @return The tool tier of the specified material
     *         or {@link #HAND} if the material is not a tool
     */
    public static @NotNull ToolTier fromMaterial(final @NotNull Material material) {
        return switch (material) {
            //<editor-fold desc="Wooden tools" defaultstate="collapsed">
            case
                    WOODEN_PICKAXE,
                    WOODEN_AXE,
                    WOODEN_HOE,
                    WOODEN_SHOVEL,
                    WOODEN_SWORD -> WOOD;
            //</editor-fold>
            //<editor-fold desc="Stone tools" defaultstate="collapsed">
            case
                    STONE_PICKAXE,
                    STONE_AXE,
                    STONE_HOE,
                    STONE_SHOVEL,
                    STONE_SWORD -> STONE;
            //</editor-fold>
            //<editor-fold desc="Iron tools" defaultstate="collapsed">
            case
                    IRON_PICKAXE,
                    IRON_AXE,
                    IRON_HOE,
                    IRON_SHOVEL,
                    IRON_SWORD -> IRON;
            //</editor-fold>
            //<editor-fold desc="Golden tools" defaultstate="collapsed">
            case
                    GOLDEN_PICKAXE,
                    GOLDEN_AXE,
                    GOLDEN_HOE,
                    GOLDEN_SHOVEL,
                    GOLDEN_SWORD -> GOLD;
            //</editor-fold>
            //<editor-fold desc="Diamond tools" defaultstate="collapsed">
            case
                    DIAMOND_PICKAXE,
                    DIAMOND_AXE,
                    DIAMOND_HOE,
                    DIAMOND_SHOVEL,
                    DIAMOND_SWORD -> DIAMOND;
            //</editor-fold>
            //<editor-fold desc="Netherite tools" defaultstate="collapsed">
            case
                    NETHERITE_PICKAXE,
                    NETHERITE_AXE,
                    NETHERITE_HOE,
                    NETHERITE_SHOVEL,
                    NETHERITE_SWORD,
                    SHEARS -> NETHERITE;
            //</editor-fold>
            default -> HAND;
        };
    }

    /**
     * @return The dig speed of the tool tier
     */
    public float getDigSpeed() {
        return this.digSpeed;
    }

    /**
     * @return The string representation of this tool tier
     */
    @Override
    public @NotNull String toString() {
        return this.name() + "{digSpeed=" + this.digSpeed + "}";
    }
}
