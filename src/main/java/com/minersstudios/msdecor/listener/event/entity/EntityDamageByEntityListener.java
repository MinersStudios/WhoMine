package com.minersstudios.msdecor.listener.event.entity;

import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.msdecor.MSDecor;
import com.minersstudios.msdecor.api.CustomDecor;
import com.minersstudios.msdecor.event.CustomDecorClickEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class EntityDamageByEntityListener extends AbstractEventListener<MSDecor> {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(final @NotNull EntityDamageByEntityEvent event) {
        if (
                !(event.getDamager() instanceof final Player player)
                || !(event.getEntity() instanceof final Interaction interaction)
        ) {
            return;
        }

        final GameMode gameMode = player.getGameMode();

        if (
                gameMode == GameMode.ADVENTURE
                || gameMode == GameMode.SPECTATOR
        ) {
            return;
        }

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
                    customDecor -> {
                        final CustomDecorClickEvent clickEvent =
                                new CustomDecorClickEvent(
                                        customDecor,
                                        player,
                                        player.getHandRaised(),
                                        interaction.getLocation().toCenterLocation().toVector(),
                                        interaction,
                                        CustomDecorClickEvent.ClickType.LEFT_CLICK
                                );

                        player.getServer().getPluginManager().callEvent(clickEvent);

                        if (!clickEvent.isCancelled()) {
                            customDecor.getData().doClickAction(clickEvent);
                        }
                    }
            );
        }
    }
}
