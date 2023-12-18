package com.minersstudios.msessentials.commands.discord;

import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.discord.command.InteractionHandler;
import com.minersstudios.msessentials.discord.command.SlashCommand;
import com.minersstudios.msessentials.discord.command.SlashCommandExecutor;
import com.minersstudios.msessentials.player.PlayerFile;
import com.minersstudios.msessentials.player.PlayerInfo;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

@SlashCommand
public final class SkinListCommand extends SlashCommandExecutor<MSEssentials> {

    public SkinListCommand() {
        super(
                Commands.slash("skinlist", "Skin list")
        );
    }

    @Override
    public void onInteract(@NotNull InteractionHandler handler) {
        final PlayerInfo playerInfo = handler.retrievePlayerInfo();

        if (playerInfo == null) {
            return;
        }

        handler.deferReply();

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
