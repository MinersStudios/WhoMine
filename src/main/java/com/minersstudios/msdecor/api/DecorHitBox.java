package com.minersstudios.msdecor.api;

import com.minersstudios.mscore.location.MSBoundingBox;
import com.minersstudios.mscore.location.MSPosition;
import com.minersstudios.mscore.util.LocationUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.ArrayList;

public final class DecorHitBox {
    private final double x;
    private final double y;
    private final double z;
    private final double offsetX;
    private final double offsetY;
    private final double offsetZ;
    private final Type type;

    public static final String HITBOX_CHILD = "hitbox_child";
    public static final String HITBOX_DISPLAY = "hitbox_display";
    public static final String HITBOX_INTERACTIONS = "hitbox_interactions";
    public static final String HITBOX_BOUNDING_BOX = "hitbox_bounding_box";
    public static final NamespacedKey HITBOX_CHILD_NAMESPACED_KEY = new NamespacedKey(CustomDecorType.NAMESPACE, HITBOX_CHILD);
    public static final NamespacedKey HITBOX_DISPLAY_NAMESPACED_KEY = new NamespacedKey(CustomDecorType.NAMESPACE, HITBOX_DISPLAY);
    public static final NamespacedKey HITBOX_INTERACTIONS_NAMESPACED_KEY = new NamespacedKey(CustomDecorType.NAMESPACE, HITBOX_INTERACTIONS);
    public static final NamespacedKey HITBOX_BOUNDING_BOX_NAMESPACED_KEY = new NamespacedKey(CustomDecorType.NAMESPACE, HITBOX_BOUNDING_BOX);

    public DecorHitBox(
            final @Range(from = -8, to = 8) double x,
            final @Range(from = -8, to = 8) double y,
            final @Range(from = -8, to = 8) double z,
            final @NotNull Type type
    ) throws IllegalArgumentException {
        this(
                x, y, z,
                0.0d, 0.0d, 0.0d,
                type
        );
    }

    public DecorHitBox(
            final @Range(from = -8, to = 8) double x,
            final @Range(from = -8, to = 8) double y,
            final @Range(from = -8, to = 8) double z,
            final double modelOffsetX,
            final double modelOffsetY,
            final double modelOffsetZ,
            final @NotNull Type type
    ) throws IllegalArgumentException {
        validSize(x);
        validSize(y);
        validSize(z);

        this.x = x;
        this.y = y;
        this.z = z;
        this.offsetX = modelOffsetX;
        this.offsetY = modelOffsetY;
        this.offsetZ = modelOffsetZ;
        this.type = type;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public double getModelOffsetX() {
        return this.offsetX;
    }

    public double getModelOffsetY() {
        return this.offsetY;
    }

    public double getModelOffsetZ() {
        return this.offsetZ;
    }

    public @NotNull Type getType() {
        return this.type;
    }

    /**
     * @return The bounding box of custom decor with location
     *         and rotation
     */
    public @NotNull MSBoundingBox getBoundingBox(
            final @NotNull MSPosition location,
            final float yaw
    ) {
        return MSBoundingBox.of(
                location,
                this.getSecondLocation(location, yaw)
        );
    }

    public static void processInteractions(
            final @NotNull CustomDecorData<?> data,
            final @NotNull ItemDisplay display,
            final Interaction @NotNull [] interactions,
            final @NotNull MSBoundingBox msbb
    ) {
        final Interaction firstInteraction = interactions[0];
        final String firstUUID = firstInteraction.getUniqueId().toString();
        final PersistentDataContainer firstContainer = firstInteraction.getPersistentDataContainer();
        final var uuids = new ArrayList<String>();

        for (int i = 1; i < interactions.length; ++i) {
            final Interaction interaction = interactions[i];

            uuids.add(interaction.getUniqueId().toString());
            interaction.getPersistentDataContainer()
            .set(
                    DecorHitBox.HITBOX_CHILD_NAMESPACED_KEY,
                    PersistentDataType.STRING,
                    firstUUID
            );
        }

        firstContainer.set(
                CustomDecorType.TYPE_NAMESPACED_KEY,
                PersistentDataType.STRING,
                data.getKey().getKey()
        );

        firstContainer.set(
                DecorHitBox.HITBOX_DISPLAY_NAMESPACED_KEY,
                PersistentDataType.STRING,
                display.getUniqueId().toString()
        );

        firstContainer.set(
                DecorHitBox.HITBOX_INTERACTIONS_NAMESPACED_KEY,
                PersistentDataType.STRING,
                String.join(",", uuids)
        );

        firstContainer.set(
                DecorHitBox.HITBOX_BOUNDING_BOX_NAMESPACED_KEY,
                PersistentDataType.STRING,
                String.join(
                        ",",
                        String.valueOf(msbb.minX()),
                        String.valueOf(msbb.minY()),
                        String.valueOf(msbb.minZ()),
                        String.valueOf(msbb.maxX()),
                        String.valueOf(msbb.maxY()),
                        String.valueOf(msbb.maxZ())
                )
        );
    }

    public static boolean isHitBoxParent(final @NotNull Interaction interaction) {
        final PersistentDataContainer container = interaction.getPersistentDataContainer();
        return container.has(CustomDecorType.TYPE_NAMESPACED_KEY)
                && container.has(DecorHitBox.HITBOX_DISPLAY_NAMESPACED_KEY)
                && container.has(DecorHitBox.HITBOX_INTERACTIONS_NAMESPACED_KEY)
                && container.has(DecorHitBox.HITBOX_BOUNDING_BOX_NAMESPACED_KEY);
    }

    public static boolean isHitBoxChild(final @NotNull Interaction interaction) {
        return interaction.getPersistentDataContainer().has(DecorHitBox.HITBOX_CHILD_NAMESPACED_KEY);
    }

    public static void validSize(final double number) throws IllegalArgumentException {
        if (number == 0.0d) {
            throw new IllegalArgumentException("Size cannot equal 0");
        }
        if (number < -8.0d || number > 8.0d) {
            throw new IllegalArgumentException("Size '" + number + "' is not in range [-8, 8]");
        }
    }

    private @NotNull MSPosition getSecondLocation(
            final @NotNull MSPosition location,
            final float yaw
    ) {
        final int x = (int) this.x == 0 ? 1 : (int) this.x;
        final int y = (int) this.y == 0 ? 1 : (int) this.y;
        final int z = (int) this.z == 0 ? 1 : (int) this.z;

        return location.directionalYawOffset(
                x > 0 ? x - 1 : x + 1,
                y > 0 ? y - 1 : y + 1,
                z > 0 ? z - 1 : z + 1,
                LocationUtils.to90(yaw)
        );
    }

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
        private final Block nmsBlock;

        Type(
                final @NotNull Material material,
                final @NotNull Block nmsBlock
        ) {
            this.material = material;
            this.nmsBlock = nmsBlock;
        }

        public @NotNull Material getMaterial() {
            return this.material;
        }

        public @NotNull Block getNMSBlock() {
            return this.nmsBlock;
        }

        public boolean isSolid() {
            return this == SOLID;
        }

        public boolean isLight() {
            return this == LIGHT;
        }

        public boolean isNone() {
            return this == NONE;
        }
    }
}