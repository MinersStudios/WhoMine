package com.minersstudios.mscore.util;

import com.minersstudios.mscore.location.MSBoundingBox;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.function.Predicate;

public final class LocationUtils {
    private static final BlockFace[] BLOCK_FACES_45 = {
            //<editor-fold desc="Block faces 45" defaultstate="collapsed">
            BlockFace.SOUTH,
            BlockFace.SOUTH_WEST,
            BlockFace.WEST,
            BlockFace.NORTH_WEST,
            BlockFace.NORTH,
            BlockFace.NORTH_EAST,
            BlockFace.EAST,
            BlockFace.SOUTH_EAST,
            BlockFace.SOUTH
            //</editor-fold>
    };
    private static final Rotation[] ROTATIONS_45 = {
            //<editor-fold desc="Rotations 45" defaultstate="collapsed">
            Rotation.NONE,
            Rotation.CLOCKWISE_45,
            Rotation.CLOCKWISE,
            Rotation.CLOCKWISE_135,
            Rotation.FLIPPED,
            Rotation.FLIPPED_45,
            Rotation.COUNTER_CLOCKWISE,
            Rotation.COUNTER_CLOCKWISE_45,
            Rotation.NONE
            //</editor-fold>
    };
    private static final int[] DEGREES_45 = {
            //<editor-fold desc="Degrees 45" defaultstate="collapsed">
            0,
            45,
            90,
            135,
            180,
            225,
            270,
            315,
            360
            //</editor-fold>
    };
    private static final BlockFace[] BLOCK_FACES_90 = {
            //<editor-fold desc="Block faces 90" defaultstate="collapsed">
            BlockFace.SOUTH,
            BlockFace.WEST,
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.SOUTH
            //</editor-fold>
    };
    private static final Rotation[] ROTATIONS_90 = {
            //<editor-fold desc="Rotations 90" defaultstate="collapsed">
            Rotation.NONE,
            Rotation.CLOCKWISE,
            Rotation.FLIPPED,
            Rotation.COUNTER_CLOCKWISE,
            Rotation.NONE
            //</editor-fold>
    };
    private static final int[] DEGREES_90 = {
            //<editor-fold desc="Degrees 90" defaultstate="collapsed">
            0,
            90,
            180,
            270,
            360
            //</editor-fold>
    };
    private static final BlockFace[] BLOCK_FACES_180 = {
            //<editor-fold desc="Block faces 180" defaultstate="collapsed">
            BlockFace.SOUTH,
            BlockFace.NORTH,
            BlockFace.SOUTH
            //</editor-fold>
    };
    private static final Rotation[] ROTATIONS_180 = {
            //<editor-fold desc="Rotations 180" defaultstate="collapsed">
            Rotation.NONE,
            Rotation.FLIPPED,
            Rotation.NONE
            //</editor-fold>
    };
    private static final int[] DEGREES_180 = {
            //<editor-fold desc="Degrees 180" defaultstate="collapsed">
            0,
            180,
            360
            //</editor-fold>
    };

    @Contract(" -> fail")
    private LocationUtils() {
        throw new AssertionError("Utility class");
    }

    public static @NotNull BoundingBox nmsToBukkit(final @NotNull net.minecraft.world.level.levelgen.structure.BoundingBox boundingBox) {
        return new BoundingBox(
                boundingBox.minX(),
                boundingBox.minY(),
                boundingBox.minZ(),
                boundingBox.maxX(),
                boundingBox.maxY(),
                boundingBox.maxZ()
        );
    }

    public static @NotNull net.minecraft.world.level.levelgen.structure.BoundingBox bukkitToNms(final @NotNull BoundingBox boundingBox) {
        return new net.minecraft.world.level.levelgen.structure.BoundingBox(
                (int) boundingBox.getMinX(),
                (int) boundingBox.getMinY(),
                (int) boundingBox.getMinZ(),
                (int) boundingBox.getMaxX(),
                (int) boundingBox.getMaxY(),
                (int) boundingBox.getMaxZ()
        );
    }

    public static @NotNull AABB bukkitToAABB(final @NotNull BoundingBox boundingBox) {
        return new AABB(
                boundingBox.getMinX(),
                boundingBox.getMinY(),
                boundingBox.getMinZ(),
                boundingBox.getMaxX(),
                boundingBox.getMaxY(),
                boundingBox.getMaxZ()
        );
    }

    public static @NotNull Location nmsToBukkit(final @NotNull BlockPos blockPos) {
        return nmsToBukkit(blockPos, (World) null);
    }

    public static @NotNull Location nmsToBukkit(
            final @NotNull BlockPos blockPos,
            final @Nullable Level level
    ) {
        return nmsToBukkit(blockPos, level == null ? null : level.getWorld());
    }

    public static @NotNull Location nmsToBukkit(
            final @NotNull BlockPos blockPos,
            final @Nullable World world
    ) {
        return new Location(
                world,
                blockPos.getX(),
                blockPos.getY(),
                blockPos.getZ()
        );
    }

    public static @NotNull BlockPos bukkitToNms(final @NotNull Location location) {
        return new BlockPos(
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
        );
    }

    public static BlockPos @NotNull [] getBlockPosesBetween(
            final int x1,
            final int y1,
            final int z1,
            final int x2,
            final int y2,
            final int z2
    ) {
        return getBetween(
                BlockPos.class,
                x1, y1, z1,
                x2, y2, z2
        );
    }

    public static Location @NotNull [] getLocationsBetween(
            final int x1,
            final int y1,
            final int z1,
            final int x2,
            final int y2,
            final int z2
    ) {
        return getBetween(
                Location.class,
                x1, y1, z1,
                x2, y2, z2
        );
    }

    public static @NotNull Location offset(
            final @NotNull Location location,
            final double x,
            final double y,
            final double z
    ) {
        return location.clone().add(x, y, z);
    }

    public static @NotNull Location directionalOffset(
            final @NotNull Location location,
            final float degrees,
            final double x,
            final double y,
            final double z
    ) {
        final float normalized = LocationUtils.normalize360(degrees);

        return location.clone().add(
                new Vector(x, y, -z).rotateAroundY(
                        Math.toRadians(
                                (normalized >= 0 && normalized <= 45)
                                || (normalized >= 180 && normalized <= 225)
                                ? normalized - 180
                                : normalized
                        )
                )
        );
    }

    public static int normalize180(final float degrees) {
        return Math.floorMod((int) degrees, 180);
    }

    public static int normalize360(final float degrees) {
        return Math.floorMod((int) degrees, 360);
    }

    public static float to45(final float degrees) {
        return DEGREES_45[nearestIndex(degrees, DEGREES_45)];
    }

    public static float to90(final float degrees) {
        return DEGREES_90[nearestIndex(degrees, DEGREES_90)];
    }

    public static float to180(final float degrees) {
        return DEGREES_180[nearestIndex(degrees, DEGREES_180)];
    }

    public static @NotNull BlockFace degreesToBlockFace45(final float degrees) {
        return BLOCK_FACES_45[nearestIndex(degrees, DEGREES_45)];
    }

    public static @NotNull BlockFace degreesToBlockFace90(final float degrees) {
        return BLOCK_FACES_90[nearestIndex(degrees, DEGREES_90)];
    }

    public static @NotNull BlockFace degreesToBlockFace180(final float degrees) {
        return BLOCK_FACES_180[nearestIndex(degrees, DEGREES_180)];
    }

    public static @NotNull Rotation degreesToRotation45(final float degrees) {
        return ROTATIONS_45[nearestIndex(degrees, DEGREES_45)];
    }

    public static @NotNull Rotation degreesToRotation90(final float degrees) {
        return ROTATIONS_90[nearestIndex(degrees, DEGREES_90)];
    }

    public static @NotNull Rotation degreesToRotation180(final float degrees) {
        return ROTATIONS_180[nearestIndex(degrees, DEGREES_180)];
    }

    public static Entity @NotNull [] getEntities(
            final @NotNull Location location,
            final double dx,
            final double dy,
            final double dz
    ) {
        return getEntities(
                location,
                dx, dy, dz,
                null
        );
    }

    public static Entity @NotNull [] getEntities(
            final @NotNull Location location,
            final double dx,
            final double dy,
            final double dz,
            final @Nullable Predicate<Entity> predicate
    ) {
        final World world = location.getWorld();

        if (world == null) {
            throw new IllegalArgumentException("Location has no world");
        }

        return getEntities(
                location.getWorld(),
                location,
                dx, dy, dz,
                predicate
        );
    }

    public static Entity @NotNull [] getEntities(
            final @NotNull World world,
            final @NotNull Location location,
            final double dx,
            final double dy,
            final double dz
    ) {
        return getEntities(
                world,
                location,
                dx, dy, dz,
                null
        );
    }

    public static Entity @NotNull [] getEntities(
            final @NotNull World world,
            final @NotNull Location location,
            final double dx,
            final double dy,
            final double dz,
            final @Nullable Predicate<Entity> predicate
    ) {
        return MSBoundingBox.ofSize(location, dx, dy, dz)
                .getEntities(
                        world,
                        predicate
                );
    }


    public static boolean isDiagonal(final @NotNull BlockFace blockFace) {
        return blockFace == BlockFace.NORTH_EAST
                || blockFace == BlockFace.NORTH_WEST
                || blockFace == BlockFace.SOUTH_EAST
                || blockFace == BlockFace.SOUTH_WEST;
    }

    public static boolean isDiagonal(final @NotNull Rotation rotation) {
        return rotation == Rotation.CLOCKWISE_45
                || rotation == Rotation.CLOCKWISE_135
                || rotation == Rotation.COUNTER_CLOCKWISE
                || rotation == Rotation.COUNTER_CLOCKWISE_45;
    }

    public static boolean isX(final @NotNull BlockFace blockFace) {
        return blockFace == BlockFace.EAST
                || blockFace == BlockFace.WEST;
    }

    public static boolean isX(final @NotNull Rotation rotation) {
        return rotation == Rotation.CLOCKWISE
                || rotation == Rotation.COUNTER_CLOCKWISE;
    }

    public static boolean isZ(final @NotNull BlockFace blockFace) {
        return blockFace == BlockFace.NORTH
                || blockFace == BlockFace.SOUTH;
    }

    public static boolean isZ(final @NotNull Rotation rotation) {
        return rotation == Rotation.NONE
                || rotation == Rotation.FLIPPED;
    }

    @SuppressWarnings("unchecked")
    private static <T> T @NotNull [] getBetween(
            final @NotNull Class<T> clazz,
            final int x1,
            final int y1,
            final int z1,
            final int x2,
            final int y2,
            final int z2
    ) throws IllegalArgumentException {
        final boolean isLocation = clazz.isAssignableFrom(Location.class);

        if (
                !isLocation
                && !clazz.isAssignableFrom(BlockPos.class)
        ) throw new IllegalArgumentException("Class must be assignable from Location or BlockPos");

        final int minX = Math.min(x1, x2);
        final int minY = Math.min(y1, y2);
        final int minZ = Math.min(z1, z2);
        final int offsetX = Math.abs(x1 - x2) + 1;
        final int offsetY = Math.abs(y1 - y2) + 1;
        final int offsetZ = Math.abs(z1 - z2) + 1;
        final var array = (T[]) Array.newInstance(clazz, offsetX * offsetY * offsetZ);

        int i = 0;

        for (int x = 0; x < offsetX; ++x) {
            for (int y = 0; y < offsetY; ++y) {
                for (int z = 0; z < offsetZ; ++z) {
                    if (isLocation) {
                        array[i++] = (T) new Location(
                                null,
                                minX + x,
                                minY + y,
                                minZ + z
                        );
                    } else {
                        array[i++] = (T) new BlockPos(
                                minX + x,
                                minY + y,
                                minZ + z
                        );
                    }
                }
            }
        }

        return array;
    }

    private static int nearestIndex(
            final float degrees,
            final int @NotNull [] yawValues
    ) {
        final int normalized = normalize360(degrees);
        int nearestIndex = 0;
        float minDifference = Math.abs(normalized - yawValues[0]);

        for (int i = 1; i < yawValues.length; ++i) {
            final int difference = Math.abs(normalized - yawValues[i]);

            if (difference < minDifference) {
                minDifference = difference;
                nearestIndex = i;
            }
        }

        return nearestIndex;
    }
}
