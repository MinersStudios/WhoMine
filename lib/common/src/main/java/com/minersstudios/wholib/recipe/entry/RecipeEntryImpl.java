package com.minersstudios.wholib.recipe.entry;

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

    @Contract("null -> false")
    @Override
    public boolean equals(final @Nullable Object obj) {
        return this == obj
                || (
                        obj instanceof final RecipeEntryImpl that
                        && this.getRecipe().equals(that.getRecipe())
                        && this.isRegisteredInMenu == that.isRegisteredInMenu
                );
    }

    @Override
    public final boolean isRegisteredInMenu() {
        return this.isRegisteredInMenu;
    }

    @Override
    public abstract @NotNull String toString();
}
