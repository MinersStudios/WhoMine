package com.minersstudios.wholib.recipe.builder;

import com.minersstudios.wholib.recipe.BlastingRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Represents a builder for blasting recipe
 *
 * @see RecipeBuilder#blasting()
 * @see RecipeBuilder#blasting(BlastingRecipe)
 */
public final class BlastingRecipeBuilder extends CookingRecipeBuilderImpl<BlastingRecipeBuilder, BlastingRecipe> {
    private static final Function<BlastingRecipeBuilder, BlastingRecipe> RECIPE_CONSTRUCTOR =
            builder -> BlastingRecipe.of(builder.path(), builder.result());

    BlastingRecipeBuilder() {
        super(RECIPE_CONSTRUCTOR);
    }

    BlastingRecipeBuilder(final @NotNull BlastingRecipe recipe) {
        super(RECIPE_CONSTRUCTOR, recipe);
    }
}
