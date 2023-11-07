package com.minersstudios.mscore.location;

import com.google.common.primitives.Doubles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.Immutable;

@SuppressWarnings("UnstableApiUsage")
@Immutable
public final class MSVector {
    private final double x;
    private final double y;
    private final double z;

    private static final double EPSILON = 1E-4;
    private static final MSVector ZERO = of(0.0d, 0.0d, 0.0d);

    private MSVector(
            double x,
            double y,
            double z
    ) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static @NotNull MSVector zero() {
        return ZERO;
    }

    @Contract("_, _, _ -> new")
    public static @NotNull MSVector of(
            final double x,
            final double y,
            final double z
    ) {
        return new MSVector(x, y, z);
    }

    @Contract("_ -> new")
    public static @NotNull MSVector of(final @NotNull Vector vec) {
        return of(vec.getX(), vec.getY(), vec.getZ());
    }

    @Contract("_ -> new")
    public static @NotNull MSVector of(final @NotNull Vec3i vec) {
        return of(vec.getX(), vec.getY(), vec.getZ());
    }

    @Contract("_ -> new")
    public static @NotNull MSVector of(final @NotNull MSPosition pos) {
        return of(pos.x(), pos.y(), pos.z());
    }

    @Contract("_ -> new")
    public static @NotNull MSVector of(final @NotNull Position pos) {
        return of(pos.x(), pos.y(), pos.z());
    }

    @Contract("_ -> new")
    public static @NotNull MSVector of(final @NotNull io.papermc.paper.math.Position pos) {
        return of(pos.x(), pos.y(), pos.z());
    }

    public double x() {
        return this.x;
    }

    @Contract("_ -> new")
    public @NotNull MSVector x(final double x) {
        return of(x, this.y, this.z);
    }

    public double y() {
        return this.y;
    }

    @Contract("_ -> new")
    public @NotNull MSVector y(final double y) {
        return of(this.x, y, this.z);
    }

    public double z() {
        return this.z;
    }

    @Contract("_ -> new")
    public @NotNull MSVector z(final double z) {
        return of(this.x, this.y, z);
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

    @Contract("_ -> new")
    public @NotNull MSVector offset(final @NotNull MSVector vec) {
        return this.offset(vec.x(), vec.y(), vec.z());
    }

    @Contract("_ -> new")
    public @NotNull MSVector offset(final @NotNull Vector vec) {
        return this.offset(vec.getX(), vec.getY(), vec.getZ());
    }

    @Contract("_ -> new")
    public @NotNull MSVector offset(final @NotNull Vec3i vec) {
        return this.offset(vec.getX(), vec.getY(), vec.getZ());
    }

    @Contract("_ -> new")
    public @NotNull MSVector offset(final @NotNull MSPosition pos) {
        return this.offset(pos.x(), pos.y(), pos.z());
    }

    @Contract("_ -> new")
    public @NotNull MSVector offset(final @NotNull Position pos) {
        return this.offset(pos.x(), pos.y(), pos.z());
    }

    @Contract("_ -> new")
    public @NotNull MSVector offset(final @NotNull io.papermc.paper.math.Position pos) {
        return this.offset(pos.x(), pos.y(), pos.z());
    }

    @Contract("_ -> new")
    public @NotNull MSVector offset(final double value) {
        return this.offset(value, value, value);
    }

    @Contract("_, _, _ -> new")
    public @NotNull MSVector offset(
            final double x,
            final double y,
            final double z
    ) {
        return of(this.x + x, this.y + y, this.z + z);
    }

    @Contract("_ -> new")
    public @NotNull MSVector multiply(final @NotNull MSVector vec) {
        return this.multiply(vec.x(), vec.y(), vec.z());
    }

    @Contract("_ -> new")
    public @NotNull MSVector multiply(final @NotNull Vector vec) {
        return this.multiply(vec.getX(), vec.getY(), vec.getZ());
    }

    @Contract("_ -> new")
    public @NotNull MSVector multiply(final @NotNull Vec3i vec) {
        return this.multiply(vec.getX(), vec.getY(), vec.getZ());
    }

    @Contract("_ -> new")
    public @NotNull MSVector multiply(final @NotNull MSPosition pos) {
        return this.multiply(pos.x(), pos.y(), pos.z());
    }

    @Contract("_ -> new")
    public @NotNull MSVector multiply(final @NotNull Position pos) {
        return this.multiply(pos.x(), pos.y(), pos.z());
    }

    @Contract("_ -> new")
    public @NotNull MSVector multiply(final @NotNull io.papermc.paper.math.Position pos) {
        return this.multiply(pos.x(), pos.y(), pos.z());
    }

    @Contract("_ -> new")
    public @NotNull MSVector multiply(final double multiplier) {
        return this.multiply(multiplier, multiplier, multiplier);
    }

    @Contract("_, _, _ -> new")
    public @NotNull MSVector multiply(
            final double x,
            final double y,
            final double z
    ) {
        return of(this.x * x, this.y * y, this.z * z);
    }

    @Contract("_ -> new")
    public @NotNull MSVector divide(final @NotNull MSVector vec) {
        return this.divide(vec.x(), vec.y(), vec.z());
    }

    @Contract("_ -> new")
    public @NotNull MSVector divide(final @NotNull Vector vec) {
        return this.divide(vec.getX(), vec.getY(), vec.getZ());
    }

    @Contract("_ -> new")
    public @NotNull MSVector divide(final @NotNull Vec3i vec) {
        return this.divide(vec.getX(), vec.getY(), vec.getZ());
    }

    @Contract("_ -> new")
    public @NotNull MSVector divide(final @NotNull MSPosition pos) {
        return this.divide(pos.x(), pos.y(), pos.z());
    }

    @Contract("_ -> new")
    public @NotNull MSVector divide(final @NotNull Position pos) {
        return this.divide(pos.x(), pos.y(), pos.z());
    }

    @Contract("_ -> new")
    public @NotNull MSVector divide(final @NotNull io.papermc.paper.math.Position pos) {
        return this.divide(pos.x(), pos.y(), pos.z());
    }

    @Contract("_ -> new")
    public @NotNull MSVector divide(final double divider) {
        return this.divide(divider, divider, divider);
    }

    @Contract("_, _, _ -> new")
    public @NotNull MSVector divide(
            final double x,
            final double y,
            final double z
    ) {
        return of(this.x / x, this.y / y, this.z / z);
    }

    public double length() {
        return Math.sqrt(this.lengthSquared());
    }

    public double lengthSquared() {
        return Math.pow(this.x, 2.0d) + Math.pow(this.y, 2.0d) + Math.pow(this.z, 2.0d);
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

    public double horizontalDistance(final @NotNull MSVector vec) {
        return this.horizontalDistance(vec.x(), vec.z());
    }

    public double horizontalDistance(final @NotNull Vector vec) {
        return this.horizontalDistance(vec.getX(), vec.getZ());
    }

    public double horizontalDistance(final @NotNull Vec3i vec) {
        return this.horizontalDistance(vec.getX(), vec.getZ());
    }

    public double horizontalDistance(final @NotNull MSPosition pos) {
        return this.horizontalDistance(pos.x(), pos.z());
    }

    public double horizontalDistance(final @NotNull Position pos) {
        return this.horizontalDistance(pos.x(), pos.z());
    }

    public double horizontalDistance(final @NotNull io.papermc.paper.math.Position pos) {
        return this.horizontalDistance(pos.x(), pos.z());
    }

    public double horizontalDistance(
            final double x,
            final double z
    ) {
        return Math.sqrt(this.horizontalDistanceSquared(x, z));
    }

    public double horizontalDistanceSquared(final @NotNull MSVector vec) {
        return this.horizontalDistanceSquared(vec.x(), vec.z());
    }

    public double horizontalDistanceSquared(final @NotNull Vector vec) {
        return this.horizontalDistanceSquared(vec.getX(), vec.getZ());
    }

    public double horizontalDistanceSquared(final @NotNull Vec3i vec) {
        return this.horizontalDistanceSquared(vec.getX(), vec.getZ());
    }

    public double horizontalDistanceSquared(final @NotNull MSPosition pos) {
        return this.horizontalDistanceSquared(pos.x(), pos.z());
    }

    public double horizontalDistanceSquared(final @NotNull Position pos) {
        return this.horizontalDistanceSquared(pos.x(), pos.z());
    }

    public double horizontalDistanceSquared(final @NotNull io.papermc.paper.math.Position pos) {
        return this.horizontalDistanceSquared(pos.x(), pos.z());
    }

    public double horizontalDistanceSquared(
            final double x,
            final double z
    ) {
        return Math.pow(this.x - x, 2.0d)
                + Math.pow(this.z - z, 2.0d);
    }

    public float angle(@NotNull MSVector vec) {
        return this.angle(vec.x(), vec.y(), vec.z());
    }

    public float angle(@NotNull Vector vec) {
        return this.angle(vec.getX(), vec.getY(), vec.getZ());
    }

    public float angle(@NotNull Vec3i vec) {
        return this.angle(vec.getX(), vec.getY(), vec.getZ());
    }

    public float angle(@NotNull MSPosition pos) {
        return this.angle(pos.x(), pos.y(), pos.z());
    }

    public float angle(@NotNull Position pos) {
        return this.angle(pos.x(), pos.y(), pos.z());
    }

    public float angle(@NotNull io.papermc.paper.math.Position pos) {
        return this.angle(pos.x(), pos.y(), pos.z());
    }

    public float angle(
            final double x,
            final double y,
            final double z
    ) {
        final MSVector other = of(x, y, z);
        return (float) Math.acos(Doubles.constrainToRange(
                        this.dot(other) / (this.length() * other.length()),
                        -1.0,
                        1.0
                )
        );
    }

    @Contract("_ -> new")
    public @NotNull MSVector midpoint(@NotNull MSVector vec) {
        return this.midpoint(vec.x(), vec.y(), vec.z());
    }

    @Contract("_ -> new")
    public @NotNull MSVector midpoint(@NotNull Vector vec) {
        return this.midpoint(vec.getX(), vec.getY(), vec.getZ());
    }

    @Contract("_ -> new")
    public @NotNull MSVector midpoint(@NotNull Vec3i vec) {
        return this.midpoint(vec.getX(), vec.getY(), vec.getZ());
    }

    @Contract("_ -> new")
    public @NotNull MSVector midpoint(@NotNull MSPosition pos) {
        return this.midpoint(pos.x(), pos.y(), pos.z());
    }

    @Contract("_ -> new")
    public @NotNull MSVector midpoint(@NotNull Position pos) {
        return this.midpoint(pos.x(), pos.y(), pos.z());
    }

    @Contract("_ -> new")
    public @NotNull MSVector midpoint(@NotNull io.papermc.paper.math.Position pos) {
        return this.midpoint(pos.x(), pos.y(), pos.z());
    }

    @Contract("_, _, _ -> new")
    public @NotNull MSVector midpoint(
            final double x,
            final double y,
            final double z
    ) {
        return of(
                (this.x + x) / 2.0d,
                (this.y + y) / 2.0d,
                (this.z + z) / 2.0d
        );
    }

    public double dot(@NotNull MSVector vec) {
        return this.dot(vec.x(), vec.y(), vec.z());
    }

    public double dot(@NotNull Vector vec) {
        return this.dot(vec.getX(), vec.getY(), vec.getZ());
    }

    public double dot(@NotNull Vec3i vec) {
        return this.dot(vec.getX(), vec.getY(), vec.getZ());
    }

    public double dot(@NotNull MSPosition pos) {
        return this.dot(pos.x(), pos.y(), pos.z());
    }

    public double dot(@NotNull Position pos) {
        return this.dot(pos.x(), pos.y(), pos.z());
    }

    public double dot(@NotNull io.papermc.paper.math.Position pos) {
        return this.dot(pos.x(), pos.y(), pos.z());
    }

    public double dot(
            final double x,
            final double y,
            final double z
    ) {
        return this.x * x + this.y * y + this.z * z;
    }

    @Contract("_ -> new")
    public @NotNull MSVector cross(@NotNull MSVector vec) {
        return this.cross(vec.x(), vec.y(), vec.z());
    }

    @Contract("_ -> new")
    public @NotNull MSVector cross(@NotNull Vector vec) {
        return this.cross(vec.getX(), vec.getY(), vec.getZ());
    }

    @Contract("_ -> new")
    public @NotNull MSVector cross(@NotNull Vec3i vec) {
        return this.cross(vec.getX(), vec.getY(), vec.getZ());
    }

    @Contract("_ -> new")
    public @NotNull MSVector cross(@NotNull MSPosition pos) {
        return this.cross(pos.x(), pos.y(), pos.z());
    }

    @Contract("_ -> new")
    public @NotNull MSVector cross(@NotNull Position pos) {
        return this.cross(pos.x(), pos.y(), pos.z());
    }

    @Contract("_ -> new")
    public @NotNull MSVector cross(@NotNull io.papermc.paper.math.Position pos) {
        return this.cross(pos.x(), pos.y(), pos.z());
    }

    @Contract("_, _, _ -> new")
    public @NotNull MSVector cross(
            final double x,
            final double y,
            final double z
    ) {
        return of(
                this.y * z - this.z * y,
                this.z * x - this.x * z,
                this.x * y - this.y * x
        );
    }

    @Contract("_ -> new")
    public @NotNull MSVector rotateAroundX(final double angle) {
        final double cos = Math.cos(angle);
        final double sin = Math.sin(angle);
        return of(
                this.x,
                this.y * cos - this.z * sin,
                this.y * sin + this.z * cos
        );
    }

    @Contract("_ -> new")
    public @NotNull MSVector rotateAroundY(final double angle) {
        final double cos = Math.cos(angle);
        final double sin = Math.sin(angle);
        return of(
                this.x * cos + this.z * sin,
                this.y,
                this.z * cos - this.x * sin
        );
    }

    @Contract("_ -> new")
    public @NotNull MSVector rotateAroundZ(final double angle) {
        final double cos = Math.cos(angle);
        final double sin = Math.sin(angle);
        return of(
                this.x * cos - this.y * sin,
                this.x * sin + this.y * cos,
                this.z
        );
    }

    @Contract("_, _ -> new")
    public @NotNull MSVector rotateAroundAxis(
            final @NotNull MSVector axis,
            final double angle
    ) {
        return this.rotateAroundAxis(
                axis.x(), axis.y(), axis.z(),
                angle
        );
    }

    @Contract("_, _ -> new")
    public @NotNull MSVector rotateAroundAxis(
            final @NotNull Vector axis,
            final double angle
    ) {
        return this.rotateAroundAxis(
                axis.getX(), axis.getY(), axis.getZ(),
                angle
        );
    }

    @Contract("_, _ -> new")
    public @NotNull MSVector rotateAroundAxis(
            final @NotNull Vec3i axis,
            final double angle
    ) {
        return this.rotateAroundAxis(
                axis.getX(), axis.getY(), axis.getZ(),
                angle
        );
    }

    @Contract("_, _ -> new")
    public @NotNull MSVector rotateAroundAxis(
            final @NotNull MSPosition axis,
            final double angle
    ) {
        return this.rotateAroundAxis(
                axis.x(), axis.y(), axis.z(),
                angle
        );
    }

    @Contract("_, _ -> new")
    public @NotNull MSVector rotateAroundAxis(
            final @NotNull Position axis,
            final double angle
    ) {
        return this.rotateAroundAxis(
                axis.x(), axis.y(), axis.z(),
                angle
        );
    }

    @Contract("_, _ -> new")
    public @NotNull MSVector rotateAroundAxis(
            final @NotNull io.papermc.paper.math.Position axis,
            final double angle
    ) {
        return this.rotateAroundAxis(
                axis.x(), axis.y(), axis.z(),
                angle
        );
    }

    @Contract("_, _, _, _ -> new")
    public @NotNull MSVector rotateAroundAxis(
            final double x,
            final double y,
            final double z,
            final double angle
    ) {
        final double sqrt = Math.sqrt(Math.pow(x, 2.0d) + Math.pow(y, 2.0d) + Math.pow(z, 2.0d));
        return this.rotateAroundNonUnitAxis(
                x / sqrt,
                y / sqrt,
                z / sqrt,
                angle
        );
    }

    @Contract("_, _ -> new")
    public @NotNull MSVector rotateAroundNonUnitAxis(
            final @NotNull MSVector axis,
            final double angle
    ) {
        return this.rotateAroundNonUnitAxis(
                axis.x(), axis.y(), axis.z(),
                angle
        );
    }

    @Contract("_, _ -> new")
    public @NotNull MSVector rotateAroundNonUnitAxis(
            final @NotNull Vector axis,
            final double angle
    ) {
        return this.rotateAroundNonUnitAxis(
                axis.getX(), axis.getY(), axis.getZ(),
                angle
        );
    }

    @Contract("_, _ -> new")
    public @NotNull MSVector rotateAroundNonUnitAxis(
            final @NotNull Vec3i axis,
            final double angle
    ) {
        return this.rotateAroundNonUnitAxis(
                axis.getX(), axis.getY(), axis.getZ(),
                angle
        );
    }

    @Contract("_, _ -> new")
    public @NotNull MSVector rotateAroundNonUnitAxis(
            final @NotNull MSPosition axis,
            final double angle
    ) {
        return this.rotateAroundNonUnitAxis(
                axis.x(), axis.y(), axis.z(),
                angle
        );
    }

    @Contract("_, _ -> new")
    public @NotNull MSVector rotateAroundNonUnitAxis(
            final @NotNull Position axis,
            final double angle
    ) {
        return this.rotateAroundNonUnitAxis(
                axis.x(), axis.y(), axis.z(),
                angle
        );
    }

    @Contract("_, _ -> new")
    public @NotNull MSVector rotateAroundNonUnitAxis(
            final @NotNull io.papermc.paper.math.Position axis,
            final double angle
    ) {
        return this.rotateAroundNonUnitAxis(
                axis.x(), axis.y(), axis.z(),
                angle
        );
    }

    @Contract("_, _, _, _ -> new")
    public @NotNull MSVector rotateAroundNonUnitAxis(
            final double x,
            final double y,
            final double z,
            final double angle
    ) {
        final double cos = Math.cos(angle);
        final double sin = Math.sin(angle);
        final double dot = this.dot(x, y, z);
        return of(
                x * dot * (1.0d - cos) + this.x * cos + (y * this.z - z * this.y) * sin,
                y * dot * (1.0d - cos) + this.y * cos + (z * this.x - x * this.z) * sin,
                z * dot * (1.0d - cos) + this.z * cos + (x * this.y - y * this.x) * sin
        );
    }

    @Contract(" -> new")
    public @NotNull MSVector normalize() {
        return this.divide(this.length());
    }

    @Contract(" -> new")
    public @NotNull MSVector invert() {
        return of(-this.x, -this.y, -this.z);
    }

    public boolean isInSphere(
            final @NotNull MSVector center,
            final double radius
    ) {
        return this.isInSphere(
                center.x(), center.y(), center.z(),
                radius
        );
    }

    public boolean isInSphere(
            final @NotNull Vector center,
            final double radius
    ) {
        return this.isInSphere(
                center.getX(), center.getY(), center.getZ(),
                radius
        );
    }

    public boolean isInSphere(
            final @NotNull Vec3i center,
            final double radius
    ) {
        return this.isInSphere(
                center.getX(), center.getY(), center.getZ(),
                radius
        );
    }

    public boolean isInSphere(
            final @NotNull MSPosition center,
            final double radius
    ) {
        return this.isInSphere(
                center.x(), center.y(), center.z(),
                radius
        );
    }

    public boolean isInSphere(
            final @NotNull Position center,
            final double radius
    ) {
        return this.isInSphere(
                center.x(), center.y(), center.z(),
                radius
        );
    }

    public boolean isInSphere(
            final @NotNull io.papermc.paper.math.Position center,
            final double radius
    ) {
        return this.isInSphere(
                center.x(), center.y(), center.z(),
                radius
        );
    }

    public boolean isInSphere(
            final double x,
            final double y,
            final double z,
            final double radius
    ) {
        return this.distanceSquared(x, y, z) <= Math.pow(radius, 2.0d);
    }

    public boolean isNormalized() {
        return Math.abs(this.lengthSquared() - 1) < EPSILON;
    }

    public boolean isZero() {
        return this.x == 0.0d
                && this.y == 0.0d
                && this.z == 0.0d;
    }

    public boolean isFinite() {
        return Double.isFinite(this.x)
                && Double.isFinite(this.y)
                && Double.isFinite(this.z);
    }

    public boolean hasInfinite() {
        return Double.isInfinite(this.x)
                || Double.isInfinite(this.y)
                || Double.isInfinite(this.z);
    }

    public boolean hasNaN() {
        return Double.isNaN(this.x)
                || Double.isNaN(this.y)
                || Double.isNaN(this.z);
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 31 * hash + Double.hashCode(this.x);
        hash = 31 * hash + Double.hashCode(this.y);
        hash = 31 * hash + Double.hashCode(this.z);

        return hash;
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MSVector other)) return false;
        return Math.abs(this.x - other.x) < EPSILON
                && Math.abs(this.y - other.y) < EPSILON
                && Math.abs(this.z - other.z) < EPSILON;
    }

    public @NotNull MSVector copy() {
        return of(this.x, this.y, this.z);
    }

    @Override
    public @NotNull String toString() {
        return this.x + "," + this.y + "," + this.z;
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
    public @NotNull MSPosition toMSPosition() {
        return MSPosition.of(this);
    }

    @Contract(" -> new")
    public @NotNull Location toLocation() {
        return new Location(null, this.x, this.y, this.z);
    }

    @Contract("_ -> new")
    public @NotNull Location toLocation(final @Nullable World world) {
        return new Location(world, this.x, this.y, this.z);
    }

    @Contract(" -> new")
    public @NotNull BlockPos toBlockPos() {
        return new BlockPos((int) this.x, (int) this.y, (int) this.z);
    }

    @Contract(" -> new")
    public @NotNull MSBoundingBox toMSBoundingBox() {
        return MSBoundingBox.of(this);
    }

    public void checkFinite() {
        if (!this.isFinite()) {
            throw new IllegalStateException("Vector is not finite");
        }
    }
}
