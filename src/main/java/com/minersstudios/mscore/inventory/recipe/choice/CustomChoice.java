package com.minersstudios.mscore.inventory.recipe.choice;

import com.google.common.collect.Maps;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscustoms.utility.MSCustomUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import javax.annotation.concurrent.Immutable;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Represents a choice that will be valid only one of the stacks is exactly
 * matched (aside from stack size). It generates a new ItemStack by using
 * {@link MSCustomUtils#getItemStack(String)}.
 * <br>
 * Use {@link #toExactChoice()} to convert this choice to an {@link ExactChoice}.
 * It is important because bukkit does not support custom choices.
 */
@Immutable
public final class CustomChoice implements RecipeChoice {
    private Object2ObjectMap<String, ItemStack> choiceMap;

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
        this(Arrays.asList(namespacedKeys));
    }

    /**
     * Constructs a new custom choice with the specified namespaced keys
     *
     * @param namespacedKeys The namespaced keys to use for the choice
     * @throws IllegalArgumentException If the namespaced keys are empty, or if
     *                                  any of the namespaced keys are null or
     *                                  invalid
     */
    public CustomChoice(final @NotNull Collection<String> namespacedKeys) throws IllegalArgumentException {
        if (namespacedKeys.isEmpty()) {
            throw new IllegalArgumentException("Must have at least one namespacedKey");
        }

        this.choiceMap = new Object2ObjectOpenHashMap<>(namespacedKeys.size());

        for (final var namespacedKey : namespacedKeys) {
            if (ChatUtils.isBlank(namespacedKey)) {
                throw new IllegalArgumentException("Cannot have a blank namespacedKey");
            }

            if (!PATTERN.matcher(namespacedKey).matches()) {
                throw new IllegalArgumentException("Invalid namespacedKey : " + namespacedKey);
            }

            MSCustomUtils.getItemStack(namespacedKey)
            .ifPresent(itemStack -> this.choiceMap.put(namespacedKey, itemStack));
        }
    }

    /**
     * Returns a clone of the first item stack
     *
     * @return A clone of the first item stack
     * @deprecated Use {@link #choices()} or {@link #getItemStack(String)}
     *             instead
     */
    @Deprecated
    @Override
    public @NotNull ItemStack getItemStack() {
        return this.choiceMap.values().iterator().next().clone();
    }

    /**
     * Returns a clone of the item stack for the specified namespaced key
     *
     * @param namespacedKey The namespaced key to get the item stack for
     * @return A clone of the item stack for the specified namespaced key
     */
    public @NotNull ItemStack getItemStack(final @NotNull String namespacedKey) {
        return this.choiceMap.get(namespacedKey).clone();
    }

    /**
     * Returns the first namespaced key
     *
     * @return The first namespaced key
     */
    public @NotNull String getNamespacedKey() {
        return this.choiceMap.keySet().iterator().next();
    }

    /**
     * Returns an unmodifiable set of the namespaced keys
     *
     * @return An unmodifiable set of the namespaced keys
     */
    public @NotNull @Unmodifiable Set<String> namespacedKeySet() {
        return Collections.unmodifiableSet(this.choiceMap.keySet());
    }

    /**
     * Returns an unmodifiable collection of the choices
     *
     * @return An unmodifiable collection of the choices
     */
    public @NotNull @Unmodifiable Collection<ItemStack> choices() {
        return Collections.unmodifiableCollection(this.choiceMap.values());
    }

    /**
     * Returns an unmodifiable set of the entries
     *
     * @return An unmodifiable set of the entries
     */
    public @NotNull @Unmodifiable Set<Map.Entry<String, ItemStack>> entrySet() {
        return Collections.unmodifiableSet(this.choiceMap.object2ObjectEntrySet());
    }

    /**
     * @return A hash code based on the choices
     */
    @Override
    public int hashCode() {
        return this.choiceMap.hashCode();
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
                        obj instanceof CustomChoice that
                        && Maps.difference(this.choiceMap, that.choiceMap).areEqual()
                );
    }

    /**
     * Returns whether the namespaced key is present in the choices
     *
     * @param namespacedKey The namespaced key to test
     * @return True if the namespaced key is present in the choices
     */
    public boolean test(final @NotNull String namespacedKey) {
        return this.choiceMap.containsKey(namespacedKey);
    }

    /**
     * Returns whether the itemStack is similar to any of the choices
     *
     * @param itemStack The input itemStack to test
     * @return True if the itemStack is similar to any of the choices
     */
    @Override
    public boolean test(final @NotNull ItemStack itemStack) {
        for (final var choice : this.choiceMap.values()) {
            if (choice.isSimilar(itemStack)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Creates and returns a clone of this {@code CustomChoice}
     *
     * @return A clone of this choice
     */
    @Override
    public @NotNull CustomChoice clone() {
        try {
            final CustomChoice clone = (CustomChoice) super.clone();

            clone.choiceMap = new Object2ObjectOpenHashMap<>(this.choiceMap);

            return clone;
        } catch (final CloneNotSupportedException e) {
            throw new AssertionError("An error occurred while cloning '" + this + "'", e);
        }
    }

    /**
     * Returns a string representation of this choice
     *
     * @return A string representation of this choice
     */
    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() + this.choiceMap;
    }

    /**
     * Converts this CustomChoice to an ExactChoice. It is important to use this
     * method because bukkit does not support custom choices.
     *
     * @return A new ExactChoice with the same choices as this CustomChoice
     */
    public @NotNull ExactChoice toExactChoice() {
        return new ExactChoice(new ObjectArrayList<>(this.choiceMap.values()));
    }
}
