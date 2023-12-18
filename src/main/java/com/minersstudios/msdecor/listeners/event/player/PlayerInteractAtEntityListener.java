package com.minersstudios.msdecor.listeners.event.player;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSEventListener;
import com.minersstudios.msdecor.MSDecor;
import com.minersstudios.msdecor.api.CustomDecor;
import com.minersstudios.msdecor.event.CustomDecorClickEvent;
import org.bukkit.entity.Interaction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.jetbrains.annotations.NotNull;

@MSEventListener
public final class PlayerInteractAtEntityListener extends AbstractMSListener<MSDecor> {

    @EventHandler
    public void onPlayerInteractAtEntity(final @NotNull PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof final Interaction interaction)) {
            return;
        }

        CustomDecor.fromInteraction(interaction)
        .ifPresent(
                customDecor -> {
                    final CustomDecorClickEvent clickEvent =
                            new CustomDecorClickEvent(
                                    customDecor,
                                    event.getPlayer(),
                                    event.getHand(),
                                    event.getClickedPosition(),
                                    interaction,
                                    CustomDecorClickEvent.ClickType.RIGHT_CLICK
                            );

                    interaction.getServer().getPluginManager().callEvent(clickEvent);

                    if (!clickEvent.isCancelled()) {
                        customDecor.getData().doClickAction(clickEvent);
                    }
                }
        );
    }
}
