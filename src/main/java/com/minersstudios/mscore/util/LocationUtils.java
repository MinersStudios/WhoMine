package com.minersstudios.mscore.util;

import io.papermc.paper.chunk.system.entity.EntityLookup;
import io.papermc.paper.world.ChunkEntitySlices;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spigotmc.AsyncCatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class LocationUtils {
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

    private static final int REGION_SHIFT = 5;
    private static final int REGION_MASK = (1 << REGION_SHIFT) - 1;

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

    public static @NotNull Location nmsToBukkit(final @NotNull net.minecraft.core.BlockPos blockPos) {
        return nmsToBukkit(blockPos, (World) null);
    }

    public static @NotNull Location nmsToBukkit(
            final @NotNull net.minecraft.core.BlockPos blockPos,
            final @Nullable net.minecraft.world.level.Level level
    ) {
        return nmsToBukkit(blockPos, level == null ? null : level.getWorld());
    }

    public static @NotNull Location nmsToBukkit(
            final @NotNull net.minecraft.core.BlockPos blockPos,
            final @Nullable World world
    ) {
        return new Location(
                world,
                blockPos.getX(),
                blockPos.getY(),
                blockPos.getZ()
        );
    }

    public static @NotNull net.minecraft.core.BlockPos bukkitToNms(final @NotNull Location location) {
        return new net.minecraft.core.BlockPos(
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
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

    public static @NotNull Rotation degreesToRotation45(final float degrees) {
        return ROTATIONS_45[nearestIndex(degrees, DEGREES_45)];
    }

    public static @NotNull Rotation degreesToRotation90(final float degrees) {
        return ROTATIONS_90[nearestIndex(degrees, DEGREES_90)];
    }

    public static @NotNull Rotation degreesToRotation180(final float degrees) {
        return ROTATIONS_180[nearestIndex(degrees, DEGREES_180)];
    }

    public static Entity @NotNull [] getNearbyEntities(
            final @NotNull Location location,
            final double x,
            final double y,
            final double z
    ) {
        return getNearbyEntities(location, x, y, z, null);
    }

    public static Entity @NotNull [] getNearbyEntities(
            final @NotNull Location location,
            final double x,
            final double y,
            final double z,
            final @Nullable Predicate<Entity> predicate
    ) {
        final World world = location.getWorld();

        if (world == null) {
            throw new IllegalArgumentException("Location has no world");
        }

        return getNearbyEntities(location.getWorld(), location, x, y, z, predicate);
    }

    public static Entity @NotNull [] getNearbyEntities(
            final @NotNull World world,
            final @NotNull Location location,
            final double x,
            final double y,
            final double z
    ) {
        return getNearbyEntities(world, location, x, y, z, null);
    }

    public static Entity @NotNull [] getNearbyEntities(
            final @NotNull World world,
            final @NotNull Location location,
            final double x,
            final double y,
            final double z,
            final @Nullable Predicate<Entity> predicate
    ) {
        return getNearbyEntities(world, BoundingBox.of(location, x, y, z), predicate);
    }

    public static Entity @NotNull [] getNearbyEntities(
            final @NotNull World world,
            final @NotNull BoundingBox boundingBox
    ) {
        return getNearbyEntities(world, boundingBox, null);
    }

    public static Entity @NotNull [] getNearbyEntities(
            final @NotNull World world,
            final @NotNull BoundingBox boundingBox,
            final @Nullable Predicate<Entity> predicate
    ) {
        final net.minecraft.world.entity.Entity[] nmsEntities = getNearbyNMSEntities(
                ((CraftWorld) world).getHandle(),
                bukkitToAABB(boundingBox),
                entity -> predicate == null || predicate.test(entity.getBukkitEntity())
        ).toArray(new net.minecraft.world.entity.Entity[0]);
        final Entity[] bukkitEntities = new Entity[nmsEntities.length];

        for (int i = 0; i < nmsEntities.length; i++) {
            bukkitEntities[i] = nmsEntities[i].getBukkitEntity();
        }

        return bukkitEntities;
    }

    public static @NotNull List<net.minecraft.world.entity.Entity> getNearbyNMSEntities(
            final @NotNull ServerLevel level,
            final @NotNull AABB aabb
    ) {
        return getNearbyNMSEntities(level, aabb, null);
    }

    public static @NotNull List<net.minecraft.world.entity.Entity> getNearbyNMSEntities(
            final @NotNull ServerLevel level,
            final @NotNull AABB aabb,
            final @Nullable Predicate<net.minecraft.world.entity.Entity> predicate
    ) {
        AsyncCatcher.catchOp("getNearbyEntities");

        final var list = new ArrayList<net.minecraft.world.entity.Entity>();
        final EntityLookup entityLookup = level.getEntityLookup();

        final int minChunkX = ((int) Math.floor(aabb.minX) - 2) >> 4;
        final int minChunkZ = ((int) Math.floor(aabb.minZ) - 2) >> 4;
        final int maxChunkX = ((int) Math.floor(aabb.maxX) + 2) >> 4;
        final int maxChunkZ = ((int) Math.floor(aabb.maxZ) + 2) >> 4;

        final int minRegionX = minChunkX >> REGION_SHIFT;
        final int minRegionZ = minChunkZ >> REGION_SHIFT;
        final int maxRegionX = maxChunkX >> REGION_SHIFT;
        final int maxRegionZ = maxChunkZ >> REGION_SHIFT;

        for (int currRegionZ = minRegionZ; currRegionZ <= maxRegionZ; ++currRegionZ) {
            final int minZ = currRegionZ == minRegionZ ? minChunkZ & REGION_MASK : 0;
            final int maxZ = currRegionZ == maxRegionZ ? maxChunkZ & REGION_MASK : REGION_MASK;

            for (int currRegionX = minRegionX; currRegionX <= maxRegionX; ++currRegionX) {
                final EntityLookup.ChunkSlicesRegion region = entityLookup.getRegion(currRegionX, currRegionZ);

                if (region == null) continue;

                final int minX = currRegionX == minRegionX ? minChunkX & REGION_MASK : 0;
                final int maxX = currRegionX == maxRegionX ? maxChunkX & REGION_MASK : REGION_MASK;

                for (int currZ = minZ; currZ <= maxZ; ++currZ) {
                    for (int currX = minX; currX <= maxX; ++currX) {
                        final ChunkEntitySlices chunk = region.get(currX | (currZ << REGION_SHIFT));

                        if (chunk == null || !chunk.status.isOrAfter(FullChunkStatus.FULL)) continue;

                        chunk.getEntities((net.minecraft.world.entity.Entity) null, aabb, list, predicate);
                    }
                }
            }
        }

        return list;
    }

    private static int nearestIndex(
            final float degrees,
            final int @NotNull [] yawValues
    ) {
        final int normalized = normalize360(degrees);
        int nearestIndex = 0;
        float minDifference = Math.abs(normalized - yawValues[0]);

        for (int i = 1; i < yawValues.length; i++) {
            final int difference = Math.abs(normalized - yawValues[i]);

            if (difference < minDifference) {
                minDifference = difference;
                nearestIndex = i;
            }
        }

        return nearestIndex;
    }
}