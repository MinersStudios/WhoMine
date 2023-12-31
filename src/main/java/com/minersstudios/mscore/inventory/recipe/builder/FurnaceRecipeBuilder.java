package com.minersstudios.mscore.inventory.recipe.builder;

import org.bukkit.inventory.FurnaceRecipe;
import org.jetbrains.annotations.NotNull;

public final class FurnaceRecipeBuilder extends CookingRecipeBuilderImpl<FurnaceRecipeBuilder, FurnaceRecipe> {

    @Override
    protected @NotNull FurnaceRecipe newRecipe() throws IllegalStateException {
        return new FurnaceRecipe(
                this.namespacedKey,
                this.result,
                this.ingredient,
                this.experience,
                this.cookingTime
        );
    }
}
