package com.minersstudios.msessentials.command.discord;

import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.language.LanguageFile;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.discord.BotHandler;
import com.minersstudios.msessentials.discord.command.InteractionHandler;
import com.minersstudios.msessentials.discord.command.SlashCommand;
import com.minersstudios.msessentials.discord.command.SlashCommandExecutor;
import com.minersstudios.msessentials.player.PlayerInfo;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

@SlashCommand
public final class UnlinkCommand extends SlashCommandExecutor<MSEssentials> {
    private static final TranslatableComponent UNLINK_SUCCESS_DISCORD = translatable("ms.command.discord.unlink.discord.success");
    private static final TranslatableComponent UNLINK_SUCCESS_MINECRAFT = translatable("ms.command.discord.unlink.minecraft.success");

    public UnlinkCommand() {
        super(
                Commands.slash("unlink", "Unlink Discord account from Minecraft account")
        );
    }

    @Override
    public void onInteract(@NotNull InteractionHandler handler) {
        final PlayerInfo playerInfo = handler.retrievePlayerInfo();

        if (playerInfo == null) {
            return;
        }

        handler.deferReply();

        final Player onlinePlayer = playerInfo.getOnlinePlayer();

        if (onlinePlayer != null) {
            MSLogger.fine(
                    onlinePlayer,
                    UNLINK_SUCCESS_MINECRAFT.args(
                            text(handler.getInteraction().getUser().getName())
                    )
            );
        }

        handler.send(
                BotHandler.craftEmbed(
                        LanguageFile.renderTranslation(
                                UNLINK_SUCCESS_DISCORD.args(
                                        playerInfo.getDefaultName(),
                                        text(playerInfo.getPlayerFile().getPlayerName().getNickname())
                                )
                        )
                )
        );
    }
}
