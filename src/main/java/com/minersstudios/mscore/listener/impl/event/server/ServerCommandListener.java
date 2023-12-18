package com.minersstudios.mscore.listener.impl.event.server;

import com.minersstudios.mscore.MSCore;
import com.minersstudios.mscore.language.LanguageRegistry;
import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.plugin.MSPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerCommandEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@EventListener
public final class ServerCommandListener extends AbstractEventListener<MSCore> {
    private static final Set<String> ONLY_PLAYER_COMMAND_SET = MSPlugin.globalCache().onlyPlayerCommandSet;

    @EventHandler
    public void onServerCommand(final @NotNull ServerCommandEvent event) {
        final String command = event.getCommand().split(" ")[0];

        if (ONLY_PLAYER_COMMAND_SET.contains(command)) {
            MSLogger.severe(
                    event.getSender(),
                    LanguageRegistry.Components.ERROR_ONLY_PLAYER_COMMAND
            );
            event.setCancelled(true);
        }
    }
}
