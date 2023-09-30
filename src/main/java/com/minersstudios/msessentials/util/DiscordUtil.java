package com.minersstudios.msessentials.util;

import com.minersstudios.msessentials.Config;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.chat.ChatType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Utility class for handling various Discord-related
 * operations in the MSEssentials plugin. This class
 * provides methods for interacting with the Discord
 * API using JDA (Java Discord API).
 */
public final class DiscordUtil {

    private DiscordUtil() {
        throw new AssertionError("Utility class");
    }

    /**
     * Retrieves an Optional instance containing the
     * JDA instance, if available
     *
     * @return An Optional containing the JDA instance,
     *         or an empty Optional if not available
     */
    public static @NotNull Optional<JDA> getJDA() {
        return Optional.ofNullable(MSEssentials.getCache().jda);
    }

    /**
     * Retrieves an Optional instance containing the
     * main guild, if available. The main guild is
     * the guild that the bot is primarily used in.
     *
     * @return An Optional containing the main guild,
     *         or an empty Optional if not available
     * @see Config#discordServerId
     */
    public static @NotNull Optional<Guild> getMainGuild() {
        return Optional.ofNullable(MSEssentials.getCache().mainGuild);
    }

    /**
     * Retrieves an Optional instance containing the
     * user, if available
     *
     * @param userId The id of the user to retrieve
     * @return An Optional containing the user, or an
     *         empty Optional if not available
     * @see #getJDA()
     */
    public static @NotNull Optional<User> getUser(final long userId) {
        return getJDA().map(jda -> jda.getUserById(userId));
    }

    /**
     * Retrieves an Optional instance containing the
     * member for the specified user, if available.
     * The member is retrieved from the main guild.
     *
     * @param user The user to retrieve the member for
     *             in the main guild
     * @return An Optional containing the member, or an
     *         empty Optional if member is not found or
     *         the main guild is not available
     * @see #getMainGuild()
     */
    public static @NotNull Optional<Member> getMember(final @NotNull User user) {
        return getMainGuild().map(guild -> guild.retrieveMember(user).complete());
    }

    /**
     * Retrieves an Optional instance containing the
     * global channel, if available. The global channel
     * is the channel that the bot sends global messages
     * to. The global channel gets from the loaded guild
     * (generally the main guild).
     *
     * @return An Optional containing the global channel,
     *         or an empty Optional if not available
     * @see Config#discordGlobalChannelId
     */
    public static @NotNull Optional<TextChannel> getGlobalChannel() {
        return Optional.ofNullable(MSEssentials.getCache().discordGlobalChannel);
    }

    /**
     * Retrieves an Optional instance containing the
     * local channel, if available. The local channel
     * is the channel that the bot sends local messages
     * to. The local channel gets from the loaded guild
     * (generally the main guild).
     *
     * @return An Optional containing the local channel,
     *         or an empty Optional if not available
     * @see Config#discordLocalChannelId
     */
    public static @NotNull Optional<TextChannel> getLocalChannel() {
        return Optional.ofNullable(MSEssentials.getCache().discordLocalChannel);
    }

    /**
     * Retrieves an Optional instance containing the
     * member role, if available. The member role is
     * the role that is given to verified members.
     *
     * @return An Optional containing the member role,
     *         or an empty Optional if not available
     * @see Config#memberRoleId
     * @see #isVerified(User)
     */
    public static @NotNull Optional<Role> getMemberRole() {
        return Optional.ofNullable(MSEssentials.getCache().memberRole);
    }

    /**
     * Checks if the specified user is a member of the
     * main guild
     *
     * @param user The user to check
     * @return True if the user is a member of the main
     *         guild, false otherwise
     * @see #getMember(User)
     */
    public static boolean isMember(final @NotNull User user) {
        return DiscordUtil.getMember(user).isPresent();
    }

    /**
     * Checks if the specified user is a member of the
     * main guild and has the member role
     *
     * @param user The user to check
     * @return True if the user is a member of the main
     *         guild and has the member role, false otherwise
     * @see #getMemberRole()
     */
    public static boolean isVerified(final @NotNull User user) {
        return DiscordUtil.getMember(user)
                .map(
                        member -> member.getRoles().contains(
                                DiscordUtil.getMemberRole().orElse(null)
                        )
                ).orElse(false);
    }

    /**
     * Sends a message to the specified chat type
     *
     * @param chatType The chat type to send the message to
     * @param message  The message to send
     * @see #getGlobalChannel()
     * @see #getLocalChannel()
     */
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

    /**
     * Sends a message to the private channel of the
     * specified user
     *
     * @param user    The user to send the message to
     * @param message The message to send
     */
    public static void sendMessage(
            final @NotNull User user,
            final @NotNull CharSequence message
    ) {
        user.openPrivateChannel().onSuccess(
                channel -> channel.sendMessage(message).queue()
        ).queue();
    }

    /**
     * Sends a message to the private channel of the
     * specified user
     *
     * @param userId  The id of the user to send the message to
     * @param message The message to send
     * @see #getUser(long)
     * @see #sendMessage(User, CharSequence)
     */
    public static void sendMessage(
            final long userId,
            final @NotNull CharSequence message
    ) {
        getUser(userId).ifPresent(
                user -> sendMessage(user, message)
        );
    }

    /**
     * Sends an embeds to the specified chat type
     *
     * @param chatType The chat type to send the message to
     * @param first    The first embed to send
     * @param rest     The rest of the embeds to send
     * @see #getGlobalChannel()
     * @see #getLocalChannel()
     */
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

    /**
     * Sends an embeds to the private channel of the
     * specified user
     *
     * @param userId The id of the user to send the embeds to
     * @param first  The first embed to send
     * @param rest   The rest of the embeds to send
     * @see #getUser(long)
     * @see #sendEmbeds(User, MessageEmbed, MessageEmbed...)
     */
    public static void sendEmbeds(
            final long userId,
            final @NotNull MessageEmbed first,
            final MessageEmbed @NotNull ... rest
    ) {
        getUser(userId).ifPresent(
                user -> sendEmbeds(user, first, rest)
        );
    }

    /**
     * Sends an embeds to the private channel of the
     * specified user
     *
     * @param user  The user to send the embeds to
     * @param first The first embed to send
     * @param rest  The rest of the embeds to send
     */
    public static void sendEmbeds(
            final @NotNull User user,
            final @NotNull MessageEmbed first,
            final MessageEmbed @NotNull ... rest
    ) {
        user.openPrivateChannel().onSuccess(
                channel -> channel.sendMessageEmbeds(first, rest).queue()
        ).queue();
    }

    /**
     * Sends an embed action message with the specified
     * color, player head and action message to the
     * specified chat type
     *
     * @param chatType      The chat type to send the message to
     * @param playerName    The name of the player who did the action
     * @param actionMessage The action message to send
     * @param colorRaw      The color of the embed
     */
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
