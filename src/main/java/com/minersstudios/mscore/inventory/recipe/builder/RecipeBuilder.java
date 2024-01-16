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
    static @NotNull ShapedRecipeBuilder shapedBuilder() {
        return new ShapedRecipeBuilder();
    }

    @Contract("_ -> new")
    static @NotNull ShapedRecipeBuilder shapedBuilder(final @NotNull ShapedRecipe recipe) {
        return new ShapedRecipeBuilder(recipe);
    }

    @Contract(" -> new")
    static @NotNull ShapelessRecipeBuilder shapelessBuilder() {
        return new ShapelessRecipeBuilder();
    }

    @Contract("_ -> new")
    static @NotNull ShapelessRecipeBuilder shapelessBuilder(final @NotNull ShapelessRecipe recipe) {
        return new ShapelessRecipeBuilder(recipe);
    }

    @Contract(" -> new")
    static @NotNull FurnaceRecipeBuilder furnaceBuilder() {
        return new FurnaceRecipeBuilder();
    }

    @Contract("_ -> new")
    static @NotNull FurnaceRecipeBuilder furnaceBuilder(final @NotNull FurnaceRecipe recipe) {
        return new FurnaceRecipeBuilder(recipe);
    }

    @Contract(" -> new")
    static @NotNull SmokingRecipeBuilder smokingBuilder() {
        return new SmokingRecipeBuilder();
    }

    @Contract("_ -> new")
    static @NotNull SmokingRecipeBuilder smokingBuilder(final @NotNull SmokingRecipe recipe) {
        return new SmokingRecipeBuilder(recipe);
    }

    @Contract(" -> new")
    static @NotNull BlastingRecipeBuilder blastingBuilder() {
        return new BlastingRecipeBuilder();
    }

    @Contract("_ -> new")
    static @NotNull BlastingRecipeBuilder blastingBuilder(final @NotNull BlastingRecipe recipe) {
        return new BlastingRecipeBuilder(recipe);
    }

    @Contract(" -> new")
    static @NotNull CampfireRecipeBuilder campfireBuilder() {
        return new CampfireRecipeBuilder();
    }

    @Contract("_ -> new")
    static @NotNull CampfireRecipeBuilder campfireBuilder(final @NotNull CampfireRecipe recipe) {
        return new CampfireRecipeBuilder(recipe);
    }

    @Contract(" -> new")
    static @NotNull StonecuttingRecipeBuilder stonecuttingBuilder() {
        return new StonecuttingRecipeBuilder();
    }

    @Contract("_ -> new")
    static @NotNull StonecuttingRecipeBuilder stonecuttingBuilder(final @NotNull StonecuttingRecipe recipe) {
        return new StonecuttingRecipeBuilder(recipe);
    }

    @Contract(" -> new")
    static @NotNull SmithingTransformRecipeBuilder smithingTransformBuilder() {
        return new SmithingTransformRecipeBuilder();
    }

    @Contract("_ -> new")
    static @NotNull SmithingTransformRecipeBuilder smithingTransformBuilder(final @NotNull SmithingTransformRecipe recipe) {
        return new SmithingTransformRecipeBuilder(recipe);
    }

    @Contract(" -> new")
    static @NotNull SmithingTrimRecipeBuilder smithingTrimBuilder() {
        return new SmithingTrimRecipeBuilder();
    }

    @Contract("_ -> new")
    static @NotNull SmithingTrimRecipeBuilder smithingTrimBuilder(final @NotNull SmithingTrimRecipe recipe) {
        return new SmithingTrimRecipeBuilder(recipe);
    }

    @Contract("_ -> new")
    static @NotNull RecipeBuilder<?> unknownRecipeBuilder(final @NotNull Recipe recipe) throws UnsupportedOperationException {
        if (recipe instanceof final ShapedRecipe shaped) {
            return shapedBuilder(shaped);
        }

        if (recipe instanceof final ShapelessRecipe shapeless) {
            return shapelessBuilder(shapeless);
        }

        if (recipe instanceof final FurnaceRecipe furnace) {
            return furnaceBuilder(furnace);
        }

        if (recipe instanceof final SmokingRecipe smoking) {
            return smokingBuilder(smoking);
        }

        if (recipe instanceof final BlastingRecipe blasting) {
            return blastingBuilder(blasting);
        }

        if (recipe instanceof final CampfireRecipe campfire) {
            return campfireBuilder(campfire);
        }

        if (recipe instanceof final StonecuttingRecipe stonecutting) {
            return stonecuttingBuilder(stonecutting);
        }

        if (recipe instanceof final SmithingTransformRecipe smithingTransform) {
            return smithingTransformBuilder(smithingTransform);
        }

        if (recipe instanceof final SmithingTrimRecipe smithingTrim) {
            return smithingTrimBuilder(smithingTrim);
        }

        throw new UnsupportedOperationException("Unknown recipe type: " + recipe.getClass().getName());
    }
}
