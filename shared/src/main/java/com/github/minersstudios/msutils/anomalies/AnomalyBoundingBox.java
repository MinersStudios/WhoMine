package com.github.minersstudios.msutils.anomalies;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class AnomalyBoundingBox extends BoundingBox {
    private final @NotNull World world;
    private final @NotNull BoundingBox boundingBox;
    private final @NotNull List<Double> radii = new ArrayList<>();
    private final @NotNull Map<Double, BoundingBox> radiusBoundingBoxes = new HashMap<>();

    public AnomalyBoundingBox(
            @NotNull World world,
            @NotNull BoundingBox boundingBox,
            @NotNull List<Double> radii
    ) {
        this.world = world;
        this.boundingBox = boundingBox;
        this.radii.addAll(radii);

        for (double radius : radii) {
            this.radiusBoundingBoxes.put(radius, boundingBox.clone().expand(radius));
        }
    }

    /**
     * Gets the radius if any bounding box contains the player position
     *
     * @param player the player
     * @return Null if the bounding box does not contain the player position
     */
    public @Nullable Double getRadiusInside(@NotNull Player player) {
        if (player.getWorld() != this.world) return null;
        for (Map.Entry<Double, BoundingBox> radiusBoundingBox : this.getRadiusBoundingBoxes().entrySet()) {
            if (radiusBoundingBox.getValue().contains(player.getLocation().toVector())) {
                return radiusBoundingBox.getKey();
            }
        }
        return null;
    }

    public @NotNull World getWorld() {
        return this.world;
    }

    public @NotNull BoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    public @NotNull List<Double> getRadii() {
        return this.radii;
    }

    public @NotNull Map<Double, BoundingBox> getRadiusBoundingBoxes() {
        return this.radiusBoundingBoxes;
    }
}
