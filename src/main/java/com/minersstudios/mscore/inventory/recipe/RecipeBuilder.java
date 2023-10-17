package com.minersstudios.mscore.inventory.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface RecipeBuilder<R extends Recipe, B extends RecipeBuilder<R, B>> {

    @NotNull R build();

    NamespacedKey namespacedKey();

    @NotNull B namespacedKey(final @NotNull NamespacedKey namespacedKey);

    ItemStack result();

    @NotNull B result(final @NotNull ItemStack result);

    @Contract(" -> new")
    static @NotNull ShapedRecipeBuilder shapedBuilder() {
        return new ShapedRecipeBuilder();
    }

    @Contract(" -> new")
    static @NotNull ShapelessRecipeBuilder shapelessBuilder() {
        return new ShapelessRecipeBuilder();
    }
}
