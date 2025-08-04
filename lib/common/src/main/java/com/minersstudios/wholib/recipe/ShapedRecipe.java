package com.minersstudios.wholib.recipe;

import com.minersstudios.wholib.key.ResourceKey;
import com.minersstudios.wholib.recipe.category.CraftingRecipeCategory;
import com.minersstudios.wholib.recipe.choice.RecipeChoice;
import com.minersstudios.wholib.recipe.choice.RecipeChoiceEntry;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import org.jetbrains.annotations.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class ShapedRecipe extends AbstractCraftingRecipe<ShapedRecipe> {
    /** The maximum length of a row in a shaped recipe */
    public static final int MAX_ROW_LENGTH = 3;

    private final Char2ObjectMap<RecipeChoice<?>> ingredientMap;
    private String[] rows;

    private ShapedRecipe(
            final @NotNull ResourceKey path,
            final @NotNull Object result,
            final @NotNull CraftingRecipeCategory category,
            final @NotNull String group
    ) {
        super(path, result, category, group);

        this.ingredientMap = new Char2ObjectOpenHashMap<>();
    }

    public @NotNull @Unmodifiable Map<Character, RecipeChoice<?>> getIngredientMap() {
        return Collections.unmodifiableMap(this.ingredientMap);
    }

    @Contract("_, _ -> this")
    public @NotNull ShapedRecipe setIngredient(
            final char key,
            final @NotNull RecipeChoice<?> ingredient
    ) throws IllegalArgumentException {
        return this.putIngredient(key, ingredient);
    }

    @Contract("_ -> this")
    public @NotNull ShapedRecipe setIngredients(final @NotNull Collection<RecipeChoiceEntry> entries) {
        for (final var entry : entries) {
            this.putIngredient(entry.getKey(), entry.getChoice());
        }

        return this;
    }

    @Contract("_, _ -> this")
    public @NotNull ShapedRecipe setIngredients(
            final @NotNull RecipeChoiceEntry first,
            final RecipeChoiceEntry @NotNull ... rest
    ) throws IllegalArgumentException {
        this.putIngredient(first.getKey(), first.getChoice());

        for (final var entry : rest) {
            this.putIngredient(entry.getKey(), entry.getChoice());
        }

        return this;
    }

    public @Unmodifiable String @UnknownNullability [] getShape() {
        return this.rows.clone();
    }

    @Contract("_ -> this")
    public @NotNull ShapedRecipe setShape(final @NotNull String first) throws IllegalArgumentException {
        return this.setShapes(first);
    }

    @Contract("_, _ -> this")
    public @NotNull ShapedRecipe setShape(
            final @NotNull String first,
            final @NotNull String second
    ) throws IllegalArgumentException {
        return this.setShapes(first, second);
    }

    @Contract("_, _, _ -> this")
    public @NotNull ShapedRecipe setShape(
            final @NotNull String first,
            final @NotNull String second,
            final @NotNull String third
    ) throws IllegalArgumentException {
        return this.setShapes(first, second, third);
    }

    @Contract("_, _ -> new")
    public static @NotNull ShapedRecipe of(
            final @NotNull ResourceKey path,
            final @NotNull Object result
    ) {
        return of(path, result, CraftingRecipeCategory.MISC);
    }

    @Contract("_, _, _ -> new")
    public static @NotNull ShapedRecipe of(
            final @NotNull ResourceKey path,
            final @NotNull Object result,
            final @NotNull CraftingRecipeCategory category
    ) {
        return of(path, result, category, "");
    }

    @Contract("_, _, _, _ -> new")
    public static @NotNull ShapedRecipe of(
            final @NotNull ResourceKey path,
            final @NotNull Object result,
            final @NotNull CraftingRecipeCategory category,
            final @NotNull String group
    ) {
        return new ShapedRecipe(path, result, category, group);
    }

    @Contract("_ -> this")
    private @NotNull ShapedRecipe setShapes(final String @NotNull ... rows) throws IllegalArgumentException {
        this.ingredientMap.clear();

        int lastLength = -1;

        for (final var row : rows) {
            lastLength = getLength(row, lastLength);
            char character;

            for (int i = 0; i < lastLength; ++i) {
                character = row.charAt(i);

                if (!Character.isWhitespace(character)) {
                    this.ingredientMap.put(character, null);
                }
            }
        }

        this.rows = rows;

        return this;
    }

    @Contract("_, _ -> this")
    private @NotNull ShapedRecipe putIngredient(
            final char key,
            final @NotNull RecipeChoice<?> ingredient
    ) throws IllegalArgumentException {
        if (Character.isWhitespace(key)) {
            throw new IllegalArgumentException("The ' ' character cannot be used as a key");
        }

        if (!this.ingredientMap.containsKey(key)) {
            throw new IllegalArgumentException("Character '" + key + "' does not appear in the shape");
        }

        this.ingredientMap.put(key, ingredient);

        return this;
    }

    @ApiStatus.Internal
    public static int getLength(
            final @NotNull String row,
            final int lastLength
    ) throws IllegalArgumentException {
        final int length = row.length();

        if (
                length == 0
                || length > MAX_ROW_LENGTH
        ) {
            throw new IllegalArgumentException(
                    "Crafting rows must be between 1 and " + MAX_ROW_LENGTH + " characters long"
            );
        }

        if (
                lastLength != -1
                && lastLength != length
        ) {
            throw new IllegalArgumentException("Crafting recipes must be rectangular");
        }

        return length;
    }
}
