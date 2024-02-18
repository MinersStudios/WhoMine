package com.minersstudios.mscore.inventory.recipe.builder;

import org.bukkit.inventory.CampfireRecipe;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a builder for campfire recipe
 *
 * @see RecipeBuilder#campfire()
 * @see RecipeBuilder#campfire(CampfireRecipe)
 */
public final class CampfireRecipeBuilder extends CookingRecipeBuilderImpl<CampfireRecipeBuilder, CampfireRecipe> {

    CampfireRecipeBuilder() {}

    CampfireRecipeBuilder(final @NotNull CampfireRecipe recipe) {
        super(recipe);
    }

    @Contract(" -> new")
    @ApiStatus.OverrideOnly
    @Override
    protected @NotNull CampfireRecipe newRecipe() {
        return new CampfireRecipe(
                this.namespacedKey(),
                this.result(),
                this.ingredient(),
                this.experience(),
                this.cookingTime()
        );
    }
}
