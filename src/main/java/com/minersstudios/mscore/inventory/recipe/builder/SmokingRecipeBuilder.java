package com.minersstudios.mscore.inventory.recipe.builder;

import org.bukkit.inventory.SmokingRecipe;
import org.jetbrains.annotations.NotNull;

public final class SmokingRecipeBuilder extends CookingRecipeBuilderImpl<SmokingRecipeBuilder, SmokingRecipe> {

    SmokingRecipeBuilder() {}

    SmokingRecipeBuilder(final @NotNull SmokingRecipe recipe) {
        super(recipe);
    }

    @Override
    public @NotNull SmokingRecipe newRecipe() throws IllegalStateException {
        return new SmokingRecipe(
                this.namespacedKey,
                this.result,
                this.ingredient,
                this.experience,
                this.cookingTime
        );
    }
}
