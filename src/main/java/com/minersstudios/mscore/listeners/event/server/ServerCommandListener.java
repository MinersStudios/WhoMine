package com.minersstudios.mscore.listeners.event.server;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.plugin.MSPlugin;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerCommandEvent;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.translatable;

@MSListener
public class ServerCommandListener extends AbstractMSListener {
    private static final TranslatableComponent ONLY_PLAYER_COMMAND = translatable("ms.error.only_player_command");

    @EventHandler
    public void onServerCommand(final @NotNull ServerCommandEvent event) {
        if (MSPlugin.getGlobalCache().onlyPlayerCommandSet.contains(event.getCommand())) {
            MSLogger.severe(event.getSender(), ONLY_PLAYER_COMMAND);
            event.setCancelled(true);
        }
    }
}
