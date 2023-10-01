package com.minersstudios.msdecor.utils;

import com.minersstudios.mscore.util.MSDecorUtils;
import com.minersstudios.msdecor.customdecor.CustomDecorData;
import com.minersstudios.msdecor.customdecor.FaceableByType;
import com.minersstudios.msdecor.customdecor.Typed;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class CustomDecorUtils {
    private static final Rotation[] ROTATIONS = {
            Rotation.NONE,
            Rotation.CLOCKWISE_45,
            Rotation.CLOCKWISE,
            Rotation.CLOCKWISE_135,
            Rotation.FLIPPED,
            Rotation.FLIPPED_45,
            Rotation.COUNTER_CLOCKWISE,
            Rotation.COUNTER_CLOCKWISE_45,
            Rotation.NONE
    };
    private static final int[] YAW_VALUES = {0, 45, 90, 135, 180, 225, 270, 315, 360};

    @Contract(value = " -> fail")
    private CustomDecorUtils() {
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

        armorStandLocation.setYaw(YAW_VALUES[nearestIndex(player.getLocation().getYaw())]);
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
        itemFrame.setRotation(ROTATIONS[nearestIndex(player.getLocation().getYaw())]);
    }

    public static @NotNull Optional<CustomDecorData> getCustomDecorDataByLocation(final @NotNull Location location) {
        for (final var nearbyEntity : location.getWorld().getNearbyEntities(location.clone().add(0.5d, 0.0d, 0.5d), 0.2d, 0.3d, 0.2d)) {
            if (nearbyEntity instanceof final ArmorStand armorStand) {
                return getCustomDecorDataByEntity(armorStand);
            }
        }

        for (final var nearbyEntity : location.getWorld().getNearbyEntities(location.toCenterLocation(), 0.5d, 0.5d, 0.5d)) {
            if (nearbyEntity instanceof final ItemFrame itemFrame) {
                return getCustomDecorDataByEntity(itemFrame);
            }
        }
        return Optional.empty();
    }

    public static @NotNull Optional<CustomDecorData> getCustomDecorDataByEntity(final @Nullable Entity entity) {
        return !MSDecorUtils.isCustomDecorEntity(entity)
                ? Optional.empty()
                : entity instanceof final ArmorStand armorStand
                ? MSDecorUtils.getCustomDecorData(armorStand.getEquipment().getHelmet())
                : entity instanceof final ItemFrame itemFrame
                ? MSDecorUtils.getCustomDecorData(itemFrame.getItem())
                : Optional.empty();
    }

    public static @NotNull Optional<CustomDecorData> getCustomDecorDataWithFace(
            final @Nullable ItemStack itemStack,
            final @Nullable BlockFace blockFace
    ) {
        final var customDecorData = MSDecorUtils.getCustomDecorData(itemStack);

        if (customDecorData.orElse(null) instanceof final FaceableByType faceableByType) {
            final Typed.Type type = faceableByType.getTypeByFace(blockFace);

            if (type != null) {
                return Optional.of(faceableByType.createCustomDecorData(type));
            }
        }

        return customDecorData;
    }

    private static int nearestIndex(final float yaw) {
        final int converted = Math.floorMod((int) yaw, 360);
        int nearestIndex = 0;
        float minDifference = Math.abs(converted - YAW_VALUES[0]);

        for (int i = 1; i < YAW_VALUES.length; i++) {
            final int difference = Math.abs(converted - YAW_VALUES[i]);

            if (difference < minDifference) {
                minDifference = difference;
                nearestIndex = i;
            }
        }

        return nearestIndex;
    }
}
