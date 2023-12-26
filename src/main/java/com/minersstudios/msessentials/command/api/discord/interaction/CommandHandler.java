package com.minersstudios.msessentials.command.api.discord.interaction;

import com.minersstudios.mscore.language.LanguageRegistry;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.discord.BotHandler;
import com.minersstudios.msessentials.player.PlayerInfo;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CommandHandler extends AbstractInteractionHandler<SlashCommandInteraction> {

    public CommandHandler(
            final @NotNull MSEssentials plugin,
            final @NotNull SlashCommandInteraction interaction
    ) {
        super(plugin, interaction);
    }

    @Override
    public @NotNull String getCommandName() {
        return this.getInteraction().getName();
    }

    public @Nullable PlayerInfo retrievePlayerInfo() {
        if (this.playerInfo != null) {
            return this.playerInfo;
        }

        final User user = this.getInteraction().getUser();

        if (!this.getPlugin().getCache().getDiscordManager().isVerified(user)) {
            this.send(LanguageRegistry.Strings.DISCORD_NOT_A_USER);

            return null;
        }

        final PlayerInfo playerInfo = PlayerInfo.fromDiscord(this.getPlugin(), user.getIdLong());

        if (playerInfo == null) {
            this.send(LanguageRegistry.Strings.DISCORD_NOT_LINKED);

            return null;
        }

        return this.playerInfo = playerInfo;
    }

    public void deferReply() {
        this.deferReply(this.getInteraction().isFromGuild());
    }

    public void deferReply(final boolean ephemeral) {
        this.getInteraction()
        .deferReply()
        .setEphemeral(ephemeral)
        .queue();
    }

    public void send(final @NotNull String message) {
        this.send(this.getInteraction().isFromGuild(), message);
    }

    public void send(
            final boolean ephemeral,
            final @NotNull String message
    ) {
        this.getInteraction().getHook()
        .sendMessage(message)
        .setEphemeral(ephemeral)
        .queue();
    }

    public void send(final @NotNull MessageCreateData message) {
        this.send(this.getInteraction().isFromGuild(), message);
    }

    public void send(
            final boolean ephemeral,
            final @NotNull MessageCreateData message
    ) {
        this.getInteraction().getHook()
        .sendMessage(message)
        .setEphemeral(ephemeral)
        .queue();
    }

    public void send(
            final @NotNull MessageEmbed first,
            final MessageEmbed @NotNull ... rest
    ) {
        this.send(
                this.getInteraction().isFromGuild(),
                first,
                rest
        );
    }

    public void send(
            final boolean ephemeral,
            final @NotNull MessageEmbed first,
            final MessageEmbed @NotNull ... rest
    ) {
        this.getInteraction().getHook()
        .sendMessageEmbeds(first, rest)
        .setEphemeral(ephemeral)
        .queue();
    }

    public void send(
            final @NotNull FileUpload first,
            final FileUpload @NotNull ... rest
    ) {
        this.send(
                this.getInteraction().isFromGuild(),
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

        this.getInteraction().getHook()
        .sendFiles(files)
        .setEphemeral(ephemeral)
        .queue();
    }

    public void sendEmbed(final @NotNull String message) {
        this.sendEmbed(this.getInteraction().isFromGuild(), message);
    }

    public void sendEmbed(
            final boolean ephemeral,
            final @NotNull String message
    ) {
        this.getInteraction().getHook()
        .sendMessageEmbeds(BotHandler.craftEmbed(message))
        .setEphemeral(ephemeral)
        .queue();
    }
}
