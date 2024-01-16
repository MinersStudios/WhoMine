package com.minersstudios.mscore.inventory.recipe.builder;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import javax.annotation.concurrent.Immutable;

public final class ShapedRecipeBuilder extends CraftingRecipeBuilderImpl<ShapedRecipeBuilder, ShapedRecipe> {
    private final Char2ObjectMap<RecipeChoice> ingredientMap;
    private String[] rows;

    /** The maximum length of a row in a shaped recipe */
    public static final int MAX_ROW_LENGTH = 3;

    ShapedRecipeBuilder() {
        this.ingredientMap = new Char2ObjectOpenHashMap<>();
    }

    ShapedRecipeBuilder(final @NotNull ShapedRecipe recipe) {
        super(recipe);

        this.ingredientMap = new Char2ObjectOpenHashMap<>();
        this.rows = recipe.getShape();

        for (final var entry : recipe.getChoiceMap().entrySet()) {
            final char key = entry.getKey();
            final RecipeChoice choice = entry.getValue();

            this.ingredientMap.put(key, choice);
        }
    }

    @Override
    protected @NotNull ShapedRecipe newRecipe() throws IllegalStateException {
        if (this.rows == null) {
            throw new IllegalStateException("Recipe has no shape");
        }

        if (this.ingredientMap.isEmpty()) {
            throw new IllegalStateException("Recipe has no ingredients");
        }

        if (this.ingredientMap.containsValue(null)) {
            throw new IllegalStateException("Recipe has null ingredients");
        }

        final ShapedRecipe recipe =
                new ShapedRecipe(this.namespacedKey, this.result)
                .shape(this.rows);

        for (final var entry : this.ingredientMap.char2ObjectEntrySet()) {
            final char key = entry.getCharKey();
            final RecipeChoice ingredient = entry.getValue();

            recipe.setIngredient(key, ingredient);
        }

        return recipe;
    }

    public String @UnknownNullability [] shape() {
        return this.rows;
    }

    public @NotNull ShapedRecipeBuilder shape(final @NotNull String first) throws IllegalArgumentException {
        return this.setShapes(first);
    }

    public @NotNull ShapedRecipeBuilder shape(
            final @NotNull String first,
            final @NotNull String second
    ) throws IllegalArgumentException {
        return this.setShapes(first, second);
    }

    public @NotNull ShapedRecipeBuilder shape(
            final @NotNull String first,
            final @NotNull String second,
            final @NotNull String third
    ) throws IllegalArgumentException {
        return this.setShapes(first, second, third);
    }

    public @NotNull Char2ObjectMap<RecipeChoice> ingredients() {
        return this.ingredientMap;
    }

    public @NotNull ShapedRecipeBuilder ingredients(
            final @NotNull RecipeChoiceEntry first,
            final RecipeChoiceEntry @NotNull ... rest
    ) throws IllegalStateException, IllegalArgumentException {
        if (this.rows == null) {
            throw new IllegalStateException("Must call shape() first");
        }

        this.putIngredient(first.key, first.choice);

        for (final var entry : rest) {
            this.putIngredient(entry.key, entry.choice);
        }

        if (this.ingredientMap.containsValue(null)) {
            throw new IllegalStateException("Recipe has null ingredients");
        }

        return this;
    }

    public static @NotNull RecipeChoiceEntry material(
            final char key,
            final @NotNull Material material
    ) {
        return choice(
                key,
                new RecipeChoice.MaterialChoice(material)
        );
    }

    public static @NotNull RecipeChoiceEntry itemStack(
            final char key,
            final @NotNull ItemStack ingredient
    ) {
        return choice(
                key,
                new RecipeChoice.ExactChoice(ingredient)
        );
    }

    public static @NotNull RecipeChoiceEntry choice(
            final char key,
            final @NotNull RecipeChoice choice
    ) {
        return new RecipeChoiceEntry(key, choice);
    }

    private @NotNull ShapedRecipeBuilder setShapes(final String @NotNull ... rows) throws IllegalArgumentException {
        int lastLength = -1;

        for (final var row : rows) {
            if (
                    row.isEmpty()
                    || row.length() > MAX_ROW_LENGTH
            ) {
                throw new IllegalArgumentException(
                        "Crafting rows must be between 1 and " + MAX_ROW_LENGTH + " characters long"
                );
            }

            if (
                    lastLength != -1
                    && lastLength != row.length()
            ) {
                throw new IllegalArgumentException("Crafting recipes must be rectangular");
            }

            lastLength = row.length();

            for (final char c : row.toCharArray()) {
                if (!Character.isWhitespace(c)) {
                    this.ingredientMap.put(c, null);
                }
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

        if (!this.ingredientMap.containsKey(key)) {
            throw new IllegalArgumentException("Character '" + key + "' does not appear in the shape");
        }

        this.ingredientMap.put(key, ingredient);
    }

    @Immutable
    public static final class RecipeChoiceEntry {
        private final char key;
        private final RecipeChoice choice;

        RecipeChoiceEntry(
                final char key,
                final @NotNull RecipeChoice choice
        ) {
            this.key = key;
            this.choice = choice;
        }

        public char getKey() {
            return this.key;
        }

        public @NotNull RecipeChoice getChoice() {
            return this.choice;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;

            result = prime * result + this.key;
            result = prime * result + this.choice.hashCode();

            return result;
        }

        @Contract("null -> false")
        @Override
        public boolean equals(final @Nullable Object obj) {
            return obj == this
                    || (
                            obj instanceof final RecipeChoiceEntry that
                            && this.key == that.key
                            && this.choice.equals(that.choice)
                    );
        }

        @Override
        public @NotNull String toString() {
            return "RecipeChoiceEntry{" +
                    "key=" + this.key +
                    ", choice=" + this.choice +
                    '}';
        }
    }
}
