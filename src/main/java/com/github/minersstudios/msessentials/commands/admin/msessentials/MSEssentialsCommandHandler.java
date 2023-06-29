package com.github.minersstudios.msessentials.commands.admin.msessentials;

import com.github.minersstudios.mscore.command.MSCommand;
import com.github.minersstudios.mscore.command.MSCommandExecutor;
import com.mojang.brigadier.tree.CommandNode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

@MSCommand(
        command = "msessentials",
        usage = " ꀑ §cИспользуй: /<command> [параметры]",
        description = "Прочие команды",
        permission = "msessentials.*",
        permissionDefault = PermissionDefault.OP,
        permissionParentKeys = {
                "msessentials.player.*",
                "msessentials.ban",
                "msessentials.mute",
                "msessentials.kick",
                "msessentials.maplocation",
                "msessentials.whitelist",
                "msessentials.teleporttolastdeathlocation",
                "msessentials.worldteleport"
        },
        permissionParentValues = {
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true
        }
)
public class MSEssentialsCommandHandler implements MSCommandExecutor {

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        if (args.length > 0) {
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "reload" -> ReloadCommand.runCommand(sender);
                case "updateids" -> UpdateIdsCommand.runCommand(sender);
                case "updatemutes" -> UpdateMutesCommand.runCommand(sender);
                default -> {
                    return false;
                }
            }
        } else {
            return false;
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
        return List.of("reload", "updateids", "updatemutes");
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return literal("msessentials")
                .then(literal("reload"))
                .then(literal("updateids"))
                .then(literal("updatemutes"))
                .build();
    }
}
