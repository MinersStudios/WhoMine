package com.minersstudios.mscore.inventory.recipe.builder;

import org.bukkit.inventory.SmithingTransformRecipe;
import org.jetbrains.annotations.NotNull;

public final class SmithingTransformRecipeBuilder extends SmithingRecipeBuilderImpl<SmithingTransformRecipeBuilder, SmithingTransformRecipe> {

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
