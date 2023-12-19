package com.minersstudios.msessentials.listener.impl.event.player;

import com.minersstudios.mscore.language.LanguageRegistry;
import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.world.WorldDark;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class PlayerCommandPreprocessListener extends AbstractEventListener<MSEssentials> {

    @EventHandler
    public void onPlayerCommandPreprocess(final @NotNull PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();
        final String message = event.getMessage();

        if (
                (
                        message.startsWith("/l")
                        && !message.startsWith("/logout")
                )
                || message.startsWith("/reg")
                || !WorldDark.isInWorldDark(player)
        ) {
            return;
        }

        event.setCancelled(true);
        MSLogger.warning(
                player,
                LanguageRegistry.Components.WARNING_YOU_CANT_DO_THIS_NOW
        );
    }
}
