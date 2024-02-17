package com.minersstudios.msessentials.command.impl.discord;

import com.minersstudios.mscore.locale.Translations;
import com.minersstudios.msessentials.command.api.discord.SlashCommand;
import com.minersstudios.msessentials.command.api.discord.SlashCommandExecutor;
import com.minersstudios.msessentials.command.api.discord.interaction.CommandHandler;
import com.minersstudios.msessentials.discord.BotHandler;
import com.minersstudios.msessentials.player.PlayerInfo;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

@SlashCommand
public final class SkinListCommand extends SlashCommandExecutor {

    public SkinListCommand() {
        super(
                Commands.slash("skinlist", "Skin list")
        );
    }

    @Override
    public void onCommand(final @NotNull CommandHandler handler) {
        handler.deferReply();

        final PlayerInfo playerInfo = handler.retrievePlayerInfo();

        if (playerInfo != null) {
            final StringBuilder skinList = new StringBuilder();

            for (final var skin : playerInfo.getPlayerFile().getSkins()) {
                skinList
                .append('\n')
                .append("- ")
                .append(skin.getName());
            }

            handler.send(
                    BotHandler.craftEmbed(
                            Translations.DISCORD_COMMAND_LIST_OF_SKINS
                            .asString(skinList.toString())
                    )
            );
        }
    }
}
