package com.github.minersstudios.msessentials.anomalies.actions;

import com.destroystokyo.paper.ParticleBuilder;
import com.github.minersstudios.msessentials.anomalies.AnomalyAction;
import com.github.minersstudios.msessentials.anomalies.AnomalyIgnorableItems;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class SpawnParticlesAction extends AnomalyAction {
    private final @NotNull List<ParticleBuilder> particleBuilderList;

    public SpawnParticlesAction(
            long time,
            int percentage,
            @NotNull List<ParticleBuilder> particleBuilderList
    ) {
        super(time, percentage);
        this.particleBuilderList = particleBuilderList;
    }

    @Override
    public void doAction(@NotNull Player player, @Nullable AnomalyIgnorableItems ignorableItems) {
        for (var particleBuilder : this.particleBuilderList) {
            particleBuilder.location(player.getLocation()).spawn();
        }
    }

    public @NotNull List<ParticleBuilder> getParticles() {
        return this.particleBuilderList;
    }
}
