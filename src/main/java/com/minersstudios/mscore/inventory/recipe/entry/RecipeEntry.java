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
 *     <li>{@link #of(RecipeBuilder)}</li>
 *     <li>{@link #of(Recipe)}</li>
 *     <li>{@link #of(RecipeBuilder, boolean)}</li>
 *     <li>{@link #of(Recipe, boolean)}</li>
 * </ul>
 *
 * @see #isSupportedInCraftsMenu(Recipe)
 * @see #isSupportedInCraftsMenu(RecipeBuilder)
 */
public interface RecipeEntry {

    /**
     * @return The recipe builder associated with this RecipeEntry.
     *         <br>
     *         If a recipe builder was not used to create this RecipeEntry, a
     *         new recipe builder will be created and returned from the recipe.
     * @throws UnsupportedOperationException If the recipe is unsupported in
     *                                       the recipe builder
     */
    @NotNull RecipeBuilder<?> getBuilder() throws UnsupportedOperationException;

    /**
     * @return The Bukkit Recipe object associated with this RecipeEntry.
     *         <br>
     *         If a recipe builder was used to create this RecipeEntry, the
     *         recipe will be built and returned.
     */
    @NotNull Recipe getRecipe();

    /**
     * @return A hash code based on the recipe and isRegisteredInMenu flag
     */
    @Override
    int hashCode();

    /**
     * @return True if the recipe should be shown in the {@link CraftsMenu}
     */
    boolean isRegisteredInMenu();

    /**
     * @return True if the recipe created with the recipe builder
     */
    boolean isBuildable();

    /**
     * @param obj The object to compare
     * @return True if the object is a RecipeEntry and has the same recipe and
     *         isRegisteredInMenu flag
     */
    @Contract("null -> false")
    @Override
    boolean equals(final @Nullable Object obj);

    /**
     * @return A string representation of this RecipeEntry
     */
    @Override
    @NotNull String toString();

    /**
     * Constructs a RecipeEntry with the specified recipe builder and false
     * isRegisteredInMenu flag
     *
     * @param builder The recipe builder
     * @return A new RecipeEntry
     * @see #of(RecipeBuilder, boolean)
     */
    @Contract("_ -> new")
    static @NotNull RecipeEntry of(final @NotNull RecipeBuilder<?> builder) {
        return of(builder, false);
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
    static @NotNull RecipeEntry of(
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
     * @see #of(Recipe, boolean)
     */
    @Contract("_ -> new")
    static @NotNull RecipeEntry of(final @NotNull Recipe recipe) {
        return of(recipe, false);
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
    static @NotNull RecipeEntry of(
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
     * @param recipe The Bukkit Recipe object
     * @return True if the recipe type is supported in the {@link CraftsMenu}
     */
    static boolean isSupportedInCraftsMenu(final @NotNull Recipe recipe) {
        return recipe instanceof ShapedRecipe;
    }

    /**
     * @param builder The recipe builder
     * @return True if the recipe builder type is supported in the
     *         {@link CraftsMenu}
     */
    static boolean isSupportedInCraftsMenu(final @NotNull RecipeBuilder<?> builder) {
        return builder instanceof ShapedRecipeBuilder;
    }
}
