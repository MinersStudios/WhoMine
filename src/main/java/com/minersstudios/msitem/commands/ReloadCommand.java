package com.minersstudios.msitem.commands;

import com.minersstudios.mscore.Cache;
import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.msitem.MSItem;
import net.kyori.adventure.key.Keyed;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class ReloadCommand {

    public static void runCommand(@NotNull CommandSender sender) {
        long time = System.currentTimeMillis();
        Server server = sender.getServer();
        Iterator<Recipe> crafts = server.recipeIterator();
        Cache cache = MSPlugin.getGlobalCache();

        while (crafts.hasNext()) {
            Recipe recipe = crafts.next();

            if (
                    recipe instanceof Keyed keyed
                    && keyed.key().namespace().equals("msitem")
            ) {
                server.removeRecipe(new NamespacedKey(MSItem.getInstance(), keyed.key().value()));
            }
        }

        cache.customItemMap.clear();
        cache.customItemRecipes.clear();
        cache.renameableItemMap.clear();
        cache.renameableItemsMenu.clear();
        MSItem.reloadConfigs();
        MSLogger.fine(
                sender,
                Component.translatable(
                        "ms.command.msitem.reload.success",
                        Component.text(System.currentTimeMillis() - time)
                )
        );
    }
}
