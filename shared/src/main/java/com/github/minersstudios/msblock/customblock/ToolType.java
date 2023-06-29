package com.github.minersstudios.msblock.customblock;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public enum ToolType {
	HAND, SWORD, PICKAXE, AXE, SHOVEL, HOE, SHEARS;

	public static @NotNull ToolType fromItemStack(@NotNull ItemStack itemStack) {
		return switch (itemStack.getType()) {
			case WOODEN_SWORD,
					STONE_SWORD,
					IRON_SWORD,
					GOLDEN_SWORD,
					DIAMOND_SWORD,
					NETHERITE_SWORD -> SWORD;
			case WOODEN_PICKAXE,
					STONE_PICKAXE,
					IRON_PICKAXE,
					GOLDEN_PICKAXE,
					DIAMOND_PICKAXE,
					NETHERITE_PICKAXE -> PICKAXE;
			case WOODEN_AXE,
					STONE_AXE,
					IRON_AXE,
					GOLDEN_AXE,
					DIAMOND_AXE,
					NETHERITE_AXE -> AXE;
			case WOODEN_SHOVEL,
					STONE_SHOVEL,
					IRON_SHOVEL,
					GOLDEN_SHOVEL,
					DIAMOND_SHOVEL,
					NETHERITE_SHOVEL -> SHOVEL;
			case WOODEN_HOE,
					STONE_HOE,
					IRON_HOE,
					GOLDEN_HOE,
					DIAMOND_HOE,
					NETHERITE_HOE -> HOE;
			case SHEARS -> SHEARS;
			default -> HAND;
		};
	}
}
