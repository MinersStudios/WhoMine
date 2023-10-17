package com.minersstudios.mscore.inventory.recipe;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class ShapedRecipeBuilder implements RecipeBuilder<ShapedRecipe, ShapedRecipeBuilder> {
    private NamespacedKey namespacedKey;
    private ItemStack result;
    private String[] rows;
    private final Map<Character, RecipeChoice> ingredients = new HashMap<>();
    private String group;
    private CraftingBookCategory category;

    ShapedRecipeBuilder() {}

    @Override
    public @NotNull ShapedRecipe build() throws IllegalStateException {
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

        recipe.setCategory(this.category == null ? CraftingBookCategory.MISC : this.category);
        recipe.setGroup(this.group == null ? "" : this.group);

        return recipe;
    }

    @Override
    public NamespacedKey namespacedKey() {
        return this.namespacedKey;
    }

    @Override
    public @NotNull ShapedRecipeBuilder namespacedKey(@NotNull NamespacedKey key) {
        this.namespacedKey = key;
        return this;
    }

    @Override
    public ItemStack result() {
        return this.result;
    }

    @Override
    public @NotNull ShapedRecipeBuilder result(@NotNull ItemStack result) throws IllegalArgumentException {
        if (result.getType().isAir()) {
            throw new IllegalArgumentException("Recipe must have non-air result");
        }

        this.result = result;
        return this;
    }

    public String group() {
        return this.group;
    }

    public @NotNull ShapedRecipeBuilder group(final @NotNull String group) {
        this.group = group;
        return this;
    }

    public CraftingBookCategory category() {
        return this.category;
    }

    public @NotNull ShapedRecipeBuilder category(final @NotNull CraftingBookCategory category) {
        this.category = category;
        return this;
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

    public Map<Character, RecipeChoice> ingredients() {
        return this.ingredients;
    }

    public @NotNull ShapedRecipeBuilder ingredients(
            final @NotNull MaterialEntry first,
            final MaterialEntry @NotNull ... rest
    ) {
        final RecipeChoiceEntry[] entries = new RecipeChoiceEntry[rest.length];

        for (int i = 0; i < rest.length; ++i) {
            final var entry = rest[i];
            entries[i] = new RecipeChoiceEntry(entry.key, new RecipeChoice.MaterialChoice(entry.material));
        }

        return this.ingredients(
                new RecipeChoiceEntry(first.key, new RecipeChoice.MaterialChoice(first.material)),
                entries
        );
    }

    public @NotNull ShapedRecipeBuilder ingredients(
            final @NotNull ItemStackEntry first,
            final ItemStackEntry @NotNull ... rest
    ) {
        final RecipeChoiceEntry[] entries = new RecipeChoiceEntry[rest.length];

        for (int i = 0; i < rest.length; ++i) {
            entries[i] = new RecipeChoiceEntry(rest[i].key, new RecipeChoice.ExactChoice(rest[i].ingredient));
        }

        return this.ingredients(
                new RecipeChoiceEntry(first.key, new RecipeChoice.ExactChoice(first.ingredient)),
                entries
        );
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

    private void putIngredient(
            final @NotNull Character key,
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

    public static @NotNull MaterialEntry material(
            final @NotNull Character key,
            final @NotNull Material material
    ) {
        return new MaterialEntry(key, material);
    }

    public static @NotNull ItemStackEntry itemStack(
            final @NotNull Character key,
            final @NotNull ItemStack ingredient
    ) {
        return new ItemStackEntry(key, ingredient);
    }

    public static @NotNull RecipeChoiceEntry recipeChoice(
            final @NotNull Character key,
            final @NotNull RecipeChoice ingredient
    ) {
        return new RecipeChoiceEntry(key, ingredient);
    }

    public record MaterialEntry(
            @NotNull Character key,
            @NotNull Material material
    ) {}

    public record ItemStackEntry(
            @NotNull Character key,
            @NotNull ItemStack ingredient
    ) {}

    public record RecipeChoiceEntry(
            @NotNull Character key,
            @NotNull RecipeChoice ingredient
    ) {}
}
