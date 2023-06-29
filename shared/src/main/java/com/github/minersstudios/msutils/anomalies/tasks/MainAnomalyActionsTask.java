package com.github.minersstudios.msutils.anomalies.tasks;

import com.github.minersstudios.msutils.MSUtils;
import com.github.minersstudios.msutils.anomalies.Anomaly;
import com.github.minersstudios.msutils.anomalies.AnomalyAction;
import com.github.minersstudios.msutils.anomalies.actions.SpawnParticlesAction;
import com.github.minersstudios.msutils.config.ConfigCache;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;

public class MainAnomalyActionsTask implements Runnable {

    @Override
    public void run() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();

        if (onlinePlayers.isEmpty()) return;

        ConfigCache configCache = MSUtils.getConfigCache();
        Map<Player, Map<AnomalyAction, Long>> playerActionMap = configCache.playerAnomalyActionMap;

        Bukkit.getScheduler().runTaskAsynchronously(
                MSUtils.getInstance(),
                () -> onlinePlayers
                        .forEach(player -> {
                            for (Anomaly anomaly : configCache.anomalies.values()) {
                                Double radiusInside = anomaly.getBoundingBox().getRadiusInside(player);
                                boolean isIgnorable = anomaly.getIgnorablePlayers().contains(player);

                                if (radiusInside == null) continue;

                                Map<AnomalyAction, Long> actionMap = playerActionMap.get(player);

                                for (AnomalyAction action : anomaly.getAnomalyActionMap().get(radiusInside)) {
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

                                for (AnomalyAction action : actionMap.keySet()) {
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
