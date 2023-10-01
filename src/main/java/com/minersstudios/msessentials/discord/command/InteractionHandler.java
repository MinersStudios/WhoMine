package com.minersstudios.msessentials.discord.command;

import com.minersstudios.msessentials.discord.BotHandler;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.util.DiscordUtil;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.minersstudios.mscore.plugin.config.LanguageFile.renderTranslation;

public final class InteractionHandler {
    private final SlashCommandInteraction interaction;
    private PlayerInfo playerInfo;

    private static final String NOT_A_USER = renderTranslation("ms.discord.not_a_user");
    private static final String NOT_LINKED = renderTranslation("ms.discord.not_linked");

    public InteractionHandler(final @NotNull SlashCommandInteraction interaction) {
        this.interaction = interaction;
    }

    public @NotNull SlashCommandInteraction getInteraction() {
        return this.interaction;
    }

    public @Nullable PlayerInfo getPlayerInfo() {
        if (this.playerInfo != null) return this.playerInfo;

        final User user = this.interaction.getUser();

        return !DiscordUtil.isVerified(user)
                ? null
                : this.playerInfo == null
                ? this.playerInfo = PlayerInfo.fromDiscord(user.getIdLong())
                : this.playerInfo;
    }

    public @Nullable PlayerInfo retrievePlayerInfo() {
        if (this.playerInfo != null) return this.playerInfo;

        final User user = this.interaction.getUser();

        if (!DiscordUtil.isVerified(user)) {
            this.send(NOT_A_USER);
            return null;
        }

        final PlayerInfo playerInfo = PlayerInfo.fromDiscord(user.getIdLong());

        if (playerInfo == null) {
            this.send(NOT_LINKED);
            return null;
        }

        return this.playerInfo = playerInfo;
    }

    public void deferReply() {
        this.deferReply(this.interaction.isFromGuild());
    }

    public void deferReply(final boolean ephemeral) {
        this.interaction
                .deferReply()
                .setEphemeral(ephemeral)
                .queue();
    }

    public void send(final @NotNull String message) {
        this.send(this.interaction.isFromGuild(), message);
    }

    public void send(
            final boolean ephemeral,
            final @NotNull String message
    ) {
        this.interaction.getHook()
                .sendMessage(message)
                .setEphemeral(ephemeral)
                .queue();
    }

    public void send(final @NotNull MessageCreateData message) {
        this.send(this.interaction.isFromGuild(), message);
    }

    public void send(
            final boolean ephemeral,
            final @NotNull MessageCreateData message
    ) {
        this.interaction.getHook()
                .sendMessage(message)
                .setEphemeral(ephemeral)
                .queue();
    }

    public void send(
            final @NotNull MessageEmbed first,
            final MessageEmbed @NotNull ... rest
    ) {
        this.send(
                this.interaction.isFromGuild(),
                first,
                rest
        );
    }

    public void send(
            final boolean ephemeral,
            final @NotNull MessageEmbed first,
            final MessageEmbed @NotNull ... rest
    ) {
        this.interaction.getHook()
                .sendMessageEmbeds(first, rest)
                .setEphemeral(ephemeral)
                .queue();
    }

    public void send(
            final @NotNull FileUpload first,
            final FileUpload @NotNull ... rest
    ) {
        this.send(
                this.interaction.isFromGuild(),
                first,
                rest
        );
    }

    public void send(
            final boolean ephemeral,
            final @NotNull FileUpload first,
            final FileUpload @NotNull ... rest
    ) {
        final FileUpload[] files = new FileUpload[rest.length + 1];

        System.arraycopy(rest, 0, files, 1, rest.length);
        files[0] = first;

        this.interaction.getHook()
                .sendFiles(files)
                .setEphemeral(ephemeral)
                .queue();
    }

    public void sendEmbed(final @NotNull String message) {
        this.sendEmbed(this.interaction.isFromGuild(), message);
    }

    public void sendEmbed(
            final boolean ephemeral,
            final @NotNull String message
    ) {
        this.interaction.getHook()
                .sendMessageEmbeds(BotHandler.craftEmbed(message))
                .setEphemeral(ephemeral)
                .queue();
    }
}
