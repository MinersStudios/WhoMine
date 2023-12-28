package com.minersstudios.msessentials.anomaly;

import com.minersstudios.mscore.location.MSBoundingBox;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import javax.annotation.concurrent.Immutable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bounding box with a list of radii and a map of bounding boxes for each radius.
 * This class is used to store the bounding boxes of anomalies. Use
 * {@link #getRadiusInside(Player)} to get the radius of the anomaly the player
 * is inside.
 */
@Immutable
public final class AnomalyBoundingBox extends BoundingBox {
    private final World world;
    private final List<Double> radii;
    private final Map<Double, MSBoundingBox> radiusBoundingBoxes;

    /**
     * Creates a new anomaly bounding box, with a list of radii and a map of
     * bounding boxes for each radius. Bounding boxes are created by expanding
     * the center bounding box by the radius.
     *
     * @param world       The world the anomaly is in
     * @param boundingBox The center-bounding box of the anomaly
     * @param radii       The radii of the anomaly
     */
    public AnomalyBoundingBox(
            final @NotNull World world,
            final @NotNull MSBoundingBox boundingBox,
            final @NotNull List<Double> radii
    ) {
        this.world = world;
        this.radii = radii;
        this.radiusBoundingBoxes = new HashMap<>();

        for (final var radius : radii) {
            this.radiusBoundingBoxes.put(radius, boundingBox.inflate(radius));
        }
    }

    /**
     * @return The world the anomaly is in
     */
    public @NotNull World getWorld() {
        return this.world;
    }

    /**
     * @return Unmodifiable list of anomaly radii
     */
    public @NotNull @Unmodifiable List<Double> getRadii() {
        return Collections.unmodifiableList(this.radii);
    }

    /**
     * @return Unmodifiable map of bounding boxes for each radius
     */
    public @NotNull @Unmodifiable Map<Double, MSBoundingBox> getRadiusBoundingBoxes() {
        return Collections.unmodifiableMap(this.radiusBoundingBoxes);
    }

    /**
     * Gets the radius if any bounding box contains the player position
     *
     * @param player The player to check
     * @return -1 if the bounding box does not contain the player position
     */
    public double getRadiusInside(final @NotNull Player player) {
        if (player.getWorld() == this.world) {
            final Vector playerPosition = player.getLocation().toVector();

            for (final var entry : this.radiusBoundingBoxes.entrySet()) {
                if (entry.getValue().contains(playerPosition)) {
                    return entry.getKey();
                }
            }
        }

        return -1.0d;
    }
}
