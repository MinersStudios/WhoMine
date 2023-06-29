package com.github.minersstudios.msitems.commands;

import com.github.minersstudios.mscore.MSCore;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.msitems.items.CustomItem;
import com.github.minersstudios.msitems.items.RenameableItem;
import com.github.minersstudios.msitems.items.Typed;
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

			int amount = args.length == 4 && args[3].matches("\\d+")
					? Integer.parseInt(args[3])
					: args.length == 5 && args[4].matches("\\d+")
					? Integer.parseInt(args[4])
					: 1;
			if (player == null) {
				ChatUtils.sendError(sender, Component.text("Данный игрок не на сервере!"));
				return true;
			}

			ItemStack itemStack;
			RenameableItem renameableItem = MSCore.getCache().renameableItemMap.getByPrimaryKey(args[2]);
			CustomItem customItem = MSCore.getCache().customItemMap.getByPrimaryKey(args[2]);
			if (customItem == null) {
				if (renameableItem == null) {
					ChatUtils.sendError(sender, Component.text("Такого предмета не существует!"));
					return true;
				} else {
					itemStack = renameableItem.getResultItemStack();
				}
			} else {
				if (
						customItem instanceof Typed typed
						&& args.length == 4
						&& !args[3].matches("\\d+")
				) {
					for (Typed.Type type : typed.getTypes()) {
						if (type.getNamespacedKey().getKey().equals(args[3])) {
							customItem = typed.createCustomItem(type);
						}
					}
				}
				itemStack = customItem.getItemStack();
			}

			itemStack.setAmount(amount);
			player.getInventory().addItem(itemStack);
			ChatUtils.sendInfo(sender, Component.text("Выдано " + amount + " " + ChatUtils.serializePlainComponent(Objects.requireNonNull(itemStack.displayName())) + " Игроку : " + player.getName()));
			return true;
		}
		ChatUtils.sendWarning(sender, Component.text("Ник не может состоять менее чем из 3 символов!"));
		return true;
	}
}
