package com.github.minersstudios.msessentials.anomalies.tasks;

import com.github.minersstudios.msessentials.MSEssentials;
import com.github.minersstudios.msessentials.anomalies.Anomaly;
import com.github.minersstudios.msessentials.anomalies.AnomalyAction;
import com.github.minersstudios.msessentials.anomalies.actions.SpawnParticlesAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;

public class ParticleTask implements Runnable {

    @Override
    public void run() {
        var entries = MSEssentials.getConfigCache().playerAnomalyActionMap.entrySet();
        if (entries.isEmpty()) return;
        Bukkit.getScheduler().runTaskAsynchronously(
                MSEssentials.getInstance(),
                () -> entries
                        .forEach(entry -> entry.getValue().keySet().stream()
                        .filter(action -> action instanceof SpawnParticlesAction)
                        .forEach(action -> {
                            Player player = entry.getKey();

                            for (var anomaly : MSEssentials.getConfigCache().anomalies.values()) {
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
