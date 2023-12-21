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
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
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
    private final double minX;
    private final double minY;
    private final double minZ;
    private final double maxX;
    private final double maxY;
    private final double maxZ;

    private static final int REGION_SHIFT = 5;
    private static final int REGION_MASK = (1 << REGION_SHIFT) - 1;

    /**
     * Creates a new bounding box with the given corner coordinates
     *
     * @param minX The minimum x value
     * @param minY The minimum y value
     * @param minZ The minimum z value
     * @param maxX The maximum x value
     * @param maxY The maximum y value
     * @param maxZ The maximum z value
     */
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

    /**
     * Creates a new bounding box with the given corner coordinates
     *
     * @param x1 The first corner's x value
     * @param y1 The first corner's y value
     * @param z1 The first corner's z value
     * @param x2 The second corner's x value
     * @param y2 The second corner's y value
     * @param z2 The second corner's z value
     * @return A new bounding box with the given corner coordinates
     */
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

    /**
     * Creates a new bounding box with the given corner coordinates
     *
     * @param minX The minimum x value
     * @param minY The minimum y value
     * @param minZ The minimum z value
     * @param maxX The maximum x value
     * @param maxY The maximum y value
     * @param maxZ The maximum z value
     * @return A new bounding box with the given corner coordinates, but without
     *         any checks which corner has lower and higher coordinates
     */
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

    /**
     * Creates a new bounding box based on the given bounding box coordinates
     *
     * @param msbb The bounding box to copy
     * @return A copy of the given bounding box
     */
    @Contract("_ -> new")
    public static @NotNull MSBoundingBox of(final @NotNull MSBoundingBox msbb) {
        return ofDummy(
                msbb.minX, msbb.minY, msbb.minZ,
                msbb.maxX, msbb.maxY, msbb.maxZ
        );
    }

    /**
     * Creates a new bounding box based on the given bounding box coordinates
     *
     * @param bb The bounding box
     * @return New bounding box based on the given bounding box
     */
    @Contract("_ -> new")
    public static @NotNull MSBoundingBox of(final @NotNull BoundingBox bb) {
        return ofDummy(
                bb.minX(), bb.minY(), bb.minZ(),
                bb.maxX(), bb.maxY(), bb.maxZ()
        );
    }

    /**
     * Creates a new bounding box based on the given bounding box coordinates
     *
     * @param bb The bounding box
     * @return New bounding box based on the given bounding box
     */
    @Contract("_ -> new")
    public static @NotNull MSBoundingBox of(final @NotNull org.bukkit.util.BoundingBox bb) {
        return ofDummy(
                bb.getMinX(), bb.getMinY(), bb.getMinZ(),
                bb.getMaxX(), bb.getMaxY(), bb.getMaxZ()
        );
    }

    /**
     * Creates a new bounding box based on the given axis aligned bounding box
     * coordinates
     *
     * @param aabb The axis aligned bounding box
     * @return New bounding box based on the given axis aligned bounding box
     */
    @Contract("_ -> new")
    public static @NotNull MSBoundingBox of(final @NotNull AABB aabb) {
        return ofDummy(
                aabb.minX, aabb.minY, aabb.minZ,
                aabb.maxX, aabb.maxY, aabb.maxZ
        );
    }

    /**
     * Creates a new bounding box based on the given vector coordinates
     *
     * @param vec The vector
     * @return New bounding box based on the given vector
     */
    @Contract("_ -> new")
    public static @NotNull MSBoundingBox of(final @NotNull MSVector vec) {
        return ofDummy(
                vec.x(), vec.y(), vec.z(),
                vec.x(), vec.y(), vec.z()
        );
    }

    /**
     * Creates a new bounding box based on the given vector coordinates
     *
     * @param vec The vector
     * @return New bounding box based on the given vector
     */
    @Contract("_ -> new")
    public static @NotNull MSBoundingBox of(final @NotNull Vector vec) {
        return ofDummy(
                vec.getX(), vec.getY(), vec.getZ(),
                vec.getX(), vec.getY(), vec.getZ()
        );
    }

    /**
     * Creates a new bounding box based on the given vector coordinates
     *
     * @param vec The vector
     * @return New bounding box based on the given vector
     */
    @Contract("_ -> new")
    public static @NotNull MSBoundingBox of(final @NotNull Vec3i vec) {
        return ofDummy(
                vec.getX(), vec.getY(), vec.getZ(),
                vec.getX(), vec.getY(), vec.getZ()
        );
    }

    /**
     * Creates a new bounding box based on the given position coordinates
     *
     * @param pos The position
     * @return New bounding box based on the given position
     */
    @Contract("_ -> new")
    public static @NotNull MSBoundingBox of(final @NotNull MSPosition pos) {
        return ofDummy(
                pos.x(), pos.y(), pos.z(),
                pos.x(), pos.y(), pos.z()
        );
    }

    /**
     * Creates a new bounding box based on the given position coordinates
     *
     * @param pos The position
     * @return New bounding box based on the given position
     */
    @Contract("_ -> new")
    public static @NotNull MSBoundingBox of(final @NotNull Position pos) {
        return ofDummy(
                pos.x(), pos.y(), pos.z(),
                pos.x(), pos.y(), pos.z()
        );
    }

    /**
     * Creates a new bounding box based on the given position coordinates
     *
     * @param pos The position
     * @return New bounding box based on the given position
     */
    @Contract("_ -> new")
    public static @NotNull MSBoundingBox of(final @NotNull io.papermc.paper.math.Position pos) {
        return ofDummy(
                pos.x(), pos.y(), pos.z(),
                pos.x(), pos.y(), pos.z()
        );
    }

    /**
     * Creates a new bounding box whose corners are the corners of the given
     * block
     *
     * @param block The block
     * @return New bounding box based on the given block
     */
    @Contract("_ -> new")
    public static @NotNull MSBoundingBox of(final @NotNull Block block) {
        return ofDummy(
                block.getX(), block.getY(), block.getZ(),
                block.getX() + 1, block.getY() + 1, block.getZ() + 1
        );
    }

    /**
     * Creates a new bounding box with the given corners
     *
     * @param first  The first corner
     * @param second The second corner
     * @return New bounding box based on the given corners
     */
    @Contract("_, _ -> new")
    public static @NotNull MSBoundingBox of(
            final @NotNull MSVector first,
            final @NotNull MSVector second
    ) {
        return of(
                first.x(), first.y(), first.z(),
                second.x(), second.y(), second.z()
        );
    }

    /**
     * Creates a new bounding box with the given corners
     *
     * @param first  The first corner
     * @param second The second corner
     * @return New bounding box based on the given corners
     */
    @Contract("_, _ -> new")
    public static @NotNull MSBoundingBox of(
            final @NotNull Vector first,
            final @NotNull Vector second
    ) {
        return of(
                first.getX(), first.getY(), first.getZ(),
                second.getX(), second.getY(), second.getZ()
        );
    }

    /**
     * Creates a new bounding box with the given corners
     *
     * @param first  The first corner
     * @param second The second corner
     * @return New bounding box based on the given corners
     */
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

    /**
     * Creates a new bounding box with the given corners
     *
     * @param first  The first corner
     * @param second The second corner
     * @return New bounding box based on the given corners
     */
    @Contract("_, _ -> new")
    public static @NotNull MSBoundingBox of(
            final @NotNull MSPosition first,
            final @NotNull MSPosition second
    ) {
        return of(
                first.x(), first.y(), first.z(),
                second.x(), second.y(), second.z()
        );
    }

    /**
     * Creates a new bounding box with the given corners
     *
     * @param first  The first corner
     * @param second The second corner
     * @return New bounding box based on the given corners
     */
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

    /**
     * Creates a new bounding box with the given corners
     *
     * @param first  The first corner
     * @param second The second corner
     * @return New bounding box based on the given corners
     */
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

    /**
     * Creates a new bounding box of the given size centered around the given
     * vector
     *
     * @param vec The center vector
     * @param dx  The x size
     * @param dy  The y size
     * @param dz  The z size
     * @return New bounding box based on the given size and center
     */
    @Contract("_, _, _, _ -> new")
    public static @NotNull MSBoundingBox ofSize(
            final @NotNull MSVector vec,
            final double dx,
            final double dy,
            final double dz
    ) {
        return ofSize(
                vec.x(), vec.y(), vec.z(),
                dx, dy, dz
        );
    }

    /**
     * Creates a new bounding box of the given size centered around the given
     * vector
     *
     * @param vec The center vector
     * @param dx  The x size
     * @param dy  The y size
     * @param dz  The z size
     * @return New bounding box based on the given size and center
     */
    @Contract("_, _, _, _ -> new")
    public static @NotNull MSBoundingBox ofSize(
            final @NotNull Vector vec,
            final double dx,
            final double dy,
            final double dz
    ) {
        return ofSize(
                vec.getX(), vec.getY(), vec.getZ(),
                dx, dy, dz
        );
    }

    /**
     * Creates a new bounding box of the given size centered around the given
     * vector
     *
     * @param vec The center vector
     * @param dx  The x size
     * @param dy  The y size
     * @param dz  The z size
     * @return New bounding box based on the given size and center
     */
    @Contract("_, _, _, _ -> new")
    public static @NotNull MSBoundingBox ofSize(
            final @NotNull Vec3i vec,
            final double dx,
            final double dy,
            final double dz
    ) {
        return ofSize(
                vec.getX(), vec.getY(), vec.getZ(),
                dx, dy, dz
        );
    }

    /**
     * Creates a new bounding box of the given size centered around the given
     * position
     *
     * @param pos The center position
     * @param dx  The x size
     * @param dy  The y size
     * @param dz  The z size
     * @return New bounding box based on the given size and center
     */
    @Contract("_, _, _, _ -> new")
    public static @NotNull MSBoundingBox ofSize(
            final @NotNull MSPosition pos,
            final double dx,
            final double dy,
            final double dz
    ) {
        return ofSize(
                pos.x(), pos.y(), pos.z(),
                dx, dy, dz
        );
    }

    /**
     * Creates a new bounding box of the given size centered around the given
     * position
     *
     * @param pos The center position
     * @param dx  The x size
     * @param dy  The y size
     * @param dz  The z size
     * @return New bounding box based on the given size and center
     */
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

    /**
     * Creates a new bounding box of the given size centered around the given
     * position
     *
     * @param pos The center position
     * @param dx  The x size
     * @param dy  The y size
     * @param dz  The z size
     * @return New bounding box based on the given size and center
     */
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

    /**
     * Creates a new bounding box of the given size centered around the given
     * coordinates
     *
     * @param x   The center x coordinate
     * @param y   The center y coordinate
     * @param z   The center z coordinate
     * @param dx  The x size
     * @param dy  The y size
     * @param dz  The z size
     * @return New bounding box based on the given size and center
     */
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

    /**
     * @return The minimum x value
     */
    public double minX() {
        return this.minX;
    }

    /**
     * Sets the minimum x value of the bounding box
     *
     * @param minX The new minimum x value
     * @return A new bounding box with the given minimum x value
     */
    @Contract("_ -> new")
    public @NotNull MSBoundingBox minX(final double minX) {
        return ofDummy(
                Math.min(minX, this.maxX), this.minY, this.minZ,
                Math.max(minX, this.maxX), this.maxY, this.maxZ
        );
    }

    /**
     * @return The minimum y value
     */
    public double minY() {
        return this.minY;
    }

    /**
     * Sets the minimum y value of the bounding box
     *
     * @param minY The new minimum y value
     * @return A new bounding box with the given minimum y value
     */
    @Contract("_ -> new")
    public @NotNull MSBoundingBox minY(final double minY) {
        return ofDummy(
                this.minX, Math.min(minY, this.maxY), this.minZ,
                this.maxX, Math.max(minY, this.maxY), this.maxZ
        );
    }

    /**
     * @return The minimum z value
     */
    public double minZ() {
        return this.minZ;
    }

    /**
     * Sets the minimum z value of the bounding box
     *
     * @param minZ The new minimum z value
     * @return A new bounding box with the given minimum z value
     */
    @Contract("_ -> new")
    public @NotNull MSBoundingBox minZ(final double minZ) {
        return ofDummy(
                this.minX, this.minY, Math.min(minZ, this.maxZ),
                this.maxX, this.maxY, Math.max(minZ, this.maxZ)
        );
    }

    /**
     * @return The minimum corner
     */
    @Contract(" -> new")
    public @NotNull MSVector min() {
        return MSVector.of(this.minX, this.minY, this.minZ);
    }

    /**
     * @return The maximum x value
     */
    public double maxX() {
        return this.maxX;
    }

    /**
     * Sets the maximum x value of the bounding box
     *
     * @param maxX The new maximum x value
     * @return A new bounding box with the given maximum x value
     */
    @Contract("_ -> new")
    public @NotNull MSBoundingBox maxX(final double maxX) {
        return ofDummy(
                Math.min(maxX, this.minX), this.minY, this.minZ,
                Math.max(maxX, this.minX), this.maxY, this.maxZ
        );
    }

    /**
     * @return The maximum y value
     */
    public double maxY() {
        return this.maxY;
    }

    /**
     * Sets the maximum y value of the bounding box
     *
     * @param maxY The new maximum y value
     * @return A new bounding box with the given maximum y value
     */
    @Contract("_ -> new")
    public @NotNull MSBoundingBox maxY(final double maxY) {
        return ofDummy(
                this.minX, Math.min(maxY, this.minY), this.minZ,
                this.maxX, Math.max(maxY, this.minY), this.maxZ
        );
    }

    /**
     * @return The maximum z value
     */
    public double maxZ() {
        return this.maxZ;
    }

    /**
     * Sets the maximum z value of the bounding box
     *
     * @param maxZ The new maximum z value
     * @return A new bounding box with the given maximum z value
     */
    @Contract("_ -> new")
    public @NotNull MSBoundingBox maxZ(final double maxZ) {
        return ofDummy(
                this.minX, this.minY, Math.min(maxZ, this.minZ),
                this.maxX, this.maxY, Math.max(maxZ, this.minZ)
        );
    }

    /**
     * @return The maximum corner
     */
    @Contract(" -> new")
    public @NotNull MSVector max() {
        return MSVector.of(this.maxX, this.maxY, this.maxZ);
    }


    /**
     * Sets the minimum corner of the bounding box
     *
     * @param vec The new minimum corner
     * @return A new bounding box with the given minimum corner
     */
    @Contract("_ -> new")
    public @NotNull MSBoundingBox min(final @NotNull MSVector vec) {
        return this.min(vec.x(), vec.y(), vec.z());
    }

    /**
     * Sets the minimum corner of the bounding box
     *
     * @param vec The new minimum corner
     * @return A new bounding box with the given minimum corner
     */
    @Contract("_ -> new")
    public @NotNull MSBoundingBox min(final @NotNull Vector vec) {
        return this.min(vec.getX(), vec.getY(), vec.getZ());
    }

    /**
     * Sets the minimum corner of the bounding box
     *
     * @param vec The new minimum corner
     * @return A new bounding box with the given minimum corner
     */
    @Contract("_ -> new")
    public @NotNull MSBoundingBox min(final @NotNull Vec3i vec) {
        return this.min(vec.getX(), vec.getY(), vec.getZ());
    }

    /**
     * Sets the minimum corner of the bounding box
     *
     * @param pos The new minimum corner
     * @return A new bounding box with the given minimum corner
     */
    @Contract("_ -> new")
    public @NotNull MSBoundingBox min(final @NotNull MSPosition pos) {
        return this.min(pos.x(), pos.y(), pos.z());
    }

    /**
     * Sets the minimum corner of the bounding box
     *
     * @param pos The new minimum corner
     * @return A new bounding box with the given minimum corner
     */
    @Contract("_ -> new")
    public @NotNull MSBoundingBox min(final @NotNull Position pos) {
        return this.min(pos.x(), pos.y(), pos.z());
    }

    /**
     * Sets the minimum corner of the bounding box
     *
     * @param pos The new minimum corner
     * @return A new bounding box with the given minimum corner
     */
    @Contract("_ -> new")
    public @NotNull MSBoundingBox min(final @NotNull io.papermc.paper.math.Position pos) {
        return this.min(pos.x(), pos.y(), pos.z());
    }

    /**
     * Sets the minimum corner of the bounding box
     *
     * @param x The new minimum x value
     * @param y The new minimum y value
     * @param z The new minimum z value
     * @return A new bounding box with the given minimum corner
     */
    @Contract("_, _, _ -> new")
    public @NotNull MSBoundingBox min(
            final double x,
            final double y,
            final double z
    ) {
        return of(
                x, y, z,
                this.maxX, this.maxY, this.maxZ
        );
    }

    /**
     * Sets the maximum corner of the bounding box
     *
     * @param vec The new maximum corner
     * @return A new bounding box with the given maximum corner
     */
    @Contract("_ -> new")
    public @NotNull MSBoundingBox max(final @NotNull MSVector vec) {
        return this.max(vec.x(), vec.y(), vec.z());
    }

    /**
     * Sets the maximum corner of the bounding box
     *
     * @param vec The new maximum corner
     * @return A new bounding box with the given maximum corner
     */
    @Contract("_ -> new")
    public @NotNull MSBoundingBox max(final @NotNull Vector vec) {
        return this.max(vec.getX(), vec.getY(), vec.getZ());
    }

    /**
     * Sets the maximum corner of the bounding box
     *
     * @param vec The new maximum corner
     * @return A new bounding box with the given maximum corner
     */
    @Contract("_ -> new")
    public @NotNull MSBoundingBox max(final @NotNull Vec3i vec) {
        return this.max(vec.getX(), vec.getY(), vec.getZ());
    }

    /**
     * Sets the maximum corner of the bounding box
     *
     * @param pos The new maximum corner
     * @return A new bounding box with the given maximum corner
     */
    @Contract("_ -> new")
    public @NotNull MSBoundingBox max(final @NotNull MSPosition pos) {
        return this.max(pos.x(), pos.y(), pos.z());
    }

    /**
     * Sets the maximum corner of the bounding box
     *
     * @param pos The new maximum corner
     * @return A new bounding box with the given maximum corner
     */
    @Contract("_ -> new")
    public @NotNull MSBoundingBox max(final @NotNull Position pos) {
        return this.max(pos.x(), pos.y(), pos.z());
    }

    /**
     * Sets the maximum corner of the bounding box
     *
     * @param pos The new maximum corner
     * @return A new bounding box with the given maximum corner
     */
    @Contract("_ -> new")
    public @NotNull MSBoundingBox max(final @NotNull io.papermc.paper.math.Position pos) {
        return this.max(pos.x(), pos.y(), pos.z());
    }

    /**
     * Sets the maximum corner of the bounding box
     *
     * @param x The new maximum x value
     * @param y The new maximum y value
     * @param z The new maximum z value
     * @return A new bounding box with the given maximum corner
     */
    @Contract("_, _, _ -> new")
    public @NotNull MSBoundingBox max(
            final double x,
            final double y,
            final double z
    ) {
        return of(
                this.minX, this.minY, this.minZ,
                x, y, z
        );
    }

    /**
     * @return The x center coordinate of the bounding box
     */
    public double centerX() {
        return this.minX + 0.5D * this.sizeX();
    }

    /**
     * @return The y center coordinate of the bounding box
     */
    public double centerY() {
        return this.minY + 0.5D * this.sizeY();
    }

    /**
     * @return The z center coordinate of the bounding box
     */
    public double centerZ() {
        return this.minZ + 0.5D * this.sizeZ();
    }

    /**
     * @return The x size of the bounding box
     */
    public double sizeX() {
        return this.maxX - this.minX;
    }

    /**
     * @return The y size of the bounding box
     */
    public double sizeY() {
        return this.maxY - this.minY;
    }

    /**
     * @return The z size of the bounding box
     */
    public double sizeZ() {
        return this.maxZ - this.minZ;
    }

    /**
     * @return The volume of the bounding box
     */
    public double volume() {
        return this.sizeX() * this.sizeY() * this.sizeZ();
    }

    /**
     * Contracts the bounding box with the given value
     *
     * @param value The value to contract the bounding box with
     * @return A new bounding box with the new contracted values
     */
    @Contract("_ -> new")
    public @NotNull MSBoundingBox contract(final double value) {
        return this.contract(value, value, value);
    }

    /**
     * Contracts the bounding box with the given values
     *
     * @param x The x value to contract
     * @param y The y value to contract
     * @param z The z value to contract
     * @return A new bounding box with the new contracted values
     */
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

    /**
     * Expands the bounding box with the given value
     *
     * @param value The value to expand the bounding box with
     * @return A new bounding box with the new expanded values
     */
    @Contract("_ -> new")
    public @NotNull MSBoundingBox expandTowards(final double value) {
        return this.expandTowards(value, value, value);
    }

    /**
     * Expands the bounding box with the given values
     *
     * @param x The x value to expand
     * @param y The y value to expand
     * @param z The z value to expand
     * @return A new bounding box with the new expanded values
     */
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

    /**
     * Inflates the bounding box with the given value
     *
     * @param value The value to inflate the bounding box with
     * @return A new bounding box with the new inflated values
     */
    @Contract("_ -> new")
    public @NotNull MSBoundingBox inflate(final double value) {
        return this.inflate(value, value, value);
    }

    /**
     * Inflates the bounding box with the given values
     *
     * @param x The x value to inflate
     * @param y The y value to inflate
     * @param z The z value to inflate
     * @return A new bounding box with the new inflated values
     */
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

    /**
     * Resizes this bounding box to represent the intersection of this bounding
     * box and the given axis aligned bounding box
     *
     * @param aabb The axis aligned bounding box to intersect with
     * @return A new bounding box with the intersection of the given axis aligned
     *         bounding box and this bounding box
     */
    @Contract("_ -> new")
    public @NotNull MSBoundingBox intersect(final @NotNull AABB aabb) {
        return this.intersect(
                aabb.minX, aabb.minY, aabb.minZ,
                aabb.maxX, aabb.maxY, aabb.maxZ
        );
    }

    /**
     * Resizes this bounding box to represent the intersection of this bounding
     * box and the given bounding box
     *
     * @param bb The bounding box to intersect with
     * @return A new bounding box with the intersection of the given bounding box
     *         and this bounding box
     */
    @Contract("_ -> new")
    public @NotNull MSBoundingBox intersect(final @NotNull BoundingBox bb) {
        return this.intersect(
                bb.minX(), bb.minY(), bb.minZ(),
                bb.maxX(), bb.maxY(), bb.maxZ()
        );
    }

    /**
     * Resizes this bounding box to represent the intersection of this bounding
     * box and the given bounding box
     *
     * @param bb The bounding box to intersect with
     * @return A new bounding box with the intersection of the given bounding box
     *         and this bounding box
     */
    @Contract("_ -> new")
    public @NotNull MSBoundingBox intersect(final @NotNull org.bukkit.util.BoundingBox bb) {
        return this.intersect(
                bb.getMinX(), bb.getMinY(), bb.getMinZ(),
                bb.getMaxX(), bb.getMaxY(), bb.getMaxZ()
        );
    }

    /**
     * Resizes this bounding box to represent the intersection of this bounding
     * box and the given bounding box
     *
     * @param msbb The bounding box to intersect with
     * @return A new bounding box with the intersection of the given bounding box
     *         and this bounding box
     */
    @Contract("_ -> new")
    public @NotNull MSBoundingBox intersect(final @NotNull MSBoundingBox msbb) {
        return this.intersect(
                msbb.minX, msbb.minY, msbb.minZ,
                msbb.maxX, msbb.maxY, msbb.maxZ
        );
    }

    /**
     * Resizes this bounding box to represent the intersection of this bounding
     * box and the given coordinates
     *
     * @param minX The minimum x value to intersect with
     * @param minY The minimum y value to intersect with
     * @param minZ The minimum z value to intersect with
     * @param maxX The maximum x value to intersect with
     * @param maxY The maximum y value to intersect with
     * @param maxZ The maximum z value to intersect with
     * @return A new bounding box with the intersection of the given coordinates
     *         and this bounding box
     */
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

    /**
     * Moves the bounding box with the given values
     *
     * @param vec The vector, which values will be added to the current corner
     *            values
     * @return A new bounding box with the given values moved
     */
    @Contract("_ -> new")
    public @NotNull MSBoundingBox move(final @NotNull MSVector vec) {
        return this.move(vec.x(), vec.y(), vec.z());
    }

    /**
     * Moves the bounding box with the given values
     *
     * @param vec The vector, which values will be added to the current corner
     *            values
     * @return A new bounding box with the given values moved
     */
    @Contract("_ -> new")
    public @NotNull MSBoundingBox move(final @NotNull Vec3i vec) {
        return this.move(vec.getX(), vec.getY(), vec.getZ());
    }

    /**
     * Moves the bounding box with the given values
     *
     * @param vec The vector, which values will be added to the current corner
     *            values
     * @return A new bounding box with the given values moved
     */
    @Contract("_ -> new")
    public @NotNull MSBoundingBox move(final @NotNull Vector vec) {
        return this.move(vec.getX(), vec.getY(), vec.getZ());
    }

    /**
     * Moves the bounding box with the given values
     *
     * @param pos The position, which values will be added to the current corner
     *            values
     * @return A new bounding box with the given values moved
     */
    @Contract("_ -> new")
    public @NotNull MSBoundingBox move(final @NotNull MSPosition pos) {
        return this.move(pos.x(), pos.y(), pos.z());
    }

    /**
     * Moves the bounding box with the given values
     *
     * @param pos The position, which values will be added to the current corner
     *            values
     * @return A new bounding box with the given values moved
     */
    @Contract("_ -> new")
    public @NotNull MSBoundingBox move(final @NotNull Position pos) {
        return this.move(pos.x(), pos.y(), pos.z());
    }

    /**
     * Moves the bounding box with the given values
     *
     * @param pos The position, which values will be added to the current corner
     *            values
     * @return A new bounding box with the given values moved
     */
    @Contract("_ -> new")
    public @NotNull MSBoundingBox move(final @NotNull io.papermc.paper.math.Position pos) {
        return this.move(pos.x(), pos.y(), pos.z());
    }

    /**
     * Moves the bounding box with the given values
     *
     * @param x The x value to move
     * @param y The y value to move
     * @param z The z value to move
     * @return A new bounding box with the given values moved
     */
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

    /**
     * @return The center of the bounding box
     */
    @Contract(" -> new")
    public @NotNull MSVector getCenter() {
        return MSVector.of(this.centerX(), this.centerY(), this.centerZ());
    }

    /**
     * @param world The world
     * @return The center of the bounding box with the specified world
     */
    @Contract("_ -> new")
    public @NotNull MSPosition getCenter(final @NotNull World world) {
        return MSPosition.of(
                world,
                this.centerX(), this.centerY(), this.centerZ()
        );
    }

    /**
     * @param serverLevel The server level, which will be used to get block
     *                    states from
     * @return An array of block states, which are inside the bounding box
     */
    public net.minecraft.world.level.block.state.BlockState @NotNull [] getNMSBlockStates(final @NotNull ServerLevel serverLevel) {
        return this.getBlockStates(
                net.minecraft.world.level.block.state.BlockState.class,
                serverLevel.getWorld()
        );
    }

    /**
     * @param world The world, which will be used to get block states from
     * @return An array of block states, which are inside the bounding box
     */
    public BlockState @NotNull [] getBlockStates(final @NotNull World world) {
        return this.getBlockStates(BlockState.class, world);
    }

    /**
     * @return An array of vectors, which are inside the bounding box
     */
    public MSVector @NotNull [] getMSVectors() {
        return getPositions(
                MSVector.class,
                (int) this.minX, (int) this.minY, (int) this.minZ,
                (int) this.maxX, (int) this.maxY, (int) this.maxZ
        );
    }

    /**
     * @return An array of vectors, which are inside the bounding box
     */
    public Vector @NotNull [] getVectors() {
        return getPositions(
                Vector.class,
                (int) this.minX, (int) this.minY, (int) this.minZ,
                (int) this.maxX, (int) this.maxY, (int) this.maxZ
        );
    }

    /**
     * @return An array of vectors, which are inside the bounding box
     */
    public Vec3i @NotNull [] getVec3i() {
        return getPositions(
                Vec3i.class,
                (int) this.minX, (int) this.minY, (int) this.minZ,
                (int) this.maxX, (int) this.maxY, (int) this.maxZ
        );
    }

    /**
     * @return An array of positions, which are inside the bounding box
     */
    public MSPosition @NotNull [] getMSPositions() {
        return getPositions(
                MSPosition.class,
                (int) this.minX, (int) this.minY, (int) this.minZ,
                (int) this.maxX, (int) this.maxY, (int) this.maxZ
        );
    }

    /**
     * @return An array of positions, which are inside the bounding box
     */
    public Position @NotNull [] getPositions() {
        return getPositions(
                Position.class,
                (int) this.minX, (int) this.minY, (int) this.minZ,
                (int) this.maxX, (int) this.maxY, (int) this.maxZ
        );
    }

    /**
     * @return An array of positions, which are inside the bounding box
     */
    public BlockPos @NotNull [] getBlockPositions() {
        return getPositions(
                BlockPos.class,
                (int) this.minX, (int) this.minY, (int) this.minZ,
                (int) this.maxX, (int) this.maxY, (int) this.maxZ
        );
    }

    /**
     * @return An array of positions, which are inside the bounding box
     */
    public io.papermc.paper.math.Position @NotNull [] getPaperPositions() {
        return getPositions(
                io.papermc.paper.math.Position.class,
                (int) this.minX, (int) this.minY, (int) this.minZ,
                (int) this.maxX, (int) this.maxY, (int) this.maxZ
        );
    }

    /**
     * @param minOffsetX The minimum x offset
     * @param minOffsetY The minimum y offset
     * @param minOffsetZ The minimum z offset
     * @param maxOffsetX The maximum x offset
     * @param maxOffsetY The maximum y offset
     * @param maxOffsetZ The maximum z offset
     * @return An array of vectors, which are inside the bounding box with the
     *         given offsets
     */
    public MSVector @NotNull [] getMSVectors(
            final int minOffsetX,
            final int minOffsetY,
            final int minOffsetZ,
            final int maxOffsetX,
            final int maxOffsetY,
            final int maxOffsetZ
    ) {
        return getPositions(
                MSVector.class,
                (int) this.minX + minOffsetX, (int) this.minY + minOffsetY, (int) this.minZ + minOffsetZ,
                (int) this.maxX + maxOffsetX, (int) this.maxY + maxOffsetY, (int) this.maxZ + maxOffsetZ
        );
    }

    /**
     * @param minOffsetX The minimum x offset
     * @param minOffsetY The minimum y offset
     * @param minOffsetZ The minimum z offset
     * @param maxOffsetX The maximum x offset
     * @param maxOffsetY The maximum y offset
     * @param maxOffsetZ The maximum z offset
     * @return An array of vectors, which are inside the bounding box with the
     *         given offsets
     */
    public Vector @NotNull [] getVectors(
            final int minOffsetX,
            final int minOffsetY,
            final int minOffsetZ,
            final int maxOffsetX,
            final int maxOffsetY,
            final int maxOffsetZ
    ) {
        return getPositions(
                Vector.class,
                (int) this.minX + minOffsetX, (int) this.minY + minOffsetY, (int) this.minZ + minOffsetZ,
                (int) this.maxX + maxOffsetX, (int) this.maxY + maxOffsetY, (int) this.maxZ + maxOffsetZ
        );
    }

    /**
     * @param minOffsetX The minimum x offset
     * @param minOffsetY The minimum y offset
     * @param minOffsetZ The minimum z offset
     * @param maxOffsetX The maximum x offset
     * @param maxOffsetY The maximum y offset
     * @param maxOffsetZ The maximum z offset
     * @return An array of vectors, which are inside the bounding box with the
     *         given offsets
     */
    public Vec3i @NotNull [] getVec3i(
            final int minOffsetX,
            final int minOffsetY,
            final int minOffsetZ,
            final int maxOffsetX,
            final int maxOffsetY,
            final int maxOffsetZ
    ) {
        return getPositions(
                Vec3i.class,
                (int) this.minX + minOffsetX, (int) this.minY + minOffsetY, (int) this.minZ + minOffsetZ,
                (int) this.maxX + maxOffsetX, (int) this.maxY + maxOffsetY, (int) this.maxZ + maxOffsetZ
        );
    }

    /**
     * @param minOffsetX The minimum x offset
     * @param minOffsetY The minimum y offset
     * @param minOffsetZ The minimum z offset
     * @param maxOffsetX The maximum x offset
     * @param maxOffsetY The maximum y offset
     * @param maxOffsetZ The maximum z offset
     * @return An array of positions, which are inside the bounding box with the
     *         given offsets
     */
    public MSPosition @NotNull [] getMSPositions(
            final int minOffsetX,
            final int minOffsetY,
            final int minOffsetZ,
            final int maxOffsetX,
            final int maxOffsetY,
            final int maxOffsetZ
    ) {
        return getPositions(
                MSPosition.class,
                (int) this.minX + minOffsetX, (int) this.minY + minOffsetY, (int) this.minZ + minOffsetZ,
                (int) this.maxX + maxOffsetX, (int) this.maxY + maxOffsetY, (int) this.maxZ + maxOffsetZ
        );
    }

    /**
     * @param minOffsetX The minimum x offset
     * @param minOffsetY The minimum y offset
     * @param minOffsetZ The minimum z offset
     * @param maxOffsetX The maximum x offset
     * @param maxOffsetY The maximum y offset
     * @param maxOffsetZ The maximum z offset
     * @return An array of positions, which are inside the bounding box with the
     *         given offsets
     */
    public Position @NotNull [] getPositions(
            final int minOffsetX,
            final int minOffsetY,
            final int minOffsetZ,
            final int maxOffsetX,
            final int maxOffsetY,
            final int maxOffsetZ
    ) {
        return getPositions(
                Position.class,
                (int) this.minX + minOffsetX, (int) this.minY + minOffsetY, (int) this.minZ + minOffsetZ,
                (int) this.maxX + maxOffsetX, (int) this.maxY + maxOffsetY, (int) this.maxZ + maxOffsetZ
        );
    }

    /**
     * @param minOffsetX The minimum x offset
     * @param minOffsetY The minimum y offset
     * @param minOffsetZ The minimum z offset
     * @param maxOffsetX The maximum x offset
     * @param maxOffsetY The maximum y offset
     * @param maxOffsetZ The maximum z offset
     * @return An array of positions, which are inside the bounding box with the
     *         given offsets
     */
    public BlockPos @NotNull [] getBlockPositions(
            final int minOffsetX,
            final int minOffsetY,
            final int minOffsetZ,
            final int maxOffsetX,
            final int maxOffsetY,
            final int maxOffsetZ
    ) {
        return getPositions(
                BlockPos.class,
                (int) this.minX + minOffsetX, (int) this.minY + minOffsetY, (int) this.minZ + minOffsetZ,
                (int) this.maxX + maxOffsetX, (int) this.maxY + maxOffsetY, (int) this.maxZ + maxOffsetZ
        );
    }

    /**
     * @param minOffsetX The minimum x offset
     * @param minOffsetY The minimum y offset
     * @param minOffsetZ The minimum z offset
     * @param maxOffsetX The maximum x offset
     * @param maxOffsetY The maximum y offset
     * @param maxOffsetZ The maximum z offset
     * @return An array of positions, which are inside the bounding box with the
     *         given offsets
     */
    public io.papermc.paper.math.Position @NotNull [] getPaperPositions(
            final int minOffsetX,
            final int minOffsetY,
            final int minOffsetZ,
            final int maxOffsetX,
            final int maxOffsetY,
            final int maxOffsetZ
    ) {
        return getPositions(
                io.papermc.paper.math.Position.class,
                (int) this.minX + minOffsetX, (int) this.minY + minOffsetY, (int) this.minZ + minOffsetZ,
                (int) this.maxX + maxOffsetX, (int) this.maxY + maxOffsetY, (int) this.maxZ + maxOffsetZ
        );
    }

    /**
     * @param world The world, which will be used to get entities from
     * @return An array of entities, which are inside the bounding box
     */
    public Entity @NotNull [] getEntities(final @NotNull World world) {
        return this.getEntities(world, null);
    }

    /**
     * @param world     The world, which will be used to get entities from
     * @param predicate The predicate, which will be used to filter the entities
     * @return An array of entities, which are inside the bounding box and match
     *         the predicate
     */
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

    /**
     * @param level The server level, which will be used to get entities from
     * @return An array of entities, which are inside the bounding box
     */
    public @NotNull List<net.minecraft.world.entity.Entity> getNMSEntities(final @NotNull ServerLevel level) {
        return this.getNMSEntities(level, null);
    }

    /**
     * @param level     The server level, which will be used to get entities from
     * @param predicate The predicate, which will be used to filter the entities
     * @return An array of entities, which are inside the bounding box
     */
    public @NotNull List<net.minecraft.world.entity.Entity> getNMSEntities(
            final @NotNull ServerLevel level,
            final @Nullable Predicate<? super net.minecraft.world.entity.Entity> predicate
    ) {
        return this.getEntities(level, predicate, false);
    }

    /**
     * @param level The server level, which will be checked for entities
     * @return Whether the bounding box has any entities inside it
     */
    public boolean hasAnyEntity(final @NotNull ServerLevel level) {
        return this.hasNMSEntity(level, null);
    }

    /**
     * @param world     The world, which will be checked for entities
     * @param predicate The predicate, which will be used to filter the entities
     * @return Whether the bounding box has entities inside it, that match the
     *         predicate
     */
    public boolean hasEntity(
            final @NotNull World world,
            final @Nullable Predicate<Entity> predicate
    ) {
        return this.hasNMSEntity(
                ((CraftWorld) world).getHandle(),
                entity ->
                        predicate == null
                        || predicate.test(entity.getBukkitEntity())
        );
    }

    /**
     * @param level     The server level, which will be checked for entities
     * @param predicate The predicate, which will be used to filter the entities
     * @return Whether the bounding box has entities inside it, that match the
     *         predicate
     */
    public boolean hasNMSEntity(
            final @NotNull ServerLevel level,
            final @Nullable Predicate<? super net.minecraft.world.entity.Entity> predicate
    ) {
        return !this.getEntities(level, predicate, true).isEmpty();
    }

    /**
     * @param aabb The bounding box to check
     * @return Whether the bounding box overlaps with the given bounding box
     */
    public boolean overlaps(final @NotNull AABB aabb) {
        return this.overlaps(
                aabb.minX, aabb.minY, aabb.minZ,
                aabb.maxX, aabb.maxY, aabb.maxZ
        );
    }

    /**
     * @param bb The bounding box to check
     * @return Whether the bounding box overlaps with the given bounding box
     */
    public boolean overlaps(final @NotNull BoundingBox bb) {
        return this.overlaps(
                bb.minX(), bb.minY(), bb.minZ(),
                bb.maxX(), bb.maxY(), bb.maxZ()
        );
    }

    /**
     * @param bb The bounding box to check
     * @return Whether the bounding box overlaps with the given bounding box
     */
    public boolean overlaps(final @NotNull org.bukkit.util.BoundingBox bb) {
        return this.overlaps(
                bb.getMinX(), bb.getMinY(), bb.getMinZ(),
                bb.getMaxX(), bb.getMaxY(), bb.getMaxZ()
        );
    }

    /**
     * @param msbb The bounding box to check
     * @return Whether the bounding box overlaps with the given bounding box
     */
    public boolean overlaps(final @NotNull MSBoundingBox msbb) {
        return this.overlaps(
                msbb.minX, msbb.minY, msbb.minZ,
                msbb.maxX, msbb.maxY, msbb.maxZ
        );
    }

    /**
     * @param minX The minimum x value to check
     * @param minY The minimum y value to check
     * @param minZ The minimum z value to check
     * @param maxX The maximum x value to check
     * @param maxY The maximum y value to check
     * @param maxZ The maximum z value to check
     * @return Whether the bounding box overlaps with the given coordinates
     */
    public boolean overlaps(
            final double minX,
            final double minY,
            final double minZ,
            final double maxX,
            final double maxY,
            final double maxZ
    ) {
        return this.maxX > minX
                && this.maxY > minY
                && this.maxZ > minZ
                && this.minX < maxX
                && this.minY < maxY
                && this.minZ < maxZ;
    }

    /**
     * @param aabb The bounding box to check
     * @return Whether the bounding box intersects with the given bounding box
     */
    public boolean intersects(final @NotNull AABB aabb) {
        return this.intersects(
                aabb.minX, aabb.minY, aabb.minZ,
                aabb.maxX, aabb.maxY, aabb.maxZ
        );
    }

    /**
     * @param bb The bounding box to check
     * @return Whether the bounding box intersects with the given bounding box
     */
    public boolean intersects(final @NotNull BoundingBox bb) {
        return this.intersects(
                bb.minX(), bb.minY(), bb.minZ(),
                bb.maxX(), bb.maxY(), bb.maxZ()
        );
    }

    /**
     * @param bb The bounding box to check
     * @return Whether the bounding box intersects with the given bounding box
     */
    public boolean intersects(final @NotNull org.bukkit.util.BoundingBox bb) {
        return this.intersects(
                bb.getMinX(), bb.getMinY(), bb.getMinZ(),
                bb.getMaxX(), bb.getMaxY(), bb.getMaxZ()
        );
    }

    /**
     * @param msbb The bounding box to check
     * @return Whether the bounding box intersects with the given bounding box
     */
    public boolean intersects(final @NotNull MSBoundingBox msbb) {
        return this.intersects(
                msbb.minX, msbb.minY, msbb.minZ,
                msbb.maxX, msbb.maxY, msbb.maxZ
        );
    }

    /**
     * @param minX The minimum x value to check
     * @param minY The minimum y value to check
     * @param minZ The minimum z value to check
     * @param maxX The maximum x value to check
     * @param maxY The maximum y value to check
     * @param maxZ The maximum z value to check
     * @return Whether the bounding box intersects with the given coordinates
     */
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

    /**
     * @param vec The vector to check
     * @return Whether the bounding box contains the given vector
     */
    public boolean contains(final @NotNull MSVector vec) {
        return this.contains(vec.x(), vec.y(), vec.z());
    }

    /**
     * @param vec The vector to check
     * @return Whether the bounding box contains the given vector
     */
    public boolean contains(final @NotNull Vector vec) {
        return this.contains(vec.getX(), vec.getY(), vec.getZ());
    }

    /**
     * @param vec The vector to check
     * @return Whether the bounding box contains the given vector
     */
    public boolean contains(final @NotNull Vec3i vec) {
        return this.contains(vec.getX(), vec.getY(), vec.getZ());
    }

    /**
     * @param pos The position to check
     * @return Whether the bounding box contains the given position
     */
    public boolean contains(final @NotNull MSPosition pos) {
        return this.contains(pos.x(), pos.y(), pos.z());
    }

    /**
     * @param pos The position to check
     * @return Whether the bounding box contains the given position
     */
    public boolean contains(final @NotNull Position pos) {
        return this.contains(pos.x(), pos.y(), pos.z());
    }

    /**
     * @param pos The position to check
     * @return Whether the bounding box contains the given position
     */
    public boolean contains(final @NotNull io.papermc.paper.math.Position pos) {
        return this.contains(pos.x(), pos.y(), pos.z());
    }

    /**
     * @param x The x value to check
     * @param y The y value to check
     * @param z The z value to check
     * @return Whether the bounding box contains the given coordinates
     */
    public boolean contains(
            final double x,
            final double y,
            final double z
    ) {
        return x >= this.minX && x <= this.maxX
                && y >= this.minY && y <= this.maxY
                && z >= this.minZ && z <= this.maxZ;
    }

    /**
     * @return Whether the bounding box has any NaN values
     */
    public boolean hasNaN() {
        return Double.isNaN(this.minX)
                || Double.isNaN(this.minY)
                || Double.isNaN(this.minZ)
                || Double.isNaN(this.maxX)
                || Double.isNaN(this.maxY)
                || Double.isNaN(this.maxZ);
    }

    /**
     * @return Whether the bounding box has any infinite values
     */
    public boolean hasInfinite() {
        return Double.isInfinite(this.minX)
                || Double.isInfinite(this.minY)
                || Double.isInfinite(this.minZ)
                || Double.isInfinite(this.maxX)
                || Double.isInfinite(this.maxY)
                || Double.isInfinite(this.maxZ);
    }

    /**
     * @return True, if all values are finite
     */
    public boolean isAllFinite() {
        return Double.isFinite(this.minX)
                && Double.isFinite(this.minY)
                && Double.isFinite(this.minZ)
                && Double.isFinite(this.maxX)
                && Double.isFinite(this.maxY)
                && Double.isFinite(this.maxZ);
    }

    /**
     * @return A hash code value for this bounding box
     */
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

    /**
     * @param object The object to compare this bounding box against
     * @return Whether the given object is equal to this bounding box
     */
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

    /**
     * @return A copy of this bounding box
     */
    @Contract(" -> new")
    public @NotNull MSBoundingBox copy() {
        return of(this);
    }

    /**
     * @return A string representation of this bounding box
     */
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

    /**
     * @return A new {@link AABB} with the same values as this bounding box
     */
    @Contract(" -> new")
    public @NotNull AABB toAABB() {
        return new AABB(
                this.minX, this.minY, this.minZ,
                this.maxX, this.maxY, this.maxZ
        );
    }

    /**
     * @return A new {@link BoundingBox} with the same values as this bounding
     */
    @Contract(" -> new")
    public @NotNull BoundingBox toBB() {
        return new BoundingBox(
                (int) this.minX, (int) this.minY, (int) this.minZ,
                (int) this.maxX, (int) this.maxY, (int) this.maxZ
        );
    }

    /**
     * @return A new {@link org.bukkit.util.BoundingBox} with the same values as
     */
    @Contract(" -> new")
    public @NotNull org.bukkit.util.BoundingBox toBukkitBB() {
        return new org.bukkit.util.BoundingBox(
                this.minX, this.minY, this.minZ,
                this.maxX, this.maxY, this.maxZ
        );
    }

    private @NotNull List<net.minecraft.world.entity.Entity> getEntities(
            final @NotNull ServerLevel level,
            final @Nullable Predicate<? super net.minecraft.world.entity.Entity> predicate,
            final boolean checkForEmpty
    ) {
        AsyncCatcher.catchOp("retrieveEntities");

        final AABB aabb = this.toAABB();
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

                if (region == null) {
                    continue;
                }

                final int minX = currRegionX == minRegionX ? minChunkX & REGION_MASK : 0;
                final int maxX = currRegionX == maxRegionX ? maxChunkX & REGION_MASK : REGION_MASK;

                for (int currZ = minZ; currZ <= maxZ; ++currZ) {
                    for (int currX = minX; currX <= maxX; ++currX) {
                        final ChunkEntitySlices chunk = region.get(currX | (currZ << REGION_SHIFT));

                        if (
                                chunk == null
                                || !chunk.status.isOrAfter(FullChunkStatus.FULL)
                        ) {
                            continue;
                        }

                        chunk.getEntities((net.minecraft.world.entity.Entity) null, aabb, list, predicate);

                        if (
                                checkForEmpty
                                && !list.isEmpty()
                        ) {
                            return list;
                        }
                    }
                }
            }
        }

        return list;
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
        ) throw new IllegalArgumentException("Invalid class");

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

    @SuppressWarnings("unchecked")
    private static  <T> T @NotNull [] getPositions(
            final @NotNull Class<T> clazz,
            final int minX,
            final int minY,
            final int minZ,
            final int maxX,
            final int maxY,
            final int maxZ
    ) throws IllegalArgumentException {
        final boolean isMSVector = clazz.isAssignableFrom(MSVector.class);
        final boolean isVector = clazz.isAssignableFrom(Vector.class);
        final boolean isVec3i = clazz.isAssignableFrom(Vec3i.class);
        final boolean isMSPosition = clazz.isAssignableFrom(MSPosition.class);
        final boolean isPosition = clazz.isAssignableFrom(Position.class);
        final boolean isBlockPos = clazz.isAssignableFrom(BlockPos.class);

        if (
                !isMSVector
                && !isVector
                && !isVec3i
                && !isMSPosition
                && !isPosition
                && !isBlockPos
                && !clazz.isAssignableFrom(io.papermc.paper.math.Position.class)
        ) throw new IllegalArgumentException("Invalid class");

        final int offsetX = Math.abs(minX - maxX) + 1;
        final int offsetY = Math.abs(minY - maxY) + 1;
        final int offsetZ = Math.abs(minZ - maxZ) + 1;
        final var array = (T[]) Array.newInstance(clazz, offsetX * offsetY * offsetZ);

        int i = 0;

        for (int x = 0; x < offsetX; ++x) {
            for (int y = 0; y < offsetY; ++y) {
                for (int z = 0; z < offsetZ; ++z) {
                    array[i++] =
                            isMSVector
                            ? (T) MSVector.of(
                                    minX + x,
                                    minY + y,
                                    minZ + z
                            )
                            : isVector
                            ? (T) new Vector(
                                    minX + x,
                                    minY + y,
                                    minZ + z
                            )
                            : isVec3i
                            ? (T) new Vec3i(
                                    minX + x,
                                    minY + y,
                                    minZ + z
                            )
                            : isMSPosition
                            ? (T) MSPosition.of(
                                    minX + x,
                                    minY + y,
                                    minZ + z
                            )
                            : isPosition
                            ? (T) new Vec3(
                                    minX + x,
                                    minY + y,
                                    minZ + z
                            )
                            : isBlockPos
                            ? (T) new BlockPos(
                                    minX + x,
                                    minY + y,
                                    minZ + z
                            )
                            : (T) new Location(
                                    null,
                                    minX + x,
                                    minY + y,
                                    minZ + z
                            );
                }
            }
        }

        return array;
    }
}
