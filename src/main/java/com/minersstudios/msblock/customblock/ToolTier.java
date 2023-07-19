package com.minersstudios.msblock.customblock;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public enum ToolTier {
    HAND(0.1f),
    WOOD(0.3f),
    STONE(0.45f),
    IRON(0.56f),
    GOLD(0.8f),
    DIAMOND(0.65f),
    NETHERITE(0.8f);

    private final float speed;

    ToolTier(float speed) {
        this.speed = speed;
    }

    public static @NotNull ToolTier fromItemStack(@NotNull ItemStack itemStack) {
        return switch (itemStack.getType()) {
            case
                    WOODEN_PICKAXE,
                    WOODEN_AXE,
                    WOODEN_HOE,
                    WOODEN_SHOVEL,
                    WOODEN_SWORD -> WOOD;
            case
                    STONE_PICKAXE,
                    STONE_AXE,
                    STONE_HOE,
                    STONE_SHOVEL,
                    STONE_SWORD -> STONE;
            case
                    IRON_PICKAXE,
                    IRON_AXE,
                    IRON_HOE,
                    IRON_SHOVEL,
                    IRON_SWORD -> IRON;
            case
                    GOLDEN_PICKAXE,
                    GOLDEN_AXE,
                    GOLDEN_HOE,
                    GOLDEN_SHOVEL,
                    GOLDEN_SWORD -> GOLD;
            case
                    DIAMOND_PICKAXE,
                    DIAMOND_AXE,
                    DIAMOND_HOE,
                    DIAMOND_SHOVEL,
                    DIAMOND_SWORD -> DIAMOND;
            case
                    NETHERITE_PICKAXE,
                    NETHERITE_AXE,
                    NETHERITE_HOE,
                    NETHERITE_SHOVEL,
                    NETHERITE_SWORD,
                    SHEARS -> NETHERITE;
            default -> HAND;
        };
    }

    public float getSpeed() {
        return this.speed;
    }
}
