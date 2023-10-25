package com.minersstudios.msessentials.listeners.event.chat;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.chat.ChatBuffer;
import com.minersstudios.msessentials.chat.ChatType;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.util.MessageUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

@MSListener
public final class AsyncChatListener extends AbstractMSListener<MSEssentials> {
    private static final TranslatableComponent MUTED = translatable("ms.command.mute.already.receiver");
    private static final TranslatableComponent YOU_CANT_DO_THIS_NOW = translatable("ms.warning.you_cant_do_this_now");

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onAsyncChat(final @NotNull AsyncChatEvent event) {
        event.setCancelled(true);

        final Player player = event.getPlayer();
        final PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);

        if (
                playerInfo.isInWorldDark()
                || !playerInfo.isAuthenticated()
        ) {
            MSLogger.warning(event.getPlayer(), YOU_CANT_DO_THIS_NOW);
            return;
        }

        if (playerInfo.isMuted() && playerInfo.getMutedTo().isBefore(Instant.now())) {
            playerInfo.unmute(null);
        }

        if (playerInfo.isMuted()) {
            MSLogger.warning(player, MUTED);
            return;
        }

        String message = ChatUtils.serializeLegacyComponent(event.originalMessage());

        if (message.startsWith("!")) {
            message = message.substring(1).trim();

            if (!message.isEmpty()) {
                MessageUtils.sendMessageToChat(playerInfo, null, ChatType.GLOBAL, text(message));
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
                final String action = message.substring(message.indexOf('*') + 1).trim();
                final String speech = message.substring(0, message.indexOf('*')).trim();

                if (action.isEmpty() || speech.isEmpty()) {
                    MSLogger.severe(player, "Используй: * [речь] * [действие]");
                } else {
                    MessageUtils.sendRPEventMessage(player, text(speech), text(action), MessageUtils.RolePlayActionType.TODO);
                }
            } else if (!message.isEmpty()) {
                MessageUtils.sendRPEventMessage(player, text(message), MessageUtils.RolePlayActionType.ME);
            }
        } else {
            MessageUtils.sendMessageToChat(playerInfo, player.getLocation(), ChatType.LOCAL, text(message));
            ChatBuffer.receiveMessage(player, message + " ");
        }
    }
}
