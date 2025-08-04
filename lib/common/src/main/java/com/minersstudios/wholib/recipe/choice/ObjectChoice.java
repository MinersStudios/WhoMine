package com.minersstudios.wholib.recipe.choice;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public class ObjectChoice<O> extends RecipeChoiceImpl<O> {

    @SafeVarargs
    protected ObjectChoice(
            final @NotNull O first,
            final O @NotNull ... rest
    ) {
        super(createCollection(first, rest));
    }

    protected ObjectChoice(final @NotNull Collection<O> choices) {
        super(new ObjectArrayList<>(choices));
    }

    @SafeVarargs
    private static <O> @NotNull Collection<O> createCollection(
            final @NotNull O first,
            final O @NotNull ... rest
    ) {
        final var choices = new ObjectArrayList<O>(rest.length + 1);

        choices.add(first);
        Collections.addAll(choices, rest);

        return choices;
    }
}
