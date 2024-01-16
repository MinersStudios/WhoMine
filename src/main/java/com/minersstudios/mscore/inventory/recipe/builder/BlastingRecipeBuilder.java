package com.minersstudios.mscore.inventory.recipe.builder;

import org.bukkit.inventory.BlastingRecipe;
import org.jetbrains.annotations.NotNull;

public final class BlastingRecipeBuilder extends CookingRecipeBuilderImpl<BlastingRecipeBuilder, BlastingRecipe> {

    BlastingRecipeBuilder() {}

    BlastingRecipeBuilder(final @NotNull BlastingRecipe recipe) {
        super(recipe);
    }

    @Override
    protected @NotNull BlastingRecipe newRecipe() throws IllegalStateException {
        return new BlastingRecipe(
                this.namespacedKey,
                this.result,
                this.ingredient,
                this.experience,
                this.cookingTime
        );
    }
}
