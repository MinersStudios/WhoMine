package com.minersstudios.msdecor.listeners.event.entity;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.msdecor.MSDecor;
import com.minersstudios.msdecor.customdecor.CustomDecor;
import com.minersstudios.msdecor.events.CustomDecorClickEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class EntityDamageByEntityListener extends AbstractMSListener<MSDecor> {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(final @NotNull EntityDamageByEntityEvent event) {
        if (
                !(event.getDamager() instanceof final Player player)
                || !(event.getEntity() instanceof final Interaction interaction)
        ) return;

        final GameMode gameMode = player.getGameMode();

        if (
                gameMode == GameMode.ADVENTURE
                || gameMode == GameMode.SPECTATOR
        ) return;

        if (
                (
                        player.isSneaking()
                        && gameMode == GameMode.SURVIVAL
                )
                || gameMode == GameMode.CREATIVE
        ) {
            CustomDecor.destroy(player, interaction);
        } else {
            CustomDecor.fromInteraction(interaction)
            .ifPresent(
                    customDecor -> player.getServer().getPluginManager().callEvent(
                            new CustomDecorClickEvent(
                                    customDecor,
                                    player,
                                    player.getHandRaised(),
                                    interaction.getLocation().toCenterLocation().toVector(),
                                    interaction,
                                    CustomDecorClickEvent.ClickType.LEFT_CLICK
                            )
                    )
            );
        }
    }
}
