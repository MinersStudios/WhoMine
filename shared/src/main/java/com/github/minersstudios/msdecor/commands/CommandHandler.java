package com.github.minersstudios.msdecor.commands;

import com.github.minersstudios.mscore.MSCore;
import com.github.minersstudios.mscore.command.MSCommand;
import com.github.minersstudios.mscore.command.MSCommandExecutor;
import com.github.minersstudios.msdecor.customdecor.CustomDecorData;
import com.github.minersstudios.msdecor.customdecor.Typed;
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
import java.util.Locale;

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
			String utilsCommand = args[0].toLowerCase(Locale.ROOT);
			if ("reload".equalsIgnoreCase(utilsCommand)) {
				ReloadCommand.runCommand(sender);
				return true;
			}
			if ("give".equalsIgnoreCase(utilsCommand)) {
				return GiveCommand.runCommand(sender, args);
			}
		}
		return false;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull ... args) {
		List<String> completions = new ArrayList<>();
		switch (args.length) {
			case 1 ->  {
				completions.add("reload");
				completions.add("give");
			}
			case 2 -> {
				for (Player player : Bukkit.getOnlinePlayers()) {
					completions.add(player.getName());
				}
			}
			case 3 -> completions.addAll(MSCore.getCache().customDecorMap.primaryKeySet());
			case 4 -> {
				CustomDecorData customDecorData = MSCore.getCache().customDecorMap.getByPrimaryKey(args[2]);
				if (customDecorData instanceof Typed typed) {
					for (Typed.Type type : typed.getTypes()) {
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
