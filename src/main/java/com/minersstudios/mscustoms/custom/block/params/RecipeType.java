package com.minersstudios.mscustoms.custom.block.params;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Enum representing different types of crafting and smelting recipes in Bukkit.
 * Each enum value corresponds to a specific recipe type, along with the
 * associated class that represents that type of recipe.
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

    private static final Map<String, RecipeType> KEY_TO_TYPE_MAP;
    private static final Map<Class<? extends Recipe>, RecipeType> CLASS_TO_TYPE_MAP;

    static {
        final RecipeType[] recipeTypes = values();
        KEY_TO_TYPE_MAP = new Object2ObjectOpenHashMap<>(recipeTypes.length);
        CLASS_TO_TYPE_MAP = new Object2ObjectOpenHashMap<>(recipeTypes.length);

        for (final var recipeType : recipeTypes) {
            KEY_TO_TYPE_MAP.put(
                    recipeType.name(),
                    recipeType
            );
            CLASS_TO_TYPE_MAP.put(
                    recipeType.getClazz(),
                    recipeType
            );
        }
    }

    /**
     * Constructs a RecipeType enum value with the specified associated class
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
     * @param name The recipe type name
     * @return The RecipeType constant with the specified name
     *         (case-sensitive)
     * @throws IllegalArgumentException If the provided recipe type name is not
     *                                  recognized
     * @see #isSupported(String)
     */
    public static @NotNull RecipeType typeOf(final @NotNull String name) throws IllegalArgumentException {
        final RecipeType recipeType = KEY_TO_TYPE_MAP.get(name);

        if (recipeType == null) {
            throw new IllegalArgumentException("Unsupported RecipeType : " + name);
        }

        return recipeType;
    }

    /**
     * Converts a Recipe class to the corresponding RecipeType enum value
     *
     * @param clazz The class representing a recipe
     * @return The RecipeType enum value associated with the provided class
     * @throws IllegalArgumentException If the provided class is not recognized
     * @see #isSupported(Class)
     */
    public static @NotNull RecipeType typeOf(final @NotNull Class<? extends Recipe> clazz) throws IllegalArgumentException {
        final RecipeType recipeType = CLASS_TO_TYPE_MAP.get(clazz);

        if (recipeType == null) {
            throw new IllegalArgumentException("Unsupported Recipe class : " + clazz);
        }

        return recipeType;
    }

    /**
     * Converts a recipe type string to the corresponding
     * Recipe class
     *
     * @param typeName The recipe type string
     * @return The Recipe class associated with the provided recipe type string
     *         (case-sensitive)
     * @throws IllegalArgumentException If the provided recipe type string is
     *                                  not recognized
     * @see #isSupported(String)
     */
    public static @NotNull Class<? extends Recipe> clazzOf(final @NotNull String typeName) throws IllegalArgumentException {
        return typeOf(typeName).getClazz();
    }

    /**
     * Converts a Recipe class to the corresponding recipe type string
     *
     * @param clazz The class representing a recipe
     * @return The recipe type name associated with the provided Recipe class
     * @throws IllegalArgumentException If the provided class is not recognized
     * @see #isSupported(Class)
     */
    public static @NotNull String nameOf(final @NotNull Class<? extends Recipe> clazz) throws IllegalArgumentException {
        return typeOf(clazz).name();
    }

    /**
     * @param name The recipe type name
     * @return True if the provided recipe type name is recognized
     *         (case-sensitive)
     */
    public static boolean isSupported(final @NotNull String name) {
        return KEY_TO_TYPE_MAP.containsKey(name);
    }

    /**
     * @param clazz The class representing a recipe
     * @return True if the provided class is a recognized recipe type
     */
    public static boolean isSupported(final @NotNull Class<? extends Recipe> clazz) {
        return CLASS_TO_TYPE_MAP.containsKey(clazz);
    }

    /**
     * @return A string representation of this RecipeType
     */
    @Override
    public @NotNull String toString() {
        return this.name() + "{class=" + this.clazz + '}';
    }
}
