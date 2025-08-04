package com.minersstudios.wholib.recipe.builder;

import com.minersstudios.wholib.recipe.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.function.Function;

/**
 * Represents a builder for a recipe
 *
 * @param <R> The type of recipe built by this builder
 */
public interface RecipeBuilder<R extends Recipe> {

    @Contract(" -> new")
    @NotNull R build();

    @UnknownNullability Object result();

    @Contract("_ -> this")
    @NotNull RecipeBuilder<R> result(final @NotNull Object result) throws IllegalArgumentException;

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
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Contract("_-> new")
    static <R extends Recipe> @NotNull RecipeBuilder<R> unknown(final @NotNull R recipe) throws UnsupportedOperationException {
        final var builderConstructor = RecipeBuilderImpl.BUILDER_TYPES.get(recipe.getClass());

        if (builderConstructor == null) {
            throw new UnsupportedOperationException("Unknown recipe type: " + recipe.getClass().getName());
        }

        return (RecipeBuilder<R>) ((Function) builderConstructor).apply(recipe);
    }

    /**
     * Registers a new recipe builder constructor
     *
     * @param recipeClass        The class of the recipe
     * @param builderConstructor The constructor of the recipe builder
     * @param <R>                The type of recipe
     */
    static <R extends Recipe> void registerType(
            final @NotNull Class<R> recipeClass,
            final @NotNull Function<R, RecipeBuilder<?>> builderConstructor
    ) {
        synchronized (RecipeBuilderImpl.BUILDER_TYPES) {
            RecipeBuilderImpl.BUILDER_TYPES.put(recipeClass, builderConstructor);
        }
    }
}
