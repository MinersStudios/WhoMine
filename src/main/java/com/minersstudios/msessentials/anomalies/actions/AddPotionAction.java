package com.minersstudios.msessentials.anomalies.actions;

import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.anomalies.AnomalyAction;
import com.minersstudios.msessentials.anomalies.AnomalyIgnorableItems;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Anomaly potion action class.
 * Used to add potion effects to player when a player is in anomaly zone
 * and the time is up and the percentage is reached,
 * and if the player has no ignorable items.
 */
public class AddPotionAction extends AnomalyAction {
    private final PotionEffect[] potionEffects;

    /**
     * @param time       Time in ticks to perform action (1 second = 20 ticks)
     * @param percentage Percentage chance of completing action
     * @param first      First potion effect to add to player when action is performed
     * @param rest       Rest of potion effects to add to player when action is performed
     */
    public AddPotionAction(
            final long time,
            final int percentage,
            final @NotNull PotionEffect first,
            final PotionEffect @NotNull ... rest
    ) {
        super(time, percentage);

        final int restLength = rest.length;
        this.potionEffects = new PotionEffect[restLength + 1];

        System.arraycopy(rest, 0, this.potionEffects, 1, restLength);
        this.potionEffects[0] = first;
    }

    /**
     * @param time            Time in ticks to perform action (1 second = 20 ticks)
     * @param percentage      Percentage chance of completing action
     * @param potionEffects   Array of potion effects to add to player when action is performed
     */
    public AddPotionAction(
            final long time,
            final int percentage,
            final PotionEffect @NotNull [] potionEffects
    ) {
        super(time, percentage);

        this.potionEffects = potionEffects;
    }

    /**
     * @return Array of potion effects to add to player when action is performed
     */
    public PotionEffect @NotNull [] getPotionEffects() {
        return this.potionEffects.clone();
    }

    /**
     * Adds potion effects to player if the time is up and the percentage is reached.
     * If the player has ignorable items, they will be damaged
     * instead of adding potion effects.
     *
     * @param player         The player to be influenced
     * @param ignorableItems Ignorable items that will be damaged
     *                       if player has them and the action will be performed
     * @see AnomalyAction#isPercentageReached()
     */
    @Override
    public void doAction(
            final @NotNull Player player,
            final @Nullable AnomalyIgnorableItems ignorableItems
    ) {
        final var actionMap = MSEssentials.cache().getPlayerAnomalyActionMap().get(player);

        if (
                actionMap.containsKey(this)
                && System.currentTimeMillis() - actionMap.get(this) >= (this.getTime() * 50)
        ) {
            this.removeAction(player);

            if (this.isPercentageReached()) {
                if (
                        ignorableItems != null
                        && ignorableItems.hasIgnorableItems(player.getInventory())
                ) {
                    ignorableItems.damageIgnorableItems(player.getInventory());
                } else {
                    MSEssentials.singleton().runTask(() -> {
                        for (final var potionEffect : this.potionEffects) {
                            player.addPotionEffect(potionEffect);
                        }
                    });
                }
            }
        }
    }
}
