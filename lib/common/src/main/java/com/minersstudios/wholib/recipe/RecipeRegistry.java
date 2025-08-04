package com.minersstudios.wholib.recipe;

import com.minersstudios.wholib.registrable.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public final class RecipeRegistry implements Registry<RecipeType> {

    @Override
    public @NotNull Iterator<RecipeType> iterator() {
        return null;
    }

    @Override
    public boolean register(final @NotNull RecipeType registrable) {
        return false;
    }

    @Override
    public boolean unregister(final @NotNull RecipeType registrable) {
        return false;
    }

    @Override
    public void unregisterAll() {

    }

    @Override
    public boolean isRegistered(final @NotNull RecipeType registrable) {
        return false;
    }
}
