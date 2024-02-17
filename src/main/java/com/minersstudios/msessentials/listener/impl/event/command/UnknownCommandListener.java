package com.minersstudios.msessentials.listener.impl.event.command;

import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.msessentials.MSEssentials;
import org.bukkit.event.EventHandler;
import org.bukkit.event.command.UnknownCommandEvent;
import org.jetbrains.annotations.NotNull;

import static com.minersstudios.mscore.locale.Translations.ERROR_UNKNOWN_COMMAND;

@EventListener
public class UnknownCommandListener extends AbstractEventListener<MSEssentials> {

    @EventHandler
    public void onUnknownCommand(final @NotNull UnknownCommandEvent event) {
        event.message(null);
        MSLogger.severe(
                event.getSender(),
                ERROR_UNKNOWN_COMMAND.asTranslatable()
        );
    }
}
