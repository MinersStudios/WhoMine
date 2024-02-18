package com.minersstudios.mscore.inventory.recipe.builder;

import org.bukkit.inventory.SmithingTransformRecipe;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a builder for smithing transform recipe
 *
 * @see RecipeBuilder#smithingTransform()
 * @see RecipeBuilder#smithingTransform(SmithingTransformRecipe)
 */
public final class SmithingTransformRecipeBuilder extends SmithingRecipeBuilderImpl<SmithingTransformRecipeBuilder, SmithingTransformRecipe> {

    SmithingTransformRecipeBuilder() {}

    SmithingTransformRecipeBuilder(final @NotNull SmithingTransformRecipe recipe) {
        super(recipe);
    }

    @Contract(" -> new")
    @ApiStatus.OverrideOnly
    @Override
    protected @NotNull SmithingTransformRecipe newRecipe() {
        return new SmithingTransformRecipe(
                this.namespacedKey(),
                this.result(),
                this.template(),
                this.base(),
                this.addition(),
                this.copyNbt()
        );
    }
}
