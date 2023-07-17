package com.github.minersstudios.msessentials.commands.other.discord;

import com.github.minersstudios.mscore.logger.MSLogger;
import com.github.minersstudios.msessentials.MSEssentials;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.github.minersstudios.msessentials.utils.MessageUtils;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.github.minersstudios.mscore.config.LanguageFile.renderTranslation;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class UnlinkCommand {

    public static void runCommand(
            @NotNull Player sender,
            @NotNull PlayerInfo playerInfo
    ) {
        long id = playerInfo.unlinkDiscord();

        if (id == -1L) {
            MSLogger.warning(sender, translatable("ms.command.discord.unlink.no_links"));
            return;
        }

        MSEssentials.getInstance().runTaskAsync(
                () -> {
                    JDA jda = DiscordUtil.getJda();
                    User user = jda.getUserById(id);

                    if (user != null) {
                        user.openPrivateChannel().complete().sendMessageEmbeds(
                                MessageUtils.craftEmbed(
                                        renderTranslation(
                                                translatable(
                                                        "ms.command.discord.unlink.discord.success",
                                                        playerInfo.getDefaultName(),
                                                        text(sender.getName())
                                                )
                                        ))
                        ).queue();
                    }

                    MSLogger.fine(
                            sender,
                            translatable(
                                    "ms.command.discord.unlink.minecraft.success",
                                    user == null
                                    ? text(id)
                                    : text(user.getName())
                            )
                    );
                }
        );
    }
}
