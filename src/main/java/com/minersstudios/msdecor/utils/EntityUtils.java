package com.minersstudios.msdecor.utils;

import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.NavigableMap;
import java.util.TreeMap;

public final class EntityUtils {
    private static final NavigableMap<Float, Rotation> DIRECTION_MAP;

    static {
        var tempMap = new TreeMap<Float, Rotation>();

        tempMap.put(0.0f, Rotation.NONE);
        tempMap.put(23.0f, Rotation.CLOCKWISE_45);
        tempMap.put(68.0f, Rotation.CLOCKWISE);
        tempMap.put(113.0f, Rotation.CLOCKWISE_135);
        tempMap.put(158.0f, Rotation.FLIPPED);
        tempMap.put(203.0f, Rotation.FLIPPED_45);
        tempMap.put(248.0f, Rotation.COUNTER_CLOCKWISE);
        tempMap.put(293.0f, Rotation.COUNTER_CLOCKWISE_45);
        tempMap.put(338.0f, Rotation.NONE);

        DIRECTION_MAP = Collections.unmodifiableNavigableMap(tempMap);
    }

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
    public static void rotateArmorStandByPlayer(
            final @NotNull ArmorStand armorStand,
            final @NotNull Player player
    ) {
       final Location armorStandLocation = armorStand.getLocation();

        switch (convertYawToRotation(player.getLocation().getYaw())) {
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
    public static void rotateItemFrameByPlayer(
            final @NotNull ItemFrame itemFrame,
            final @NotNull Player player
    ) {
        itemFrame.setRotation(convertYawToRotation(player.getLocation().getYaw()));
    }

    /**
     * Converts player yaw to rotation
     *
     * @param yaw Player yaw
     * @return Rotation by player yaw
     */
    public static @NotNull Rotation convertYawToRotation(final float yaw) {
        return DIRECTION_MAP.floorEntry((yaw + 360) % 360).getValue();
    }
}
