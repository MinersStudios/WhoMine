package com.minersstudios.msitem.commands;

import com.minersstudios.mscore.command.MSCommand;
import com.minersstudios.mscore.command.MSCommandExecutor;
import com.minersstudios.msessentials.util.MSPlayerUtils;
import com.minersstudios.msitem.item.CustomItemType;
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
        command = "msitem",
        usage = " ꀑ §cИспользуй: /<command> [параметры]",
        description = "Прочие команды",
        permission = "msitem.*",
        permissionDefault = PermissionDefault.OP
)
public class MSItemCommandHandler implements MSCommandExecutor {
    private static final List<String> TAB = List.of("reload", "give");
    private static final CommandNode<?> COMMAND_NODE = literal("msitem")
            .then(literal("reload"))
            .then(
                    literal("give")
                    .then(
                            argument("id/nickname", StringArgumentType.word())
                            .then(
                                    argument("item id", StringArgumentType.word())
                                    .then(argument("amount", IntegerArgumentType.integer()))
                            )
                    )
            )
            .build();

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
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
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        return switch (args.length) {
            case 1 -> TAB;
            case 2 -> MSPlayerUtils.getLocalPlayerNames();
            case 3 -> new ArrayList<>(CustomItemType.keySet());
            default -> EMPTY_TAB;
        };
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}
