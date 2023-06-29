package com.github.minersstudios.msutils.anomalies.tasks;

import com.github.minersstudios.msutils.MSUtils;
import com.github.minersstudios.msutils.anomalies.Anomaly;
import com.github.minersstudios.msutils.anomalies.AnomalyAction;
import com.github.minersstudios.msutils.anomalies.actions.SpawnParticlesAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;

public class ParticleTask implements Runnable {

    @Override
    public void run() {
        Set<Map.Entry<Player, Map<AnomalyAction, Long>>> entries = MSUtils.getConfigCache().playerAnomalyActionMap.entrySet();
        if (entries.isEmpty()) return;
        Bukkit.getScheduler().runTaskAsynchronously(
                MSUtils.getInstance(),
                () -> entries
                        .forEach(entry -> entry.getValue().keySet().stream()
                        .filter(action -> action instanceof SpawnParticlesAction)
                        .forEach(action -> {
                            Player player = entry.getKey();

                            for (Anomaly anomaly : MSUtils.getConfigCache().anomalies.values()) {
                                Double radiusInside = anomaly.getBoundingBox().getRadiusInside(player);

                                if (radiusInside == null) continue;
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
