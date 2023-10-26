package com.minersstudios.mscore.inventory.recipe;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class ShapedRecipeBuilder extends CraftingRecipeBuilderImpl<ShapedRecipeBuilder, ShapedRecipe> {
    private String[] rows;
    private final Map<Character, RecipeChoice> ingredients = new HashMap<>();

    ShapedRecipeBuilder() {}

    @Override
    protected @NotNull ShapedRecipe newRecipe() throws IllegalStateException {
        if (this.rows == null) {
            throw new IllegalStateException("Recipe has no shape");
        }

        if (this.ingredients.isEmpty()) {
            throw new IllegalStateException("Recipe has no ingredients");
        }

        if (this.ingredients.containsValue(null)) {
            throw new IllegalStateException("Recipe has null ingredients");
        }

        final ShapedRecipe recipe =
                new ShapedRecipe(this.namespacedKey, this.result)
                .shape(this.rows);

        for (final var entry : this.ingredients.entrySet()) {
            recipe.setIngredient(entry.getKey(), entry.getValue());
        }

        return recipe;
    }

    public String[] shape() {
        return this.rows;
    }

    public @NotNull ShapedRecipeBuilder shape(
            final @NotNull String first,
            final @NotNull String second,
            final @NotNull String third
    ) throws IllegalArgumentException {
        return this.shape0(first, second, third);
    }

    public @NotNull ShapedRecipeBuilder shape(
            final @NotNull String first,
            final @NotNull String second
    ) throws IllegalArgumentException {
        return this.shape0(first, second);
    }

    public @NotNull ShapedRecipeBuilder shape(final @NotNull String first) throws IllegalArgumentException {
        return this.shape0(first);
    }

    public Map<Character, RecipeChoice> ingredients() {
        return this.ingredients;
    }

    public @NotNull ShapedRecipeBuilder ingredients(
            final @NotNull RecipeChoiceEntry first,
            final RecipeChoiceEntry @NotNull ... rest
    ) throws IllegalStateException, IllegalArgumentException {
        if (this.rows == null) {
            throw new IllegalStateException("Must call shape() first");
        }

        this.putIngredient(first.key, first.ingredient);

        for (final var entry : rest) {
            this.putIngredient(entry.key, entry.ingredient);
        }

        if (this.ingredients.containsValue(null)) {
            throw new IllegalStateException("Recipe has null ingredients");
        }

        return this;
    }

    public static @NotNull RecipeChoiceEntry material(
            final char key,
            final @NotNull Material material
    ) {
        return new RecipeChoiceEntry(
                key,
                new RecipeChoice.MaterialChoice(material)
        );
    }

    public static @NotNull RecipeChoiceEntry itemStack(
            final char key,
            final @NotNull ItemStack ingredient
    ) {
        return new RecipeChoiceEntry(
                key,
                new RecipeChoice.ExactChoice(ingredient)
        );
    }

    public static @NotNull RecipeChoiceEntry recipeChoice(
            final char key,
            final @NotNull RecipeChoice ingredient
    ) {
        return new RecipeChoiceEntry(key, ingredient);
    }

    private @NotNull ShapedRecipeBuilder shape0(final String @NotNull ... rows) throws IllegalArgumentException {
        int lastLength = -1;

        for (final var row : rows) {
            if (row.isEmpty() || row.length() > 3) {
                throw new IllegalArgumentException("Crafting rows should be 1, 2, or 3 characters, not " + row.length());
            }

            if (lastLength != -1 && lastLength != row.length()) {
                throw new IllegalArgumentException("Crafting recipes must be rectangular");
            }

            lastLength = row.length();

            for (final char c : row.toCharArray()) {
                if (Character.isWhitespace(c)) continue;

                this.ingredients.put(c, null);
            }
        }

        this.rows = rows;
        return this;
    }

    private void putIngredient(
            final char key,
            final @NotNull RecipeChoice ingredient
    ) throws IllegalArgumentException {
        if (Character.isWhitespace(key)) {
            throw new IllegalArgumentException("The ' ' character cannot be used as a key");
        }

        if (!this.ingredients.containsKey(key)) {
            throw new IllegalArgumentException("Character '" + key + "' does not appear in the shape");
        }

        this.ingredients.put(key, ingredient);
    }

    public record MaterialEntry(
            char key,
            @NotNull Material material
    ) {}

    public record ItemStackEntry(
            char key,
            @NotNull ItemStack ingredient
    ) {}

    public record RecipeChoiceEntry(
            char key,
            @NotNull RecipeChoice ingredient
    ) {}
}
