package com.github.minersstudios.msdecor.commands;

import com.github.minersstudios.mscore.MSCore;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.msdecor.MSDecor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class ReloadCommand {

	public static void runCommand(@NotNull CommandSender sender) {
		long time = System.currentTimeMillis();
		Iterator<Recipe> crafts = Bukkit.recipeIterator();
		while (crafts.hasNext()) {
			Recipe recipe = crafts.next();
			if (recipe instanceof ShapedRecipe shapedRecipe && shapedRecipe.getKey().getNamespace().equals("msdecor")) {
				Bukkit.removeRecipe(shapedRecipe.getKey());
			}
		}
		MSCore.getCache().customDecorRecipes.clear();
		MSDecor.reloadConfigs();
		if (MSDecor.getInstance().isEnabled()) {
			ChatUtils.sendFine(sender, Component.text("Плагин был успешно перезагружён за " + (System.currentTimeMillis() - time) + "ms"));
			return;
		}
		ChatUtils.sendError(sender, Component.text("Плагин был перезагружён неудачно"));
	}
}
