package com.minersstudios.wholib.recipe.builder;

import com.minersstudios.wholib.recipe.SmithingRecipe;
import com.minersstudios.wholib.recipe.SmithingTransformRecipe;
import com.minersstudios.wholib.recipe.SmithingTrimRecipe;
import com.minersstudios.wholib.recipe.choice.RecipeChoice;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Represents a builder for smithing recipes
 *
 * @param <R> Type of the smithing recipe built by this builder
 * @see RecipeBuilder#smithingTrim()
 * @see RecipeBuilder#smithingTrim(SmithingTrimRecipe)
 * @see RecipeBuilder#smithingTransform()
 * @see RecipeBuilder#smithingTransform(SmithingTransformRecipe)
 */
public interface SmithingRecipeBuilder<R extends SmithingRecipe> extends PathedRecipeBuilder<R> {

    /**
     * Returns the template of the recipe
     *
     * @return The template of the recipe
     */
    @UnknownNullability RecipeChoice<?> template();

    /**
     * Sets the template of the recipe
     *
     * @param template New template of the recipe
     * @return This builder, for chaining
     */
    @Contract("_ -> this")
    @NotNull SmithingRecipeBuilder<R> template(final @NotNull RecipeChoice<?> template);

    /**
     * Returns the base of the recipe
     *
     * @return The base of the recipe
     */
    @UnknownNullability RecipeChoice<?> base();

    /**
     * Sets the base of the recipe
     *
     * @param base New base of the recipe
     * @return This builder, for chaining
     */
    @Contract("_ -> this")
    @NotNull SmithingRecipeBuilder<R> base(final @NotNull RecipeChoice<?> base);

    /**
     * Returns the addition of the recipe
     *
     * @return The addition of the recipe
     */
    @UnknownNullability RecipeChoice<?> addition();

    /**
     * Sets the addition of the recipe
     *
     * @param addition New addition of the recipe
     * @return This builder, for chaining
     */
    @Contract("_ -> this")
    @NotNull SmithingRecipeBuilder<R> addition(final @NotNull RecipeChoice<?> addition);

    /**
     * Returns whether the NBT of the input items should be copied to the result
     * item
     *
     * @return Returns whether the NBT of the input items should be copied to
     *         the result item
     */
    boolean copyDataComponents();

    /**
     * Sets whether the NBT of the input items should be copied to the result
     * item
     *
     * @param state New state
     * @return This builder, for chaining
     */
    @Contract("_ -> this")
    @NotNull SmithingRecipeBuilder<R> copyDataComponents(final boolean state);
}
