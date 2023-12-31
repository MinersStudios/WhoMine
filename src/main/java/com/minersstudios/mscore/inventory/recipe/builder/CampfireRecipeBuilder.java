package com.minersstudios.mscore.inventory.recipe.builder;

import org.bukkit.inventory.CampfireRecipe;
import org.jetbrains.annotations.NotNull;

public final class CampfireRecipeBuilder extends CookingRecipeBuilderImpl<CampfireRecipeBuilder, CampfireRecipe> {

    @Override
    protected @NotNull CampfireRecipe newRecipe() throws IllegalStateException {
        return new CampfireRecipe(
                this.namespacedKey,
                this.result,
                this.ingredient,
                this.experience,
                this.cookingTime
        );
    }
}
