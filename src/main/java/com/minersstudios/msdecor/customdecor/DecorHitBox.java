package com.minersstudios.msdecor.customdecor;

import com.minersstudios.mscore.util.LocationUtils;
import com.minersstudios.mscore.util.MSDecorUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public class DecorHitBox {
    private final double x;
    private final double y;
    private final double z;
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
            final double x,
            final double y,
            final double z,
            final @NotNull Type type
    ) throws IllegalArgumentException {
        if (
                x == 0.0d
                || y == 0.0d
                || z == 0.0d
        ) {
            throw new IllegalArgumentException("Hit box values cannot be zero");
        }

        this.x = x;
        this.y = y;
        this.z = z;
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

    public @NotNull Type getType() {
        return this.type;
    }

    /**
     * @return The bounding box of custom decor with location
     *         and rotation
     */
    public @NotNull BoundingBox getBoundingBox(
            final @NotNull Location location,
            final float yaw
    ) {
        final Location secondLocation = getSecondLocation(location, yaw);

        return new BoundingBox(
                location.x(),
                location.y(),
                location.z(),
                secondLocation.x(),
                secondLocation.y(),
                secondLocation.z()
        );
    }

    /**
     * @return The bounding box of custom decor with location
     *         and rotation
     */
    public @NotNull net.minecraft.world.level.levelgen.structure.BoundingBox getNMSBoundingBox(
            final @NotNull Location location,
            final float yaw
    ) {
        final Location secondLocation = getSecondLocation(location, yaw);

        return new net.minecraft.world.level.levelgen.structure.BoundingBox(
                (int) Math.min(location.x(), secondLocation.x()),
                (int) Math.min(location.y(), secondLocation.y()),
                (int) Math.min(location.z(), secondLocation.z()),
                (int) Math.max(location.x(), secondLocation.x()),
                (int) Math.max(location.y(), secondLocation.y()),
                (int) Math.max(location.z(), secondLocation.z())
        );
    }

    public static void processInteractions(
            final @NotNull CustomDecorData<?> data,
            final @NotNull ItemDisplay display,
            final @NotNull List<Interaction> interactions,
            final @NotNull net.minecraft.world.level.levelgen.structure.BoundingBox boundingBox
    ) {
        final Interaction firstInteraction = interactions.get(0);
        final PersistentDataContainer firstContainer = firstInteraction.getPersistentDataContainer();
        final var uuids = new ArrayList<String>();

        for (int i = 1; i < interactions.size(); i++) {
            uuids.add(interactions.get(i).getUniqueId().toString());
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
                        String.valueOf(boundingBox.minX()),
                        String.valueOf(boundingBox.minY()),
                        String.valueOf(boundingBox.minZ()),
                        String.valueOf(boundingBox.maxX()),
                        String.valueOf(boundingBox.maxY()),
                        String.valueOf(boundingBox.maxZ())
                )
        );

        for (int i = 1; i < interactions.size(); i++) {
            interactions.get(i).getPersistentDataContainer()
            .set(
                    DecorHitBox.HITBOX_CHILD_NAMESPACED_KEY,
                    PersistentDataType.STRING,
                    firstInteraction.getUniqueId().toString()
            );
        }
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

    private @NotNull Location getSecondLocation(
            final @NotNull Location location,
            final float yaw
    ) {
        final int x = (int) this.x == 0 ? 1 : (int) this.x;
        final int y = (int) this.y == 0 ? 1 : (int) this.y;
        final int z = (int) this.z == 0 ? 1 : (int) this.z;

        return LocationUtils.directionalOffset(
                location,
                LocationUtils.to90(yaw),
                x > 0 ? x - 1 : x + 1,
                y > 0 ? y - 1 : y + 1,
                z > 0 ? z - 1 : z + 1
        );
    }

    public static class Elements {
        private final CustomDecorType type;
        private final ItemDisplay display;
        private final List<Interaction> interactions;
        private final net.minecraft.world.level.levelgen.structure.BoundingBox nmsBoundingBox;

        public Elements(
                final @NotNull CustomDecorType type,
                final @NotNull ItemDisplay display,
                final @NotNull List<Interaction> interactions,
                final @NotNull net.minecraft.world.level.levelgen.structure.BoundingBox nmsBoundingBox
        ) {
            this.type = type;
            this.display = display;
            this.interactions = Collections.unmodifiableList(interactions);
            this.nmsBoundingBox = nmsBoundingBox;
        }

        public static @NotNull Optional<DecorHitBox.Elements> fromBlock(final @Nullable org.bukkit.block.Block block) {
            return block == null
                    ? Optional.empty()
                    : fromInteraction(MSDecorUtils.getNearbyInteraction(block.getLocation().toCenterLocation()));
        }

        public static @NotNull Optional<DecorHitBox.Elements> fromInteraction(final @Nullable Interaction interaction) {
            if (interaction == null) return Optional.empty();

            final PersistentDataContainer container = interaction.getPersistentDataContainer();
            return container.isEmpty()
                    ? Optional.empty()
                    : isHitBoxParent(interaction)
                    ? Optional.ofNullable(fromParent(interaction))
                    : isHitBoxChild(interaction)
                    ? Optional.ofNullable(fromChild(interaction))
                    : Optional.empty();
        }

        public @NotNull CustomDecorType getType() {
            return this.type;
        }

        public @NotNull CustomDecorData<?> getData() {
            return this.type.getCustomDecorData();
        }

        public @NotNull ItemDisplay getDisplay() {
            return this.display;
        }

        public @NotNull @Unmodifiable List<Interaction> getInteractions() {
            return this.interactions;
        }

        public @NotNull net.minecraft.world.level.levelgen.structure.BoundingBox getNMSBoundingBox() {
            return this.nmsBoundingBox;
        }

        private static @Nullable DecorHitBox.Elements fromParent(final @NotNull Interaction interaction) {
            final PersistentDataContainer container = interaction.getPersistentDataContainer();

            if (container.isEmpty()) return null;

            CustomDecorType type = null;
            ItemDisplay display = null;
            final var interactions = new ArrayList<Interaction>();
            net.minecraft.world.level.levelgen.structure.BoundingBox boundingBox = null;

            interactions.add(interaction);

            for (final var key : container.getKeys()) {
                final String value = container.get(key, PersistentDataType.STRING);

                if (StringUtils.isBlank(value)) continue;

                switch (key.getKey()) {
                    case CustomDecorType.TYPE_TAG_NAME -> type = CustomDecorType.fromKey(value);
                    case HITBOX_DISPLAY -> {
                        try {
                            if (interaction.getWorld().getEntity(UUID.fromString(value)) instanceof final ItemDisplay itemDisplay) {
                                display = itemDisplay;
                            }
                        } catch (final IllegalArgumentException ignored) {
                            return null;
                        }
                    }
                    case HITBOX_INTERACTIONS -> {
                        for (final var uuid : value.split(",")) {
                            try {
                                if (interaction.getWorld().getEntity(UUID.fromString(uuid)) instanceof final Interaction child) {
                                    interactions.add(child);
                                }
                            } catch (final IllegalArgumentException ignored) {
                                return null;
                            }
                        }
                    }
                    case HITBOX_BOUNDING_BOX -> {
                        final String[] coordinates = value.split(",");

                        if (coordinates.length != 6) return null;

                        try {
                            boundingBox = new net.minecraft.world.level.levelgen.structure.BoundingBox(
                                    Integer.parseInt(coordinates[0]),
                                    Integer.parseInt(coordinates[1]),
                                    Integer.parseInt(coordinates[2]),
                                    Integer.parseInt(coordinates[3]),
                                    Integer.parseInt(coordinates[4]),
                                    Integer.parseInt(coordinates[5])
                            );
                        } catch (final NumberFormatException ignored) {
                            return null;
                        }
                    }
                }
            }

            return type == null
                    || display == null
                    || boundingBox == null
                    ? null
                    : new Elements(
                            type,
                            display,
                            interactions,
                            boundingBox
                    );
        }

        private static @Nullable DecorHitBox.Elements fromChild(final @NotNull Interaction interaction) {
            final String uuid =
                    interaction.getPersistentDataContainer()
                    .get(
                            DecorHitBox.HITBOX_CHILD_NAMESPACED_KEY,
                            PersistentDataType.STRING
                    );

            try {
                return StringUtils.isBlank(uuid)
                        || !(interaction.getWorld().getEntity(UUID.fromString(uuid)) instanceof final Interaction parent)
                        ? null
                        : fromParent(parent);
            } catch (final IllegalArgumentException ignored) {
                return null;
            }
        }
    }

    public enum Type {
        STRUCTURE_VOID(
                Material.STRUCTURE_VOID,
                Blocks.STRUCTURE_VOID
        ),
        BARRIER(
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

        public boolean isStructure() {
            return this == STRUCTURE_VOID;
        }

        public boolean isSolid() {
            return this == BARRIER;
        }

        public boolean isLight() {
            return this == LIGHT;
        }

        public boolean isNone() {
            return this == NONE;
        }
    }
}
