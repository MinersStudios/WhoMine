package com.minersstudios.mscore.inventory.recipe.builder;

import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Represents a recipe builder that can have a category
 *
 * @param <R> Type of the recipe built by this builder
 * @param <C> Type of recipe category
 * @see CraftingRecipeBuilder
 * @see CookingRecipeBuilder
 */
public interface CategorizedRecipeBuilder<R extends Recipe, C> extends RecipeBuilder<R> {

    /**
     * Returns the category of the recipe
     *
     * @return The category of the recipe
     */
    @UnknownNullability C category();

    /**
     * Sets the category of the recipe
     *
     * @param category New category of the recipe
     * @return This builder, for chaining
     */
    @Contract("_ -> this")
    @NotNull CategorizedRecipeBuilder<R, C> category(final @NotNull C category);
}
