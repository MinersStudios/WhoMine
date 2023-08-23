package com.minersstudios.msblock.customblock.file;

import com.google.common.base.Preconditions;
import com.minersstudios.mscore.util.MSCustomUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Represents a choice that will be valid only one of the stacks is exactly
 * matched (aside from stack size). It generates a new ItemStack by using
 * {@link MSCustomUtils#getItemStack(String)}.
 * <p>
 * Use {@link #toExactChoice()} to convert this choice to an {@link ExactChoice}.
 * It is important, because bukkit does not support custom choices.
 */
public class CustomChoice implements RecipeChoice {
    private List<ItemStack> choices;
    private List<String> namespacedKeys;

    private static final String REGEX = "[a-z0-9/._-]+:[a-z0-9/._-]+";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    /**
     * Constructs a new custom choice with the specified namespaced keys
     *
     * @param namespacedKey The namespaced key to use for the choice
     * @throws IllegalArgumentException If the namespaced key is invalid
     */
    public CustomChoice(final @NotNull String namespacedKey) throws IllegalArgumentException {
        this(List.of(namespacedKey));
    }

    /**
     * Constructs a new custom choice with the specified namespaced keys
     *
     * @param namespacedKeys The namespaced keys to use for the choice
     * @throws IllegalArgumentException If the namespaced keys are empty
     *                                  or if any of the namespaced keys
     *                                  are invalid
     */
    public CustomChoice(final String @NotNull ... namespacedKeys) throws IllegalArgumentException {
        this(List.of(namespacedKeys));
    }

    /**
     * Constructs a new custom choice with the specified namespaced keys
     *
     * @param namespacedKeys The namespaced keys to use for the choice
     * @throws IllegalArgumentException If the namespaced keys are empty
     *                                  or if any of the namespaced keys
     *                                  are null or invalid
     */
    public CustomChoice(final @NotNull List<String> namespacedKeys) throws IllegalArgumentException {
        Preconditions.checkArgument(!namespacedKeys.isEmpty(), "Must have at least one namespacedKey");

        this.namespacedKeys = new ArrayList<>(namespacedKeys);
        this.choices = new ArrayList<>();

        for (var namespacedKey : namespacedKeys) {
            Preconditions.checkArgument(namespacedKey != null, "Cannot have null namespacedKey");
            Preconditions.checkArgument(PATTERN.matcher(namespacedKey).matches(), "Invalid namespacedKey: " + namespacedKey);

            MSCustomUtils.getItemStack(namespacedKey)
            .ifPresent(itemStack -> this.choices.add(itemStack));
        }
    }

    /**
     * @return A clone of the first item stack
     */
    @Override
    public @NotNull ItemStack getItemStack() {
        return this.choices.get(0).clone();
    }

    /**
     * @return The first namespaced key
     */
    public @NotNull String getNamespacedKey() {
        return this.namespacedKeys.get(0);
    }

    /**
     * @return An unmodifiable view of the choices
     */
    public @NotNull @UnmodifiableView List<ItemStack> getChoices() {
        return Collections.unmodifiableList(this.choices);
    }

    /**
     * @return An unmodifiable view of the namespaced keys
     */
    public @NotNull @UnmodifiableView List<String> getNamespacedKeys() {
        return Collections.unmodifiableList(this.namespacedKeys);
    }

    /**
     * Converts this CustomChoice to an ExactChoice. It is important
     * to use this method, because bukkit does not support custom
     * choices.
     *
     * @return A new ExactChoice with the same choices
     *         as this CustomChoice
     */
    public @NotNull ExactChoice toExactChoice() {
        return new ExactChoice(this.choices);
    }

    /**
     * Creates and returns a clone of this CustomChoice
     *
     * @return A clone of this choice
     */
    @Override
    public @NotNull CustomChoice clone() {
        try {
            final CustomChoice clone = (CustomChoice) super.clone();

            clone.choices = new ArrayList<>(this.choices);
            clone.namespacedKeys = new ArrayList<>(this.namespacedKeys);

            return clone;
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError(ex);
        }
    }

    /**
     * @param itemStack The input itemStack to test
     * @return True if the itemStack is similar to any of the choices
     */
    @Override
    public boolean test(final @NotNull ItemStack itemStack) {
        for (final var choice : this.choices) {
            if (choice.isSimilar(itemStack)) return true;
        }

        return false;
    }

    /**
     * @return A hash code based on the choices
     */
    @Override
    public int hashCode() {
        final int prime = 41;
        int result = 7;

        result = prime * result + Objects.hashCode(this.choices);

        return result;
    }

    /**
     * @param obj The object to compare
     * @return True if the object is a CustomChoice and has the same choices
     */
    @Contract(value = "null -> false")
    @Override
    public boolean equals(final @Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        return Objects.equals(this.choices, ((CustomChoice) obj).choices);
    }

    /**
     * @return A string representation of this choice
     */
    @Override
    public @NotNull String toString() {
        return "CustomChoice{choices=" + this.choices + '}';
    }
}
