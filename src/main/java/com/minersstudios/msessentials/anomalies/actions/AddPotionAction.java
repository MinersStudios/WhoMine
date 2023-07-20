package com.minersstudios.msessentials.anomalies.actions;

import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.anomalies.AnomalyAction;
import com.minersstudios.msessentials.anomalies.AnomalyIgnorableItems;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/**
 * Anomaly potion action class.
 * Used to add potion effects to player when a player is in anomaly zone
 * and the time is up and the percentage is reached,
 * and if the player has no ignorable items.
 */
public class AddPotionAction extends AnomalyAction {
    private final @NotNull List<PotionEffect> potionEffects;

    /**
     * @param time          Time in ticks to perform action (1 second = 20 ticks)
     * @param percentage    Percentage chance of completing action
     * @param potionEffects Potion effects to add to player when action is performed
     */
    public AddPotionAction(
            long time,
            int percentage,
            @NotNull List<PotionEffect> potionEffects
    ) {
        super(time, percentage);
        this.potionEffects = potionEffects;
    }

    /**
     * Adds potion effects to player if the time is up and the percentage is reached.
     * If the player has ignorable items, they will be damaged
     * instead of adding potion effects.
     *
     * @param player         The player to be influenced
     * @param ignorableItems Ignorable items that will be damaged
     *                       if player has them and the action will be performed
     * @see AnomalyAction#isDo()
     */
    @Override
    public void doAction(
            @NotNull Player player,
            @Nullable AnomalyIgnorableItems ignorableItems
    ) {
        var actionMap = MSEssentials.getCache().playerAnomalyActionMap.get(player);

        if (
                actionMap.containsKey(this)
                && System.currentTimeMillis() - actionMap.get(this) >= (this.time * 50)
        ) {
            this.removeAction(player);

            if (this.isDo()) {
                if (
                        ignorableItems != null
                        && ignorableItems.hasIgnorableItems(player.getInventory())
                ) {
                    ignorableItems.damageIgnorableItems(player.getInventory());
                } else {
                    MSEssentials.getInstance().runTask(() -> player.addPotionEffects(this.potionEffects));
                }
            }
        }
    }

    /**
     * @return Unmodifiable list of potion effects to add to player when action is performed
     */
    public @NotNull @Unmodifiable List<PotionEffect> getPotionEffects() {
        return List.copyOf(this.potionEffects);
    }
}
