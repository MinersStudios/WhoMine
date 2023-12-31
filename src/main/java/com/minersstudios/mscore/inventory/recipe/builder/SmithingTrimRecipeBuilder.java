package com.minersstudios.mscore.inventory.recipe.builder;

import org.bukkit.inventory.SmithingTrimRecipe;
import org.jetbrains.annotations.NotNull;

public final class SmithingTrimRecipeBuilder extends SmithingRecipeBuilderImpl<SmithingTrimRecipeBuilder, SmithingTrimRecipe> {

    @Override
    protected @NotNull SmithingTrimRecipe newRecipe() {
        return new SmithingTrimRecipe(
                this.namespacedKey,
                this.template,
                this.base,
                this.addition,
                this.copyNbt
        );
    }
}
