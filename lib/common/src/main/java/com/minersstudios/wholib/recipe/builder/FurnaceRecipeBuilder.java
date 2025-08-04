package com.minersstudios.wholib.recipe.builder;

import com.minersstudios.wholib.recipe.FurnaceRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Represents a builder for furnace recipe
 *
 * @see RecipeBuilder#furnace()
 * @see RecipeBuilder#furnace(FurnaceRecipe)
 */
public final class FurnaceRecipeBuilder extends CookingRecipeBuilderImpl<FurnaceRecipeBuilder, FurnaceRecipe> {
    private static final Function<FurnaceRecipeBuilder, FurnaceRecipe> RECIPE_CONSTRUCTOR =
            builder -> FurnaceRecipe.of(builder.path(), builder.result());

    FurnaceRecipeBuilder() {
        super(RECIPE_CONSTRUCTOR);
    }

    FurnaceRecipeBuilder(final @NotNull FurnaceRecipe recipe) {
        super(RECIPE_CONSTRUCTOR, recipe);
    }
}
