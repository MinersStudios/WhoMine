package com.minersstudios.mscore.inventory.recipe.builder;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public interface RecipeBuilder<R extends Recipe> {

    @NotNull R build();

    @UnknownNullability NamespacedKey namespacedKey();

    @NotNull RecipeBuilder<R> namespacedKey(final @NotNull NamespacedKey namespacedKey);

    @UnknownNullability ItemStack result();

    @NotNull RecipeBuilder<R> result(final @NotNull ItemStack result) throws IllegalArgumentException;

    @Contract(" -> new")
    static @NotNull ShapedRecipeBuilder shaped() {
        return new ShapedRecipeBuilder();
    }

    @Contract("_ -> new")
    static @NotNull ShapedRecipeBuilder shaped(final @NotNull ShapedRecipe recipe) {
        return new ShapedRecipeBuilder(recipe);
    }

    @Contract(" -> new")
    static @NotNull ShapelessRecipeBuilder shapeless() {
        return new ShapelessRecipeBuilder();
    }

    @Contract("_ -> new")
    static @NotNull ShapelessRecipeBuilder shapeless(final @NotNull ShapelessRecipe recipe) {
        return new ShapelessRecipeBuilder(recipe);
    }

    @Contract(" -> new")
    static @NotNull FurnaceRecipeBuilder furnace() {
        return new FurnaceRecipeBuilder();
    }

    @Contract("_ -> new")
    static @NotNull FurnaceRecipeBuilder furnace(final @NotNull FurnaceRecipe recipe) {
        return new FurnaceRecipeBuilder(recipe);
    }

    @Contract(" -> new")
    static @NotNull SmokingRecipeBuilder smoking() {
        return new SmokingRecipeBuilder();
    }

    @Contract("_ -> new")
    static @NotNull SmokingRecipeBuilder smoking(final @NotNull SmokingRecipe recipe) {
        return new SmokingRecipeBuilder(recipe);
    }

    @Contract(" -> new")
    static @NotNull BlastingRecipeBuilder blasting() {
        return new BlastingRecipeBuilder();
    }

    @Contract("_ -> new")
    static @NotNull BlastingRecipeBuilder blasting(final @NotNull BlastingRecipe recipe) {
        return new BlastingRecipeBuilder(recipe);
    }

    @Contract(" -> new")
    static @NotNull CampfireRecipeBuilder campfire() {
        return new CampfireRecipeBuilder();
    }

    @Contract("_ -> new")
    static @NotNull CampfireRecipeBuilder campfire(final @NotNull CampfireRecipe recipe) {
        return new CampfireRecipeBuilder(recipe);
    }

    @Contract(" -> new")
    static @NotNull StonecuttingRecipeBuilder stonecutting() {
        return new StonecuttingRecipeBuilder();
    }

    @Contract("_ -> new")
    static @NotNull StonecuttingRecipeBuilder stonecutting(final @NotNull StonecuttingRecipe recipe) {
        return new StonecuttingRecipeBuilder(recipe);
    }

    @Contract(" -> new")
    static @NotNull SmithingTransformRecipeBuilder smithingTransform() {
        return new SmithingTransformRecipeBuilder();
    }

    @Contract("_ -> new")
    static @NotNull SmithingTransformRecipeBuilder smithingTransform(final @NotNull SmithingTransformRecipe recipe) {
        return new SmithingTransformRecipeBuilder(recipe);
    }

    @Contract(" -> new")
    static @NotNull SmithingTrimRecipeBuilder smithingTrim() {
        return new SmithingTrimRecipeBuilder();
    }

    @Contract("_ -> new")
    static @NotNull SmithingTrimRecipeBuilder smithingTrim(final @NotNull SmithingTrimRecipe recipe) {
        return new SmithingTrimRecipeBuilder(recipe);
    }

    @Contract("_ -> new")
    static @NotNull RecipeBuilder<?> unknown(final @NotNull Recipe recipe) throws UnsupportedOperationException {
        if (recipe instanceof final ShapedRecipe shaped) {
            return shaped(shaped);
        }

        if (recipe instanceof final ShapelessRecipe shapeless) {
            return shapeless(shapeless);
        }

        if (recipe instanceof final FurnaceRecipe furnace) {
            return furnace(furnace);
        }

        if (recipe instanceof final SmokingRecipe smoking) {
            return smoking(smoking);
        }

        if (recipe instanceof final BlastingRecipe blasting) {
            return blasting(blasting);
        }

        if (recipe instanceof final CampfireRecipe campfire) {
            return campfire(campfire);
        }

        if (recipe instanceof final StonecuttingRecipe stonecutting) {
            return stonecutting(stonecutting);
        }

        if (recipe instanceof final SmithingTransformRecipe smithingTransform) {
            return smithingTransform(smithingTransform);
        }

        if (recipe instanceof final SmithingTrimRecipe smithingTrim) {
            return smithingTrim(smithingTrim);
        }

        throw new UnsupportedOperationException("Unknown recipe type: " + recipe.getClass().getName());
    }
}
