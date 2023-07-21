package com.minersstudios.msessentials.anomalies.tasks;

import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.anomalies.AnomalyBoundingBox;
import com.minersstudios.msessentials.anomalies.actions.SpawnParticlesAction;
import com.minersstudios.msessentials.Config;
import org.bukkit.entity.Player;

/**
 * Particle anomaly task.
 * This task is used to check if the player is in anomaly zone.
 * When player is in anomaly zone, the action will be performed
 * and particles will be spawned.
 * Otherwise, the action will be removed.
 * <br>
 * The task is registered in {@link Config#reload()}
 * with {@link Config#anomalyParticlesCheckRate}.
 *
 * @see SpawnParticlesAction
 * @see AnomalyBoundingBox
 */
public class ParticleTask implements Runnable {

    @Override
    public void run() {
        var entries = MSEssentials.getCache().playerAnomalyActionMap.entrySet();
        var anomalies = MSEssentials.getCache().anomalies.values();

        if (anomalies.isEmpty() || entries.isEmpty()) return;

        MSEssentials.getInstance().runTaskAsync(() ->
                entries
                .forEach(entry -> entry.getValue().keySet().stream()
                .filter(action -> action instanceof SpawnParticlesAction)
                .forEach(action -> {
                    Player player = entry.getKey();

                    for (var anomaly : anomalies) {
                        double radiusInside = anomaly.getBoundingBox().getRadiusInside(player);

                        if (radiusInside == -1.0d) continue;
                        if (anomaly.getAnomalyActionMap().get(radiusInside).contains(action)) {
                            action.doAction(player, null);
                        } else {
                            action.removeAction(player);
                        }
                    }
                }))
        );
    }
}