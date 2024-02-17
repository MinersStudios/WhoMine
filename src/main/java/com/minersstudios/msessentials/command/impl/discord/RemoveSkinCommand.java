package com.minersstudios.msessentials.command.impl.discord;

import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.msessentials.command.api.discord.SlashCommand;
import com.minersstudios.msessentials.command.api.discord.SlashCommandExecutor;
import com.minersstudios.msessentials.command.api.discord.interaction.CommandHandler;
import com.minersstudios.msessentials.command.api.discord.interaction.TabCompleterHandler;
import com.minersstudios.msessentials.discord.BotHandler;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.skin.Skin;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.minersstudios.mscore.locale.Translations.*;
import static net.kyori.adventure.text.Component.text;

@SlashCommand
public final class RemoveSkinCommand extends SlashCommandExecutor {

    public RemoveSkinCommand() {
        super(
                Commands.slash("removeskin", "Remove skin")
                .addOption(OptionType.STRING, "name", "Skin name", true, true)
        );
    }

    @Override
    public void onCommand(final @NotNull CommandHandler handler) {
        handler.deferReply();

        final PlayerInfo playerInfo = handler.retrievePlayerInfo();

        if (playerInfo == null) {
            return;
        }

        final SlashCommandInteraction interaction = handler.getInteraction();
        final OptionMapping nameOption = interaction.getOption("name");

        if (nameOption == null) {
            handler.send(DISCORD_SKIN_INVALID_NAME_REGEX.asString());

            return;
        }

        final String name = nameOption.getAsString();

        if (!Skin.matchesNameRegex(name)) {
            handler.send(DISCORD_SKIN_INVALID_NAME_REGEX.asString());

            return;
        }

        final Skin skin = playerInfo.getPlayerFile().getSkin(name);

        if (
                skin == null
                || !remove(playerInfo, skin, null, handler)
        ) {
            handler.send(
                    ChatUtils.serializePlainComponent(
                            DISCORD_COMMAND_SKIN_NOT_FOUND
                            .asComponent(text(name))
                    )
            );
        }
    }

    @Override
    protected @NotNull List<Command.Choice> onTabComplete(final @NotNull TabCompleterHandler handler) {
        if (!"name".equals(handler.getCurrentOption().getName())) {
            return EMPTY_TAB;
        }

        final PlayerInfo playerInfo = handler.getPlayerInfo();

        if (playerInfo == null) {
            return EMPTY_TAB;
        }

        final var skinNames = new ObjectArrayList<Command.Choice>(MAX_TAB_SIZE);

        for (final var skin : playerInfo.getPlayerFile().getSkins()) {
            skinNames.add(
                    new Command.Choice(
                            skin.getName(),
                            skin.getName()
                    )
            );
        }

        return skinNames;
    }

    public static boolean remove(
            final @NotNull PlayerInfo playerInfo,
            final @NotNull Skin skin,
            final @Nullable Message messageForReply,
            final @Nullable CommandHandler handler
    ) {
        final boolean isDeleted = playerInfo.getPlayerFile().removeSkin(skin);

        if (!isDeleted) {
            return false;
        }

        final String skinName = skin.getName();
        final Player player = playerInfo.getOnlinePlayer();
        final MessageEmbed embed =
                BotHandler.craftEmbed(
                        ChatUtils.serializePlainComponent(
                                DISCORD_SKIN_SUCCESSFULLY_REMOVED
                                .asComponent(
                                        text(skinName),
                                        playerInfo.getDefaultName(),
                                        text(playerInfo.getNickname())
                                )
                        )
                );

        if (player != null) {
            MSLogger.fine(
                    player,
                    DISCORD_SKIN_SUCCESSFULLY_REMOVED_MINECRAFT.asTranslatable()
                    .arguments(text(skinName))
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
