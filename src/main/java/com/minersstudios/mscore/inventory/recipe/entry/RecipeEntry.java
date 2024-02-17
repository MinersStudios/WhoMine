package com.minersstudios.mscore.inventory.recipe.entry;

import com.minersstudios.mscore.inventory.recipe.builder.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.builder.ShapedRecipeBuilder;
import com.minersstudios.mscustoms.menu.CraftsMenu;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a custom recipe entry that encapsulates a Bukkit Recipe and a flag
 * indicating whether the recipe should be shown in the {@link CraftsMenu}.
 * <br>
 * Factory methods :
 * <ul>
 *     <li>{@link #fromBuilder(RecipeBuilder)}</li>
 *     <li>{@link #fromBuilder(RecipeBuilder, boolean)}</li>
 *     <li>{@link #fromRecipe(Recipe)}</li>
 *     <li>{@link #fromRecipe(Recipe, boolean)}</li>
 * </ul>
 *
 * @see #isSupportedInCraftsMenu(Recipe)
 * @see #isSupportedInCraftsMenu(RecipeBuilder)
 */
public interface RecipeEntry {

    /**
     * Returns the recipe builder associated with this RecipeEntry.
     * <br>
     * If a recipe builder was not used to create this RecipeEntry, a new recipe
     * builder will be created and returned from the recipe.
     *
     * @return The recipe builder associated with this RecipeEntry
     * @throws UnsupportedOperationException If the recipe is unsupported in
     *                                       the recipe builder
     */
    @NotNull RecipeBuilder<?> getBuilder() throws UnsupportedOperationException;

    /**
     * Returns the Bukkit Recipe object associated with this RecipeEntry.
     * <br>
     * If a recipe builder was used to create this RecipeEntry, the recipe will
     * be built and returned.
     *
     * @return The Bukkit Recipe object associated with this RecipeEntry
     */
    @NotNull Recipe getRecipe();

    /**
     * Returns a hash code based on the recipe and isRegisteredInMenu flag
     *
     * @return A hash code based on the recipe and isRegisteredInMenu flag
     */
    @Override
    int hashCode();

    /**
     * Compares the specified object with this RecipeEntry for equality
     *
     * @param obj The object to compare
     * @return True if the object is a RecipeEntry and has the same recipe and
     *         isRegisteredInMenu flag
     */
    @Contract("null -> false")
    @Override
    boolean equals(final @Nullable Object obj);

    /**
     * Returns whether the recipe should be shown in the {@link CraftsMenu}
     *
     * @return True if the recipe should be shown in the {@link CraftsMenu}
     */
    boolean isRegisteredInMenu();

    /**
     * Returns whether the recipe created with the recipe builder
     *
     * @return True if the recipe created with the recipe builder
     */
    boolean isBuildable();

    /**
     * Returns a string representation of this {@code RecipeEntry}
     *
     * @return A string representation of this {@code RecipeEntry}
     */
    @Override
    @NotNull String toString();

    /**
     * Constructs a RecipeEntry with the specified recipe builder and false
     * isRegisteredInMenu flag
     *
     * @param builder The recipe builder
     * @return A new RecipeEntry
     * @see #fromBuilder(RecipeBuilder, boolean)
     */
    @Contract("_ -> new")
    static @NotNull RecipeEntry fromBuilder(final @NotNull RecipeBuilder<?> builder) {
        return fromBuilder(builder, false);
    }

    /**
     * Constructs a RecipeEntry with the specified recipe builder and
     * isRegisteredInMenu flag.
     * <br>
     * If the recipe type is not supported in the {@link CraftsMenu}, the
     * isRegisteredInMenu flag will be ignored.
     *
     * @param builder            The recipe builder
     * @param isRegisteredInMenu A boolean flag indicating whether the recipe
     *                           should be shown in the craft menu
     * @return A new RecipeEntry
     * @see #isSupportedInCraftsMenu(RecipeBuilder)
     */
    @Contract("_, _ -> new")
    static @NotNull RecipeEntry fromBuilder(
            final @NotNull RecipeBuilder<?> builder,
            final boolean isRegisteredInMenu
    ) {
        return new BuilderRecipeEntry(
                builder,
                isRegisteredInMenu
                && isSupportedInCraftsMenu(builder)
        );
    }

    /**
     * Constructs a RecipeEntry with the specified recipe and false
     * isRegisteredInMenu flag
     *
     * @param recipe The Bukkit Recipe object
     * @return A new RecipeEntry
     * @see #fromRecipe(Recipe, boolean)
     */
    @Contract("_ -> new")
    static @NotNull RecipeEntry fromRecipe(final @NotNull Recipe recipe) {
        return fromRecipe(recipe, false);
    }

    /**
     * Constructs a RecipeEntry with the specified recipe and isRegisteredInMenu
     * flag.
     * <br>
     * If the recipe type is not supported in the {@link CraftsMenu}, the
     * isRegisteredInMenu flag will be ignored.
     *
     * @param recipe             The Bukkit Recipe object
     * @param isRegisteredInMenu A boolean flag indicating whether the recipe
     *                           should be shown in the craft menu
     * @return A new RecipeEntry
     * @see #isSupportedInCraftsMenu(Recipe)
     */
    @Contract("_, _ -> new")
    static @NotNull RecipeEntry fromRecipe(
            final @NotNull Recipe recipe,
            final boolean isRegisteredInMenu
    ) {
        return new SimpleRecipeEntry(
                recipe,
                isRegisteredInMenu
                && isSupportedInCraftsMenu(recipe)
        );
    }

    /**
     * Returns whether the recipe type is supported in the {@link CraftsMenu}
     *
     * @param recipe The Bukkit Recipe object
     * @return True if the recipe type is supported in the {@link CraftsMenu}
     */
    static boolean isSupportedInCraftsMenu(final @NotNull Recipe recipe) {
        return recipe instanceof ShapedRecipe;
    }

    /**
     * Returns whether the recipe builder type is supported in the
     * {@link CraftsMenu}
     *
     * @param builder The recipe builder
     * @return True if the recipe builder type is supported in the
     *         {@link CraftsMenu}
     */
    static boolean isSupportedInCraftsMenu(final @NotNull RecipeBuilder<?> builder) {
        return builder instanceof ShapedRecipeBuilder;
    }
}
