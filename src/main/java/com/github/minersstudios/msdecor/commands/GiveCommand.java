package com.github.minersstudios.msdecor.commands;

import com.github.minersstudios.mscore.MSCore;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.msdecor.customdecor.CustomDecorData;
import com.github.minersstudios.msdecor.customdecor.Typed;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class GiveCommand {

	public static boolean runCommand(@NotNull CommandSender sender, String @NotNull ... args) {
		if (args.length < 3) return false;
		if (args[1].length() > 2) {
			Player player = Bukkit.getPlayer(args[1]);

			int amount = args.length == 4 && args[3].matches("[0-99]+")
					? Integer.parseInt(args[3])
					: args.length == 5 && args[4].matches("[0-99]+")
					? Integer.parseInt(args[4])
					: 1;
			if (player == null) {
				ChatUtils.sendError(sender, Component.text("Данный игрок не на сервере!"));
				return true;
			}

			CustomDecorData customDecorData = MSCore.getCache().customDecorMap.getByPrimaryKey(args[2]);
			if (customDecorData == null) {
				ChatUtils.sendError(sender, Component.text("Такого декора не существует!"));
				return true;
			}

			if (
					customDecorData instanceof Typed typed
					&& args.length == 4
					&& !args[3].matches("[0-99]+")
			) {
				for (Typed.Type type : typed.getTypes()) {
					if (type.getNamespacedKey().getKey().equals(args[3])) {
						customDecorData = typed.createCustomDecorData(type);
					}
				}
			}

			ItemStack itemStack = customDecorData.getItemStack();
			itemStack.setAmount(amount);
			player.getInventory().addItem(itemStack);
			ChatUtils.sendInfo(sender, Component.text("Выдано " + amount + " " + ChatUtils.serializePlainComponent(Objects.requireNonNull(itemStack.displayName())) + " Игроку : " + player.getName()));
			return true;
		}
		ChatUtils.sendWarning(sender, Component.text("Ник не может состоять менее чем из 3 символов!"));
		return true;
	}
}
