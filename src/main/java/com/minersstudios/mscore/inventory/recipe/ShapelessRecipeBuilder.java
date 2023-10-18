package com.minersstudios.mscore.inventory.recipe;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;

public final class ShapelessRecipeBuilder extends CraftingRecipeBuilderImpl<ShapelessRecipeBuilder, ShapelessRecipe>  {
    private RecipeChoice[] ingredients;

    ShapelessRecipeBuilder() {}

    @Override
    protected @NotNull ShapelessRecipe newRecipe() throws IllegalStateException {
        if (this.ingredients == null || this.ingredients.length == 0) {
            throw new IllegalStateException("Recipe must have at least one non-air ingredient");
        }

        final ShapelessRecipe recipe = new ShapelessRecipe(this.namespacedKey, this.result);

        for (final var ingredient : this.ingredients) {
            recipe.addIngredient(ingredient);
        }

        return recipe;
    }

    public RecipeChoice[] ingredients() {
        return this.ingredients;
    }

    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull Material first,
            final @NotNull Material second,
            final @NotNull Material third,
            final @NotNull Material fourth,
            final @NotNull Material fifth,
            final @NotNull Material sixth,
            final @NotNull Material seventh,
            final @NotNull Material eighth,
            final @NotNull Material ninth
    ) {
        return this.ingredients0(first, second, third, fourth, fifth, sixth, seventh, eighth, ninth);
    }

    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull Material first,
            final @NotNull Material second,
            final @NotNull Material third,
            final @NotNull Material fourth,
            final @NotNull Material fifth,
            final @NotNull Material sixth,
            final @NotNull Material seventh,
            final @NotNull Material eighth
    ) {
        return this.ingredients0(first, second, third, fourth, fifth, sixth, seventh, eighth);
    }

    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull Material first,
            final @NotNull Material second,
            final @NotNull Material third,
            final @NotNull Material fourth,
            final @NotNull Material fifth,
            final @NotNull Material sixth,
            final @NotNull Material seventh
    ) {
        return this.ingredients0(first, second, third, fourth, fifth, sixth, seventh);
    }

    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull Material first,
            final @NotNull Material second,
            final @NotNull Material third,
            final @NotNull Material fourth,
            final @NotNull Material fifth,
            final @NotNull Material sixth
    ) {
        return this.ingredients0(first, second, third, fourth, fifth, sixth);
    }

    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull Material first,
            final @NotNull Material second,
            final @NotNull Material third,
            final @NotNull Material fourth,
            final @NotNull Material fifth
    ) {
        return this.ingredients0(first, second, third, fourth, fifth);
    }

    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull Material first,
            final @NotNull Material second,
            final @NotNull Material third,
            final @NotNull Material fourth
    ) {
        return this.ingredients0(first, second, third, fourth);
    }

    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull Material first,
            final @NotNull Material second,
            final @NotNull Material third
    ) {
        return this.ingredients0(first, second, third);
    }

    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull Material first,
            final @NotNull Material second
    ) {
        return this.ingredients0(first, second);
    }

    public @NotNull ShapelessRecipeBuilder ingredients(final @NotNull Material first) {
        return this.ingredients0(first);
    }

    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull ItemStack first,
            final @NotNull ItemStack second,
            final @NotNull ItemStack third,
            final @NotNull ItemStack fourth,
            final @NotNull ItemStack fifth,
            final @NotNull ItemStack sixth,
            final @NotNull ItemStack seventh,
            final @NotNull ItemStack eighth,
            final @NotNull ItemStack ninth
    ) {
        return this.ingredients0(first, second, third, fourth, fifth, sixth, seventh, eighth, ninth);
    }

    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull ItemStack first,
            final @NotNull ItemStack second,
            final @NotNull ItemStack third,
            final @NotNull ItemStack fourth,
            final @NotNull ItemStack fifth,
            final @NotNull ItemStack sixth,
            final @NotNull ItemStack seventh,
            final @NotNull ItemStack eighth
    ) {
        return this.ingredients0(first, second, third, fourth, fifth, sixth, seventh, eighth);
    }

    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull ItemStack first,
            final @NotNull ItemStack second,
            final @NotNull ItemStack third,
            final @NotNull ItemStack fourth,
            final @NotNull ItemStack fifth,
            final @NotNull ItemStack sixth,
            final @NotNull ItemStack seventh
    ) {
        return this.ingredients0(first, second, third, fourth, fifth, sixth, seventh);
    }

    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull ItemStack first,
            final @NotNull ItemStack second,
            final @NotNull ItemStack third,
            final @NotNull ItemStack fourth,
            final @NotNull ItemStack fifth,
            final @NotNull ItemStack sixth
    ) {
        return this.ingredients0(first, second, third, fourth, fifth, sixth);
    }

    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull ItemStack first,
            final @NotNull ItemStack second,
            final @NotNull ItemStack third,
            final @NotNull ItemStack fourth,
            final @NotNull ItemStack fifth
    ) {
        return this.ingredients0(first, second, third, fourth, fifth);
    }

    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull ItemStack first,
            final @NotNull ItemStack second,
            final @NotNull ItemStack third,
            final @NotNull ItemStack fourth
    ) {
        return this.ingredients0(first, second, third, fourth);
    }

    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull ItemStack first,
            final @NotNull ItemStack second,
            final @NotNull ItemStack third
    ) {
        return this.ingredients0(first, second, third);
    }

    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull ItemStack first,
            final @NotNull ItemStack second
    ) {
        return this.ingredients0(first, second);
    }

    public @NotNull ShapelessRecipeBuilder ingredients(final @NotNull ItemStack first) {
        return this.ingredients0(first);
    }

    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull RecipeChoice first,
            final @NotNull RecipeChoice second,
            final @NotNull RecipeChoice third,
            final @NotNull RecipeChoice fourth,
            final @NotNull RecipeChoice fifth,
            final @NotNull RecipeChoice sixth,
            final @NotNull RecipeChoice seventh,
            final @NotNull RecipeChoice eighth,
            final @NotNull RecipeChoice ninth
    ) {
        return this.ingredients0(first, second, third, fourth, fifth, sixth, seventh, eighth, ninth);
    }

    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull RecipeChoice first,
            final @NotNull RecipeChoice second,
            final @NotNull RecipeChoice third,
            final @NotNull RecipeChoice fourth,
            final @NotNull RecipeChoice fifth,
            final @NotNull RecipeChoice sixth,
            final @NotNull RecipeChoice seventh,
            final @NotNull RecipeChoice eighth
    ) {
        return this.ingredients0(first, second, third, fourth, fifth, sixth, seventh, eighth);
    }

    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull RecipeChoice first,
            final @NotNull RecipeChoice second,
            final @NotNull RecipeChoice third,
            final @NotNull RecipeChoice fourth,
            final @NotNull RecipeChoice fifth,
            final @NotNull RecipeChoice sixth,
            final @NotNull RecipeChoice seventh
    ) {
        return this.ingredients0(first, second, third, fourth, fifth, sixth, seventh);
    }

    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull RecipeChoice first,
            final @NotNull RecipeChoice second,
            final @NotNull RecipeChoice third,
            final @NotNull RecipeChoice fourth,
            final @NotNull RecipeChoice fifth,
            final @NotNull RecipeChoice sixth
    ) {
        return this.ingredients0(first, second, third, fourth, fifth, sixth);
    }

    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull RecipeChoice first,
            final @NotNull RecipeChoice second,
            final @NotNull RecipeChoice third,
            final @NotNull RecipeChoice fourth,
            final @NotNull RecipeChoice fifth
    ) {
        return this.ingredients0(first, second, third, fourth, fifth);
    }

    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull RecipeChoice first,
            final @NotNull RecipeChoice second,
            final @NotNull RecipeChoice third,
            final @NotNull RecipeChoice fourth
    ) {
        return this.ingredients0(first, second, third, fourth);
    }

    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull RecipeChoice first,
            final @NotNull RecipeChoice second,
            final @NotNull RecipeChoice third
    ) {
        return this.ingredients0(first, second, third);
    }

    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull RecipeChoice first,
            final @NotNull RecipeChoice second
    ) {
        return this.ingredients0(first, second);
    }

    public @NotNull ShapelessRecipeBuilder ingredients(final @NotNull RecipeChoice first) {
        return this.ingredients0(first);
    }

    private @NotNull ShapelessRecipeBuilder ingredients0(final Material @NotNull ... ingredients) throws IllegalArgumentException {
        final RecipeChoice[] choices = new RecipeChoice[ingredients.length];

        for (int i = 0; i < ingredients.length; ++i) {
            choices[i] = new RecipeChoice.MaterialChoice(ingredients[i]);
        }

        return this.ingredients0(choices);
    }

    private @NotNull ShapelessRecipeBuilder ingredients0(final ItemStack @NotNull ... ingredients) throws IllegalArgumentException {
        final RecipeChoice[] choices = new RecipeChoice[ingredients.length];

        for (int i = 0; i < ingredients.length; ++i) {
            choices[i] = new RecipeChoice.ExactChoice(ingredients[i]);
        }

        return this.ingredients0(choices);
    }

    private @NotNull ShapelessRecipeBuilder ingredients0(final RecipeChoice @NotNull ... ingredients) throws IllegalArgumentException {
        if (ingredients.length > 9) {
            throw new IllegalArgumentException("Shapeless recipes cannot have more than 9 ingredients");
        }

        this.ingredients = ingredients;
        return this;
    }
}