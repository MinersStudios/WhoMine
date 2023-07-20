package com.minersstudios.msessentials.anomalies.actions;

import com.destroystokyo.paper.ParticleBuilder;
import com.minersstudios.msessentials.anomalies.AnomalyAction;
import com.minersstudios.msessentials.anomalies.AnomalyIgnorableItems;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/**
 * Anomaly particle action class.
 * Used to spawn particles when a player is in anomaly zone.
 * Particles are spawned only for receiving players.
 */
public class SpawnParticlesAction extends AnomalyAction {
    private final @NotNull List<ParticleBuilder> particleBuilderList;

    /**
     * Spawns particles when a player is in anomaly zone.
     * Particles are spawned only for receiving players.
     *
     * @param time                Time in ticks to perform action (1 second = 20 ticks)
     * @param percentage          Percentage chance of completing action
     * @param particleBuilderList Particle builders to spawn when player is in anomaly zone
     */
    public SpawnParticlesAction(
            long time,
            int percentage,
            @NotNull List<ParticleBuilder> particleBuilderList
    ) {
        super(time, percentage);
        this.particleBuilderList = particleBuilderList;
    }

    /**
     * Spawn particles for receiver when it is in anomaly zone
     *
     * @param player         The receiving player
     * @param ignorableItems (ignored)
     */
    @Override
    public void doAction(
            @NotNull Player player,
            @Nullable AnomalyIgnorableItems ignorableItems
    ) {
        for (var particleBuilder : this.particleBuilderList) {
            particleBuilder.receivers(player).location(player.getLocation()).spawn();
        }
    }

    /**
     * @return Unmodifiable list of particle builders to spawn when player is in anomaly zone
     */
    public @NotNull @Unmodifiable List<ParticleBuilder> getParticles() {
        return List.copyOf(this.particleBuilderList);
    }
}
