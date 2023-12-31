package com.minersstudios.msblock.api.params.settings;

import com.google.common.base.Joiner;
import com.minersstudios.msblock.api.params.PlacingType;
import org.bukkit.Material;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import javax.annotation.concurrent.Immutable;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Class representing the placing rules for a custom block. This class holds
 * information about the placing type and the set of materials that can be
 * placed on the custom block (like a Grass).
 */
@Immutable
public final class Placing {
    private final PlacingType type;
    private final Set<Material> placeableMaterials;

    private Placing(
            final @NotNull PlacingType type,
            final @NotNull Set<Material> placeableMaterials
    ) {
        this.type = type;
        this.placeableMaterials = placeableMaterials;
    }

    /**
     * Factory method for creating a new Placing object with the specified
     * placing type and placeable materials
     *
     * @param type               The placing type for the custom block
     * @param placeableMaterials The set of materials that can be placed on the
     *                           custom block
     * @return A new Placing object with the specified placing type and
     *         placeable materials
     */
    @Contract("_, _ -> new")
    public static @NotNull Placing create(
            final @NotNull PlacingType type,
            final @NotNull Collection<Material> placeableMaterials
    ) {
        final var set = EnumSet.noneOf(Material.class);

        set.addAll(placeableMaterials);

        return new Placing(type, set);
    }

    /**
     * Factory method for creating a new Placing object with the specified
     * placing type and placeable materials
     *
     * @param type               The placing type for the custom block
     * @param placeableMaterials The array of materials that can be placed on
     *                           the custom block
     * @return A new Placing object with the specified placing type and
     *         placeable materials
     */
    @Contract("_, _ -> new")
    public static @NotNull Placing create(
            final @NotNull PlacingType type,
            final Material @NotNull ... placeableMaterials
    ) {
        return new Placing(
                type,
                placeableMaterials.length == 0
                        ? EnumSet.noneOf(Material.class)
                        : EnumSet.of(placeableMaterials[0], placeableMaterials)
        );
    }

    /**
     * @return The placing type for the custom block
     */
    public @NotNull PlacingType getType() {
        return this.type;
    }

    /**
     * @return The unmodifiable set of materials that can be placed on the
     *         custom block or an empty set if no materials are specified
     */
    public @NotNull @Unmodifiable Set<Material> placeableMaterials() {
        return Collections.unmodifiableSet(this.placeableMaterials);
    }

    /**
     * @param material The material to check
     * @return True if the specified material can be placed on the custom block
     */
    public boolean isPlaceable(final @Nullable Material material) {
        return material != null
                && this.placeableMaterials.contains(material);
    }

    /**
     * @return A string representation of the Placing rules
     */
    @Override
    public @NotNull String toString() {
        return "Placing{" +
                "type=" + this.type +
                ", placeableMaterials=[" + Joiner.on(", ").join(this.placeableMaterials) + ']' +
                '}';
    }
}
