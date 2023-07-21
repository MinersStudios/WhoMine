package com.minersstudios.msdecor.listeners.entity;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.utils.MSDecorUtils;
import com.minersstudios.msdecor.customdecor.CustomDecor;
import com.minersstudios.msdecor.utils.CustomDecorUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class EntityDamageByEntityListener extends AbstractMSListener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(@NotNull EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();

        if (MSDecorUtils.isCustomDecorEntity(entity)) {
            event.setCancelled(true);
        }

        if (
                !(event.getDamager() instanceof Player player)
                || player.getGameMode() == GameMode.ADVENTURE
                || ((!player.isSneaking()
                || player.getGameMode() != GameMode.SURVIVAL)
                && player.getGameMode() != GameMode.CREATIVE)
        ) return;

        CustomDecorUtils.getCustomDecorDataByEntity(entity)
        .ifPresent(
                customDecorData -> new CustomDecor(entity.getLocation().getBlock(), player, customDecorData).breakCustomDecor()
        );
    }
}
