package com.minersstudios.mscore.inventory.recipe.builder;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface RecipeBuilder<R extends Recipe> {

    @NotNull R build();

    NamespacedKey namespacedKey();

    @NotNull RecipeBuilder<R> namespacedKey(final @NotNull NamespacedKey namespacedKey);

    ItemStack result();

    @NotNull RecipeBuilder<R> result(final @NotNull ItemStack result);

    @Contract(" -> new")
    static @NotNull ShapedRecipeBuilder shapedBuilder() {
        return new ShapedRecipeBuilder();
    }

    @Contract(" -> new")
    static @NotNull ShapelessRecipeBuilder shapelessBuilder() {
        return new ShapelessRecipeBuilder();
    }

    @Contract(" -> new")
    static @NotNull FurnaceRecipeBuilder furnaceBuilder() {
        return new FurnaceRecipeBuilder();
    }

    @Contract(" -> new")
    static @NotNull SmokingRecipeBuilder smokingBuilder() {
        return new SmokingRecipeBuilder();
    }

    @Contract(" -> new")
    static @NotNull BlastingRecipeBuilder blastingBuilder() {
        return new BlastingRecipeBuilder();
    }

    @Contract(" -> new")
    static @NotNull CampfireRecipeBuilder campfireBuilder() {
        return new CampfireRecipeBuilder();
    }

    @Contract(" -> new")
    static @NotNull StonecuttingRecipeBuilder stonecuttingBuilder() {
        return new StonecuttingRecipeBuilder();
    }

    @Contract(" -> new")
    static @NotNull SmithingTransformRecipeBuilder smithingTransformBuilder() {
        return new SmithingTransformRecipeBuilder();
    }

    @Contract(" -> new")
    static @NotNull SmithingTrimRecipeBuilder smithingTrimBuilder() {
        return new SmithingTrimRecipeBuilder();
    }
}
