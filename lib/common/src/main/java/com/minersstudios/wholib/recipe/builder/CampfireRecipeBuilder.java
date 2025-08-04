package com.minersstudios.wholib.recipe.builder;

import com.minersstudios.wholib.recipe.CampfireRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Represents a builder for campfire recipe
 *
 * @see RecipeBuilder#campfire()
 * @see RecipeBuilder#campfire(CampfireRecipe)
 */
public final class CampfireRecipeBuilder extends CookingRecipeBuilderImpl<CampfireRecipeBuilder, CampfireRecipe> {
    private static final Function<CampfireRecipeBuilder, CampfireRecipe> RECIPE_CONSTRUCTOR =
            builder -> CampfireRecipe.of(builder.path(), builder.result());

    CampfireRecipeBuilder() {
        super(RECIPE_CONSTRUCTOR);
    }

    CampfireRecipeBuilder(final @NotNull CampfireRecipe recipe) {
        super(RECIPE_CONSTRUCTOR, recipe);
    }
}
