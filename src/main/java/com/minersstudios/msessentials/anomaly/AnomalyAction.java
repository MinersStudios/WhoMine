package com.minersstudios.msessentials.anomaly;

import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.anomaly.action.AddPotionAction;
import com.minersstudios.msessentials.anomaly.action.SpawnParticlesAction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Anomaly action class. Used to do something when a player is in anomaly zone
 * and the time is up and the percentage is reached.
 *
 * @see AddPotionAction
 * @see SpawnParticlesAction
 */
public abstract class AnomalyAction {
    protected final MSEssentials plugin;
    protected final Map<Player, Map<AnomalyAction, Long>> actionMap;
    protected final long time;
    protected final int percentage;

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * @param plugin     MSEssentials plugin
     * @param time       Time in ticks to perform action
     *                   (1 second = 20 ticks)
     * @param percentage Percentage chance of completing action
     */
    protected AnomalyAction(
            final @NotNull MSEssentials plugin,
            final long time,
            final int percentage
    ) {
        this.plugin = plugin;
        this.actionMap = plugin.getCache().getPlayerAnomalyActionMap();
        this.time = time;
        this.percentage = percentage;
    }

    /**
     * @return MSEssentials plugin
     */
    public final @NotNull MSEssentials getPlugin() {
        return this.plugin;
    }

    /**
     * @return Map of actions to player's last action time
     */
    public final @NotNull @UnmodifiableView Map<Player, Map<AnomalyAction, Long>> getActionMap() {
        return Collections.unmodifiableMap(this.actionMap);
    }

    /**
     * @return Time in ticks to perform action
     *         (1 second = 20 ticks)
     */
    public final long getTime() {
        return this.time;
    }

    /**
     * @return Percentage chance of completing the action
     */
    public final int getPercentage() {
        return this.percentage;
    }

    /**
     * @return True if the percentage is reached
     */
    public final boolean isPercentageReached() {
        return RANDOM.nextInt(100) < this.percentage;
    }

    /**
     * Puts this action to player's action map with current time
     *
     * @param player The player to be influenced
     * @return The previous action map associated with player, or null if there
     *         was no mapping for player
     */
    public final @Nullable Map<AnomalyAction, Long> putAction(final @NotNull Player player) {
        final var action = this.actionMap.getOrDefault(player, new ConcurrentHashMap<>());

        action.put(this, System.currentTimeMillis());
        return this.actionMap.put(player, action);
    }

    /**
     * Removes this action from the player's action map
     *
     * @param player A player who has been influenced and from which the action
     *               will be removed
     */
    public final void removeAction(final @NotNull Player player) {
        final var action = this.actionMap.get(player);

        if (action != null) {
            action.remove(this);
            this.actionMap.put(player, action);
        }
    }

    /**
     * Do action if the time is up and the percentage is reached
     *
     * @param player         The player to be influenced
     * @param ignorableItems Ignorable items that will be damaged if the player
     *                       has them and the action will be performed
     */
    public abstract void doAction(
            final @NotNull Player player,
            final @Nullable AnomalyIgnorableItems ignorableItems
    );
}
