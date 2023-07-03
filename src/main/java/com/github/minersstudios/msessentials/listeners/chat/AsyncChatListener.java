package com.github.minersstudios.msessentials.listeners.chat;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.msessentials.chat.ChatBuffer;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.github.minersstudios.msessentials.utils.MessageUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

import static com.github.minersstudios.mscore.utils.ChatUtils.*;
import static net.kyori.adventure.text.Component.text;

@MSListener
public class AsyncChatListener implements Listener {

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onAsyncChat(@NotNull AsyncChatEvent event) {
        event.setCancelled(true);

        Player player = event.getPlayer();
        PlayerInfo playerInfo = PlayerInfo.fromMap(player);

        if (playerInfo.isInWorldDark() || !playerInfo.isAuthenticated()) return;

        if (playerInfo.isMuted() && playerInfo.getMutedTo().isBefore(Instant.now())) {
            playerInfo.setMuted(false, null);
        }

        if (playerInfo.isMuted()) {
            sendWarning(player, Component.translatable("ms.command.mute.already.receiver"));
            return;
        }

        String message = serializeLegacyComponent(event.originalMessage());

        if (message.startsWith("!")) {
            message = message.substring(1).trim();

            if (!message.isEmpty()) {
                MessageUtils.sendMessageToChat(playerInfo, null, MessageUtils.Chat.GLOBAL, text(message));
            }
        } else if (message.startsWith("*")) {
            message = message.substring(1).trim();

            if (message.startsWith("*")) {
                message = message.substring(1).trim();

                if (message.startsWith("*")) {
                    message = message.substring(1).trim();

                    if (!message.isEmpty()) {
                        MessageUtils.sendRPEventMessage(player, text(message), MessageUtils.RolePlayActionType.IT);
                    }
                } else if (!message.isEmpty()) {
                    MessageUtils.sendRPEventMessage(player, text(message), MessageUtils.RolePlayActionType.DO);
                }
            } else if (message.contains("*")) {
                String action = message.substring(message.indexOf('*') + 1).trim(),
                        speech = message.substring(0, message.indexOf('*')).trim();

                if (action.length() == 0 || speech.length() == 0) {
                    sendError(player, "Используй: * [речь] * [действие]");
                } else {
                    MessageUtils.sendRPEventMessage(player, text(speech), text(action), MessageUtils.RolePlayActionType.TODO);
                }
            } else if (!message.isEmpty()) {
                MessageUtils.sendRPEventMessage(player, text(message), MessageUtils.RolePlayActionType.ME);
            }
        } else {
            MessageUtils.sendMessageToChat(playerInfo, player.getLocation(), MessageUtils.Chat.LOCAL, text(message));
            ChatBuffer.receiveMessage(player, message + " ");
        }
    }
}
