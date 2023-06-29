package com.github.minersstudios.msessentials.anomalies.actions;

import com.github.minersstudios.msessentials.MSEssentials;
import com.github.minersstudios.msessentials.anomalies.AnomalyAction;
import com.github.minersstudios.msessentials.anomalies.AnomalyIgnorableItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class AddPotionAction extends AnomalyAction {
    private final @NotNull List<PotionEffect> potionEffects;

    public AddPotionAction(
            long time,
            int percentage,
            @NotNull List<PotionEffect> potionEffects
    ) {
        super(time, percentage);
        this.potionEffects = potionEffects;
    }

    @Override
    public void doAction(@NotNull Player player, @Nullable AnomalyIgnorableItems ignorableItems) {
        Map<AnomalyAction, Long> actionMap = MSEssentials.getConfigCache().playerAnomalyActionMap.get(player);
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
                    Bukkit.getScheduler().runTask(
                            MSEssentials.getInstance(),
                            () -> player.addPotionEffects(this.potionEffects)
                    );
                }
            }
        }
    }

    public @NotNull List<PotionEffect> getPotionEffects() {
        return this.potionEffects;
    }
}
