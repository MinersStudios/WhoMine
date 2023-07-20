package com.minersstudios.msessentials.listeners.player;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.msessentials.world.WorldDark;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerCommandPreprocessListener extends AbstractMSListener {
    private static final TranslatableComponent YOU_CANT_DO_THIS_NOW = Component.translatable("ms.warning.you_cant_do_this_now");

    @EventHandler
    public void onPlayerCommandPreprocess(@NotNull PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (
                (message.startsWith("/l")
                && !message.startsWith("/logout"))
                || message.startsWith("/reg")
                || !WorldDark.isInWorldDark(player)
        ) return;

        event.setCancelled(true);
        MSLogger.warning(event.getPlayer(), YOU_CANT_DO_THIS_NOW);
    }
}
