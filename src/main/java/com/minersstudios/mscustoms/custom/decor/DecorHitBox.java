package com.minersstudios.mscustoms.custom.decor;

import com.minersstudios.mscore.location.MSBoundingBox;
import com.minersstudios.mscore.location.MSPosition;
import com.minersstudios.mscore.location.MSVector;
import com.minersstudios.mscore.utility.LocationUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Interaction;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import javax.annotation.concurrent.Immutable;
import java.util.EnumSet;
import java.util.Set;

import static com.minersstudios.mscore.utility.SharedConstants.MSDECOR_NAMESPACE;

/**
 * Represents a DecorHitBox, defining the hitbox properties for custom decor
 */
@Immutable
public final class DecorHitBox {
    private final Type type;
    private final EnumSet<Facing> facingSet;
    private final double x;
    private final double y;
    private final double z;
    private final double modelOffsetX;
    private final double modelOffsetY;
    private final double modelOffsetZ;
    private final boolean wallDirected;

    public static final String HITBOX_CHILD_KEY =        "hitbox_child";
    public static final String HITBOX_DISPLAY_KEY =      "hitbox_display";
    public static final String HITBOX_INTERACTIONS_KEY = "hitbox_interactions";
    public static final String HITBOX_BOUNDING_BOX_KEY = "hitbox_bounding_box";

    public static final NamespacedKey HITBOX_CHILD_NAMESPACED_KEY =        new NamespacedKey(MSDECOR_NAMESPACE, HITBOX_CHILD_KEY);
    public static final NamespacedKey HITBOX_DISPLAY_NAMESPACED_KEY =      new NamespacedKey(MSDECOR_NAMESPACE, HITBOX_DISPLAY_KEY);
    public static final NamespacedKey HITBOX_INTERACTIONS_NAMESPACED_KEY = new NamespacedKey(MSDECOR_NAMESPACE, HITBOX_INTERACTIONS_KEY);
    public static final NamespacedKey HITBOX_BOUNDING_BOX_NAMESPACED_KEY = new NamespacedKey(MSDECOR_NAMESPACE, HITBOX_BOUNDING_BOX_KEY);

    /**
     * Constructs a DecorHitBox based on the provided Builder
     *
     * @param builder The Builder used to construct the DecorHitBox
     */
    private DecorHitBox(final @NotNull Builder builder) {
        this.type = builder.type;
        this.facingSet = builder.facingSet;
        this.x = builder.x;
        this.y = builder.y;
        this.z = builder.z;
        this.modelOffsetX = builder.modelOffsetX;
        this.modelOffsetY = builder.modelOffsetY;
        this.modelOffsetZ = builder.modelOffsetZ;
        this.wallDirected = builder.wallDirected;
    }

    /**
     * @return A new instance of the decor hitbox builder
     */
    public static @NotNull Builder builder() {
        return new Builder();
    }

    /**
     * @return The type of the hitbox
     */
    public @NotNull Type getType() {
        return this.type;
    }

    /**
     * @return The clone of the facing set
     */
    public @NotNull EnumSet<Facing> getFacingSet() {
        return this.facingSet.clone();
    }

    /**
     * @return The x size of the hitbox
     */
    public double getX() {
        return this.x;
    }

    /**
     * @return The y size of the hitbox
     */
    public double getY() {
        return this.y;
    }

    /**
     * @return The z size of the hitbox
     */
    public double getZ() {
        return this.z;
    }

    /**
     * @return The size of the hitbox
     */
    public @NotNull MSVector getSize() {
        return MSVector.of(this.x, this.y, this.z);
    }

    /**
     * @return The x offset of the decor model
     */
    public double getModelOffsetX() {
        return this.modelOffsetX;
    }

    /**
     * @return The y offset of the decor model
     */
    public double getModelOffsetY() {
        return this.modelOffsetY;
    }

    /**
     * @return The z offset of the decor model
     */
    public double getModelOffsetZ() {
        return this.modelOffsetZ;
    }

    /**
     * @return The offset of the decor model
     */
    public @NotNull MSVector getModelOffset() {
        return MSVector.of(this.modelOffsetX, this.modelOffsetY, this.modelOffsetZ);
    }

    /**
     * @return The width of the hitbox interaction
     */
    public float getInteractionWidth() {
        return this.z > 1.0d
                ? 1.0f
                : (float) this.z;
    }

    /**
     * @return The height of the hitbox interaction
     */
    public float getInteractionHeight(final @NotNull BlockFace blockFace) {
        return this.facingSet.contains(Facing.CEILING)
                && blockFace == BlockFace.DOWN
                ? (float) -this.y
                : (float) this.y;
    }

    /**
     * @return The bounding box of the hitbox
     */
    public @NotNull MSBoundingBox getBoundingBox(
            final @NotNull MSPosition position,
            final @NotNull BlockFace blockFace,
            final float yaw
    ) {
        final int interactionHeight = (int) this.getInteractionHeight(blockFace);

        final int x = (int) this.x == 0 ? 1 : (int) this.x;
        final int y = interactionHeight == 0 ? 1 : interactionHeight;
        final int z = (int) this.z == 0 ? 1 : (int) this.z;

        return MSBoundingBox.of(
                position,
                position
                .directionalYawOffset(
                        x > 0 ? x - 1 : x + 1,
                        y > 0 ? y - 1 : y + 1,
                        z > 0 ? z - 1 : z + 1,
                        LocationUtils.to90(yaw)
                )
        );
    }

    /**
     * @param blockFace The block face
     * @param rotation  The rotation of the player
     * @return The offset vector of the hitbox interaction, used for interaction
     *         summoning in the correct position in the world
     */
    public @NotNull MSVector getVectorInBlock(
            final @NotNull BlockFace blockFace,
            final float rotation
    ) {
        double x;
        final double y;
        double z;

        final boolean isWall = this.isWall(blockFace);
        final boolean isCeiling = this.isCeiling(blockFace);

        if (isWall) {
            final BlockFace rotationFace = LocationUtils.degreesToBlockFace45(-rotation);
            final boolean diagonal = LocationUtils.isDiagonal(rotationFace);

            x =
                    diagonal
                    || LocationUtils.isX(rotationFace)
                            ? this.getInteractionWidth() / 2.0d
                            : 0.5d;
            z =
                    diagonal
                    || LocationUtils.isZ(rotationFace)
                            ? this.getInteractionWidth() / 2.0d
                            : 0.5d;

            switch (rotationFace) {
                case WEST, NORTH_WEST -> x = 1.0d - x;
                case SOUTH, SOUTH_EAST -> z = 1.0d - z;
                case SOUTH_WEST -> {
                    x = 1.0d - x;
                    z = 1.0d - z;
                }
            }
        } else {
            x = 0.5d;
            z = 0.5d;
        }

        if (
                isWall
                && !isCeiling
                && !this.isFloor(blockFace)
        ) {
            y = 0.5d - this.getInteractionHeight(blockFace) / 2.0d;
        } else if (isCeiling) {
            y = 1.0d;
        } else {
            y = 0.0d;
        }

        return MSVector.of(x, y, z);
    }

    /**
     * @return Whether the hitbox is wall directed
     */
    public boolean isWallDirected() {
        return this.wallDirected;
    }

    /**
     * @param blockFace The block face
     * @return True, if the hitbox is ceiling directed and the block face is down
     */
    public boolean isCeiling(final @NotNull BlockFace blockFace) {
        return this.facingSet.contains(Facing.CEILING)
                && blockFace == BlockFace.DOWN;
    }

    /**
     * @param blockFace The block face
     * @return True, if the hitbox is floor directed and the block face is up
     */
    public boolean isFloor(final @NotNull BlockFace blockFace) {
        return this.facingSet.contains(Facing.FLOOR)
                && blockFace == BlockFace.UP;
    }

    /**
     * @param blockFace The block face
     * @return True, if the hitbox is wall directed and the block face is not up
     *         or down
     */
    public boolean isWall(final @NotNull BlockFace blockFace) {
        return this.facingSet.contains(Facing.WALL)
                && (
                        this.wallDirected
                        || Facing.WALL.hasFace(blockFace)
                );
    }

    /**
     * @param interaction The interaction to be checked
     * @return True, if the interaction is a parent
     */
    public static boolean isParent(final @NotNull Interaction interaction) {
        return isParent(interaction.getPersistentDataContainer());
    }

    /**
     * @param interaction The interaction to be checked
     * @return True, if the interaction is a child
     */
    public static boolean isChild(final @NotNull Interaction interaction) {
        return isChild(interaction.getPersistentDataContainer());
    }

    /**
     * @param dataContainer The data container to be checked
     * @return True, if the data container is a parent
     */
    public static boolean isParent(final @NotNull PersistentDataContainer dataContainer) {
        return dataContainer.has(CustomDecorType.TYPE_NAMESPACED_KEY);
    }

    /**
     * @param dataContainer The data container to be checked
     * @return True, if the data container is a child
     */
    public static boolean isChild(final @NotNull PersistentDataContainer dataContainer) {
        return dataContainer.has(DecorHitBox.HITBOX_CHILD_NAMESPACED_KEY);
    }

    /**
     * @return Builder with the same values as this DecorHitBox
     */
    public @NotNull Builder toBuilder() {
        final Builder builder = new Builder();

        builder.type = this.type;
        builder.facingSet = this.facingSet;
        builder.x = this.x;
        builder.y = this.y;
        builder.z = this.z;
        builder.modelOffsetX = this.modelOffsetX;
        builder.modelOffsetY = this.modelOffsetY;
        builder.modelOffsetZ = this.modelOffsetZ;
        builder.wallDirected = this.wallDirected;

        return builder;
    }

    /**
     * @param number The number to be validated
     * @throws IllegalArgumentException If the number is not in range [0, 8]
     */
    public static void validVerticalSize(final double number) throws IllegalArgumentException {
        if (number == 0.0d) {
            throw new IllegalArgumentException("Size cannot equal 0");
        }

        if (number < 0.0d || number > 8.0d) {
            throw new IllegalArgumentException("Vertical size '" + number + "' is not in range [0, 8]");
        }
    }

    /**
     * @param number The number to be validated
     * @throws IllegalArgumentException If the number is not in range [-8, 8]
     */
    public static void validHorizontalSize(final double number) throws IllegalArgumentException {
        if (number == 0.0d) {
            throw new IllegalArgumentException("Size cannot equal 0");
        }

        if (number < -8.0d || number > 8.0d) {
            throw new IllegalArgumentException("Horizontal size '" + number + "' is not in range [-8, 8]");
        }

        if (
                (
                        number > 1.0d
                        && Math.floor(number) - number != 0.0d
                )
                || (
                        number < -1.0d
                        && Math.ceil(number) - number != 0.0d
                )
        ) {
            throw new IllegalArgumentException("Horizontal size '" + number + "' cannot be greater than 1 with a decimal");
        }
    }

    /**
     * @param number The number to be validated
     * @throws IllegalArgumentException If the number is not in range [-8, 8]
     */
    public static void validModelOffset(final double number) throws IllegalArgumentException {
        if (number < -8.0d || number > 8.0d) {
            throw new IllegalArgumentException("Model offset '" + number + "' is not in range [-8, 8]");
        }
    }

    /**
     * Builder class for constructing instances of DecorHitBox
     */
    public static final class Builder {
        private Type type;
        private EnumSet<Facing> facingSet;
        private double x;
        private double y;
        private double z;
        private double modelOffsetX;
        private double modelOffsetY;
        private double modelOffsetZ;
        private boolean wallDirected;

        private Builder() {
            this.facingSet = EnumSet.of(Facing.FLOOR);
            this.x = Double.NaN;
            this.y = Double.NaN;
            this.z = Double.NaN;
        }

        /**
         * @return The type of the hitbox
         */
        public Type type() {
            return this.type;
        }

        /**
         * Sets the type of the hitbox
         *
         * @param type The new type of the hitbox
         * @return The current builder instance with the new type
         */
        public @NotNull Builder type(final @NotNull Type type) {
            this.type = type;
            return this;
        }

        /**
         * @return The clone of the facing set
         */
        public @NotNull Set<Facing> facings() {
            return this.facingSet.clone();
        }

        /**
         * Sets the facings of the hitbox
         *
         * @param first The first facing
         * @param rest  The rest of the facings
         * @return The current builder instance with the new facings
         */
        public @NotNull Builder facings(
                final @NotNull Facing first,
                final Facing @NotNull ... rest
        ) {
            this.facingSet = EnumSet.of(first, rest);
            return this;
        }

        /**
         * @return The x size of the hitbox
         */
        public double x() {
            return this.x;
        }

        /**
         * Sets the x size of the hitbox
         *
         * @param x The new x size of the hitbox
         * @return The current builder instance with the new x size
         * @throws IllegalArgumentException If the x size is not in range [-8, 8]
         */
        public @NotNull Builder x(final @Range(from = -8, to = 8) double x) throws IllegalArgumentException {
            validHorizontalSize(x);

            this.x = x;
            return this;
        }

        /**
         * @return The y size of the hitbox
         */
        public double y() {
            return this.y;
        }

        /**
         * Sets the y size of the hitbox
         *
         * @param y The new y size of the hitbox
         * @return The current builder instance with the new y size
         * @throws IllegalArgumentException If the y size is not in range [0, 8]
         */
        public @NotNull Builder y(final @Range(from = 0, to = 8) double y) throws IllegalArgumentException {
            validVerticalSize(y);

            this.y = y;
            return this;
        }

        /**
         * @return The z size of the hitbox
         */
        public double z() {
            return this.z;
        }

        /**
         * Sets the z size of the hitbox
         *
         * @param z The new z size of the hitbox
         * @return The current builder instance with the new z size
         * @throws IllegalArgumentException If the z size is not in range [-8, 8]
         */
        public @NotNull Builder z(final @Range(from = -8, to = 8) double z) throws IllegalArgumentException {
            validHorizontalSize(z);

            this.z = z;
            return this;
        }

        /**
         * Sets the size of the hitbox
         *
         * @param x The new x size of the hitbox
         * @param y The new y size of the hitbox
         * @param z The new z size of the hitbox
         * @return The current builder instance with the new size
         * @throws IllegalArgumentException If the x or z size is not in range [-8, 8],
         *                                  or if the y size is not in range [0, 8]
         */
        public @NotNull Builder size(
                final @Range(from = -8, to = 8) double x,
                final @Range(from = 0, to = 8) double y,
                final @Range(from = -8, to = 8) double z
        ) throws IllegalArgumentException {
            validHorizontalSize(x);
            validVerticalSize(y);
            validHorizontalSize(z);

            this.x = x;
            this.y = y;
            this.z = z;

            return this;
        }

        /**
         * @return The x offset of the decor model
         */
        public double modelOffsetX() {
            return this.modelOffsetX;
        }

        /**
         * Sets the x offset of the decor model
         *
         * @param x The new x offset of the decor model
         * @return The current builder instance with the new x offset
         * @throws IllegalArgumentException If the x offset is not in range [-8, 8]
         */
        public @NotNull Builder modelOffsetX(final @Range(from = -8, to = 8) double x) throws IllegalArgumentException {
            validModelOffset(x);

            this.modelOffsetX = x;
            return this;
        }

        /**
         * @return The y offset of the decor model
         */
        public double modelOffsetY() {
            return this.modelOffsetY;
        }

        /**
         * Sets the y offset of the decor model
         *
         * @param y The new y offset of the decor model
         * @return The current builder instance with the new y offset
         * @throws IllegalArgumentException If the y offset is not in range [-8, 8]
         */
        public @NotNull Builder modelOffsetY(final @Range(from = -8, to = 8) double y) throws IllegalArgumentException {
            validModelOffset(y);

            this.modelOffsetY = y;
            return this;
        }

        /**
         * @return The z offset of the decor model
         */
        public double modelOffsetZ() {
            return this.modelOffsetZ;
        }

        /**
         * Sets the z offset of the decor model
         *
         * @param z The new z offset of the decor model
         * @return The current builder instance with the new z offset
         * @throws IllegalArgumentException If the z offset is not in range [-8, 8]
         */
        public @NotNull Builder modelOffsetZ(final @Range(from = -8, to = 8) double z) throws IllegalArgumentException {
            validModelOffset(z);

            this.modelOffsetZ = z;
            return this;
        }

        /**
         * Sets the offset of the decor model
         *
         * @param x The new x offset of the decor model
         * @param y The new y offset of the decor model
         * @param z The new z offset of the decor model
         * @return The current builder instance with the new offset
         * @throws IllegalArgumentException If the x, y, or z offset is not in
         *                                  range [-8, 8]
         */
        public @NotNull Builder modelOffset(
                final @Range(from = -8, to = 8) double x,
                final @Range(from = -8, to = 8) double y,
                final @Range(from = -8, to = 8) double z
        ) throws IllegalArgumentException {
            validModelOffset(x);
            validModelOffset(y);
            validModelOffset(z);

            this.modelOffsetX = x;
            this.modelOffsetY = y;
            this.modelOffsetZ = z;

            return this;
        }

        /**
         * @return Whether the hitbox is wall directed
         */
        public boolean wallDirected() {
            return this.wallDirected;
        }

        /**
         * Sets whether the hitbox is wall directed
         *
         * @param wallDirected Whether the hitbox is wall directed
         * @return The current builder instance with the new wall directed state
         */
        public @NotNull Builder wallDirected(final boolean wallDirected) {
            this.wallDirected = wallDirected;
            return this;
        }

        /**
         * Builds a new DecorHitBox instance
         *
         * @return A new DecorHitBox instance
         * @throws IllegalStateException If the type, x size, y size, or z size
         *                               is not set
         */
        public @NotNull DecorHitBox build() throws IllegalStateException {
            if (this.type == null) {
                throw new IllegalStateException("Type is not set");
            }

            if (Double.isNaN(this.x)) {
                throw new IllegalStateException("X size is not set");
            }

            if (Double.isNaN(this.y)) {
                throw new IllegalStateException("Y size is not set");
            }

            if (Double.isNaN(this.z)) {
                throw new IllegalStateException("Z size is not set");
            }

            if (
                    this.facingSet.contains(Facing.WALL)
                    && this.facingSet.size() == 1
            ) {
                this.wallDirected = true;
            }

            return new DecorHitBox(this);
        }
    }

    /**
     * Enum representing different types of hitbox
     */
    public enum Type {
        SOLID(
                Material.BARRIER,
                Blocks.BARRIER
        ),
        LIGHT(
                Material.LIGHT,
                Blocks.LIGHT
        ),
        NONE(
                Material.AIR,
                Blocks.AIR
        );

        private final Material material;
        private final Block nmsMaterial;

        private static final Type[] VALUES = values();

        Type(
                final @NotNull Material material,
                final @NotNull Block nmsMaterial
        ) {
            this.material = material;
            this.nmsMaterial = nmsMaterial;
        }

        /**
         * @param material The material
         * @return The type of the hitbox represented by the given material
         */
        public static @Nullable Type fromMaterial(final @NotNull Material material) {
            for (final var type : VALUES) {
                if (type.material == material) {
                    return type;
                }
            }

            return null;
        }

        /**
         * @param nmsMaterial The NMS material
         * @return The type of the hitbox represented by the given NMS material
         */
        public static @Nullable Type fromNMSMaterial(final @NotNull Block nmsMaterial) {
            for (final var type : VALUES) {
                if (type.nmsMaterial == nmsMaterial) {
                    return type;
                }
            }

            return null;
        }

        /**
         * @return The Material of the hitbox type
         */
        public @NotNull Material getMaterial() {
            return this.material;
        }

        /**
         * @return The NMS Material of the hitbox type
         */
        public @NotNull Block getNMSMaterial() {
            return this.nmsMaterial;
        }

        /**
         * @return Whether the hitbox type is solid
         */
        public boolean isSolid() {
            return this == SOLID;
        }

        /**
         * @return Whether the hitbox type is light
         */
        public boolean isLight() {
            return this == LIGHT;
        }

        /**
         * @return Whether the hitbox type is none
         */
        public boolean isNone() {
            return this == NONE;
        }
    }
}
