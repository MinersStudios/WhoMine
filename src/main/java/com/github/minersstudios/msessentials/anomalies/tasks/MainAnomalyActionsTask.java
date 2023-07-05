package com.github.minersstudios.msessentials.anomalies.tasks;

import com.github.minersstudios.msessentials.Cache;
import com.github.minersstudios.msessentials.MSEssentials;
import com.github.minersstudios.msessentials.anomalies.AnomalyAction;
import com.github.minersstudios.msessentials.anomalies.AnomalyBoundingBox;
import com.github.minersstudios.msessentials.anomalies.actions.SpawnParticlesAction;
import com.github.minersstudios.msessentials.config.Config;
import org.bukkit.Bukkit;

/**
 * Main anomaly action task.
 * This task is used to check if the player is in anomaly zone.
 * When player is in anomaly zone, the action will be performed.
 * Otherwise, the action will be removed.
 * <br>
 * The task is registered in {@link Config#reload()}
 * with {@link Config#anomalyCheckRate}.
 *
 * @see AnomalyAction
 * @see AnomalyBoundingBox
 */
public class MainAnomalyActionsTask implements Runnable {

    @Override
    public void run() {
        var onlinePlayers = Bukkit.getOnlinePlayers();

        if (onlinePlayers.isEmpty()) return;

        Cache cache = MSEssentials.getCache();
        var playerActionMap = cache.playerAnomalyActionMap;

        MSEssentials.getInstance().runTaskAsync(() ->
                onlinePlayers
                .forEach(player -> {
                    for (var anomaly : cache.anomalies.values()) {
                        double radiusInside = anomaly.getBoundingBox().getRadiusInside(player);
                        boolean isIgnorable = anomaly.getIgnorablePlayers().contains(player);

                        if (radiusInside == -1.0d) continue;

                        var actionMap = playerActionMap.get(player);

                        for (var action : anomaly.getAnomalyActionMap().get(radiusInside)) {
                            if (actionMap == null || !actionMap.containsKey(action)) {
                                if (isIgnorable && action instanceof SpawnParticlesAction) {
                                    action.putAction(player);
                                    return;
                                } else if (!isIgnorable) {
                                    actionMap = action.putAction(player);
                                }
                            }
                        }

                        if (actionMap == null) return;

                        for (var action : actionMap.keySet()) {
                            if (anomaly.isAnomalyActionRadius(action, radiusInside)) {
                                if (!(action instanceof SpawnParticlesAction)) {
                                    action.doAction(player, anomaly.getIgnorableItems());
                                }
                            } else {
                                action.removeAction(player);
                            }
                        }
                        return;
                    }
                    playerActionMap.remove(player);
                })
        );
    }
}
