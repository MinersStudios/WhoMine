package com.minersstudios.msessentials.command.impl.discord;

import com.minersstudios.msessentials.command.api.discord.InteractionHandler;
import com.minersstudios.msessentials.command.api.discord.SlashCommand;
import com.minersstudios.msessentials.command.api.discord.SlashCommandExecutor;
import com.minersstudios.msessentials.player.PlayerFile;
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
    public void onInteract(final @NotNull InteractionHandler handler) {
        handler.deferReply();

        final PlayerInfo playerInfo = handler.retrievePlayerInfo();

        if (playerInfo == null) {
            return;
        }

        final PlayerFile playerFile = playerInfo.getPlayerFile();
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

        handler.send(skinList.toString());
    }
}
