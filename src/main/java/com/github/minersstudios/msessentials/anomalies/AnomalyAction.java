package com.github.minersstudios.msessentials.anomalies;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.minersstudios.msessentials.MSEssentials.getConfigCache;

@SuppressWarnings("unused")
public abstract class AnomalyAction {
    protected static final SecureRandom random = new SecureRandom();

    protected final long time;
    protected final int percentage;

    protected AnomalyAction(
            long time,
            int percentage
    ) {
        this.time = time;
        this.percentage = percentage;
    }

    public abstract void doAction(@NotNull Player player, @Nullable AnomalyIgnorableItems ignorableItems);

    public Map<AnomalyAction, Long> putAction(@NotNull Player player) {
        return this.putAction(player, System.currentTimeMillis());
    }

    public Map<AnomalyAction, Long> putAction(@NotNull Player player, long time) {
        Map<AnomalyAction, Long> actionMap = getConfigCache().playerAnomalyActionMap.getOrDefault(player, new ConcurrentHashMap<>());
        actionMap.put(this, time);
        return getConfigCache().playerAnomalyActionMap.put(player, actionMap);
    }

    public void removeAction(@NotNull Player player) {
        Map<AnomalyAction, Long> actionMap = getConfigCache().playerAnomalyActionMap.get(player);
        if (actionMap != null) {
            actionMap.remove(this);
            getConfigCache().playerAnomalyActionMap.put(player, actionMap);
        }
    }

    public boolean isDo() {
        return random.nextInt(100) < this.percentage;
    }

    public long getTime() {
        return this.time;
    }

    public int getPercentage() {
        return this.percentage;
    }
}
