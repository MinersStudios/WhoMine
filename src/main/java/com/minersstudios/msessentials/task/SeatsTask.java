package com.minersstudios.msessentials.task;

import com.minersstudios.msessentials.MSEssentials;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class SeatsTask implements Runnable {
    private final Map<Player, ArmorStand> seats;

    public SeatsTask(final @NotNull MSEssentials plugin) {
        this.seats = plugin.getCache().getSeats();
    }

    @Override
    public void run() {
        this.seats.entrySet().parallelStream()
        .forEach(
                entry -> {
                    final Player player = entry.getKey();
                    final ArmorStand armorStand = entry.getValue();

                    armorStand.setRotation(
                            player.getLocation().getYaw(),
                            0.0f
                    );
                }
        );
    }
}
