package com.minersstudios.msessentials.listeners.chat;

import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.util.Badges;
import com.minersstudios.msessentials.util.MessageUtils;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessagePreProcessEvent;
import github.scarsz.discordsrv.dependencies.google.common.base.Function;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.util.LangUtil;
import github.scarsz.discordsrv.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

public class DiscordGuildMessagePreProcessListener {

    @Subscribe
    public void onDiscordGuildMessagePreProcess(final @NotNull DiscordGuildMessagePreProcessEvent event) {
        final Message message = event.getMessage();
        final Message referencedMessage = message.getReferencedMessage();
        final String reply = referencedMessage != null
                ? replaceReplyPlaceholders(LangUtil.Message.CHAT_TO_MINECRAFT_REPLY.toString(), referencedMessage)
                : "";
        final String attachment = message.getAttachments().isEmpty()
                        ? ""
                        : message.getAttachments().size() > 1
                        ? "(вложения) "
                        : "(вложение) ";
        final Component messageComponent =
                Badges.DISCORD
                .color(NamedTextColor.WHITE)
                .append(text(message.getAuthor().getName(), TextColor.color(112, 125, 223)))
                .append(text(reply, TextColor.color(152, 162, 249)))
                .append(text(" : ", TextColor.color(112, 125, 223)))
                .append(text(attachment, TextColor.color(165, 165, 255)))
                .append(text(message.getContentDisplay(), TextColor.color(202, 202, 255)));

        MessageUtils.sendGlobalMessage(messageComponent);
        MSLogger.info(messageComponent);
    }

    private static @NotNull String replaceReplyPlaceholders(
            final @NotNull String format,
            final @NotNull Message repliedMessage
    ) {
        final Function<String, String> escape = MessageUtil.isLegacy(format)
                ? str -> str
                : str -> str.replaceAll("([<>])", "\\\\$1");
        final String attachment = repliedMessage.getAttachments().isEmpty()
                ? ""
                : repliedMessage.getAttachments().size() > 1
                ? "(вложения) "
                : "(вложение) ",
                message = escape.apply(MessageUtil.strip(repliedMessage.getContentDisplay()));
        return message.isEmpty() && attachment.isEmpty()
                ? ""
                : " (отвечая на \"" + attachment + message + "\")";
    }
}
