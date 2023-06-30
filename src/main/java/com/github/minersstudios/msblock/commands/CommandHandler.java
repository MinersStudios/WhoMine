package com.github.minersstudios.msblock.commands;

import com.github.minersstudios.mscore.command.MSCommand;
import com.github.minersstudios.mscore.command.MSCommandExecutor;
import com.github.minersstudios.mscore.MSCore;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

@MSCommand(
        command = "msblock",
        usage = " ꀑ §cИспользуй: /<command> [параметры]",
        description = "Прочие команды",
        permission = "msblock.*",
        permissionDefault = PermissionDefault.OP
)
public class CommandHandler implements MSCommandExecutor {

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        if (args.length < 1) return false;
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "reload" -> {
                ReloadCommand.runCommand(sender);
                return true;
            }
            case "give" -> {
                return GiveCommand.runCommand(sender, args);
            }
            default -> {
                return false;
            }
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        List<String> completions = new ArrayList<>();
        switch (args.length) {
            case 1 -> {
                completions.add("reload");
                completions.add("give");
            }
            case 2 -> Bukkit.getOnlinePlayers().forEach(player -> completions.add(player.getName()));
            case 3 -> completions.addAll(MSCore.getCache().customBlockMap.primaryKeySet());
        }
        return completions;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return literal("msblock")
                .then(literal("reload"))
                .then(
                        literal("give")
                        .then(
                                argument("nametag", StringArgumentType.word())
                                .then(
                                        argument("block id", StringArgumentType.word())
                                        .then(argument("amount", IntegerArgumentType.integer()))
                                )
                        )
                )
                .build();
    }
}
