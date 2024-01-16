package com.minersstudios.mscore.inventory.recipe.builder;

import org.bukkit.inventory.CampfireRecipe;
import org.jetbrains.annotations.NotNull;

public final class CampfireRecipeBuilder extends CookingRecipeBuilderImpl<CampfireRecipeBuilder, CampfireRecipe> {

    CampfireRecipeBuilder() {}

    CampfireRecipeBuilder(final @NotNull CampfireRecipe recipe) {
        super(recipe);
    }

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
