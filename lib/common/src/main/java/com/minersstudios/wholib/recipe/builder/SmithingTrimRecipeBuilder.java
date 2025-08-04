package com.minersstudios.wholib.recipe.builder;

import com.minersstudios.wholib.recipe.SmithingTrimRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Represents a builder for smithing trim recipe
 *
 * @see RecipeBuilder#smithingTrim()
 * @see RecipeBuilder#smithingTrim(SmithingTrimRecipe)
 */
public final class SmithingTrimRecipeBuilder extends SmithingRecipeBuilderImpl<SmithingTrimRecipeBuilder, SmithingTrimRecipe> {
    private static final Function<SmithingTrimRecipeBuilder, SmithingTrimRecipe> RECIPE_CONSTRUCTOR =
            builder -> SmithingTrimRecipe.of(builder.path(), builder.result(), builder.template(), builder.base(), builder.addition());

    SmithingTrimRecipeBuilder() {
        super(RECIPE_CONSTRUCTOR);
    }

    SmithingTrimRecipeBuilder(final @NotNull SmithingTrimRecipe recipe) {
        super(RECIPE_CONSTRUCTOR, recipe);
    }
}
