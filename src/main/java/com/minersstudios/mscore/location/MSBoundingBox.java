package com.minersstudios.mscore.location;

import io.papermc.paper.chunk.system.entity.EntityLookup;
import io.papermc.paper.world.ChunkEntitySlices;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spigotmc.AsyncCatcher;

import javax.annotation.concurrent.Immutable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("UnstableApiUsage")
@Immutable
public final class MSBoundingBox {
    final double minX;
    final double minY;
    final double minZ;
    final double maxX;
    final double maxY;
    final double maxZ;

    private static final int REGION_SHIFT = 5;
    private static final int REGION_MASK = (1 << REGION_SHIFT) - 1;

    private MSBoundingBox(
            final double minX,
            final double minY,
            final double minZ,
            final double maxX,
            final double maxY,
            final double maxZ
    ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    @Contract("_, _, _, _, _, _ -> new")
    public static @NotNull MSBoundingBox of(
            final double x1,
            final double y1,
            final double z1,
            final double x2,
            final double y2,
            final double z2
    ) {
        return new MSBoundingBox(
                Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2),
                Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2)
        );
    }

    @Contract("_, _, _, _, _, _ -> new")
    public static @NotNull MSBoundingBox ofDummy(
            final double minX,
            final double minY,
            final double minZ,
            final double maxX,
            final double maxY,
            final double maxZ
    ) {
        return new MSBoundingBox(
                minX, minY, minZ,
                maxX, maxY, maxZ
        );
    }

    @Contract("_ -> new")
    public static @NotNull MSBoundingBox of(final @NotNull BoundingBox bb) {
        return ofDummy(
                bb.minX(), bb.minY(), bb.minZ(),
                bb.maxX(), bb.maxY(), bb.maxZ()
        );
    }

    @Contract("_ -> new")
    public static @NotNull MSBoundingBox of(final @NotNull org.bukkit.util.BoundingBox bb) {
        return ofDummy(
                bb.getMinX(), bb.getMinY(), bb.getMinZ(),
                bb.getMaxX(), bb.getMaxY(), bb.getMaxZ()
        );
    }

    @Contract("_ -> new")
    public static @NotNull MSBoundingBox of(final @NotNull AABB aabb) {
        return ofDummy(
                aabb.minX, aabb.minY, aabb.minZ,
                aabb.maxX, aabb.maxY, aabb.maxZ
        );
    }

    @Contract("_ -> new")
    public static @NotNull MSBoundingBox of(final @NotNull io.papermc.paper.math.Position pos) {
        return ofDummy(
                pos.x(), pos.y(), pos.z(),
                pos.x(), pos.y(), pos.z()
        );
    }

    @Contract("_ -> new")
    public static @NotNull MSBoundingBox of(final @NotNull Position pos) {
        return ofDummy(
                pos.x(), pos.y(), pos.z(),
                pos.x(), pos.y(), pos.z()
        );
    }

    @Contract("_ -> new")
    public static @NotNull MSBoundingBox of(final @NotNull Vec3i vec3i) {
        return ofDummy(
                vec3i.getX(), vec3i.getY(), vec3i.getZ(),
                vec3i.getX(), vec3i.getY(), vec3i.getZ()
        );
    }

    @Contract("_, _ -> new")
    public static @NotNull MSBoundingBox of(
            final @NotNull io.papermc.paper.math.Position first,
            final @NotNull io.papermc.paper.math.Position second
    ) {
        return of(
                first.x(), first.y(), first.z(),
                second.x(), second.y(), second.z()
        );
    }

    @Contract("_, _ -> new")
    public static @NotNull MSBoundingBox of(
            final @NotNull Position first,
            final @NotNull Position second
    ) {
        return of(
                first.x(), first.y(), first.z(),
                second.x(), second.y(), second.z()
        );
    }

    @Contract("_, _ -> new")
    public static @NotNull MSBoundingBox of(
            final @NotNull Vec3i first,
            final @NotNull Vec3i second
    ) {
        return of(
                first.getX(), first.getY(), first.getZ(),
                second.getX(), second.getY(), second.getZ()
        );
    }

    @Contract("_, _, _, _ -> new")
    public static @NotNull MSBoundingBox ofSize(
            final @NotNull Position pos,
            final double dx,
            final double dy,
            final double dz
    ) {
        return ofSize(
                pos.x(), pos.y(), pos.z(),
                dx, dy, dz
        );
    }

    @Contract("_, _, _, _ -> new")
    public static @NotNull MSBoundingBox ofSize(
            final @NotNull Vec3i vec3i,
            final double dx,
            final double dy,
            final double dz
    ) {
        return ofSize(
                vec3i.getX(), vec3i.getY(), vec3i.getZ(),
                dx, dy, dz
        );
    }

    @Contract("_, _, _, _ -> new")
    public static @NotNull MSBoundingBox ofSize(
            final @NotNull io.papermc.paper.math.Position pos,
            final double dx,
            final double dy,
            final double dz
    ) {
        return ofSize(
                pos.x(), pos.y(), pos.z(),
                dx, dy, dz
        );
    }

    @Contract("_, _, _, _, _, _ -> new")
    public static @NotNull MSBoundingBox ofSize(
            final double x,
            final double y,
            final double z,
            final double dx,
            final double dy,
            final double dz
    ) {
        return of(
                x - dx / 2,
                y - dy / 2,
                z - dz / 2,
                x + dx / 2,
                y + dy / 2,
                z + dz / 2
        );
    }

    public double minX() {
        return this.minX;
    }

    @Contract("_ -> new")
    public @NotNull MSBoundingBox minX(final double minX) {
        return ofDummy(
                Math.min(minX, this.maxX), this.minY, this.minZ,
                Math.max(minX, this.maxX), this.maxY, this.maxZ
        );
    }

    public double minY() {
        return this.minY;
    }

    @Contract("_ -> new")
    public @NotNull MSBoundingBox minY(final double minY) {
        return ofDummy(
                this.minX, Math.min(minY, this.maxY), this.minZ,
                this.maxX, Math.max(minY, this.maxY), this.maxZ
        );
    }

    public double minZ() {
        return this.minZ;
    }

    @Contract("_ -> new")
    public @NotNull MSBoundingBox minZ(final double minZ) {
        return ofDummy(
                this.minX, this.minY, Math.min(minZ, this.maxZ),
                this.maxX, this.maxY, Math.max(minZ, this.maxZ)
        );
    }

    public double maxX() {
        return this.maxX;
    }

    @Contract("_ -> new")
    public @NotNull MSBoundingBox maxX(final double maxX) {
        return ofDummy(
                Math.min(maxX, this.minX), this.minY, this.minZ,
                Math.max(maxX, this.minX), this.maxY, this.maxZ
        );
    }

    public double maxY() {
        return this.maxY;
    }

    @Contract("_ -> new")
    public @NotNull MSBoundingBox maxY(final double maxY) {
        return ofDummy(
                this.minX, Math.min(maxY, this.minY), this.minZ,
                this.maxX, Math.max(maxY, this.minY), this.maxZ
        );
    }

    public double maxZ() {
        return this.maxZ;
    }

    @Contract("_ -> new")
    public @NotNull MSBoundingBox maxZ(final double maxZ) {
        return ofDummy(
                this.minX, this.minY, Math.min(maxZ, this.minZ),
                this.maxX, this.maxY, Math.max(maxZ, this.minZ)
        );
    }

    @Contract("_ -> new")
    public @NotNull MSBoundingBox contract(final double value) {
        return this.contract(value, value, value);
    }

    @Contract("_, _, _ -> new")
    public @NotNull MSBoundingBox contract(
            final double x,
            final double y,
            final double z
    ) {
        double minX = this.minX;
        double minY = this.minY;
        double minZ = this.minZ;
        double maxX = this.maxX;
        double maxY = this.maxY;
        double maxZ = this.maxZ;

        if (x < 0.0D) {
            minX -= x;
        } else if (x > 0.0D) {
            maxX -= x;
        }

        if (y < 0.0D) {
            minY -= y;
        } else if (y > 0.0D) {
            maxY -= y;
        }

        if (z < 0.0D) {
            minZ -= z;
        } else if (z > 0.0D) {
            maxZ -= z;
        }

        return of(
                minX, minY, minZ,
                maxX, maxY, maxZ
        );
    }

    @Contract("_ -> new")
    public @NotNull MSBoundingBox expandTowards(final double value) {
        return this.expandTowards(value, value, value);
    }

    @Contract("_, _, _ -> new")
    public @NotNull MSBoundingBox expandTowards(
            final double x,
            final double y,
            final double z
    ) {
        double minX = this.minX;
        double minY = this.minY;
        double minZ = this.minZ;
        double maxX = this.maxX;
        double maxY = this.maxY;
        double maxZ = this.maxZ;

        if (x < 0.0D) {
            minX += x;
        } else if (x > 0.0D) {
            maxX += x;
        }

        if (y < 0.0D) {
            minY += y;
        } else if (y > 0.0D) {
            maxY += y;
        }

        if (z < 0.0D) {
            minZ += z;
        } else if (z > 0.0D) {
            maxZ += z;
        }

        return of(
                minX, minY, minZ,
                maxX, maxY, maxZ
        );
    }

    @Contract("_ -> new")
    public @NotNull MSBoundingBox inflate(final double value) {
        return this.inflate(value, value, value);
    }

    @Contract("_, _, _ -> new")
    public @NotNull MSBoundingBox inflate(
            final double x,
            final double y,
            final double z
    ) {
        return of(
                this.minX - x, this.minY - y, this.minZ - z,
                this.maxX + x, this.maxY + y, this.maxZ + z
        );
    }

    @Contract("_ -> new")
    public @NotNull MSBoundingBox intersect(final @NotNull AABB aabb) {
        return this.intersect(
                aabb.minX, aabb.minY, aabb.minZ,
                aabb.maxX, aabb.maxY, aabb.maxZ
        );
    }

    @Contract("_ -> new")
    public @NotNull MSBoundingBox intersect(final @NotNull BoundingBox bb) {
        return this.intersect(
                bb.minX(), bb.minY(), bb.minZ(),
                bb.maxX(), bb.maxY(), bb.maxZ()
        );
    }

    @Contract("_ -> new")
    public @NotNull MSBoundingBox intersect(final @NotNull org.bukkit.util.BoundingBox bb) {
        return this.intersect(
                bb.getMinX(), bb.getMinY(), bb.getMinZ(),
                bb.getMaxX(), bb.getMaxY(), bb.getMaxZ()
        );
    }

    @Contract("_ -> new")
    public @NotNull MSBoundingBox intersect(final @NotNull MSBoundingBox msbb) {
        return this.intersect(
                msbb.minX, msbb.minY, msbb.minZ,
                msbb.maxX, msbb.maxY, msbb.maxZ
        );
    }

    @Contract("_, _, _, _, _, _ -> new")
    public @NotNull MSBoundingBox intersect(
            final double minX,
            final double minY,
            final double minZ,
            final double maxX,
            final double maxY,
            final double maxZ
    ) {
        return ofDummy(
                Math.max(this.minX, minX), Math.max(this.minY, minY), Math.max(this.minZ, minZ),
                Math.min(this.maxX, maxX), Math.min(this.maxY, maxY), Math.min(this.maxZ, maxZ)
        );
    }

    @Contract("_ -> new")
    public @NotNull MSBoundingBox move(final @NotNull Position pos) {
        return this.move(pos.x(), pos.y(), pos.z());
    }

    @Contract("_ -> new")
    public @NotNull MSBoundingBox move(final @NotNull Vec3i vec3i) {
        return this.move(vec3i.getX(), vec3i.getY(), vec3i.getZ());
    }

    @Contract("_ -> new")
    public @NotNull MSBoundingBox move(final @NotNull io.papermc.paper.math.Position pos) {
        return this.move(pos.x(), pos.y(), pos.z());
    }

    @Contract("_, _, _ -> new")
    public @NotNull MSBoundingBox move(
            final double x,
            final double y,
            final double z
    ) {
        return ofDummy(
                this.minX + x, this.minY + y, this.minZ + z,
                this.maxX + x, this.maxY + y, this.maxZ + z
        );
    }

    public double centerX() {
        return this.minX + 0.5D * this.sizeX();
    }

    public double centerY() {
        return this.minY + 0.5D * this.sizeY();
    }

    public double centerZ() {
        return this.minZ + 0.5D * this.sizeZ();
    }

    public double sizeX() {
        return this.maxX - this.minX;
    }

    public double sizeY() {
        return this.maxY - this.minY;
    }

    public double sizeZ() {
        return this.maxZ - this.minZ;
    }

    public double volume() {
        return this.sizeX() * this.sizeY() * this.sizeZ();
    }

    @Contract(" -> new")
    public @NotNull MSPosition getCenter() {
        return MSPosition.of(this.centerX(), this.centerY(), this.centerZ());
    }

    @Contract("_ -> new")
    public @NotNull MSPosition getCenter(final @NotNull World world) {
        return MSPosition.of(
                world,
                this.centerX(), this.centerY(), this.centerZ()
        );
    }

    public net.minecraft.world.level.block.state.BlockState @NotNull [] getNMSBlockStates(final @NotNull ServerLevel serverLevel) {
        return this.getBlockStates(
                net.minecraft.world.level.block.state.BlockState.class,
                serverLevel.getWorld()
        );
    }

    public BlockState @NotNull [] getBlockStates(final @NotNull World world) {
        return this.getBlockStates(BlockState.class, world);
    }

    public Position @NotNull [] getPositions() {
        return this.getPositions(Position.class);
    }

    public Vec3i @NotNull [] getVec3i() {
        return this.getPositions(Vec3i.class);
    }

    public BlockPos @NotNull [] getBlockPositions() {
        return this.getPositions(BlockPos.class);
    }

    public io.papermc.paper.math.Position @NotNull [] getPaperPositions() {
        return this.getPositions(io.papermc.paper.math.Position.class);
    }

    public Entity @NotNull [] getEntities(final @NotNull World world) {
        return this.getEntities(world, null);
    }

    public Entity @NotNull [] getEntities(
            final @NotNull World world,
            final @Nullable Predicate<Entity> predicate
    ) {
        final var nmsEntities =
                this.getNMSEntities(
                        ((CraftWorld) world).getHandle(),
                        entity ->
                                predicate == null
                                || predicate.test(entity.getBukkitEntity())
                );
        final int size = nmsEntities.size();
        final Entity[] bukkitEntities = new Entity[size];

        for (int i = 0; i < size; ++i) {
            bukkitEntities[i] = nmsEntities.get(i).getBukkitEntity();
        }

        return bukkitEntities;
    }

    public @NotNull List<net.minecraft.world.entity.Entity> getNMSEntities(final @NotNull ServerLevel level) {
        return this.getNMSEntities(level, null);
    }

    public @NotNull List<net.minecraft.world.entity.Entity> getNMSEntities(
            final @NotNull ServerLevel level,
            final @Nullable Predicate<net.minecraft.world.entity.Entity> predicate
    ) {
        AsyncCatcher.catchOp("getNearbyEntities");

        final var list = new ArrayList<net.minecraft.world.entity.Entity>();
        final EntityLookup entityLookup = level.getEntityLookup();

        final int minChunkX = ((int) Math.floor(this.minX) - 2) >> 4;
        final int minChunkZ = ((int) Math.floor(this.minZ) - 2) >> 4;
        final int maxChunkX = ((int) Math.floor(this.maxX) + 2) >> 4;
        final int maxChunkZ = ((int) Math.floor(this.maxZ) + 2) >> 4;

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

                        if (
                                chunk == null
                                || !chunk.status.isOrAfter(FullChunkStatus.FULL)
                        ) continue;

                        chunk.getEntities((net.minecraft.world.entity.Entity) null, this.toAABB(), list, predicate);
                    }
                }
            }
        }

        return list;
    }

    public boolean intersects(final @NotNull AABB aabb) {
        return this.intersects(
                aabb.minX, aabb.minY, aabb.minZ,
                aabb.maxX, aabb.maxY, aabb.maxZ
        );
    }

    public boolean intersects(final @NotNull BoundingBox bb) {
        return this.intersects(
                bb.minX(), bb.minY(), bb.minZ(),
                bb.maxX(), bb.maxY(), bb.maxZ()
        );
    }

    public boolean intersects(final @NotNull org.bukkit.util.BoundingBox bb) {
        return this.intersects(
                bb.getMinX(), bb.getMinY(), bb.getMinZ(),
                bb.getMaxX(), bb.getMaxY(), bb.getMaxZ()
        );
    }

    public boolean intersects(final @NotNull MSBoundingBox msbb) {
        return this.intersects(
                msbb.minX, msbb.minY, msbb.minZ,
                msbb.maxX, msbb.maxY, msbb.maxZ
        );
    }

    public boolean intersects(
            final double minX,
            final double minY,
            final double minZ,
            final double maxX,
            final double maxY,
            final double maxZ
    ) {
        return this.maxX >= minX
                && this.maxY >= minY
                && this.maxZ >= minZ
                && this.minX <= maxX
                && this.minY <= maxY
                && this.minZ <= maxZ;
    }

    public boolean contains(final @NotNull Position pos) {
        return this.contains(pos.x(), pos.y(), pos.z());
    }

    public boolean contains(final @NotNull Vec3i vec3i) {
        return this.contains(vec3i.getX(), vec3i.getY(), vec3i.getZ());
    }

    public boolean contains(final @NotNull io.papermc.paper.math.Position pos) {
        return this.contains(pos.x(), pos.y(), pos.z());
    }

    public boolean contains(
            final double x,
            final double y,
            final double z
    ) {
        return x >= this.minX && x <= this.maxX
                && y >= this.minY && y <= this.maxY
                && z >= this.minZ && z <= this.maxZ;
    }

    public boolean hasNaN() {
        return Double.isNaN(this.minX)
                || Double.isNaN(this.minY)
                || Double.isNaN(this.minZ)
                || Double.isNaN(this.maxX)
                || Double.isNaN(this.maxY)
                || Double.isNaN(this.maxZ);
    }

    public boolean hasInfinite() {
        return Double.isInfinite(this.minX)
                || Double.isInfinite(this.minY)
                || Double.isInfinite(this.minZ)
                || Double.isInfinite(this.maxX)
                || Double.isInfinite(this.maxY)
                || Double.isInfinite(this.maxZ);
    }

    public boolean isAllFinite() {
        return Double.isFinite(this.minX)
                && Double.isFinite(this.minY)
                && Double.isFinite(this.minZ)
                && Double.isFinite(this.maxX)
                && Double.isFinite(this.maxY)
                && Double.isFinite(this.maxZ);
    }

    @Contract(" -> new")
    public @NotNull AABB toAABB() {
        return new AABB(
                this.minX, this.minY, this.minZ,
                this.maxX, this.maxY, this.maxZ
        );
    }

    @Contract(" -> new")
    public @NotNull BoundingBox toBB() {
        return new BoundingBox(
                (int) this.minX, (int) this.minY, (int) this.minZ,
                (int) this.maxX, (int) this.maxY, (int) this.maxZ
        );
    }

    @Contract(" -> new")
    public @NotNull org.bukkit.util.BoundingBox toBukkitBB() {
        return new org.bukkit.util.BoundingBox(
                this.minX, this.minY, this.minZ,
                this.maxX, this.maxY, this.maxZ
        );
    }

    @Override
    public int hashCode() {
        long l = Double.doubleToLongBits(this.minX);
        int i = (int)(l ^ l >>> 32);
        l = Double.doubleToLongBits(this.minY);
        i = 31 * i + (int)(l ^ l >>> 32);
        l = Double.doubleToLongBits(this.minZ);
        i = 31 * i + (int)(l ^ l >>> 32);
        l = Double.doubleToLongBits(this.maxX);
        i = 31 * i + (int)(l ^ l >>> 32);
        l = Double.doubleToLongBits(this.maxY);
        i = 31 * i + (int)(l ^ l >>> 32);
        l = Double.doubleToLongBits(this.maxZ);
        return 31 * i + (int)(l ^ l >>> 32);
    }

    @Override
    public boolean equals(final Object object) {
        return this == object
                || (
                        object instanceof final MSBoundingBox that
                        && Double.compare(that.minX, this.minX) == 0
                        && Double.compare(that.minY, this.minY) == 0
                        && Double.compare(that.minZ, this.minZ) == 0
                        && Double.compare(that.maxX, this.maxX) == 0
                        && Double.compare(that.maxY, this.maxY) == 0
                        && Double.compare(that.maxZ, this.maxZ) == 0
                );
    }

    @Override
    public @NotNull String toString() {
        return "MSBoundingBox["
                        + this.minX + ", "
                        + this.minY + ", "
                        + this.minZ
                + "] -> ["
                        + this.maxX + ", "
                        + this.maxY + ", "
                        + this.maxZ
                + "]";
    }

    @SuppressWarnings("unchecked")
    private <T> T @NotNull [] getPositions(final @NotNull Class<T> clazz) throws IllegalArgumentException {
        final boolean isPosition = clazz.isAssignableFrom(Position.class);
        final boolean isVec3i = clazz.isAssignableFrom(Vec3i.class);
        final boolean isBlockPos = clazz.isAssignableFrom(BlockPos.class);

        if (
                !isPosition
                && !isVec3i
                && !isBlockPos
                && !clazz.isAssignableFrom(io.papermc.paper.math.Position.class)
        ) throw new IllegalArgumentException("Class must be assignable from Position or Vec3i or BlockPos or io.papermc.paper.math.Position");

        final int minX = (int) this.minX;
        final int minY = (int) this.minY;
        final int minZ = (int) this.minZ;
        final int offsetX = (int) (Math.abs(this.minX - this.maxX) + 1);
        final int offsetY = (int) (Math.abs(this.minY - this.maxY) + 1);
        final int offsetZ = (int) (Math.abs(this.minZ - this.maxZ) + 1);
        final var array = (T[]) Array.newInstance(clazz, offsetX * offsetY * offsetZ);

        int i = 0;

        for (int x = 0; x < offsetX; ++x) {
            for (int y = 0; y < offsetY; ++y) {
                for (int z = 0; z < offsetZ; ++z) {
                    if (isPosition) {
                        array[i++] = (T) new Location(
                                null,
                                minX + x,
                                minY + y,
                                minZ + z
                        );
                    } else if (isVec3i) {
                        array[i++] = (T) new Vec3i(
                                minX + x,
                                minY + y,
                                minZ + z
                        );
                    } else if (isBlockPos) {
                        array[i++] = (T) new BlockPos(
                                minX + x,
                                minY + y,
                                minZ + z
                        );
                    }else {
                        array[i++] = (T) MSPosition.of(
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

    @SuppressWarnings("unchecked")
    private <T> T @NotNull [] getBlockStates(
            final @NotNull Class<T> clazz,
            final @NotNull World world
    ) throws IllegalArgumentException {
        final boolean isBukkit = clazz.isAssignableFrom(BlockState.class);

        if (
                !isBukkit
                && !clazz.isAssignableFrom(net.minecraft.world.level.block.state.BlockState.class)
        ) throw new IllegalArgumentException("Class must be assignable from org.bukkit.block.BlockState or net.minecraft.world.level.block.state.BlockState");

        final ServerLevel serverLevel = ((CraftWorld) world).getHandle();
        final int offsetX = (int) Math.abs(this.minX - this.maxX) + 1;
        final int offsetY = (int) Math.abs(this.minY - this.maxY) + 1;
        final int offsetZ = (int) Math.abs(this.minZ - this.maxZ) + 1;
        final T[] blockStates = (T[]) Array.newInstance(clazz, offsetX * offsetY * offsetZ);

        int i = 0;

        for (int x = 0; x < offsetX; ++x) {
            for (int y = 0; y < offsetY; ++y) {
                for (int z = 0; z < offsetZ; ++z) {
                    if (isBukkit) {
                        blockStates[i++] = (T) world.getBlockState(
                                (int) this.minX + x,
                                (int) this.minY + y,
                                (int) this.minZ + z
                        );
                    } else {
                        blockStates[i++] = (T) serverLevel.getBlockState(
                                new BlockPos(
                                        (int) this.minX + x,
                                        (int) this.minY + y,
                                        (int) this.minZ + z
                                )
                        );
                    }
                }
            }
        }

        return blockStates;
    }
}
