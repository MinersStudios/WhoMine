package com.minersstudios.msessentials.listener.impl.event.player;

import com.minersstudios.mscore.locale.Translations;
import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.msessentials.MSEssentials;
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
                || !this.getPlugin().getCache().getWorldDark().isInWorldDark(player)
        ) {
            return;
        }

        event.setCancelled(true);
        MSLogger.warning(
                player,
                Translations.WARNING_YOU_CANT_DO_THIS_NOW.asTranslatable()
        );
    }
}
