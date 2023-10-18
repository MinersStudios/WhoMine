package com.minersstudios.mscore.inventory.recipe;

import org.bukkit.inventory.BlastingRecipe;
import org.jetbrains.annotations.NotNull;

public final class BlastingRecipeBuilder extends CookingRecipeBuilderImpl<BlastingRecipeBuilder, BlastingRecipe> {

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
