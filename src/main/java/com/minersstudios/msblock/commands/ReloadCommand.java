package com.minersstudios.msblock.commands;

import com.minersstudios.msblock.MSBlock;
import com.minersstudios.mscore.Cache;
import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.plugin.MSPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class ReloadCommand {

    public static void runCommand(@NotNull CommandSender sender) {
        long time = System.currentTimeMillis();
        Server server = sender.getServer();
        Iterator<Recipe> crafts = server.recipeIterator();

        while (crafts.hasNext()) {
            Recipe recipe = crafts.next();

            if (
                    recipe instanceof ShapedRecipe shapedRecipe
                    && "msblock".equals(shapedRecipe.getKey().getNamespace())
            ) {
                server.removeRecipe(shapedRecipe.getKey());
            }
        }

        Cache cache = MSPlugin.getGlobalCache();
        cache.customBlockMap.clear();
        cache.cachedNoteBlockData.clear();
        cache.customBlockRecipes.clear();
        MSBlock.reloadConfigs();
        MSLogger.fine(
                sender,
                Component.translatable(
                        "ms.command.msblock.reload.success",
                        Component.text(System.currentTimeMillis() - time)
                )
        );
    }
}
