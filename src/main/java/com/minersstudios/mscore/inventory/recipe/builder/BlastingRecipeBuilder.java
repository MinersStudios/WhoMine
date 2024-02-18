package com.minersstudios.mscore.inventory.recipe.builder;

import org.bukkit.inventory.BlastingRecipe;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a builder for blasting recipe
 *
 * @see RecipeBuilder#blasting()
 * @see RecipeBuilder#blasting(BlastingRecipe)
 */
public final class BlastingRecipeBuilder extends CookingRecipeBuilderImpl<BlastingRecipeBuilder, BlastingRecipe> {

    BlastingRecipeBuilder() {}

    BlastingRecipeBuilder(final @NotNull BlastingRecipe recipe) {
        super(recipe);
    }

    @Contract(" -> new")
    @ApiStatus.OverrideOnly
    @Override
    protected @NotNull BlastingRecipe newRecipe() {
        return new BlastingRecipe(
                this.namespacedKey(),
                this.result(),
                this.ingredient(),
                this.experience(),
                this.cookingTime()
        );
    }
}
