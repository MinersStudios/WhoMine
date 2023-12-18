package com.minersstudios.mscore.location;

import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.utility.LocationUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import javax.annotation.concurrent.Immutable;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@SuppressWarnings("UnstableApiUsage")
@Immutable
public final class MSPosition implements Cloneable {
    private Reference<World> world;
    private double x;
    private double y;
    private double z;
    private float pitch;
    private float yaw;

    private MSPosition(
            final @Nullable World world,
            final double x,
            final double y,
            final double z,
            final float pitch,
            final float yaw
    ) {
        this.world =
                world == null
                ? null
                : new WeakReference<>(world);
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    @Contract("_, _, _ -> new")
    public static @NotNull MSPosition of(
            final double x,
            final double y,
            final double z
    ) {
        return of(
                null,
                x, y, z
        );
    }

    @Contract("_, _, _, _ -> new")
    public static @NotNull MSPosition of(
            final @Nullable World world,
            final double x,
            final double y,
            final double z
    ) {
        return of(
                world,
                x, y, z,
                0.0f, 0.0f
        );
    }

    @Contract("_, _, _, _, _ -> new")
    public static @NotNull MSPosition of(
            final double x,
            final double y,
            final double z,
            final float pitch,
            final float yaw
    ) {
        return of(
                null,
                x, y, z,
                pitch, yaw
        );
    }

    @Contract("_, _, _, _, _, _ -> new")
    public static @NotNull MSPosition of(
            final @Nullable World world,
            final double x,
            final double y,
            final double z,
            final float pitch,
            final float yaw
    ) {
        return new MSPosition(
                world,
                x, y, z,
                pitch, yaw
        );
    }

    @Contract("_ -> new")
    public static @NotNull MSPosition of(final @NotNull MSVector vec) {
        return of(
                null,
                vec
        );
    }

    @Contract("_, _ -> new")
    public static @NotNull MSPosition of(
            final @Nullable World world,
            final @NotNull MSVector vec
    ) {
        return of(
                world,
                vec,
                0.0f, 0.0f
        );
    }

    @Contract("_, _, _ -> new")
    public static @NotNull MSPosition of(
            final @NotNull MSVector vec,
            final float pitch,
            final float yaw
    ) {
        return of(
                null,
                vec,
                pitch, yaw
        );
    }

    @Contract("_, _, _, _ -> new")
    public static @NotNull MSPosition of(
            final @Nullable World world,
            final @NotNull MSVector vec,
            final float pitch,
            final float yaw
    ) {
        return of(
                world,
                vec.x(), vec.y(), vec.z(),
                pitch, yaw
        );
    }

    @Contract("_ -> new")
    public static @NotNull MSPosition of(final @NotNull Vector vec) {
        return of(
                null,
                vec
        );
    }

    @Contract("_, _ -> new")
    public static @NotNull MSPosition of(
            final @Nullable World world,
            final @NotNull Vector vec
    ) {
        return of(
                world,
                vec,
                0.0f, 0.0f
        );
    }

    @Contract("_, _, _ -> new")
    public static @NotNull MSPosition of(
            final @NotNull Vector vec,
            final float pitch,
            final float yaw
    ) {
        return of(
                null,
                vec,
                pitch, yaw
        );
    }

    @Contract("_, _, _, _ -> new")
    public static @NotNull MSPosition of(
            final @Nullable World world,
            final @NotNull Vector vec,
            final float pitch,
            final float yaw
    ) {
        return of(
                world,
                vec.getX(), vec.getY(), vec.getZ(),
                pitch, yaw
        );
    }

    @Contract("_ -> new")
    public static @NotNull MSPosition of(final @NotNull Vec3i vec) {
        return of(
                null,
                vec
        );
    }

    @Contract("_, _ -> new")
    public static @NotNull MSPosition of(
            final @Nullable World world,
            final @NotNull Vec3i vec
    ) {
        return of(
                world,
                vec,
                0.0f, 0.0f
        );
    }

    @Contract("_, _, _ -> new")
    public static @NotNull MSPosition of(
            final @NotNull Vec3i vec,
            final float pitch,
            final float yaw
    ) {
        return of(
                null,
                vec,
                pitch, yaw
        );
    }

    @Contract("_, _, _, _ -> new")
    public static @NotNull MSPosition of(
            final @Nullable World world,
            final @NotNull Vec3i vec,
            final float pitch,
            final float yaw
    ) {
        return of(
                world,
                vec.getX(), vec.getY(), vec.getZ(),
                pitch, yaw
        );
    }

    @Contract("_ -> new")
    public static @NotNull MSPosition of(final @NotNull Position pos) {
        return of(
                null,
                pos
        );
    }

    @Contract("_, _ -> new")
    public static @NotNull MSPosition of(
            final @Nullable World world,
            final @NotNull Position pos
    ) {
        return of(
                world,
                pos,
                0.0f, 0.0f
        );
    }

    @Contract("_, _, _ -> new")
    public static @NotNull MSPosition of(
            final @NotNull Position pos,
            final float pitch,
            final float yaw
    ) {
        return of(
                null,
                pos,
                pitch, yaw
        );
    }

    @Contract("_, _, _, _ -> new")
    public static @NotNull MSPosition of(
            final @Nullable World world,
            final @NotNull Position pos,
            final float pitch,
            final float yaw
    ) {
        return of(
                world,
                pos.x(), pos.y(), pos.z(),
                pitch, yaw
        );
    }

    @Contract("_ -> new")
    public static @NotNull MSPosition of(final @NotNull Location location) {
        return of(
                location.getWorld(),
                location
        );
    }

    @Contract("_, _ -> new")
    public static @NotNull MSPosition of(
            final @Nullable World world,
            final @NotNull Location location
    ) {
        return of(
                world,
                location,
                0.0f, 0.0f
        );
    }

    @Contract("_, _, _ -> new")
    public static @NotNull MSPosition of(
            final @NotNull Location location,
            final float pitch,
            final float yaw
    ) {
        return of(
                location.getWorld(),
                location,
                pitch, yaw
        );
    }

    @Contract("_, _, _, _ -> new")
    public static @NotNull MSPosition of(
            final @Nullable World world,
            final @NotNull Location location,
            final float pitch,
            final float yaw
    ) {
        return of(
                world,
                location.getX(), location.getY(), location.getZ(),
                pitch, yaw
        );
    }

    @Contract("_ -> new")
    public static @NotNull MSPosition of(final @NotNull io.papermc.paper.math.Position pos) {
        return of(
                null,
                pos
        );
    }

    @Contract("_, _ -> new")
    public static @NotNull MSPosition of(
            final @Nullable World world,
            final @NotNull io.papermc.paper.math.Position pos
    ) {
        return of(
                world,
                pos,
                0.0f, 0.0f
        );
    }

    @Contract("_, _, _ -> new")
    public static @NotNull MSPosition of(
            final @NotNull io.papermc.paper.math.Position pos,
            final float pitch,
            final float yaw
    ) {
        return of(
                null,
                pos,
                pitch, yaw
        );
    }

    @Contract("_, _, _, _ -> new")
    public static @NotNull MSPosition of(
            final @Nullable World world,
            final @NotNull io.papermc.paper.math.Position pos,
            final float pitch,
            final float yaw
    ) {
        return of(
                world,
                pos.x(), pos.y(), pos.z(),
                pitch, yaw
        );
    }

    public @UnknownNullability World world() {
        return this.world == null
                ? null
                : this.world.get();
    }

    public @NotNull MSPosition world(final @Nullable World world) {
        final MSPosition clone = this.clone();
        clone.world =
                world == null
                ? null
                : new WeakReference<>(world);
        return clone;
    }

    public double x() {
        return this.x;
    }

    public @NotNull MSPosition x(final double x) {
        final MSPosition clone = this.clone();
        clone.x = x;
        return clone;
    }

    public double y() {
        return this.y;
    }

    public @NotNull MSPosition y(final double y) {
        final MSPosition clone = this.clone();
        clone.y = y;
        return clone;
    }

    public double z() {
        return this.z;
    }

    public @NotNull MSPosition z(final double z) {
        final MSPosition clone = this.clone();
        clone.z = z;
        return clone;
    }

    public float pitch() {
        return this.pitch;
    }

    public @NotNull MSPosition pitch(final float pitch) {
        final MSPosition clone = this.clone();
        clone.pitch = pitch;
        return clone;
    }

    public float yaw() {
        return this.yaw;
    }

    public @NotNull MSPosition yaw(final float yaw) {
        final MSPosition clone = this.clone();
        clone.yaw = yaw;
        return clone;
    }

    public int blockX() {
        return (int) this.x;
    }

    public int blockY() {
        return (int) this.y;
    }

    public int blockZ() {
        return (int) this.z;
    }

    public @NotNull MSPosition offset(final @NotNull MSVector vec) {
        return this.offset(vec.x(), vec.y(), vec.z());
    }

    public @NotNull MSPosition offset(final @NotNull Vector vec) {
        return this.offset(vec.getX(), vec.getY(), vec.getZ());
    }

    public @NotNull MSPosition offset(final @NotNull Vec3i vec) {
        return this.offset(vec.getX(), vec.getY(), vec.getZ());
    }

    public @NotNull MSPosition offset(final @NotNull MSPosition pos) {
        return this.offset(pos.x(), pos.y(), pos.z());
    }

    public @NotNull MSPosition offset(final @NotNull Position pos) {
        return this.offset(pos.x(), pos.y(), pos.z());
    }

    public @NotNull MSPosition offset(final @NotNull io.papermc.paper.math.Position pos) {
        return this.offset(pos.x(), pos.y(), pos.z());
    }

    public @NotNull MSPosition offset(final double value) {
        return this.offset(value, value, value);
    }

    public @NotNull MSPosition offset(
            final double x,
            final double y,
            final double z
    ) {
        final MSPosition clone = this.clone();
        clone.x += x;
        clone.y += y;
        clone.z += z;
        return clone;
    }

    public @NotNull MSPosition directionalPitchOffset(
            final double value,
            final float pitch
    ) {
        return this.directionalPitchOffset(
                value, value, value,
                pitch
        );
    }

    public @NotNull MSPosition directionalPitchOffset(
            final double x,
            final double y,
            final double z,
            final float pitch
    ) {
        return this
                .yaw(Float.NaN)
                .pitch(pitch)
                .directionalOffset(x, y, z);
    }

    public @NotNull MSPosition directionalYawOffset(
            final double value,
            final float yaw
    ) {
        return this.directionalYawOffset(
                value, value, value,
                yaw
        );
    }

    public @NotNull MSPosition directionalYawOffset(
            final double x,
            final double y,
            final double z,
            final float yaw
    ) {
        return this
                .pitch(Float.NaN)
                .yaw(yaw)
                .directionalOffset(x, y, z);
    }

    public @NotNull MSPosition directionalOffset(final double value) {
        return this.directionalOffset(value, value, value);
    }

    public @NotNull MSPosition directionalOffset(
            final double x,
            final double y,
            final double z
    ) {
        MSVector vector = MSVector.of(x, y, -z);

        if (!Float.isNaN(this.yaw)) {
            final float normalizedYaw = LocationUtils.normalize360(this.yaw);

            vector = vector.rotateAroundY(
                    Math.toRadians(
                            (normalizedYaw >= 0 && normalizedYaw <= 45)
                            || (normalizedYaw >= 180 && normalizedYaw <= 225)
                                    ? normalizedYaw - 180
                                    : normalizedYaw
                    )
            );
        }

        if (!Float.isNaN(this.pitch)) {
            final float normalizedPitch = LocationUtils.normalize360(this.pitch);

            vector = vector.rotateAroundX(
                    Math.toRadians(
                            (normalizedPitch >= 0 && normalizedPitch <= 45)
                            || (normalizedPitch >= 180 && normalizedPitch <= 225)
                                    ? normalizedPitch - 180
                                    : normalizedPitch
                    )
            );
        }

        return this.offset(vector);
    }

    public @NotNull MSPosition multiply(final double multiplier) {
        return this.multiply(multiplier, multiplier, multiplier);
    }

    public @NotNull MSPosition multiply(
            final double x,
            final double y,
            final double z
    ) {
        final MSPosition clone = this.clone();

        clone.x *= x;
        clone.y *= y;
        clone.z *= z;

        return clone;
    }

    public @NotNull MSPosition divide(final double divisor) {
        return this.divide(divisor, divisor, divisor);
    }

    public @NotNull MSPosition divide(
            final double x,
            final double y,
            final double z
    ) {
        final MSPosition clone = this.clone();

        clone.x /= x;
        clone.y /= y;
        clone.z /= z;

        return clone;
    }

    public @NotNull MSPosition zero() {
        final MSPosition clone = this.clone();

        clone.x = 0.0d;
        clone.y = 0.0d;
        clone.z = 0.0d;

        return clone;
    }

    public @NotNull MSPosition block() {
        final MSPosition clone = this.clone();

        clone.x = this.blockX();
        clone.y = this.blockY();
        clone.z = this.blockZ();

        return clone;
    }

    public @NotNull MSPosition center() {
        final MSPosition clone = this.clone();

        clone.x = this.blockX() + 0.5;
        clone.y = this.blockY() + 0.5;
        clone.z = this.blockZ() + 0.5;

        return clone;
    }

    public @NotNull MSPosition highest() {
        return this.highest(HeightMap.WORLD_SURFACE);
    }

    public @NotNull MSPosition highest(final @NotNull HeightMap heightMap) {
        final World world = this.world();

        if (world == null) {
            MSLogger.warning("World is null MSPosition#highest(HeightMap)");
            return this;
        }

        return this.highest(
                world,
                heightMap
        );
    }

    public @NotNull MSPosition highest(
            final @NotNull World world,
            final @NotNull HeightMap heightMap
    ) {
        final MSPosition clone = this.clone();
        clone.y = world.getHighestBlockYAt(
                this.blockX(),
                this.blockZ(),
                heightMap
        );
        return clone;
    }

    public double length() {
        return Math.sqrt(this.lengthSquared());
    }

    public double lengthSquared() {
        return Math.pow(this.x, 2.0d)
                + Math.pow(this.y, 2.0d)
                + Math.pow(this.z, 2.0d);
    }

    public double distance(final @NotNull MSVector vec) {
        return this.distance(vec.x(), vec.y(), vec.z());
    }

    public double distance(final @NotNull Vector vec) {
        return this.distance(vec.getX(), vec.getY(), vec.getZ());
    }

    public double distance(final @NotNull Vec3i vec) {
        return this.distance(vec.getX(), vec.getY(), vec.getZ());
    }

    public double distance(final @NotNull MSPosition pos) {
        return this.distance(pos.x(), pos.y(), pos.z());
    }

    public double distance(final @NotNull Position pos) {
        return this.distance(pos.x(), pos.y(), pos.z());
    }

    public double distance(final @NotNull io.papermc.paper.math.Position pos) {
        return this.distance(pos.x(), pos.y(), pos.z());
    }

    public double distance(
            final double x,
            final double y,
            final double z
    ) {
        return Math.sqrt(this.distanceSquared(x, y, z));
    }

    public double distanceSquared(final @NotNull MSVector vec) {
        return this.distanceSquared(vec.x(), vec.y(), vec.z());
    }

    public double distanceSquared(final @NotNull Vector vec) {
        return this.distanceSquared(vec.getX(), vec.getY(), vec.getZ());
    }

    public double distanceSquared(final @NotNull Vec3i vec) {
        return this.distanceSquared(vec.getX(), vec.getY(), vec.getZ());
    }

    public double distanceSquared(final @NotNull MSPosition pos) {
        return this.distanceSquared(pos.x(), pos.y(), pos.z());
    }

    public double distanceSquared(final @NotNull Position pos) {
        return this.distanceSquared(pos.x(), pos.y(), pos.z());
    }

    public double distanceSquared(final @NotNull io.papermc.paper.math.Position pos) {
        return this.distanceSquared(pos.x(), pos.y(), pos.z());
    }

    public double distanceSquared(
            final double x,
            final double y,
            final double z
    ) {
        return Math.pow(this.x - x, 2.0d)
                + Math.pow(this.y - y, 2.0d)
                + Math.pow(this.z - z, 2.0d);
    }

    public @UnknownNullability LevelChunk getNMSChunk() {
        final World world = this.world();
        return world == null
                ? null
                : this.getNMSChunk(((CraftWorld) world).getHandle());
    }

    public @NotNull LevelChunk getNMSChunk(final @NotNull ServerLevel level) {
        return level.getChunk(
                this.blockX() >> 4,
                this.blockZ() >> 4
        );
    }

    public @UnknownNullability Chunk getChunk() {
        final World world = this.world();
        return world == null
                ? null
                : this.getChunk(world);
    }

    public @NotNull Chunk getChunk(final @NotNull World world) {
        return world.getChunkAt(
                this.blockX() >> 4,
                this.blockZ() >> 4
        );
    }

    public @UnknownNullability BlockState getNMSBlockState() {
        final World world = this.world();
        return world == null
                ? null
                : this.getNMSBlockState(((CraftWorld) world).getHandle());
    }

    public @NotNull BlockState getNMSBlockState(final @NotNull ServerLevel level) {
        return level.getBlockState(
                new BlockPos(
                        this.blockX(),
                        this.blockY(),
                        this.blockZ()
                )
        );
    }

    public @UnknownNullability Block getBlock() {
        final World world = this.world();
        return world == null
                ? null
                : this.getBlock(world);
    }

    public @NotNull Block getBlock(final @NotNull World world) {
        return world.getBlockAt(
                this.blockX(),
                this.blockY(),
                this.blockZ()
        );
    }

    public @NotNull List<net.minecraft.world.entity.Entity> getNearbyNMSEntities() {
        final World world = this.world();
        return world == null
                ? Collections.emptyList()
                : this.getNearbyNMSEntities(((CraftWorld) world).getHandle());
    }

    public @NotNull List<net.minecraft.world.entity.Entity> getNearbyNMSEntities(final @NotNull ServerLevel level) {
        return this.getNearbyNMSEntities(level, null);
    }

    public @NotNull List<net.minecraft.world.entity.Entity> getNearbyNMSEntities(
            final @NotNull ServerLevel level,
            final @Nullable Predicate<net.minecraft.world.entity.Entity> predicate
    ) {
        return this.center()
                .getNearbyNMSEntities(
                        level,
                        1.0d,
                        predicate
                );
    }

    public @NotNull List<net.minecraft.world.entity.Entity> getNearbyNMSEntities(final double radius) {
        return this.getNearbyNMSEntities(radius, null);
    }

    public @NotNull List<net.minecraft.world.entity.Entity> getNearbyNMSEntities(
            final double radius,
            final @Nullable Predicate<net.minecraft.world.entity.Entity> predicate
    ) {
        final double diameter = radius * 2.0d;
        return this.getNearbyNMSEntities(
                diameter, diameter, diameter,
                predicate
        );
    }

    public @NotNull List<net.minecraft.world.entity.Entity> getNearbyNMSEntities(
            final @NotNull ServerLevel level,
            final double radius,
            final @Nullable Predicate<net.minecraft.world.entity.Entity> predicate
    ) {
        final double diameter = radius * 2.0d;
        return this.getNearbyNMSEntities(
                level,
                diameter, diameter, diameter,
                predicate
        );
    }

    public @NotNull List<net.minecraft.world.entity.Entity> getNearbyNMSEntities(
            final double dx,
            final double dy,
            final double dz
    ) {
        return this.getNearbyNMSEntities(
                dx, dy, dz,
                null
        );
    }

    public @NotNull List<net.minecraft.world.entity.Entity> getNearbyNMSEntities(
            final double dx,
            final double dy,
            final double dz,
            final @Nullable Predicate<net.minecraft.world.entity.Entity> predicate
    ) {
        final World world = this.world();
        return world == null
                ? Collections.emptyList()
                : this.getNearbyNMSEntities(
                        ((CraftWorld) world).getHandle(),
                        dx, dy, dz,
                        predicate
                );
    }

    public @NotNull List<net.minecraft.world.entity.Entity> getNearbyNMSEntities(
            final @NotNull ServerLevel level,
            final double dx,
            final double dy,
            final double dz
    ) {
        return this.getNearbyNMSEntities(
                level,
                dx, dy, dz,
                null
        );
    }

    public @NotNull List<net.minecraft.world.entity.Entity> getNearbyNMSEntities(
            final @NotNull ServerLevel level,
            final double dx,
            final double dy,
            final double dz,
            final @Nullable Predicate<net.minecraft.world.entity.Entity> predicate
    ) {
        return MSBoundingBox
                .ofSize(
                        this,
                        dx, dy, dz
                ).getNMSEntities(
                        level,
                        predicate
                );
    }

    public Entity @NotNull [] getNearbyEntities() {
        final World world = this.world();
        return world == null
                ? new Entity[0]
                : this.getNearbyEntities(world);
    }

    public Entity @NotNull [] getNearbyEntities(final @NotNull World world) {
        return this.getNearbyEntities(world, null);
    }

    public Entity @NotNull [] getNearbyEntities(
            final @NotNull World world,
            final @Nullable Predicate<Entity> predicate
    ) {
        return this.center()
                .getNearbyEntities(
                        world,
                        1.0d,
                        predicate
                );
    }

    public Entity @NotNull [] getNearbyEntities(final double radius) {
        return this.getNearbyEntities(radius, null);
    }

    public Entity @NotNull [] getNearbyEntities(
            final double radius,
            final @Nullable Predicate<Entity> predicate
    ) {
        final double diameter = radius * 2.0d;
        return this.getNearbyEntities(
                diameter, diameter, diameter,
                predicate
        );
    }

    public Entity @NotNull [] getNearbyEntities(
            final @NotNull World world,
            final double radius,
            final @Nullable Predicate<Entity> predicate
    ) {
        final double diameter = radius * 2.0d;
        return this.getNearbyEntities(
                world,
                diameter, diameter, diameter,
                predicate
        );
    }

    public Entity @NotNull [] getNearbyEntities(
            final double dx,
            final double dy,
            final double dz
    ) {
        return this.getNearbyEntities(
                dx, dy, dz,
                null
        );
    }

    public Entity @NotNull [] getNearbyEntities(
            final double dx,
            final double dy,
            final double dz,
            final @Nullable Predicate<Entity> predicate
    ) {
        final World world = this.world();
        return world == null
                ? new Entity[0]
                : this.getNearbyEntities(
                        world,
                        dx, dy, dz,
                        predicate
                );
    }

    public Entity @NotNull [] getNearbyEntities(
            final @NotNull World world,
            final double dx,
            final double dy,
            final double dz
    ) {
        return this.getNearbyEntities(
                world,
                dx, dy, dz,
                null
        );
    }

    public Entity @NotNull [] getNearbyEntities(
            final @NotNull World world,
            final double dx,
            final double dy,
            final double dz,
            final @Nullable Predicate<Entity> predicate
    ) {
        return MSBoundingBox
                .ofSize(
                        this,
                        dx, dy, dz
                ).getEntities(
                        world,
                        predicate
                );
    }

    public boolean isFinite() {
        return Double.isFinite(this.x)
                && Double.isFinite(this.y)
                && Double.isFinite(this.z)
                && Float.isFinite(this.pitch)
                && Float.isFinite(this.yaw);
    }

    public boolean isWorldLoaded() {
        final World world = this.world();
        return world != null
                && Bukkit.getWorld(world.getUID()) != null;
    }

    public boolean isChunkLoaded() {
        final World world = this.world();
        return world != null
                && this.isChunkLoaded(world);
    }

    public boolean isChunkLoaded(final @NotNull World world) {
        return world.isChunkLoaded(
                (int) this.x >> 4,
                (int) this.z >> 4
        );
    }

    public boolean isChunkGenerated() {
        final World world = this.world();
        return world != null
                && this.isChunkGenerated(world);
    }

    public boolean isChunkGenerated(final @NotNull World world) {
        return world.isChunkGenerated(
                (int) this.x >> 4,
                (int) this.z >> 4
        );
    }

    @Override
    public int hashCode() {
        int hash = 3;
        final World world = this.world == null ? null : this.world.get();

        hash = 19 * hash + (world != null ? world.hashCode() : 0);
        hash = 19 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 19 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        hash = 19 * hash + (int) (Double.doubleToLongBits(this.z) ^ (Double.doubleToLongBits(this.z) >>> 32));
        hash = 19 * hash + Float.floatToIntBits(this.pitch);
        hash = 19 * hash + Float.floatToIntBits(this.yaw);

        return hash;
    }

    @Override
    public boolean equals(final @Nullable Object object) {
        return this == object
                || (
                        object instanceof MSPosition that
                        && Double.compare(that.x, this.x) == 0
                        && Double.compare(that.y, this.y) == 0
                        && Double.compare(that.z, this.z) == 0
                        && Float.compare(that.pitch, this.pitch) == 0
                        && Float.compare(that.yaw, this.yaw) == 0
                        && !Objects.equals(
                                this.world == null ? null : this.world.get(),
                                that.world == null ? null : that.world.get()
                        )
                );
    }

    @Override
    public @NotNull MSPosition clone() {
        try {
            return (MSPosition) super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new AssertionError("An error occurred while cloning an MSPosition", e);
        }
    }

    @Override
    public @NotNull String toString() {
        return "MSPosition{" +
                "world=" + (this.world == null ? null : this.world.get()) +
                ",x=" + this.x +
                ",y=" + this.y +
                ",z=" + this.z +
                ",pitch=" + this.pitch +
                ",yaw=" + this.yaw +
                '}';
    }

    @Contract(" -> new")
    public @NotNull Location toLocation() {
        return new Location(
                this.world == null
                        ? null
                        : this.world.get(),
                this.x, this.y, this.z,
                this.yaw, this.pitch
        );
    }

    @Contract("_ -> new")
    public @NotNull Location toLocation(final @Nullable World world) {
        return new Location(
                world,
                this.x, this.y, this.z,
                this.yaw, this.pitch
        );
    }

    @Contract(" -> new")
    public @NotNull MSVector toMSVector() {
        return MSVector.of(this);
    }

    @Contract(" -> new")
    public @NotNull Vector toVector() {
        return new Vector(this.x, this.y, this.z);
    }

    @Contract(" -> new")
    public @NotNull Vec3 toVec3() {
        return new Vec3(this.x, this.y, this.z);
    }

    @Contract(" -> new")
    public @NotNull Vec3i toVec3i() {
        return new Vec3i((int) this.x, (int) this.y, (int) this.z);
    }

    @Contract(" -> new")
    public @NotNull BlockPos toBlockPos() {
        return new BlockPos(this.blockX(), this.blockY(), this.blockZ());
    }

    @Contract(" -> new")
    public @NotNull MSBoundingBox toBoundingBox() {
        return MSBoundingBox.of(this);
    }

    public void checkFinite() throws IllegalArgumentException {
        if (!this.isFinite()) {
            throw new IllegalArgumentException("Location is not finite");
        }
    }
}
