package com.minersstudios.msdecor.listeners.event.entity;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.msdecor.customdecor.CustomDecorData;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class EntityDamageByEntityListener extends AbstractMSListener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(final @NotNull EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();

        if (
                !(event.getDamager() instanceof final Player player)
                || player.getGameMode() == GameMode.ADVENTURE
                || ((!player.isSneaking()
                || player.getGameMode() != GameMode.SURVIVAL)
                && player.getGameMode() != GameMode.CREATIVE)
        ) return;

        if (entity instanceof final Interaction interaction) {
            CustomDecorData.destroy(player, interaction, entity.getLocation());
        }
    }
}
