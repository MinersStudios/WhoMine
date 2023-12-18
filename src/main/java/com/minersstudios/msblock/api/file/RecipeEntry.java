package com.minersstudios.msblock.api.file;

import com.minersstudios.msessentials.menu.CraftsMenu;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a custom recipe entry that encapsulates
 * a Bukkit Recipe and a flag indicating whether the
 * recipe should be shown in the {@link CraftsMenu}
 */
@SuppressWarnings("ClassCanBeRecord")
public class RecipeEntry {
    private final @NotNull Recipe recipe;
    private final boolean showInCraftsMenu;

    /**
     * Constructs a RecipeEntry with the specified recipe
     * and showInCraftsMenu flag. If the recipe type is not
     * supported in the {@link CraftsMenu}, the showInCraftsMenu
     * flag will be ignored.
     *
     * @param recipe           The Bukkit Recipe object
     * @param showInCraftsMenu A boolean flag indicating
     *                         whether the recipe should
     *                         be shown in the craft menu
     */
    public RecipeEntry(
            final @NotNull Recipe recipe,
            final boolean showInCraftsMenu
    ) {
        this.recipe = recipe;
        this.showInCraftsMenu = showInCraftsMenu && isSupportedInCraftsMenu(recipe);
    }

    /**
     * @return The Bukkit Recipe object associated with
     *         this RecipeEntry
     */
    public @NotNull Recipe getRecipe() {
        return this.recipe;
    }

    /**
     * @return True if the recipe should be shown in
     *         the {@link CraftsMenu}
     */
    public boolean isShowInCraftsMenu() {
        return this.showInCraftsMenu;
    }

    /**
     * @param recipe The Bukkit Recipe object
     * @return True if the recipe type is supported in
     *         the {@link CraftsMenu}
     */
    public static boolean isSupportedInCraftsMenu(final @NotNull Recipe recipe) {
        return recipe instanceof ShapedRecipe;
    }

    /**
     * @return A string representation of this RecipeEntry
     */
    @Override
    public @NotNull String toString() {
        return "RecipeEntry{" +
                "recipe=" + this.recipe +
                ", showInCraftsMenu=" + this.showInCraftsMenu +
                '}';
    }
}
