package com.minersstudios.mscore.inventory.recipe.builder;

import org.bukkit.inventory.SmokingRecipe;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a builder for smoking recipe
 *
 * @see RecipeBuilder#smoking()
 * @see RecipeBuilder#smoking(SmokingRecipe)
 */
public final class SmokingRecipeBuilder extends CookingRecipeBuilderImpl<SmokingRecipeBuilder, SmokingRecipe> {

    SmokingRecipeBuilder() {}

    SmokingRecipeBuilder(final @NotNull SmokingRecipe recipe) {
        super(recipe);
    }

    @Contract(" -> new")
    @ApiStatus.OverrideOnly
    @Override
    protected @NotNull SmokingRecipe newRecipe() throws IllegalStateException {
        return new SmokingRecipe(
                this.namespacedKey(),
                this.result(),
                this.ingredient(),
                this.experience(),
                this.cookingTime()
        );
    }
}
