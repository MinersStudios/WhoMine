package com.minersstudios.mscore.inventory.recipe;

import com.minersstudios.mscustoms.menu.CraftsMenu;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;

/**
 * Represents a custom recipe entry that encapsulates a Bukkit Recipe and a flag
 * indicating whether the recipe should be shown in the {@link CraftsMenu}
 */
@Immutable
public final class RecipeEntry {
    private final @NotNull Recipe recipe;
    private final boolean isRegisteredInMenu;

    /**
     * Constructs a RecipeEntry with the specified recipe and showInCraftsMenu
     * flag. If the recipe type is not supported in the {@link CraftsMenu}, the
     * showInCraftsMenu flag will be ignored.
     *
     * @param recipe             The Bukkit Recipe object
     * @param isRegisteredInMenu A boolean flag indicating whether the recipe
     *                           should be shown in the craft menu
     */
    public RecipeEntry(
            final @NotNull Recipe recipe,
            final boolean isRegisteredInMenu
    ) {
        this.recipe = recipe;
        this.isRegisteredInMenu = isRegisteredInMenu && isSupportedInCraftsMenu(recipe);
    }

    /**
     * @return The Bukkit Recipe object associated with this RecipeEntry
     */
    public @NotNull Recipe getRecipe() {
        return this.recipe;
    }

    /**
     * @return True if the recipe should be shown in the {@link CraftsMenu}
     */
    public boolean isRegisteredInMenu() {
        return this.isRegisteredInMenu;
    }

    /**
     * @param recipe The Bukkit Recipe object
     * @return True if the recipe type is supported in the {@link CraftsMenu}
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
                ", isRegisteredInMenu=" + this.isRegisteredInMenu +
                '}';
    }
}
