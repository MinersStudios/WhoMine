package com.github.minersstudios.msdecor.utils;

import com.github.minersstudios.mscore.utils.MSDecorUtils;
import com.github.minersstudios.msdecor.customdecor.CustomDecorData;
import com.github.minersstudios.msdecor.customdecor.FaceableByType;
import com.github.minersstudios.msdecor.customdecor.Typed;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CustomDecorUtils {

	@Contract(value = " -> fail")
	private CustomDecorUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static @Nullable CustomDecorData getCustomDecorDataByLocation(@NotNull Location location) {
		for (Entity nearbyEntity : location.getWorld().getNearbyEntities(location.clone().add(0.5d, 0.0d, 0.5d), 0.2d, 0.3d, 0.2d)) {
			if (nearbyEntity instanceof ArmorStand armorStand) {
				return getCustomDecorDataByEntity(armorStand);
			}
		}
		for (Entity nearbyEntity : location.getWorld().getNearbyEntities(location.toCenterLocation(), 0.5d, 0.5d, 0.5d)) {
			if (nearbyEntity instanceof ItemFrame itemFrame) {
				return getCustomDecorDataByEntity(itemFrame);
			}
		}
		return null;
	}

	@Contract("null -> null")
	public static @Nullable CustomDecorData getCustomDecorDataByEntity(@Nullable Entity entity) {
		if (!MSDecorUtils.isCustomDecorEntity(entity)) return null;
		if (entity instanceof ArmorStand armorStand) {
			return MSDecorUtils.getCustomDecorData(armorStand.getEquipment().getHelmet());
		} else if (entity instanceof ItemFrame itemFrame) {
			return MSDecorUtils.getCustomDecorData(itemFrame.getItem());
		}
		return null;
	}

	@Contract("null, _ -> null")
	public static @Nullable CustomDecorData getCustomDecorDataWithFace(@Nullable ItemStack itemStack, @Nullable BlockFace blockFace) {
		CustomDecorData customDecorData = MSDecorUtils.getCustomDecorData(itemStack);
		if (customDecorData instanceof FaceableByType faceableByType) {
			Typed.Type type = faceableByType.getTypeByFace(blockFace);
			if (type != null) {
				return faceableByType.createCustomDecorData(type);
			}
		}
		return customDecorData;
	}
}
