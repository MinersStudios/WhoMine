package com.minersstudios.mscore.inventory.recipe.builder;

import org.bukkit.inventory.FurnaceRecipe;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a builder for furnace recipe
 *
 * @see RecipeBuilder#furnace()
 * @see RecipeBuilder#furnace(FurnaceRecipe)
 */
public final class FurnaceRecipeBuilder extends CookingRecipeBuilderImpl<FurnaceRecipeBuilder, FurnaceRecipe> {

    FurnaceRecipeBuilder() {}

    FurnaceRecipeBuilder(final @NotNull FurnaceRecipe recipe) {
        super(recipe);
    }

    @Contract(" -> new")
    @ApiStatus.OverrideOnly
    @Override
    protected @NotNull FurnaceRecipe newRecipe() throws IllegalStateException {
        return new FurnaceRecipe(
                this.namespacedKey(),
                this.result(),
                this.ingredient(),
                this.experience(),
                this.cookingTime()
        );
    }
}
