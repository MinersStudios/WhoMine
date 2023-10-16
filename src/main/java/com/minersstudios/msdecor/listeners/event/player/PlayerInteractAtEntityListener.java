package com.minersstudios.msdecor.listeners.event.player;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.msdecor.customdecor.CustomDecorData;
import com.minersstudios.msdecor.events.CustomDecorRightClickEvent;
import org.bukkit.entity.Interaction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerInteractAtEntityListener extends AbstractMSListener {

    @EventHandler
    public void onPlayerInteractAtEntity(final @NotNull PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof final Interaction interaction)) return;

        CustomDecorData.fromInteraction(interaction)
        .ifPresent(
                data -> {
                    final CustomDecorRightClickEvent rightClickEvent = new CustomDecorRightClickEvent(
                            data,
                            event.getPlayer(),
                            event.getHand(),
                            event.getClickedPosition()
                    );

                    interaction.getServer().getPluginManager().callEvent(rightClickEvent);

                    if (rightClickEvent.isCancelled()) return;

                    data.doRightClickAction(event, interaction);
                }
        );
    }
}
