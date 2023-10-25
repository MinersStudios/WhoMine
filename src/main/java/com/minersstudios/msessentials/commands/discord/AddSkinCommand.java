package com.minersstudios.msessentials.commands.discord;

import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.msessentials.discord.command.InteractionHandler;
import com.minersstudios.msessentials.discord.command.SlashCommand;
import com.minersstudios.msessentials.discord.command.SlashCommandExecutor;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.skin.Skin;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.minersstudios.mscore.plugin.config.LanguageFile.renderTranslation;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

@SlashCommand
public final class AddSkinCommand extends SlashCommandExecutor {
    private static final String SERVICE_UNAVAILABLE = renderTranslation("ms.discord.skin.service_unavailable");
    private static final String INVALID_IMG = renderTranslation("ms.discord.skin.invalid_img");
    private static final String INVALID_NAME = renderTranslation("ms.discord.skin.invalid_name_regex");
    private static final TranslatableComponent ALREADY_SET = translatable("ms.discord.skin.already_set");
    private static final TranslatableComponent SKIN_SUCCESSFULLY_ADDED = translatable("ms.discord.skin.successfully_added");
    private static final TranslatableComponent SKIN_SUCCESSFULLY_ADDED_MINE = translatable("ms.discord.skin.successfully_added.minecraft");

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
    public void onInteract(@NotNull InteractionHandler handler) {
        final PlayerInfo playerInfo = handler.retrievePlayerInfo();

        if (playerInfo == null) return;

        handler.deferReply();

        final SlashCommandInteraction interaction = handler.getInteraction();
        final OptionMapping nameOption = interaction.getOption("name");

        if (nameOption == null) {
            handler.send(INVALID_NAME);
            return;
        }

        final String name = nameOption.getAsString();

        if (!Skin.matchesNameRegex(name)) {
            handler.send(INVALID_NAME);
            return;
        }

        if (playerInfo.getPlayerFile().containsSkin(name)) {
            handler.send(
                    renderTranslation(
                            ALREADY_SET.args(
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
                final Skin skin = Skin.create(name, urlOption.getAsString());

                if (skin == null) {
                    handler.send(SERVICE_UNAVAILABLE);
                } else {
                    addSkin(handler, playerInfo, skin);
                }
            } catch (final IllegalArgumentException ignored) {
                handler.send(INVALID_IMG);
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
                handler.send(INVALID_IMG);
            }
        } else {
            handler.send("Invalid arguments!");
        }
    }

    private static void addSkin(
            final @NotNull InteractionHandler handler,
            final @NotNull PlayerInfo playerInfo,
            final @NotNull Skin skin
    ) {
        final String skinName = skin.getName();
        final Player onlinePlayer = playerInfo.getOnlinePlayer();

        playerInfo.getPlayerFile().addSkin(skin);
        handler.sendEmbed(
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
}
