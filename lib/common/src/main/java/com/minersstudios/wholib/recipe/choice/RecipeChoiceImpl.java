package com.minersstudios.wholib.recipe.choice;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Collections;

abstract class RecipeChoiceImpl<C> implements RecipeChoice<C> {

    private final Collection<C> choices;

    protected RecipeChoiceImpl(final @NotNull Collection<C> choices) {
        this.choices = choices;
    }

    @Override
    public final @NotNull @Unmodifiable Collection<C> choices() {
        return Collections.unmodifiableCollection(this.choices);
    }
}
