package com.minersstudios.msessentials.anomalies;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bounding box with a list of radii and a map of bounding boxes for each radius.
 * This class is used to store the bounding boxes of anomalies.
 * Use {@link #getRadiusInside(Player)} to get the radius of the anomaly the player is inside.
 */
public class AnomalyBoundingBox extends BoundingBox {
    private final @NotNull World world;
    private final @NotNull List<Double> radii = new ArrayList<>();
    private final @NotNull Map<Double, BoundingBox> radiusBoundingBoxes = new HashMap<>();

    /**
     * Creates a new anomaly bounding box,
     * with a list of radii and a map of bounding boxes for each radius.
     * Bounding boxes are created by expanding the center bounding box by the radius.
     *
     * @param world       The world the anomaly is in
     * @param boundingBox The center bounding box of the anomaly
     * @param radii       The radii of the anomaly
     */
    public AnomalyBoundingBox(
            @NotNull World world,
            @NotNull BoundingBox boundingBox,
            @NotNull List<Double> radii
    ) {
        this.world = world;
        this.radii.addAll(radii);

        for (var radius : radii) {
            this.radiusBoundingBoxes.put(radius, boundingBox.clone().expand(radius));
        }
    }

    /**
     * Gets the radius if any bounding box contains the player position
     *
     * @param player The player to check
     * @return -1 if the bounding box does not contain the player position
     */
    public double getRadiusInside(@NotNull Player player) {
        if (player.getWorld() != this.world) return -1.0d;
        for (var radiusBoundingBox : this.getRadiusBoundingBoxes().entrySet()) {
            if (radiusBoundingBox.getValue().contains(player.getLocation().toVector())) {
                return radiusBoundingBox.getKey();
            }
        }
        return -1.0d;
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
    public @NotNull @UnmodifiableView List<Double> getRadii() {
        return List.copyOf(this.radii);
    }

    /**
     * @return Unmodifiable map of bounding boxes for each radius
     */
    public @NotNull @UnmodifiableView Map<Double, BoundingBox> getRadiusBoundingBoxes() {
        return Map.copyOf(this.radiusBoundingBoxes);
    }
}
