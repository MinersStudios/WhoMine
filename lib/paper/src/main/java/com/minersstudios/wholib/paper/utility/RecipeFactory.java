package com.minersstudios.wholib.paper.utility;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

public class RecipeFactory {

    public static @NotNull ShapedRecipe shaped(final @NotNull com.minersstudios.wholib.recipe.ShapedRecipe recipe) {
        final ShapedRecipe shapedRecipe = new ShapedRecipe(
                ApiConverter.apiToBukkit(recipe.getResourceKey()),
                recipe.getResult(ItemStack.class)
        );

        shapedRecipe.shape(recipe.getShape());

        //TODO: Implement the rest of the method

        return shapedRecipe;
    }
}
