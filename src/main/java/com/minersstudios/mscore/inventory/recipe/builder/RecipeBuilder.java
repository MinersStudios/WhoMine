package com.minersstudios.mscore.inventory.recipe.builder;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Represents a builder for a recipe.
 * <br>
 * Supports building the following recipe types:
 * <ul>
 *     <li>{@link #shaped() ShapedRecipe}</li>
 *     <li>{@link #shapeless() ShapelessRecipe}</li>
 *     <li>{@link #furnace() FurnaceRecipe}</li>
 *     <li>{@link #smoking() SmokingRecipe}</li>
 *     <li>{@link #blasting() BlastingRecipe}</li>
 *     <li>{@link #campfire() CampfireRecipe}</li>
 *     <li>{@link #stonecutting() StonecuttingRecipe}</li>
 *     <li>{@link #smithingTransform() SmithingTransformRecipe}</li>
 *     <li>{@link #smithingTrim() SmithingTrimRecipe}</li>
 * </ul>
 *
 * You can also create a builder with the same properties as a given recipe
 * using the static methods with the recipe as a parameter.
 *
 * @param <R> The type of recipe built by this builder
 */
public interface RecipeBuilder<R extends Recipe> {

    /**
     * Builds the recipe
     *
     * @return The recipe built by this builder
     */
    @Contract(" -> new")
    @NotNull R build();

    /**
     * Returns the namespaced key of the recipe
     *
     * @return The namespaced key of the recipe
     */
    @UnknownNullability NamespacedKey namespacedKey();

    /**
     * Sets the namespaced key of the recipe
     *
     * @param namespacedKey New namespaced key
     * @return This builder, for chaining
     */
    @Contract("_ -> this")
    @NotNull RecipeBuilder<R> namespacedKey(final @NotNull NamespacedKey namespacedKey);

    /**
     * Returns the result of the recipe
     *
     * @return The result of the recipe
     */
    @UnknownNullability ItemStack result();

    /**
     * Sets the result of the recipe
     *
     * @param result New result
     * @return This builder, for chaining
     * @throws IllegalArgumentException If the result is air or the amount is
     *                                  less than 1
     */
    @Contract("_ -> this")
    @NotNull RecipeBuilder<R> result(final @NotNull ItemStack result) throws IllegalArgumentException;

    /**
     * Creates a new instance of the shaped recipe builder
     *
     * @return A new instance of the shaped recipe builder
     */
    @Contract(" -> new")
    static @NotNull ShapedRecipeBuilder shaped() {
        return new ShapedRecipeBuilder();
    }

    /**
     * Creates a new instance of the shaped recipe builder with the same
     * properties as the given recipe
     *
     * @param recipe The recipe to copy
     * @return A new instance of the shaped recipe builder
     */
    @Contract("_ -> new")
    static @NotNull ShapedRecipeBuilder shaped(final @NotNull ShapedRecipe recipe) {
        return new ShapedRecipeBuilder(recipe);
    }

    /**
     * Creates a new instance of the shapeless recipe builder
     *
     * @return A new instance of the shapeless recipe builder
     */
    @Contract(" -> new")
    static @NotNull ShapelessRecipeBuilder shapeless() {
        return new ShapelessRecipeBuilder();
    }

    /**
     * Creates a new instance of the shapeless recipe builder with the same
     * properties as the given recipe
     *
     * @param recipe The recipe to copy
     * @return A new instance of the shapeless recipe builder
     */
    @Contract("_ -> new")
    static @NotNull ShapelessRecipeBuilder shapeless(final @NotNull ShapelessRecipe recipe) {
        return new ShapelessRecipeBuilder(recipe);
    }

    /**
     * Creates a new instance of the furnace recipe builder
     *
     * @return A new instance of the furnace recipe builder
     */
    @Contract(" -> new")
    static @NotNull FurnaceRecipeBuilder furnace() {
        return new FurnaceRecipeBuilder();
    }

    /**
     * Creates a new instance of the furnace recipe builder with the same
     * properties as the given recipe
     *
     * @param recipe The recipe to copy
     * @return A new instance of the furnace recipe builder
     */
    @Contract("_ -> new")
    static @NotNull FurnaceRecipeBuilder furnace(final @NotNull FurnaceRecipe recipe) {
        return new FurnaceRecipeBuilder(recipe);
    }

    /**
     * Creates a new instance of the smoking recipe builder
     *
     * @return A new instance of the smoking recipe builder
     */
    @Contract(" -> new")
    static @NotNull SmokingRecipeBuilder smoking() {
        return new SmokingRecipeBuilder();
    }

    /**
     * Creates a new instance of the smoking recipe builder with the same
     * properties as the given recipe
     *
     * @param recipe The recipe to copy
     * @return A new instance of the smoking recipe builder
     */
    @Contract("_ -> new")
    static @NotNull SmokingRecipeBuilder smoking(final @NotNull SmokingRecipe recipe) {
        return new SmokingRecipeBuilder(recipe);
    }

    /**
     * Creates a new instance of the blasting recipe builder
     *
     * @return A new instance of the blasting recipe builder
     */
    @Contract(" -> new")
    static @NotNull BlastingRecipeBuilder blasting() {
        return new BlastingRecipeBuilder();
    }

    /**
     * Creates a new instance of the blasting recipe builder with the same
     * properties as the given recipe
     *
     * @param recipe The recipe to copy
     * @return A new instance of the blasting recipe builder
     */
    @Contract("_ -> new")
    static @NotNull BlastingRecipeBuilder blasting(final @NotNull BlastingRecipe recipe) {
        return new BlastingRecipeBuilder(recipe);
    }

    /**
     * Creates a new instance of the campfire recipe builder
     *
     * @return A new instance of the campfire recipe builder
     */
    @Contract(" -> new")
    static @NotNull CampfireRecipeBuilder campfire() {
        return new CampfireRecipeBuilder();
    }

    /**
     * Creates a new instance of the campfire recipe builder with the same
     * properties as the given recipe
     *
     * @param recipe The recipe to copy
     * @return A new instance of the campfire recipe builder
     */
    @Contract("_ -> new")
    static @NotNull CampfireRecipeBuilder campfire(final @NotNull CampfireRecipe recipe) {
        return new CampfireRecipeBuilder(recipe);
    }

    /**
     * Creates a new instance of the stonecutting recipe builder
     *
     * @return A new instance of the stonecutting recipe builder
     */
    @Contract(" -> new")
    static @NotNull StonecuttingRecipeBuilder stonecutting() {
        return new StonecuttingRecipeBuilder();
    }

    /**
     * Creates a new instance of the stonecutting recipe builder with the same
     * properties as the given recipe
     *
     * @param recipe The recipe to copy
     * @return A new instance of the stonecutting recipe builder
     */
    @Contract("_ -> new")
    static @NotNull StonecuttingRecipeBuilder stonecutting(final @NotNull StonecuttingRecipe recipe) {
        return new StonecuttingRecipeBuilder(recipe);
    }

    /**
     * Creates a new instance of the smithing transform recipe builder
     *
     * @return A new instance of the smithing transform recipe builder
     */
    @Contract(" -> new")
    static @NotNull SmithingTransformRecipeBuilder smithingTransform() {
        return new SmithingTransformRecipeBuilder();
    }

    /**
     * Creates a new instance of the smithing transform recipe builder with the
     * same properties as the given recipe
     *
     * @param recipe The recipe to copy
     * @return A new instance of the smithing transform recipe builder
     */
    @Contract("_ -> new")
    static @NotNull SmithingTransformRecipeBuilder smithingTransform(final @NotNull SmithingTransformRecipe recipe) {
        return new SmithingTransformRecipeBuilder(recipe);
    }

    /**
     * Creates a new instance of the smithing trim recipe builder
     *
     * @return A new instance of the smithing trim recipe builder
     */
    @Contract(" -> new")
    static @NotNull SmithingTrimRecipeBuilder smithingTrim() {
        return new SmithingTrimRecipeBuilder();
    }

    /**
     * Creates a new instance of the smithing trim recipe builder with the same
     * properties as the given recipe
     *
     * @param recipe The recipe to copy
     * @return A new instance of the smithing trim recipe builder
     */
    @Contract("_ -> new")
    static @NotNull SmithingTrimRecipeBuilder smithingTrim(final @NotNull SmithingTrimRecipe recipe) {
        return new SmithingTrimRecipeBuilder(recipe);
    }

    /**
     * Returns a new instance of the recipe builder with the same properties as
     * the given recipe
     *
     * @param recipe The recipe to copy
     * @return A new instance of the recipe builder
     * @throws UnsupportedOperationException If the recipe type is not supported
     */
    @Contract("_-> new")
    static @NotNull RecipeBuilder<? extends Recipe> unknown(final @NotNull Recipe recipe) throws UnsupportedOperationException {
        if (recipe instanceof final ShapedRecipe shaped) {
            return shaped(shaped);
        } else if (recipe instanceof final ShapelessRecipe shapeless) {
            return shapeless(shapeless);
        } else if (recipe instanceof final FurnaceRecipe furnace) {
            return furnace(furnace);
        } else if (recipe instanceof final SmokingRecipe smoking) {
            return smoking(smoking);
        } else if (recipe instanceof final BlastingRecipe blasting) {
            return blasting(blasting);
        } else if (recipe instanceof final CampfireRecipe campfire) {
            return campfire(campfire);
        } else if (recipe instanceof final StonecuttingRecipe stonecutting) {
            return stonecutting(stonecutting);
        } else if (recipe instanceof final SmithingTransformRecipe smithingTransform) {
            return smithingTransform(smithingTransform);
        } else if (recipe instanceof final SmithingTrimRecipe smithingTrim) {
            return smithingTrim(smithingTrim);
        } else {
            throw new UnsupportedOperationException("Unknown recipe type: " + recipe.getClass().getName());
        }
    }
}
