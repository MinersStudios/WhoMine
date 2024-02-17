package com.minersstudios.msessentials.command.impl.discord;

import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.msessentials.command.api.discord.SlashCommand;
import com.minersstudios.msessentials.command.api.discord.SlashCommandExecutor;
import com.minersstudios.msessentials.command.api.discord.interaction.CommandHandler;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.skin.Skin;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.minersstudios.mscore.locale.Translations.*;
import static net.kyori.adventure.text.Component.text;

@SlashCommand
public final class AddSkinCommand extends SlashCommandExecutor {

    public AddSkinCommand() {
        super(
                Commands.slash("addskin", "Add skin")
                .addOption(
                        OptionType.STRING,
                        "name",
                        "Skin Name",
                        true
                )
                .addOption(
                        OptionType.STRING,
                        "url",
                        "Skin URL"
                )
                .addOption(
                        OptionType.STRING,
                        "value",
                        "Skin Value"
                )
                .addOption(
                        OptionType.STRING,
                        "signature",
                        "Skin Signature"
                )
        );
    }

    @Override
    protected void onCommand(final @NotNull CommandHandler handler) {
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

        if (playerInfo.getPlayerFile().containsSkin(name)) {
            handler.send(
                    ChatUtils.serializePlainComponent(
                            DISCORD_SKIN_ALREADY_SET
                            .asComponent(
                                    playerInfo.getDefaultName(),
                                    text(playerInfo.getNickname())
                            )
                    )
            );

            return;
        }

        final OptionMapping urlOption = interaction.getOption("url");
        final OptionMapping valueOption = interaction.getOption("value");
        final OptionMapping signatureOption = interaction.getOption("signature");

        if (
                urlOption != null
                && valueOption == null
                && signatureOption == null
        ) {
            try {
                final Skin skin = Skin.create(this.getPlugin(), name, urlOption.getAsString());

                if (skin == null) {
                    handler.send(DISCORD_SKIN_SERVICE_UNAVAILABLE.asString());
                } else {
                    addSkin(handler, playerInfo, skin);
                }
            } catch (final IllegalArgumentException ignored) {
                handler.send(DISCORD_SKIN_INVALID_IMG.asString());
            }
        } else if (
                urlOption == null
                && valueOption != null
                && signatureOption != null
        ) {
            try {
                addSkin(
                        handler,
                        playerInfo,
                        Skin.create(
                                name,
                                valueOption.getAsString(),
                                signatureOption.getAsString()
                        )
                );
            } catch (final IllegalArgumentException ignored) {
                handler.send(DISCORD_SKIN_INVALID_IMG.asString());
            }
        } else {
            handler.send(DISCORD_COMMAND_INVALID_ARGUMENTS.asString());
        }
    }

    private static void addSkin(
            final @NotNull CommandHandler handler,
            final @NotNull PlayerInfo playerInfo,
            final @NotNull Skin skin
    ) {
        final String skinName = skin.getName();
        final Player onlinePlayer = playerInfo.getOnlinePlayer();

        playerInfo.getPlayerFile().addSkin(skin);
        handler.sendEmbed(
                ChatUtils.serializePlainComponent(
                        DISCORD_SKIN_SUCCESSFULLY_ADDED
                        .asComponent(
                                text(skinName),
                                playerInfo.getDefaultName(),
                                text(playerInfo.getNickname())
                        )
                )
        );

        if (onlinePlayer != null) {
            MSLogger.fine(
                    onlinePlayer,
                    DISCORD_SKIN_SUCCESSFULLY_ADDED_MINECRAFT.asTranslatable().arguments(text(skinName))
            );
        }
    }
}
