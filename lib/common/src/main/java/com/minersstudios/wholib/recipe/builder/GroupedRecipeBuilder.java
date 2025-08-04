package com.minersstudios.wholib.recipe.builder;

import com.minersstudios.wholib.recipe.Recipe;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a recipe builder that can have a group
 *
 * @param <R> Type of the recipe built by this builder
 * @see CraftingRecipeBuilder
 * @see CookingRecipeBuilder
 * @see StonecuttingRecipeBuilder
 */
public interface GroupedRecipeBuilder<R extends Recipe> extends RecipeBuilder<R> {

    /**
     * Returns the group for the recipe
     *
     * @return The group for the recipe
     */
    @NotNull String group();

    /**
     * Sets the group for the recipe
     *
     * @param group New group
     * @return This builder, for chaining
     */
    @Contract("_ -> this")
    @NotNull GroupedRecipeBuilder<R> group(final @NotNull String group);
}
