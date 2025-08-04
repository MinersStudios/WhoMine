package com.minersstudios.wholib.recipe.choice;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Predicate;

public interface RecipeChoice<C> extends Predicate<C>, Cloneable {

    @NotNull Collection<C> choices();

    @Override
    default boolean test(final @NotNull C choice) {
        return this.choices().contains(choice);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static <C> @NotNull RecipeChoice<C> of(
            final @NotNull C first,
            final C @NotNull ... rest
    ) {
        return first instanceof Enum
               ? ofEnum((Enum) first, (Enum[]) rest)
               : ofObject(first, rest);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static <C> @NotNull RecipeChoice<C> of(final @NotNull Collection<C> choices) throws IllegalArgumentException {
        if (choices.isEmpty()) {
            throw new IllegalArgumentException("Choices cannot be empty");
        }

        final var first = choices.iterator().next();

        return first instanceof Enum
                ? ofEnum((Collection) choices)
                : ofObject(choices);
    }

    @SafeVarargs
    static <E extends Enum<E>> @NotNull EnumChoice<E> ofEnum(
            final @NotNull E first,
            final E @NotNull ... rest
    ) {
        return new EnumChoice<>(first, rest);
    }

    static <E extends Enum<E>> @NotNull EnumChoice<E> ofEnum(final @NotNull Collection<E> choices) throws IllegalArgumentException {
        if (choices.isEmpty()) {
            throw new IllegalArgumentException("Choices cannot be empty");
        }

        return new EnumChoice<>(choices);
    }

    @SafeVarargs
    static <O> @NotNull ObjectChoice<O> ofObject(
            final @NotNull O first,
            final O @NotNull ... rest
    ) {
        return new ObjectChoice<>(first, rest);
    }

    static <O> @NotNull ObjectChoice<O> ofObject(final @NotNull Collection<O> choices) throws IllegalArgumentException {
        if (choices.isEmpty()) {
            throw new IllegalArgumentException("Choices cannot be empty");
        }

        return new ObjectChoice<>(choices);
    }
}
