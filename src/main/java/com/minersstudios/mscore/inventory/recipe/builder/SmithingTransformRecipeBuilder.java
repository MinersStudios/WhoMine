package com.minersstudios.mscore.inventory.recipe.builder;

import org.bukkit.inventory.SmithingTransformRecipe;
import org.jetbrains.annotations.NotNull;

public final class SmithingTransformRecipeBuilder extends SmithingRecipeBuilderImpl<SmithingTransformRecipeBuilder, SmithingTransformRecipe> {

    SmithingTransformRecipeBuilder() {}

    SmithingTransformRecipeBuilder(final @NotNull SmithingTransformRecipe recipe) {
        super(recipe);

        this.template = recipe.getTemplate();
    }

    @Override
    protected @NotNull SmithingTransformRecipe newRecipe() {
        return new SmithingTransformRecipe(
                this.namespacedKey,
                this.result,
                this.template,
                this.base,
                this.addition,
                this.copyNbt
        );
    }
}
