package com.minersstudios.mscore.inventory.recipe.builder;

import org.bukkit.inventory.SmithingTrimRecipe;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a builder for smithing trim recipe
 *
 * @see RecipeBuilder#smithingTrim()
 * @see RecipeBuilder#smithingTrim(SmithingTrimRecipe)
 */
public final class SmithingTrimRecipeBuilder extends SmithingRecipeBuilderImpl<SmithingTrimRecipeBuilder, SmithingTrimRecipe> {

    SmithingTrimRecipeBuilder() {}

    SmithingTrimRecipeBuilder(final @NotNull SmithingTrimRecipe recipe) {
        super(recipe);
    }

    @Contract(" -> new")
    @ApiStatus.OverrideOnly
    @Override
    protected @NotNull SmithingTrimRecipe newRecipe() {
        return new SmithingTrimRecipe(
                this.namespacedKey(),
                this.template(),
                this.base(),
                this.addition(),
                this.copyNbt()
        );
    }
}
