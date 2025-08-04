package com.minersstudios.wholib.recipe.entry;

import com.minersstudios.wholib.recipe.Recipe;
import com.minersstudios.wholib.recipe.builder.RecipeBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RecipeEntry {

    <R extends Recipe> @NotNull R getRecipe();

    @NotNull RecipeBuilder<? extends Recipe> getBuilder() throws UnsupportedOperationException;

    @Override
    int hashCode();

    @Contract("null -> false")
    @Override
    boolean equals(final @Nullable Object obj);

    boolean isRegisteredInMenu();

    boolean isBuildable();

    @Override
    @NotNull String toString();

    @Contract("_ -> new")
    static <R extends Recipe> @NotNull RecipeEntry fromBuilder(final @NotNull RecipeBuilder<R> builder) {
        return fromBuilder(builder, false);
    }

    @Contract("_, _ -> new")
    static <R extends Recipe> @NotNull RecipeEntry fromBuilder(
            final @NotNull RecipeBuilder<R> builder,
            final boolean isRegisteredInMenu
    ) {
        return new BuilderRecipeEntry(builder, isRegisteredInMenu);
    }

    @Contract("_ -> new")
    static <R extends Recipe> @NotNull RecipeEntry fromRecipe(final @NotNull R recipe) {
        return fromRecipe(recipe, false);
    }

    @Contract("_, _ -> new")
    static <R extends Recipe> @NotNull RecipeEntry fromRecipe(
            final @NotNull R recipe,
            final boolean isRegisteredInMenu
    ) {
        return new SimpleRecipeEntry(recipe, isRegisteredInMenu);
    }
}
