package com.github.minersstudios.msessentials.commands.other.discord;

import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
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

        if (id == -1) {
            ChatUtils.sendWarning(sender, translatable("ms.command.discord.unlink.no_links"));
            return;
        }

        JDA jda = DiscordSRV.getPlugin().getJda();
        User user = jda.getUserById(id);

        if (user != null) {
            user.openPrivateChannel().complete().sendMessage(
                    renderTranslation(
                            translatable(
                                    "ms.command.discord.unlink.discord.success",
                                    playerInfo.getDefaultName(),
                                    text(sender.getName())
                            )
                    )
            ).queue();
        }

        ChatUtils.sendFine(
                sender,
                translatable(
                        "ms.command.discord.unlink.minecraft.success",
                        user == null
                        ? text(id)
                        : text(user.getName())
                )
        );
    }
}
