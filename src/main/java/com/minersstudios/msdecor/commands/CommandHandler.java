package com.minersstudios.msdecor.commands;

import com.minersstudios.mscore.command.MSCommand;
import com.minersstudios.mscore.command.MSCommandExecutor;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.msdecor.customdecor.CustomDecorData;
import com.minersstudios.msdecor.customdecor.Typed;
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
import java.util.List;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

@MSCommand(
        command = "msdecor",
        usage = " ꀑ §cИспользуй: /<command> [параметры]",
        description = "Прочие команды",
        permission = "msdecor.*",
        permissionDefault = PermissionDefault.OP
)
public class CommandHandler implements MSCommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull ... args) {
        if (args.length > 0) {
            switch (args[0]) {
                case "reload" -> {
                    ReloadCommand.runCommand(sender);
                    return true;
                }
                case "give" -> {
                    return GiveCommand.runCommand(sender, args);
                }
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull ... args) {
        List<String> completions = new ArrayList<>();
        switch (args.length) {
            case 1 -> {
                completions.add("reload");
                completions.add("give");
            }
            case 2 -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    completions.add(player.getName());
                }
            }
            case 3 -> completions.addAll(MSPlugin.getGlobalCache().customDecorMap.primaryKeySet());
            case 4 -> {
                CustomDecorData customDecorData = MSPlugin.getGlobalCache().customDecorMap.getByPrimaryKey(args[2]);
                if (customDecorData instanceof Typed typed) {
                    for (var type : typed.getTypes()) {
                        completions.add(type.getNamespacedKey().getKey());
                    }
                }
            }
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
                                        argument("decor id", StringArgumentType.word())
                                        .then(argument("amount", IntegerArgumentType.integer()))
                                )
                        )
                )
                .build();
    }
}
