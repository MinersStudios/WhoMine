package com.minersstudios.msessentials.command.impl.discord;

import com.minersstudios.mscore.language.LanguageRegistry;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.msessentials.command.api.discord.InteractionHandler;
import com.minersstudios.msessentials.command.api.discord.SlashCommand;
import com.minersstudios.msessentials.command.api.discord.SlashCommandExecutor;
import com.minersstudios.msessentials.discord.BotHandler;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.skin.Skin;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.minersstudios.mscore.language.LanguageFile.renderTranslation;
import static com.minersstudios.mscore.language.LanguageRegistry.Strings.DISCORD_SKIN_INVALID_NAME_REGEX;
import static net.kyori.adventure.text.Component.text;

@SlashCommand
public final class RemoveSkinCommand extends SlashCommandExecutor {

    public RemoveSkinCommand() {
        super(
                Commands.slash("removeskin", "Remove skin")
                .addOption(OptionType.STRING, "name", "Skin name", true)
        );
    }

    @Override
    public void onInteract(final @NotNull InteractionHandler handler) {
        handler.deferReply();

        final PlayerInfo playerInfo = handler.retrievePlayerInfo();

        if (playerInfo == null) {
            return;
        }

        final SlashCommandInteraction interaction = handler.getInteraction();
        final OptionMapping nameOption = interaction.getOption("name");

        if (nameOption == null) {
            handler.send(DISCORD_SKIN_INVALID_NAME_REGEX);
            return;
        }

        final String name = nameOption.getAsString();

        if (!Skin.matchesNameRegex(name)) {
            handler.send(DISCORD_SKIN_INVALID_NAME_REGEX);
            return;
        }

        final Skin skin = playerInfo.getPlayerFile().getSkin(name);

        if (
                skin == null
                || !remove(playerInfo, skin, null, handler)
        ) {
            handler.send(
                    renderTranslation(
                            LanguageRegistry.Components.DISCORD_COMMAND_SKIN_NOT_FOUND
                            .args(text(name))
                    )
            );
        }
    }

    public static boolean remove(
            final @NotNull PlayerInfo playerInfo,
            final @NotNull Skin skin,
            final @Nullable Message messageForReply,
            final @Nullable InteractionHandler handler
    ) {
        final String skinName = skin.getName();
        final boolean isDeleted = playerInfo.getPlayerFile().removeSkin(skin);

        if (!isDeleted) {
            return false;
        }

        final Player player = playerInfo.getOnlinePlayer();
        final MessageEmbed embed =
                BotHandler.craftEmbed(
                        renderTranslation(
                                LanguageRegistry.Components.DISCORD_SKIN_SUCCESSFULLY_REMOVED
                                .args(
                                        text(skinName),
                                        playerInfo.getDefaultName(),
                                        text(playerInfo.getNickname())
                                )
                        )
                );

        if (player != null) {
            MSLogger.fine(
                    player,
                    LanguageRegistry.Components.DISCORD_SKIN_SUCCESSFULLY_REMOVED_MINECRAFT
                    .args(text(skinName))
            );
        }

        if (messageForReply != null) {
            messageForReply.replyEmbeds(embed).queue();
        } else if (handler != null) {
            handler.send(embed);
        } else {
            playerInfo.sendPrivateDiscordMessage(embed);
        }

        return true;
    }
}
