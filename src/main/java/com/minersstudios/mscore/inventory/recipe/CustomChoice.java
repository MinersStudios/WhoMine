package com.minersstudios.mscore.inventory.recipe;

import com.minersstudios.mscore.utility.MSCustomUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Represents a choice that will be valid only one of the stacks is exactly
 * matched (aside from stack size). It generates a new ItemStack by using
 * {@link MSCustomUtils#getItemStack(String)}.
 * <br>
 * Use {@link #toExactChoice()} to convert this choice to an {@link ExactChoice}.
 * It is important because bukkit does not support custom choices.
 */
public final class CustomChoice implements RecipeChoice {
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
        this(Collections.singletonList(namespacedKey));
    }

    /**
     * Constructs a new custom choice with the specified namespaced keys
     *
     * @param namespacedKeys The namespaced keys to use for the choice
     * @throws IllegalArgumentException If the namespaced keys are empty, or if
     *                                  any of the namespaced keys are invalid
     */
    public CustomChoice(final String @NotNull ... namespacedKeys) throws IllegalArgumentException {
        this(List.of(namespacedKeys));
    }

    /**
     * Constructs a new custom choice with the specified namespaced keys
     *
     * @param namespacedKeys The namespaced keys to use for the choice
     * @throws IllegalArgumentException If the namespaced keys are empty, or if
     *                                  any of the namespaced keys are null or
     *                                  invalid
     */
    public CustomChoice(final @NotNull List<String> namespacedKeys) throws IllegalArgumentException {
        if (namespacedKeys.isEmpty()) {
            throw new IllegalArgumentException("Must have at least one namespacedKey");
        }

        this.namespacedKeys = namespacedKeys;
        this.choices = new ArrayList<>();

        for (final var namespacedKey : namespacedKeys) {
            if (namespacedKey == null) {
                throw new IllegalArgumentException("Cannot have null namespacedKey");
            }

            if (!PATTERN.matcher(namespacedKey).matches()) {
                throw new IllegalArgumentException("Invalid namespacedKey: " + namespacedKey);
            }

            MSCustomUtils.getItemStack(namespacedKey)
            .ifPresent(itemStack -> this.choices.add(itemStack));
        }
    }

    /**
     * @return A clone of the first item stack
     * @deprecated Use {@link #getChoices()} instead
     */
    @Deprecated
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
    public @NotNull @Unmodifiable List<ItemStack> getChoices() {
        return Collections.unmodifiableList(this.choices);
    }

    /**
     * @return An unmodifiable view of the namespaced keys
     */
    public @NotNull @Unmodifiable List<String> getNamespacedKeys() {
        return Collections.unmodifiableList(this.namespacedKeys);
    }

    /**
     * Converts this CustomChoice to an ExactChoice. It is important to use this
     * method because bukkit does not support custom choices.
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
        } catch (final CloneNotSupportedException e) {
            throw new AssertionError("An error occurred while cloning '" + this + "'", e);
        }
    }

    /**
     * @param itemStack The input itemStack to test
     * @return True if the itemStack is similar to any of the choices
     */
    @Override
    public boolean test(final @NotNull ItemStack itemStack) {
        for (final var choice : this.choices) {
            if (choice.isSimilar(itemStack)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return A hash code based on the choices
     */
    @Override
    public int hashCode() {
        return 41 * 7 + Objects.hashCode(this.choices);
    }

    /**
     * @param obj The object to compare
     * @return True if the object is a CustomChoice and has the same choices
     */
    @Contract("null -> false")
    @Override
    public boolean equals(final @Nullable Object obj) {
        return this == obj
                || (
                        obj != null
                        && this.getClass() == obj.getClass()
                        && Objects.equals(this.choices, ((CustomChoice) obj).choices)
                );
    }

    /**
     * @return A string representation of this choice
     */
    @Override
    public @NotNull String toString() {
        return "CustomChoice{choices=" + this.choices + '}';
    }
}
