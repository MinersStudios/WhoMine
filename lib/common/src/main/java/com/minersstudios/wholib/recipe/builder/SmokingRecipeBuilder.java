package com.minersstudios.wholib.recipe.builder;

import com.minersstudios.wholib.recipe.SmokingRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Represents a builder for smoking recipe
 *
 * @see RecipeBuilder#smoking()
 * @see RecipeBuilder#smoking(SmokingRecipe)
 */
public final class SmokingRecipeBuilder extends CookingRecipeBuilderImpl<SmokingRecipeBuilder, SmokingRecipe> {
    private static final Function<SmokingRecipeBuilder, SmokingRecipe> RECIPE_CONSTRUCTOR =
            builder -> SmokingRecipe.of(builder.path(), builder.result());

    SmokingRecipeBuilder() {
        super(RECIPE_CONSTRUCTOR);
    }

    SmokingRecipeBuilder(final @NotNull SmokingRecipe recipe) {
        super(RECIPE_CONSTRUCTOR, recipe);
    }
}
