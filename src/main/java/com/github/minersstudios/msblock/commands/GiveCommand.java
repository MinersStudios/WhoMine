package com.github.minersstudios.msblock.commands;

import com.github.minersstudios.msblock.customblock.CustomBlockData;
import com.github.minersstudios.mscore.MSCore;
import com.github.minersstudios.mscore.utils.ChatUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GiveCommand {

	public static boolean runCommand(@NotNull CommandSender sender, String @NotNull ... args) {
		if (args.length < 3) return false;
		if (args[1].length() > 2) {
			Player player = Bukkit.getPlayer(args[1]);
			CustomBlockData customBlockData = MSCore.getCache().customBlockMap.getByPrimaryKey(args[2]);
			int amount = args.length == 4 && args[3].matches("[0-99]+")
					? Integer.parseInt(args[3])
					: 1;
			if (player == null) {
				ChatUtils.sendError(sender, Component.text("Данный игрок не на сервере!"));
				return true;
			}
			if (customBlockData == null) {
				ChatUtils.sendError(sender, Component.text("Такого блока не существует!"));
				return true;
			}
			ItemStack itemStack = customBlockData.craftItemStack();
			itemStack.setAmount(amount);
			player.getInventory().addItem(itemStack);
			ChatUtils.sendInfo(sender, Component.text("Выдано " + amount + " [" + customBlockData.getItemName() + "] Игроку : " + player.getName()));
			return true;
		}
		ChatUtils.sendWarning(sender, Component.text("Ник не может состоять менее чем из 3 символов!"));
		return true;
	}
}
