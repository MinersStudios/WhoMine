package com.minersstudios.msessentials.listener.impl.event.entity;

import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.msessentials.MSEssentials;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class EntityDamageListener extends AbstractEventListener<MSEssentials> {

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(final @NotNull EntityDamageEvent event) {
        if (
                event.getEntity() instanceof Player player
                && this.getPlugin().getCache().getWorldDark().isInWorldDark(player)
        ) {
            event.setCancelled(true);
        }
    }
}
