package com.minersstudios.wholib.recipe.entry;

import com.minersstudios.wholib.recipe.Recipe;
import com.minersstudios.wholib.recipe.builder.RecipeBuilder;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;

@Immutable
final class SimpleRecipeEntry extends RecipeEntryImpl {

    private final Object recipe;

    <R extends Recipe> SimpleRecipeEntry(
            final @NotNull R recipe,
            final boolean isRegisteredInMenu
    ) {
        super(isRegisteredInMenu);

        this.recipe = recipe;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R extends Recipe> @NotNull R getRecipe() {
        return (R) this.recipe;
    }

    @Override
    public @NotNull RecipeBuilder<? extends Recipe> getBuilder() throws UnsupportedOperationException {
        return RecipeBuilder.unknown((Recipe) this.recipe);
    }

    @Override
    public boolean isBuildable() {
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + this.recipe.hashCode();
        result = prime * result + Boolean.hashCode(this.isRegisteredInMenu());

        return result;
    }

    @Override
    public @NotNull String toString() {
        return "SimpleRecipeEntry{" +
                "recipe=" + this.recipe +
                ", isRegisteredInMenu=" + this.isRegisteredInMenu() +
                '}';
    }
}
