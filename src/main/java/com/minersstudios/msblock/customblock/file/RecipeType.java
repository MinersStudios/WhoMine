package com.minersstudios.msblock.customblock.file;

import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Enum representing different types of crafting and smelting
 * recipes in Bukkit. Each enum value corresponds to a specific
 * recipe type, along with the associated class that represents
 * that type of recipe.
 */
public enum RecipeType {
    SHAPED(ShapedRecipe.class),
    SHAPELESS(ShapelessRecipe.class),
    FURNACE(FurnaceRecipe.class),
    BLAST_FURNACE(BlastingRecipe.class),
    SMOKER(SmokingRecipe.class),
    CAMPFIRE(CampfireRecipe.class),
    STONECUTTER(StonecuttingRecipe.class);

    private final Class<? extends Recipe> clazz;

    /**
     * Constructs a RecipeType enum value with the specified
     * associated class
     *
     * @param clazz The class that represents this recipe type
     */
    RecipeType(final @NotNull Class<? extends Recipe> clazz) {
        this.clazz = clazz;
    }

    /**
     * @return The class representing this recipe type
     */
    public @NotNull Class<? extends Recipe> getClazz() {
        return this.clazz;
    }

    /**
     * Converts a Recipe class to the corresponding RecipeType
     * enum value
     *
     * @param clazz The class representing a recipe
     * @return The RecipeType enum value associated with
     *         the provided class
     * @throws IllegalArgumentException If the provided class does
     *                                  not match any known recipe
     *                                  types
     */
    public static @NotNull RecipeType valueOf(final @NotNull Class<? extends Recipe> clazz) throws IllegalArgumentException {
        for (final var recipeType : values()) {
            if (recipeType.getClazz() == clazz) return recipeType;
        }

        throw new IllegalArgumentException("Unknown recipe type: " + clazz);
    }

    /**
     * Converts a recipe type string to the corresponding
     * Recipe class
     *
     * @param type The recipe type string : shaped, shapeless,
     *             furnace, blast_furnace,  smoker, campfire,
     *             stonecutter
     * @return The Recipe class associated with the provided
     *         recipe type
     * @throws IllegalArgumentException If the provided recipe
     *                                  type string is not recognized
     */
    public static @NotNull Class<? extends Recipe> clazzOf(final @NotNull String type) throws IllegalArgumentException {
        return valueOf(type.toUpperCase(Locale.ENGLISH)).getClazz();
    }

    /**
     * Converts a Recipe class to the corresponding recipe
     * type string
     *
     * @param clazz The class representing a recipe
     * @return The recipe type string associated with the
     *         provided Recipe class
     * @throws IllegalArgumentException If the provided Recipe
     *                                  class is not recognized
     */
    public static @NotNull String nameOf(final @NotNull Class<? extends Recipe> clazz) throws IllegalArgumentException {
        return valueOf(clazz).name();
    }

    /**
     * @param clazz The class representing a recipe
     * @return True if the provided class is a recognized
     *         recipe type
     */
    public static boolean isSupported(final @NotNull Class<? extends Recipe> clazz) {
        for (final var recipeType : values()) {
            if (recipeType.getClazz() == clazz) return true;
        }

        return false;
    }

    /**
     * @return A string representation of this RecipeType
     */
    @Override
    public @NotNull String toString() {
        return this.name() + '{' + "clazz=" + this.clazz + '}';
    }
}
