package com.minersstudios.msessentials.listeners.event.chat;

import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.plugin.config.LanguageFile;
import com.minersstudios.msessentials.discord.BotHandler;
import com.minersstudios.msessentials.player.PlayerFile;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.skin.Skin;
import com.minersstudios.msessentials.util.DiscordUtil;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.minersstudios.mscore.plugin.config.LanguageFile.renderTranslation;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class SlashCommandInteractionListener extends ListenerAdapter {
    private static final String NOT_A_USER = renderTranslation("ms.discord.not_a_user");
    private static final String NOT_LINKED = renderTranslation("ms.discord.not_linked");
    private static final TranslatableComponent UNLINK_SUCCESS_DISCORD = translatable("ms.command.discord.unlink.success");
    private static final TranslatableComponent UNLINK_SUCCESS_MINECRAFT = translatable("ms.command.discord.unlink.minecraft.success");
    private static final String SERVICE_UNAVAILABLE = renderTranslation("ms.discord.skin.service_unavailable");
    private static final String INVALID_IMG = renderTranslation("ms.discord.skin.invalid_img");
    private static final String INVALID_NAME = renderTranslation("ms.discord.skin.invalid_name_regex");
    private static final TranslatableComponent ALREADY_SET = translatable("ms.discord.skin.already_set");
    private static final TranslatableComponent SKIN_SUCCESSFULLY_ADDED = translatable("ms.discord.skin.successfully_added");
    private static final TranslatableComponent SKIN_SUCCESSFULLY_ADDED_MINE = translatable("ms.discord.skin.successfully_added.minecraft");

    @Override
    public void onSlashCommandInteraction(final @NotNull SlashCommandInteractionEvent event) {
        new Handler(event).handle();
    }

    private static class Handler {
        private final SlashCommandInteractionEvent event;
        private final User user;
        private final InteractionHook hook;

        Handler(final @NotNull SlashCommandInteractionEvent event) {
            this.event = event;
            this.user = event.getUser();
            this.hook = event.getHook();
        }

        void handle() {
            if (!DiscordUtil.isVerified(this.user)) {
                this.send(NOT_A_USER);
                return;
            }

            final PlayerInfo playerInfo = PlayerInfo.fromDiscord(this.user.getIdLong());

            if (playerInfo == null) {
                this.send(NOT_LINKED);
                return;
            }

            final PlayerFile playerFile = playerInfo.getPlayerFile();
            final Player onlinePlayer = playerInfo.getOnlinePlayer();

            switch (this.event.getName()) {
                case "unlink" -> {
                    this.deferReply();

                    this.send(
                            BotHandler.craftEmbed(
                                    LanguageFile.renderTranslation(
                                            UNLINK_SUCCESS_DISCORD.args(
                                                    playerInfo.getDefaultName(),
                                                    text(playerFile.getPlayerName().getNickname())
                                            )
                                    )
                            )
                    );

                    if (onlinePlayer != null) {
                        MSLogger.fine(onlinePlayer, UNLINK_SUCCESS_MINECRAFT.args(text(this.user.getName())));
                    }
                }
                case "skinlist" -> {
                    this.deferReply();

                    final var skins = playerFile.getSkins();
                    final StringBuilder skinList = new StringBuilder();

                    for (int i = 0; i < skins.size(); i++) {
                        skinList
                        .append("\n")
                        .append(i + 1)
                        .append(" : \"")
                        .append(skins.get(i).getName())
                        .append("\"");
                    }

                    this.send(skinList.toString());
                }
                case "addskin" -> {
                    this.deferReply();

                    final OptionMapping nameOption = this.event.getOption("name");

                    if (nameOption == null) {
                        this.send(INVALID_NAME);
                        return;
                    }

                    final String name = nameOption.getAsString();

                    if (!Skin.matchesNameRegex(name)) {
                        this.send(INVALID_NAME);
                        return;
                    }

                    if (playerFile.containsSkin(name)) {
                        this.send(
                                renderTranslation(
                                        ALREADY_SET.args(
                                                playerInfo.getDefaultName(),
                                                text(playerInfo.getNickname())
                                        )
                                )
                        );
                        return;
                    }

                    final OptionMapping urlOption = this.event.getOption("url");
                    final OptionMapping valueOption = this.event.getOption("value");
                    final OptionMapping signatureOption = this.event.getOption("signature");

                    if (
                            urlOption != null
                            && valueOption == null
                            && signatureOption == null
                    ) {
                        try {
                            final Skin skin = Skin.create(name, urlOption.getAsString());

                            if (skin == null) {
                                this.send(SERVICE_UNAVAILABLE);
                            } else {
                                this.addSkin(playerInfo, skin);
                            }
                        } catch (IllegalArgumentException e) {
                            this.send(INVALID_IMG);
                        }
                    } else if (
                            urlOption == null
                            && valueOption != null
                            && signatureOption != null
                    ) {
                        try {
                            this.addSkin(
                                    playerInfo, Skin.create(
                                            name,
                                            valueOption.getAsString(),
                                            signatureOption.getAsString()
                                    )
                            );
                        } catch (IllegalArgumentException e) {
                            this.send(INVALID_IMG);
                        }
                    } else {
                        this.send("Invalid arguments!");
                    }
                }
                case "removeskin" -> {

                }
                case "editskin" -> {

                }
                case "help" -> {
                    this.deferReply();
                }
            }
        }

        private void addSkin(
                final @NotNull PlayerInfo playerInfo,
                final @NotNull Skin skin
        ) {
            final String skinName = skin.getName();
            final Player onlinePlayer = playerInfo.getOnlinePlayer();

            playerInfo.getPlayerFile().addSkin(skin);
            this.sendEmbed(
                    renderTranslation(
                            SKIN_SUCCESSFULLY_ADDED.args(
                                    text(skinName),
                                    playerInfo.getDefaultName(),
                                    text(playerInfo.getNickname())
                            )
                    )
            );

            if (onlinePlayer != null) {
                MSLogger.fine(onlinePlayer, SKIN_SUCCESSFULLY_ADDED_MINE.args(text(skinName)));
            }
        }

        void deferReply() {
            this.event.deferReply().setEphemeral(this.event.isFromGuild()).queue();
        }

        void send(final @NotNull String message) {
            this.hook.sendMessage(message).setEphemeral(this.event.isFromGuild()).queue();
        }

        void sendEmbed(final @NotNull String message) {
            this.hook.sendMessageEmbeds(BotHandler.craftEmbed(message)).setEphemeral(this.event.isFromGuild()).queue();
        }

        void send(final @NotNull MessageCreateData message) {
            this.hook.sendMessage(message).setEphemeral(this.event.isFromGuild()).queue();
        }

        void send(
                final @NotNull MessageEmbed first,
                final @NotNull MessageEmbed... rest
        ) {
            this.hook.sendMessageEmbeds(first, rest).setEphemeral(this.event.isFromGuild()).queue();
        }
    }
}
