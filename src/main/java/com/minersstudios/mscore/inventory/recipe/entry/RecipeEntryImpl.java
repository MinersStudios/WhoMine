package com.minersstudios.mscore.inventory.recipe.entry;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.Immutable;

@Immutable
abstract class RecipeEntryImpl implements RecipeEntry {
    private final boolean isRegisteredInMenu;

    RecipeEntryImpl(final boolean isRegisteredInMenu) {
        this.isRegisteredInMenu = isRegisteredInMenu;
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + this.getRecipe().hashCode();
        result = prime * result + Boolean.hashCode(this.isRegisteredInMenu);

        return result;
    }

    @Override
    public final boolean isRegisteredInMenu() {
        return this.isRegisteredInMenu;
    }

    @Contract("null -> false")
    @Override
    public final boolean equals(final @Nullable Object obj) {
        return this == obj
                || (
                        obj instanceof RecipeEntryImpl that
                        && this.getRecipe().equals(that.getRecipe())
                        && this.isRegisteredInMenu == that.isRegisteredInMenu
                );
    }

    @Override
    public abstract @NotNull String toString();
}
