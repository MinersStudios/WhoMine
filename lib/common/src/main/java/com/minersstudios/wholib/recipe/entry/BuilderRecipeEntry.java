package com.minersstudios.wholib.recipe.entry;

import com.minersstudios.wholib.recipe.Recipe;
import com.minersstudios.wholib.recipe.builder.RecipeBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

final class BuilderRecipeEntry extends RecipeEntryImpl {

    private final RecipeBuilder<? extends Recipe> builder;

    BuilderRecipeEntry(
            final @NotNull RecipeBuilder<? extends Recipe> builder,
            final boolean isRegisteredInMenu
    ) {
        super(isRegisteredInMenu);

        this.builder = builder;
    }

    @SuppressWarnings("unchecked")
    @Contract(" -> new")
    @Override
    public <R extends Recipe> @NotNull R getRecipe() {
        return (R) this.builder.build();
    }

    @Override
    public @NotNull RecipeBuilder<? extends Recipe> getBuilder() {
        return this.builder;
    }

    @Override
    public boolean isBuildable() {
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + this.builder.hashCode();
        result = prime * result + Boolean.hashCode(this.isRegisteredInMenu());

        return result;
    }

    @Override
    public @NotNull String toString() {
        return "BuilderRecipeEntry{" +
                "recipe=" + this.builder +
                ", isRegisteredInMenu=" + this.isRegisteredInMenu() +
                '}';
    }
}
