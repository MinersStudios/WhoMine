package com.minersstudios.wholib.recipe.choice;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.Immutable;

/**
 * Represents an entry of a recipe choice.
 * <br>
 * Use these methods to create new recipe choice entry :
 * <ul>
 *     <li>{@link #ofEnum(char, Enum, Enum[])}</li>
 *     <li>{@link #ofObject(char, Object, Object[])}</li>
 *     <li>{@link #of(char, RecipeChoice)}</li>
 * </ul>
 */
@Immutable
public final class RecipeChoiceEntry {

    private final char key;
    private final RecipeChoice<?> choice;

    private RecipeChoiceEntry(
            final char key,
            final @NotNull RecipeChoice<?> choice
    ) {
        this.key = key;
        this.choice = choice;
    }

    /**
     * Returns the key of the ingredient
     *
     * @return The key of the ingredient
     */
    public char getKey() {
        return this.key;
    }

    /**
     * Returns the choice of the ingredient
     *
     * @return The choice of the ingredient
     */
    public @NotNull RecipeChoice<?> getChoice() {
        return this.choice;
    }

    /**
     * Returns the hash code of this recipe choice entry
     *
     * @return The hash code of this recipe choice entry
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + this.key;
        result = prime * result + this.choice.hashCode();

        return result;
    }

    /**
     * Returns whether the object is equal to this recipe choice entry
     *
     * @param obj The object to compare
     * @return Whether the object is equal to this recipe choice entry
     */
    @Contract("null -> false")
    @Override
    public boolean equals(final @Nullable Object obj) {
        return obj == this
                || (
                        obj instanceof final RecipeChoiceEntry that
                        && this.key == that.key
                        && this.choice.equals(that.choice)
                );
    }

    /**
     * Returns a string representation of this recipe choice entry
     *
     * @return A string representation of this recipe choice entry
     */
    @Override
    public @NotNull String toString() {
        return "RecipeChoiceEntry{" +
                "key=" + this.key +
                ", choice=" + this.choice +
                '}';
    }

    @SafeVarargs
    @Contract("_, _, _ -> new")
    public static <E extends Enum<E>> @NotNull RecipeChoiceEntry ofEnum(
            final char key,
            final @NotNull E first,
            final E @NotNull ... rest
    ) {
        return of(
                key,
                RecipeChoice.ofEnum(first, rest)
        );
    }

    @SafeVarargs
    @Contract("_, _, _ -> new")
    public static <O> @NotNull RecipeChoiceEntry ofObject(
            final char key,
            final @NotNull O first,
            final O @NotNull ... rest
    ) {
        return of(
                key,
                RecipeChoice.ofObject(first, rest)
        );
    }

    @Contract("_, _ -> new")
    public static @NotNull RecipeChoiceEntry of(
            final char key,
            final @NotNull RecipeChoice<?> choice
    ) {
        return new RecipeChoiceEntry(key, choice);
    }
}
