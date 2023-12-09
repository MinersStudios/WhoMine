package com.minersstudios.msessentials.anomalies.tasks;

import com.minersstudios.msessentials.Cache;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.anomalies.AnomalyAction;
import com.minersstudios.msessentials.anomalies.AnomalyBoundingBox;
import com.minersstudios.msessentials.anomalies.actions.SpawnParticlesAction;
import com.minersstudios.msessentials.Config;
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
public final class MainAnomalyActionsTask implements Runnable {

    @Override
    public void run() {
        final var onlinePlayers = Bukkit.getOnlinePlayers();

        if (onlinePlayers.isEmpty()) return;

        final Cache cache = MSEssentials.cache();
        final var playerActionMap = cache.getPlayerAnomalyActionMap();

        MSEssentials.singleton().runTaskAsync(() ->
                onlinePlayers
                .forEach(player -> {
                    for (final var anomaly : cache.getAnomalies().values()) {
                        final double radiusInside = anomaly.getBoundingBox().getRadiusInside(player);
                        final boolean isIgnorable = anomaly.getIgnorablePlayers().contains(player);

                        if (radiusInside == -1.0d) continue;

                        var actionMap = playerActionMap.get(player);

                        for (final var action : anomaly.getAnomalyActionMap().get(radiusInside)) {
                            if (
                                    actionMap == null
                                    || !actionMap.containsKey(action)
                            ) {
                                if (
                                        isIgnorable
                                        && action instanceof SpawnParticlesAction
                                ) {
                                    action.putAction(player);
                                    return;
                                } else if (!isIgnorable) {
                                    actionMap = action.putAction(player);
                                }
                            }
                        }

                        if (actionMap == null) return;

                        for (final var action : actionMap.keySet()) {
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
