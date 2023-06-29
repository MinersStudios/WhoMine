package com.github.minersstudios.msdecor.customdecor;

import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public interface FaceableByType extends Typed {

	@Contract("null -> null")
	default @Nullable Typed.Type getTypeByFace(@Nullable BlockFace blockFace) {
		if (blockFace == null) return null;
		for (Typed.Type type : this.getTypes()) {
			Facing facing = type.getFacing();
			if (facing != null && facing.hasFace(blockFace)) {
				return type;
			}
		}
		return null;
	}
}
