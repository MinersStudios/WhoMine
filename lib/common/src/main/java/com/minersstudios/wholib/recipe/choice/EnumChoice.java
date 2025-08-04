package com.minersstudios.wholib.recipe.choice;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EnumChoice<E extends Enum<E>> extends RecipeChoiceImpl<E> {

    @SafeVarargs
    protected EnumChoice(
            final @NotNull E first,
            final E @NotNull ... rest
    ) {
        super(EnumSet.of(first, rest));
    }

    protected EnumChoice(final @NotNull Collection<E> choices) {
        super(EnumSet.copyOf(choices));
    }
}
