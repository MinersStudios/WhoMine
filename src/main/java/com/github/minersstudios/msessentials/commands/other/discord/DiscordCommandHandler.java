package com.github.minersstudios.msessentials.commands.other.discord;

import com.github.minersstudios.mscore.command.MSCommand;
import com.github.minersstudios.mscore.command.MSCommandExecutor;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.event.HoverEvent.showText;

@MSCommand(
        command = "discord",
        usage = " ꀑ §cИспользуй: /<command> [параметры]",
        description = "Дискорд команды",
        permissionDefault = PermissionDefault.NOT_OP
)
public class DiscordCommandHandler implements MSCommandExecutor {
    private static final List<String> ARGUMENTS = List.of("link", "unlink");
    private static final String DISCORD_LINK = "https://discord.whomine.net";

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        if (!(sender instanceof Player player)) {
            ChatUtils.sendError(sender, translatable("ms.error.only_player_command"));
            return true;
        }

        PlayerInfo playerInfo = PlayerInfo.fromMap(player);

        if (args.length > 0) {
            switch (args[0]) {
                case "link" -> LinkCommand.runCommand(player, playerInfo);
                case "unlink" -> UnlinkCommand.runCommand(player, playerInfo);
                default -> {
                    return false;
                }
            }
        } else {
            ChatUtils.sendWarning(
                    sender,
                    translatable(
                            "ms.command.discord",
                            text(DISCORD_LINK)
                            .hoverEvent(showText(translatable("ms.link.hover", NamedTextColor.GRAY)))
                            .clickEvent(ClickEvent.openUrl(DISCORD_LINK)),
                            text("/discord link")
                            .hoverEvent(showText(translatable("ms.command.hover.run", NamedTextColor.GRAY)))
                            .clickEvent(ClickEvent.runCommand("/discord link"))
                    )
            );
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        return ARGUMENTS;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return literal("discord")
                .then(literal("link"))
                .then(literal("unlink"))
                .build();
    }
}
