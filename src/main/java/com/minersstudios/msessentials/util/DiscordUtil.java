package com.minersstudios.msessentials.util;

import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.chat.ChatType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class DiscordUtil {

    private DiscordUtil() {
        throw new AssertionError("Utility class");
    }

    public static @NotNull Optional<JDA> getJDA() {
        return Optional.ofNullable(MSEssentials.getCache().jda);
    }

    public static @NotNull Optional<Guild> getMainGuild() {
        return Optional.ofNullable(MSEssentials.getCache().mainGuild);
    }

    public static @NotNull Optional<Member> getMember(final @NotNull User user) {
        return getMainGuild().map(guild -> guild.getMember(user));
    }

    public static @NotNull Optional<TextChannel> getGlobalChannel() {
        return Optional.ofNullable(MSEssentials.getCache().discordGlobalChannel);
    }

    public static @NotNull Optional<TextChannel> getLocalChannel() {
        return Optional.ofNullable(MSEssentials.getCache().discordLocalChannel);
    }

    public static @NotNull Optional<Role> getMemberRole() {
        return Optional.ofNullable(MSEssentials.getCache().memberRole);
    }

    public static boolean isMember(final @NotNull User user) {
        return DiscordUtil.getMember(user).isPresent();
    }

    public static boolean isVerified(final @NotNull User user) {
        return DiscordUtil.getMember(user)
                .map(
                        member -> member.getRoles().contains(
                                DiscordUtil.getMemberRole().orElse(null)
                        )
                ).orElse(false);
    }

    public static void sendMessage(
            final @NotNull ChatType chatType,
            final CharSequence message
    ) {
        switch (chatType) {
            case GLOBAL -> getGlobalChannel().ifPresent(
                    channel -> channel.sendMessage(message).queue()
            );
            case LOCAL -> getLocalChannel().ifPresent(
                    channel -> channel.sendMessage(message).queue()
            );
        }
    }

    public static void sendMessage(
            final @NotNull User user,
            final @NotNull CharSequence message
    ) {
        user.openPrivateChannel().complete().sendMessage(message).queue();
    }

    public static void sendMessage(
            final long userId,
            final @NotNull CharSequence message
    ) {
        getJDA().ifPresent(
                jda -> {
                    final User user = jda.getUserById(userId);

                    if (user != null) {
                        sendMessage(user, message);
                    }
                }
        );
    }

    public static void sendEmbeds(
            final @NotNull ChatType chatType,
            final @NotNull MessageEmbed first,
            final MessageEmbed @NotNull ... rest
    ) {
        switch (chatType) {
            case GLOBAL -> getGlobalChannel().ifPresent(
                    channel -> channel.sendMessageEmbeds(first, rest).queue()
            );
            case LOCAL -> getLocalChannel().ifPresent(
                    channel -> channel.sendMessageEmbeds(first, rest).queue()
            );
        }
    }

    public static void sendEmbeds(
            final long userId,
            final @NotNull MessageEmbed first,
            final MessageEmbed @NotNull ... rest
    ) {
        getJDA().ifPresent(
                jda -> {
                    final User user = jda.getUserById(userId);

                    if (user != null) {
                        sendEmbeds(user, first, rest);
                    }
                }
        );
    }

    public static void sendEmbeds(
            final @NotNull User user,
            final @NotNull MessageEmbed first,
            final MessageEmbed @NotNull ... rest
    ) {
        user.openPrivateChannel().complete().sendMessageEmbeds(first, rest).queue();
    }

    public static void sendActionMessage(
            final @NotNull ChatType chatType,
            final @NotNull String playerName,
            final @NotNull String actionMessage,
            final int colorRaw
    ) {
        if (DiscordUtil.getJDA().isEmpty()) return;

        sendEmbeds(
                chatType,
                new MessageEmbed(
                        null,
                        null,
                        null,
                        EmbedType.RICH,
                        null,
                        colorRaw,
                        null,
                        null,
                        new MessageEmbed.AuthorInfo(
                                actionMessage,
                                null,
                                "https://cravatar.eu/helmavatar/" + playerName + "/128.png",
                                null
                        ),
                        null,
                        null,
                        null,
                        null
                )

        );
    }
}
