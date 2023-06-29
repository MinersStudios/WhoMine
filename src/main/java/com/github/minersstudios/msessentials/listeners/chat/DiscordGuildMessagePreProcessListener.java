package com.github.minersstudios.msessentials.listeners.chat;

import com.github.minersstudios.mscore.utils.Badges;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.msessentials.utils.MessageUtils;
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
    public void onDiscordGuildMessagePreProcess(@NotNull DiscordGuildMessagePreProcessEvent event) {
        Message message = event.getMessage();
        Message referencedMessage = message.getReferencedMessage();
        String reply = referencedMessage != null
                ? replaceReplyPlaceholders(LangUtil.Message.CHAT_TO_MINECRAFT_REPLY.toString(), referencedMessage)
                : "";
        String attachment = message.getAttachments().isEmpty()
                        ? ""
                        : message.getAttachments().size() > 1
                        ? "(вложения) "
                        : "(вложение) ";
        Component messageComponent =
                Badges.DISCORD
                .color(NamedTextColor.WHITE)
                .append(text(message.getAuthor().getName(), TextColor.color(112, 125, 223)))
                .append(text(reply, TextColor.color(152, 162, 249)))
                .append(text(" : ", TextColor.color(112, 125, 223)))
                .append(text(attachment, TextColor.color(165, 165, 255)))
                .append(text(message.getContentDisplay(), TextColor.color(202, 202, 255)));

        MessageUtils.sendGlobalMessage(messageComponent);
        ChatUtils.sendInfo(messageComponent);
    }

    private static @NotNull String replaceReplyPlaceholders(
            @NotNull String format,
            @NotNull Message repliedMessage
    ) {
        Function<String, String> escape = MessageUtil.isLegacy(format)
                ? str -> str
                : str -> str.replaceAll("([<>])", "\\\\$1");
        String attachment = repliedMessage.getAttachments().isEmpty()
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
