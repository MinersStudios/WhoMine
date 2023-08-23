package com.minersstudios.mscore.commands;

import com.minersstudios.mscore.command.MSCommand;
import com.minersstudios.mscore.command.MSCommandExecutor;
import com.mojang.brigadier.tree.CommandNode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

@MSCommand(
        command = "mscore",
        usage = " ꀑ §cИспользуй: /<command> [параметры]",
        description = "Прочие команды",
        permission = "mscore.*",
        permissionDefault = PermissionDefault.OP
)
public class MSCoreCommandHandler implements MSCommandExecutor {
    private static final List<String> TAB = List.of("reloadlanguage", "reloadconfig");
    private static final CommandNode<?> COMMAND_NODE =
            literal("mscore")
            .then(literal("reloadlanguage"))
            .then(literal("reloadconfig"))
            .build();

    @Override
    public boolean onCommand(
            final @NotNull CommandSender sender,
            final @NotNull Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        return switch (args[0]) {
            case "reloadlanguage" -> ReloadLanguageCommand.runCommand(sender);
            case "reloadconfig" -> ReloadConfigCommand.runCommand(sender);
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
