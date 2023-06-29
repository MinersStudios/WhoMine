package com.github.minersstudios.msdecor.utils;

import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.NavigableMap;
import java.util.TreeMap;

public final class EntityUtils {

	@Contract(value = " -> fail")
	private EntityUtils() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Rotates armor stand by player yaw
	 *
	 * @param armorStand armor stand entity used for rotate
	 * @param player     player used for rotate armor stand
	 */
	public static void rotateArmorStandByPlayer(@NotNull ArmorStand armorStand, @NotNull Player player) {
		Location armorStandLocation = armorStand.getLocation();
		switch (playerDirectionHandler(player.getLocation().getYaw())) {
			case NONE -> armorStandLocation.setYaw(0.0f);
			case CLOCKWISE_45 -> armorStandLocation.setYaw(45.0f);
			case CLOCKWISE -> armorStandLocation.setYaw(90.0f);
			case CLOCKWISE_135 -> armorStandLocation.setYaw(135.0f);
			case FLIPPED -> armorStandLocation.setYaw(180.0f);
			case FLIPPED_45 -> armorStandLocation.setYaw(-135.0f);
			case COUNTER_CLOCKWISE -> armorStandLocation.setYaw(-90.0f);
			case COUNTER_CLOCKWISE_45 -> armorStandLocation.setYaw(-45.0f);
		}
		armorStand.teleport(armorStandLocation);
	}

	/**
	 * Rotates item frame item by player yaw
	 *
	 * @param itemFrame item frame entity used for rotate item
	 * @param player    player used for rotate item frame item
	 */
	public static void rotateItemFrameByPlayer(@NotNull ItemFrame itemFrame, @NotNull Player player) {
		itemFrame.setRotation(playerDirectionHandler(player.getLocation().getYaw()));
	}

	private static Rotation playerDirectionHandler(float yaw) {
		yaw = (yaw + 360) % 360;
		NavigableMap<Float, Rotation> map = new TreeMap<>();
		map.put(0.0f,  Rotation.NONE);
		map.put(23.0f, Rotation.CLOCKWISE_45);
		map.put(68.0f, Rotation.CLOCKWISE);
		map.put(113.0f, Rotation.CLOCKWISE_135);
		map.put(158.0f, Rotation.FLIPPED);
		map.put(203.0f, Rotation.FLIPPED_45);
		map.put(248.0f, Rotation.COUNTER_CLOCKWISE);
		map.put(293.0f, Rotation.COUNTER_CLOCKWISE_45);
		map.put(338.0f, Rotation.NONE);
		return map.floorEntry(yaw).getValue();
	}
}
