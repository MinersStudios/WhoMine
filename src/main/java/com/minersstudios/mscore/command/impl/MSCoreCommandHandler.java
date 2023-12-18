package com.minersstudios.mscore.command.impl;

import com.google.common.collect.ImmutableList;
import com.minersstudios.mscore.MSCore;
import com.minersstudios.mscore.command.api.Command;
import com.minersstudios.mscore.command.api.CommandExecutor;
import com.minersstudios.mscore.utility.Font;
import com.mojang.brigadier.tree.CommandNode;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

@Command(
        command = "mscore",
        usage = " " + Font.Chars.RED_EXCLAMATION_MARK + " §cИспользуй: /<command> [параметры]",
        description = "Прочие команды",
        permission = "mscore.*",
        permissionDefault = PermissionDefault.OP
)
public final class MSCoreCommandHandler extends CommandExecutor<MSCore> {
    private static final List<String> TAB = ImmutableList.of("reloadconfig");
    private static final CommandNode<?> COMMAND_NODE =
            literal("mscore")
            .then(literal("reloadconfig"))
            .build();

    @Override
    public boolean onCommand(
            final @NotNull CommandSender sender,
            final @NotNull org.bukkit.command.Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        return switch (args[0]) {
            case "reloadconfig" -> ReloadConfigCommand.runCommand(sender);
            default -> false;
        };
    }

    @Override
    public @NotNull List<String> onTabComplete(
            final @NotNull CommandSender sender,
            final @NotNull org.bukkit.command.Command command,
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