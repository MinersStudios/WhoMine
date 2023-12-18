package com.minersstudios.msessentials.commands.minecraft.admin.msessentials;

import com.google.common.collect.ImmutableList;
import com.minersstudios.mscore.command.MSCommand;
import com.minersstudios.mscore.command.MSCommandExecutor;
import com.minersstudios.msessentials.MSEssentials;
import com.mojang.brigadier.tree.CommandNode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
                "msessentials.worldteleport",
                "msessentials.setserverspawn"
        },
        permissionParentValues = {
                true,
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
public final class MSEssentialsCommandHandler extends MSCommandExecutor<MSEssentials> {
    private static final List<String> TAB = ImmutableList.of("reload", "updateids", "updatemutes");
    private static final CommandNode<?> COMMAND_NODE =
            literal("msessentials")
            .then(literal("reload"))
            .then(literal("updateids"))
            .then(literal("updatemutes"))
            .build();

    @Override
    public boolean onCommand(
            final @NotNull CommandSender sender,
            final @NotNull Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        return args.length != 0
                && switch (args[0]) {
                    case "reload" -> ReloadCommand.runCommand(this.getPlugin(), sender);
                    case "updateids" -> UpdateIdsCommand.runCommand(this.getPlugin(), sender);
                    case "updatemutes" -> UpdateMutesCommand.runCommand(this.getPlugin(), sender);
                    default -> false;
                };
    }

    @Override
    public @NotNull List<String> onTabComplete(
            final @NotNull CommandSender sender,
            final @NotNull Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        return args.length == 1 ? TAB : EMPTY_TAB;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}
