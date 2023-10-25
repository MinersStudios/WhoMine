package com.minersstudios.msdecor.listeners.event.player;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.msdecor.MSDecor;
import com.minersstudios.msdecor.api.CustomDecor;
import com.minersstudios.msdecor.event.CustomDecorClickEvent;
import org.bukkit.entity.Interaction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public final class PlayerInteractAtEntityListener extends AbstractMSListener<MSDecor> {

    @EventHandler
    public void onPlayerInteractAtEntity(final @NotNull PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof final Interaction interaction)) return;

        CustomDecor.fromInteraction(interaction)
        .ifPresent(
                customDecor -> interaction.getServer().getPluginManager().callEvent(
                        new CustomDecorClickEvent(
                                customDecor,
                                event.getPlayer(),
                                event.getHand(),
                                event.getClickedPosition(),
                                interaction,
                                CustomDecorClickEvent.ClickType.RIGHT_CLICK
                        )
                )
        );
    }
}
