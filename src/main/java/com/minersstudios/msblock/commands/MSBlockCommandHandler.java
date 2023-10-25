package com.minersstudios.msblock.commands;

import com.google.common.collect.ImmutableList;
import com.minersstudios.msblock.api.CustomBlockRegistry;
import com.minersstudios.mscore.command.MSCommand;
import com.minersstudios.mscore.command.MSCommandExecutor;
import com.minersstudios.msessentials.util.MSPlayerUtils;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

@MSCommand(
        command = "msblock",
        usage = " ꀑ §cИспользуй: /<command> [параметры]",
        description = "Прочие команды",
        permission = "msblock.*",
        permissionDefault = PermissionDefault.OP
)
public final class MSBlockCommandHandler implements MSCommandExecutor {
    private static final List<String> TAB = ImmutableList.of("reload", "give");
    private static final CommandNode<?> COMMAND_NODE =
            literal("msblock")
            .then(literal("reload"))
            .then(
                    literal("give")
                    .then(
                            argument("id/nickname", StringArgumentType.word())
                            .then(
                                    argument("block id", StringArgumentType.word())
                                    .then(argument("amount", IntegerArgumentType.integer()))
                            )
                    )
            )
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
                    case "reload" -> ReloadCommand.runCommand(sender);
                    case "give" -> GiveCommand.runCommand(sender, args);
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
        return switch (args.length) {
            case 1 -> TAB;
            case 2 -> MSPlayerUtils.getLocalPlayerNames();
            case 3 -> new ArrayList<>(CustomBlockRegistry.keySet());
            default -> EMPTY_TAB;
        };
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}
