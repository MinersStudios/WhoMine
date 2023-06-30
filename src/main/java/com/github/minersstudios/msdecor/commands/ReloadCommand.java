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

            if (
                    recipe instanceof ShapedRecipe shapedRecipe
                    && "msdecor".equals(shapedRecipe.getKey().getNamespace())
            ) {
                Bukkit.removeRecipe(shapedRecipe.getKey());
            }
        }

        MSCore.getCache().customDecorRecipes.clear();
        MSDecor.reloadConfigs();
        ChatUtils.sendFine(
                sender,
                Component.translatable(
                        "ms.command.msdecor.reload.success",
                        Component.text(System.currentTimeMillis() - time)
                )
        );
    }
}
