package com.minersstudios.mscore.inventory.recipe.builder;

import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

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
     * Returns the group of the recipe
     *
     * @return The group of the recipe
     */
    @UnknownNullability String group();

    /**
     * Sets the group of the recipe
     *
     * @param group New group of the recipe
     * @return This builder, for chaining
     */
    @Contract("_ -> this")
    @NotNull GroupedRecipeBuilder<R> group(final @NotNull String group);
}
