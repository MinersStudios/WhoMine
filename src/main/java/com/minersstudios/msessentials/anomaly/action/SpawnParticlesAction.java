package com.minersstudios.msessentials.anomaly.action;

import com.destroystokyo.paper.ParticleBuilder;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.anomaly.AnomalyAction;
import com.minersstudios.msessentials.anomaly.AnomalyIgnorableItems;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Anomaly particle action class.
 * Used to spawn particles when a player is in anomaly zone.
 * Particles are spawned only for receiving players.
 */
public class SpawnParticlesAction extends AnomalyAction {
    private final ParticleBuilder[] particleBuilders;

    /**
     * Spawns particles when a player is in anomaly zone.
     * Particles are spawned only for receiving players.
     *
     * @param plugin     MSEssentials plugin
     * @param time       Time in ticks to perform action (1 second = 20 ticks)
     * @param percentage Percentage chance of completing action
     * @param first      First particle builder to spawn when player is in anomaly zone
     * @param rest       Rest of particle builders to spawn when player is in anomaly zone
     */
    public SpawnParticlesAction(
            final @NotNull MSEssentials plugin,
            final long time,
            final int percentage,
            final @NotNull ParticleBuilder first,
            final ParticleBuilder @NotNull ... rest
    ) {
        super(plugin, time, percentage);

        final int restLength = rest.length;
        this.particleBuilders = new ParticleBuilder[restLength + 1];

        System.arraycopy(rest, 0, this.particleBuilders, 1, restLength);
        this.particleBuilders[0] = first;
    }

    /**
     * Spawns particles when a player is in anomaly zone.
     * Particles are spawned only for receiving players.
     *
     * @param plugin            MSEssentials plugin
     * @param time              Time in ticks to perform action (1 second = 20 ticks)
     * @param percentage        Percentage chance of completing action
     * @param particleBuilders  Array of particle builders to spawn when player is in anomaly zone
     */
    public SpawnParticlesAction(
            final @NotNull MSEssentials plugin,
            final long time,
            final int percentage,
            final ParticleBuilder @NotNull [] particleBuilders
    ) {
        super(plugin, time, percentage);

        this.particleBuilders = particleBuilders;
    }

    /**
     * @return Array of particle builders to spawn when player is in anomaly zone
     */
    public ParticleBuilder @NotNull [] getParticles() {
        return this.particleBuilders.clone();
    }

    /**
     * Spawn particles for receiver when it is in anomaly zone
     *
     * @param player         The receiving player
     * @param ignorableItems (ignored)
     */
    @Override
    public void doAction(
            final @NotNull Player player,
            final @Nullable AnomalyIgnorableItems ignorableItems
    ) {
        final Location location = player.getLocation();

        for (final var particleBuilder : this.particleBuilders) {
            particleBuilder
            .receivers(player)
            .location(location)
            .spawn();
        }
    }
}
