package com.minersstudios.msessentials.listeners.chat;

import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.discord.BotHandler;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordPrivateMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

public class DiscordPrivateMessageReceivedListener {

    @Subscribe
    public void onDiscordPrivateMessageReceived(final @NotNull DiscordPrivateMessageReceivedEvent event) {
        final long userID = event.getAuthor().getIdLong();
        final var handlerMap = MSEssentials.getCache().botHandlers;
        BotHandler handler = handlerMap.get(userID);

        if (handler == null) {
            handler = new BotHandler(event);

            handlerMap.put(userID, handler);
        }

        handler.handleMessage(event.getMessage());
    }
}
