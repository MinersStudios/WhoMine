package com.minersstudios.msessentials.anomalies.tasks;

import com.minersstudios.msessentials.Config;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.anomalies.AnomalyBoundingBox;
import com.minersstudios.msessentials.anomalies.actions.SpawnParticlesAction;

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
        var map = MSEssentials.getCache().playerAnomalyActionMap;
        var anomalies = MSEssentials.getCache().anomalies.values();

        if (anomalies.isEmpty() || map.isEmpty()) return;

        MSEssentials.getInstance().runTaskAsync(() -> map.forEach(
                (player, actionMap) -> actionMap.keySet()
                .forEach(action -> {
                    if (!(action instanceof SpawnParticlesAction)) return;

                    for (var anomaly : anomalies) {
                        double radiusInside = anomaly.getBoundingBox().getRadiusInside(player);

                        if (radiusInside == -1.0d) continue;
                        if (anomaly.getAnomalyActionMap().get(radiusInside).contains(action)) {
                            action.doAction(player, null);
                        } else {
                            action.removeAction(player);
                        }
                    }
                })
        ));
    }
}
