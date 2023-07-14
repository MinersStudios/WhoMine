package com.github.minersstudios.msessentials.listeners.chat;

import com.github.minersstudios.msessentials.MSEssentials;
import com.github.minersstudios.msessentials.discord.BotHandler;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordPrivateMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

public class DiscordPrivateMessageReceivedListener {

    @Subscribe
    public void onDiscordPrivateMessageReceived(@NotNull DiscordPrivateMessageReceivedEvent event) {
        long userID = event.getAuthor().getIdLong();
        var handlerMap = MSEssentials.getCache().botHandlers;
        BotHandler handler = handlerMap.get(userID);

        if (handler == null) {
            handler = new BotHandler(event);

            handlerMap.put(userID, handler);
        }

        handler.handleMessage(event.getMessage());
    }
}
