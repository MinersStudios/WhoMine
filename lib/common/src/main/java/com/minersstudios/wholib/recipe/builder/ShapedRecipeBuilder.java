package com.minersstudios.wholib.recipe.builder;

import com.minersstudios.wholib.recipe.ShapedRecipe;
import com.minersstudios.wholib.recipe.choice.RecipeChoice;
import com.minersstudios.wholib.recipe.choice.RecipeChoiceEntry;
import it.unimi.dsi.fastutil.chars.Char2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.function.Function;

/**
 * Represents a builder for shaped recipe
 *
 * @see RecipeBuilder#shaped()
 * @see RecipeBuilder#shaped(ShapedRecipe)
 */
public final class ShapedRecipeBuilder extends CraftingRecipeBuilderImpl<ShapedRecipeBuilder, ShapedRecipe> {
    private static final Function<ShapedRecipeBuilder, ShapedRecipe> RECIPE_CONSTRUCTOR =
            builder -> ShapedRecipe.of(builder.path(), builder.result());

    private final Char2ObjectMap<RecipeChoice<?>> ingredientMap;
    private String[] rows;

    ShapedRecipeBuilder() {
        super(RECIPE_CONSTRUCTOR);

        this.ingredientMap = new Char2ObjectAVLTreeMap<>();
    }

    ShapedRecipeBuilder(final @NotNull ShapedRecipe recipe) {
        super(RECIPE_CONSTRUCTOR, recipe);

        this.ingredientMap = new Char2ObjectAVLTreeMap<>(recipe.getIngredientMap());
        this.rows = recipe.getShape();
    }

    @Contract(" -> new")
    @Override
    public @NotNull ShapedRecipe build() throws IllegalStateException {
        if (this.rows == null) {
            throw new IllegalStateException("Recipe has no shape");
        }

        if (this.ingredientMap.isEmpty()) {
            throw new IllegalStateException("Recipe has no ingredients");
        }

        if (this.ingredientMap.containsValue(null)) {
            throw new IllegalStateException("Recipe has null ingredients");
        }

        final var ingredients = new ObjectArrayList<RecipeChoiceEntry>(this.ingredientMap.size());

        return super.build()
                .setShape(this.rows[0], this.rows[1], this.rows[2])
                .setIngredients(ingredients);
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
     *                                  {@link ShapedRecipe#MAX_ROW_LENGTH}
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
     *                                  {@link ShapedRecipe#MAX_ROW_LENGTH}
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
     *                                  {@link ShapedRecipe#MAX_ROW_LENGTH}
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
    public @NotNull Char2ObjectMap<RecipeChoice<?>> ingredients() {
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
            lastLength = ShapedRecipe.getLength(row, lastLength);

            for (int i = 0; i < lastLength; ++i) {
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
            final @NotNull RecipeChoice<?> ingredient
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
