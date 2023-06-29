package com.github.minersstudios.msessentials.listeners.entity;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.msessentials.MSEssentials;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class EntityDamageListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(@NotNull EntityDamageEvent event) {
        if (
                event.getEntity() instanceof Player player
                && player.getWorld().equals(MSEssentials.getWorldDark())
        ) {
            event.setCancelled(true);
        }
    }
}
