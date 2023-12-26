package com.minersstudios.msessentials.command.impl.discord;

import com.minersstudios.mscore.language.LanguageFile;
import com.minersstudios.msessentials.command.api.discord.InteractionHandler;
import com.minersstudios.msessentials.command.api.discord.SlashCommand;
import com.minersstudios.msessentials.command.api.discord.SlashCommandExecutor;
import com.minersstudios.msessentials.discord.BotHandler;
import com.minersstudios.msessentials.player.PlayerInfo;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import static com.minersstudios.mscore.language.LanguageRegistry.Components.DISCORD_COMMAND_LIST_OF_SKINS;
import static net.kyori.adventure.text.Component.text;

@SlashCommand
public final class SkinListCommand extends SlashCommandExecutor {

    public SkinListCommand() {
        super(
                Commands.slash("skinlist", "Skin list")
        );
    }

    @Override
    public void onInteract(final @NotNull InteractionHandler handler) {
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
                            LanguageFile.renderTranslation(
                                    DISCORD_COMMAND_LIST_OF_SKINS
                                    .args(text(skinList.toString()))
                            )
                    )
            );
        }
    }
}
