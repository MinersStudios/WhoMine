package com.minersstudios.mscore.inventory.recipe.choice;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.Immutable;

/**
 * Represents an entry of a recipe choice.
 * <br>
 * Use these methods to create new recipe choice entry :
 * <ul>
 *     <li>{@link #material(char, Material)}</li>
 *     <li>{@link #itemStack(char, ItemStack)}</li>
 *     <li>{@link #choice(char, RecipeChoice)}</li>
 * </ul>
 */
@Immutable
public final class RecipeChoiceEntry {
    private final char key;
    private final RecipeChoice choice;

    private RecipeChoiceEntry(
            final char key,
            final @NotNull RecipeChoice choice
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
    public @NotNull RecipeChoice getChoice() {
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

    /**
     * Creates a new recipe choice entry
     *
     * @param key      The key of the ingredient
     * @param material The material of the ingredient
     * @return A new recipe choice entry
     */
    @Contract("_, _ -> new")
    public static @NotNull RecipeChoiceEntry material(
            final char key,
            final @NotNull Material material
    ) {
        return choice(
                key,
                new RecipeChoice.MaterialChoice(material)
        );
    }

    /**
     * Creates a new recipe choice entry
     *
     * @param key        The key of the ingredient
     * @param ingredient The ingredient
     * @return A new recipe choice entry
     */
    @Contract("_, _ -> new")
    public static @NotNull RecipeChoiceEntry itemStack(
            final char key,
            final @NotNull ItemStack ingredient
    ) {
        return choice(
                key,
                new RecipeChoice.ExactChoice(ingredient)
        );
    }

    /**
     * Creates a new recipe choice entry
     *
     * @param key    The key of the ingredient
     * @param choice The choice of the ingredient
     * @return A new recipe choice entry
     */
    @Contract("_, _ -> new")
    public static @NotNull RecipeChoiceEntry choice(
            final char key,
            final @NotNull RecipeChoice choice
    ) {
        return new RecipeChoiceEntry(
                key,
                choice instanceof final CustomChoice custom
                ? custom.toExactChoice()
                : choice
        );
    }
}
