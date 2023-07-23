package com.minersstudios.msitem.commands;

import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.plugin.GlobalCache;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.msitem.MSItem;
import net.kyori.adventure.key.Keyed;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class ReloadCommand {
    private static final TranslatableComponent RELOAD_SUCCESS = translatable("ms.command.msitem.reload.success");

    public static void runCommand(@NotNull CommandSender sender) {
        long time = System.currentTimeMillis();
        Server server = sender.getServer();
        Iterator<Recipe> crafts = server.recipeIterator();
        GlobalCache cache = MSPlugin.getGlobalCache();

        while (crafts.hasNext()) {
            Recipe recipe = crafts.next();

            if (
                    recipe instanceof Keyed keyed
                    && keyed.key().namespace().equals("msitems")
            ) {
                server.removeRecipe(new NamespacedKey("msitems", keyed.key().value()));
            }
        }

        cache.customItemMap.clear();
        cache.customItemRecipes.clear();
        cache.renameableItemMap.clear();
        cache.renameableItemsMenu.clear();
        MSItem.reloadConfigs();
        MSLogger.fine(sender, RELOAD_SUCCESS.args(text(System.currentTimeMillis() - time)));
    }
}
