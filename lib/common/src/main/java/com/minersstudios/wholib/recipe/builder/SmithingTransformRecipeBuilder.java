package com.minersstudios.wholib.recipe.builder;

import com.minersstudios.wholib.recipe.SmithingTransformRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Represents a builder for smithing transform recipe
 *
 * @see RecipeBuilder#smithingTransform()
 * @see RecipeBuilder#smithingTransform(SmithingTransformRecipe)
 */
public final class SmithingTransformRecipeBuilder extends SmithingRecipeBuilderImpl<SmithingTransformRecipeBuilder, SmithingTransformRecipe> {
    private static final Function<SmithingTransformRecipeBuilder, SmithingTransformRecipe> RECIPE_CONSTRUCTOR =
            builder -> SmithingTransformRecipe.of(builder.path(), builder.result(), builder.template(), builder.base(), builder.addition());

    SmithingTransformRecipeBuilder() {
        super(RECIPE_CONSTRUCTOR);
    }

    SmithingTransformRecipeBuilder(final @NotNull SmithingTransformRecipe recipe) {
        super(RECIPE_CONSTRUCTOR, recipe);
    }
}
