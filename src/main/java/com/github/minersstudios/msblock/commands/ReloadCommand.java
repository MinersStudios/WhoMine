package com.github.minersstudios.msblock.commands;

import com.github.minersstudios.msblock.MSBlock;
import com.github.minersstudios.mscore.Cache;
import com.github.minersstudios.mscore.MSCore;
import com.github.minersstudios.mscore.utils.ChatUtils;
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
			if (recipe instanceof ShapedRecipe shapedRecipe && shapedRecipe.getKey().getNamespace().equals("msblock")) {
				Bukkit.removeRecipe(shapedRecipe.getKey());
			}
		}
		Cache cache = MSCore.getCache();
		cache.customBlockMap.clear();
		cache.cachedNoteBlockData.clear();
		cache.customBlockRecipes.clear();
		MSBlock.reloadConfigs();
		if (MSBlock.getInstance().isEnabled()) {
			ChatUtils.sendFine(sender, Component.text("Плагин был успешно перезагружён за " + (System.currentTimeMillis() - time) + "ms"));
			return;
		}
		ChatUtils.sendError(sender, Component.text("Плагин был перезагружён неудачно"));
	}
}
