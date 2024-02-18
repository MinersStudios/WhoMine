package com.minersstudios.mscore.inventory.recipe.builder;

import org.bukkit.Material;
import org.bukkit.inventory.*;
import org.bukkit.inventory.recipe.CookingBookCategory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Represents a builder for cooking recipes
 *
 * @param <R> Type of the cooking recipe built by this builder
 * @see RecipeBuilder#campfire()
 * @see RecipeBuilder#campfire(CampfireRecipe)
 * @see RecipeBuilder#furnace()
 * @see RecipeBuilder#furnace(FurnaceRecipe)
 * @see RecipeBuilder#smoking()
 * @see RecipeBuilder#smoking(SmokingRecipe)
 * @see RecipeBuilder#blasting()
 * @see RecipeBuilder#blasting(BlastingRecipe)
 */
public interface CookingRecipeBuilder<R extends CookingRecipe<R>> extends GroupedRecipeBuilder<R>, CategorizedRecipeBuilder<R, CookingBookCategory> {

    /**
     * Returns the ingredient of the recipe
     *
     * @return The ingredient of the recipe
     */
    @UnknownNullability RecipeChoice ingredient();

    /**
     * Sets the ingredient of the recipe
     *
     * @param ingredient New ingredient of the recipe
     * @return This builder, for chaining
     */
    @Contract("_ -> this")
    @NotNull CookingRecipeBuilder<R> ingredient(final @NotNull Material ingredient);

    /**
     * Sets the ingredient of the recipe
     *
     * @param ingredient New ingredient of the recipe
     * @return This builder, for chaining
     */
    @Contract("_ -> this")
    @NotNull CookingRecipeBuilder<R> ingredient(final @NotNull ItemStack ingredient);

    /**
     * Sets the ingredient of the recipe
     *
     * @param ingredient New ingredient of the recipe
     * @return This builder, for chaining
     */
    @Contract("_ -> this")
    @NotNull CookingRecipeBuilder<R> ingredient(final @NotNull RecipeChoice ingredient);

    /**
     * Returns the experience of the recipe
     *
     * @return The experience of the recipe
     */
    float experience();

    /**
     * Sets the experience of the recipe
     *
     * @param experience New experience of the recipe
     * @return This builder, for chaining
     */
    @Contract("_ -> this")
    @NotNull CookingRecipeBuilder<R> experience(final float experience);

    /**
     * Returns the cooking time of the recipe
     *
     * @return The cooking time of the recipe
     */
    int cookingTime();

    /**
     * Sets the cooking time of the recipe
     *
     * @param cookingTime New cooking time of the recipe
     * @return This builder, for chaining
     */
    @Contract("_ -> this")
    @NotNull CookingRecipeBuilder<R> cookingTime(final @Range(from = 0, to = Integer.MAX_VALUE) int cookingTime);
}
