package com.minersstudios.msdecor.commands;

import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.msdecor.MSDecor;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class ReloadCommand {
    private static final TranslatableComponent RELOAD_SUCCESS = translatable("ms.command.msdecor.reload.success");

    public static void runCommand(@NotNull CommandSender sender) {
        long time = System.currentTimeMillis();
        Server server = sender.getServer();
        Iterator<Recipe> crafts = server.recipeIterator();

        while (crafts.hasNext()) {
            Recipe recipe = crafts.next();

            if (
                    recipe instanceof ShapedRecipe shapedRecipe
                    && "msdecor".equals(shapedRecipe.getKey().getNamespace())
            ) {
                server.removeRecipe(shapedRecipe.getKey());
            }
        }

        MSPlugin.getGlobalCache().customDecorRecipes.clear();
        MSDecor.reloadConfigs();
        MSLogger.fine(sender, RELOAD_SUCCESS.args(text(System.currentTimeMillis() - time)));
    }
}
