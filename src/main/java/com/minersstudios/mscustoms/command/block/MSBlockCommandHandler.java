package com.minersstudios.mscustoms.command.block;

import com.minersstudios.mscore.command.api.AbstractCommandExecutor;
import com.minersstudios.mscore.command.api.MSCommand;
import com.minersstudios.mscore.utility.Font;
import com.minersstudios.mscustoms.MSCustoms;
import com.minersstudios.mscustoms.custom.block.CustomBlockRegistry;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.utility.MSPlayerUtils;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

@MSCommand(
        command = "msblock",
        usage = " " + Font.Chars.RED_EXCLAMATION_MARK + " §cИспользуй: /<command> [параметры]",
        description = "Прочие команды",
        permission = "msblock.*",
        permissionDefault = PermissionDefault.OP
)
public final class MSBlockCommandHandler extends AbstractCommandExecutor<MSCustoms> {
    private static final List<String> TAB = Arrays.asList("reload", "give");
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
                    case "reload" -> ReloadCommand.runCommand(this.getPlugin(), sender);
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
            case 2 -> args[0].equals("give")
                    ? MSPlayerUtils.getLocalPlayerNames(MSEssentials.singleton())
                    : EMPTY_TAB;
            case 3 -> args[0].equals("give")
                    ? new ObjectArrayList<>(CustomBlockRegistry.keySet())
                    : EMPTY_TAB;
            default -> EMPTY_TAB;
        };
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}
