package com.minersstudios.mscore.inventory.recipe.builder;

import com.minersstudios.mscore.inventory.recipe.choice.RecipeChoiceEntry;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Represents a builder for shaped recipe
 *
 * @see RecipeBuilder#shaped()
 * @see RecipeBuilder#shaped(ShapedRecipe)
 */
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

    @Contract(" -> new")
    @ApiStatus.OverrideOnly
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
                new ShapedRecipe(this.namespacedKey(), this.result())
                .shape(this.rows);

        for (final var entry : this.ingredientMap.char2ObjectEntrySet()) {
            final char key = entry.getCharKey();
            final RecipeChoice ingredient = entry.getValue();

            recipe.setIngredient(key, ingredient);
        }

        return recipe;
    }

    /**
     * Returns the shape of the recipe
     *
     * @return The shape of the recipe
     */
    public String @UnknownNullability [] shape() {
        return this.rows;
    }

    /**
     * Sets the shape of the recipe
     *
     * @param first The first row of the recipe
     * @return This builder, for chaining
     * @throws IllegalArgumentException If the row is empty or longer than
     *                                  {@link #MAX_ROW_LENGTH}
     */
    @Contract("_ -> this")
    public @NotNull ShapedRecipeBuilder shape(final @NotNull String first) throws IllegalArgumentException {
        return this.setShapes(first);
    }

    /**
     * Sets the shape of the recipe
     *
     * @param first  The first row of the recipe
     * @param second The second row of the recipe
     * @return This builder, for chaining
     * @throws IllegalArgumentException If any row is empty or longer than
     *                                  {@link #MAX_ROW_LENGTH}
     */
    @Contract("_, _ -> this")
    public @NotNull ShapedRecipeBuilder shape(
            final @NotNull String first,
            final @NotNull String second
    ) throws IllegalArgumentException {
        return this.setShapes(first, second);
    }

    /**
     * Sets the shape of the recipe
     *
     * @param first  The first row of the recipe
     * @param second The second row of the recipe
     * @param third  The third row of the recipe
     * @return This builder, for chaining
     * @throws IllegalArgumentException If any row is empty or longer than
     *                                  {@link #MAX_ROW_LENGTH}
     */
    @Contract("_, _, _ -> this")
    public @NotNull ShapedRecipeBuilder shape(
            final @NotNull String first,
            final @NotNull String second,
            final @NotNull String third
    ) throws IllegalArgumentException {
        return this.setShapes(first, second, third);
    }

    /**
     * Returns the ingredients of the recipe
     *
     * @return The ingredients of the recipe
     */
    public @NotNull Char2ObjectMap<RecipeChoice> ingredients() {
        return this.ingredientMap;
    }

    /**
     * Sets the ingredients of the recipe
     *
     * @param first The first ingredient of the recipe
     * @param rest  The rest of the ingredients of the recipe
     * @return This builder, for chaining
     * @throws IllegalStateException    If the shape has not been set
     * @throws IllegalArgumentException If any of the characters in the entries
     *                                  do not appear in the shape
     */
    @Contract("_, _ -> this")
    public @NotNull ShapedRecipeBuilder ingredients(
            final @NotNull RecipeChoiceEntry first,
            final RecipeChoiceEntry @NotNull ... rest
    ) throws IllegalStateException, IllegalArgumentException {
        if (this.rows == null) {
            throw new IllegalStateException("Must call shape() first");
        }

        this.putIngredient(first.getKey(), first.getChoice());

        for (final var entry : rest) {
            this.putIngredient(entry.getKey(), entry.getChoice());
        }

        if (this.ingredientMap.containsValue(null)) {
            throw new IllegalStateException("Recipe has null ingredients");
        }

        return this;
    }

    @Contract("_ -> this")
    private @NotNull ShapedRecipeBuilder setShapes(final String @NotNull ... rows) throws IllegalArgumentException {
        int lastLength = -1;

        for (final var row : rows) {
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

            lastLength = length;

            for (int i = 0; i < length; ++i) {
                final char character = row.charAt(i);

                if (!Character.isWhitespace(character)) {
                    this.ingredientMap.put(character, null);
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
}
