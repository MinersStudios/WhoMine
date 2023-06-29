package com.github.minersstudios.msutils.anomalies.actions;

import com.github.minersstudios.msutils.MSUtils;
import com.github.minersstudios.msutils.anomalies.AnomalyAction;
import com.github.minersstudios.msutils.anomalies.AnomalyIgnorableItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

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
        Map<AnomalyAction, Long> actionMap = MSUtils.getConfigCache().playerAnomalyActionMap.get(player);
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
                            MSUtils.getInstance(),
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
