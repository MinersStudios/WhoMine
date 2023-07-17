package com.github.minersstudios.msessentials.listeners.entity;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.msessentials.world.WorldDark;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import com.github.minersstudios.mscore.listener.AbstractMSListener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class EntityDamageListener extends AbstractMSListener {

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(@NotNull EntityDamageEvent event) {
        if (
                event.getEntity() instanceof Player player
                && WorldDark.isInWorldDark(player)
        ) {
            event.setCancelled(true);
        }
    }
}
