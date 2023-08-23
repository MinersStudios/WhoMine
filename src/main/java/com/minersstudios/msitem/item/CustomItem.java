package com.minersstudios.msitem.item;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * The {@code CustomItem} interface represents an
 * abstraction for custom items in the game. It defines
 * methods and behaviors that can be applied to custom
 * items, such as getting the item's key, retrieving and
 * setting the item's properties, managing recipes, and
 * checking for item similarity. This interface serves
 * as a foundation for creating custom item
 * implementations.
 * <p>
 * It is recommended to extend the {@link CustomItemImpl}
 * class when creating custom items. This class provides
 * a base implementation of the {@code CustomItem} interface
 * and can be used to manage the properties, recipes, and
 * similarity checks of custom items.
 * <p>
 * You can also use the
 * {@link CustomItemImpl#create(String, ItemStack)}
 * or {@link CustomItemImpl#create(String, ItemStack, Function)}
 * methods to create a custom item instance from a given key
 * and item stack.
 *
 * @see CustomItemImpl
 */
public interface CustomItem extends Keyed {

    /**
     * @return The unique namespaced key identifying
     *         the custom item
     */
    @Override
    @NotNull NamespacedKey getKey();

    /**
     * @return The clone of the {@link ItemStack}
     *         representing the custom item
     */
    @NotNull ItemStack getItem();

    /**
     * Sets the {@link ItemStack} representing the
     * custom item
     *
     * @param itemStack The {@link ItemStack} to set
     */
    void setItem(final @NotNull ItemStack itemStack);

    /**
     * @return An unmodifiable list of {@link Recipe}
     *         entries representing the associated recipes
     */
    @NotNull @UnmodifiableView List<Map.Entry<Recipe, Boolean>> getRecipes();

    /**
     * Set the list of associated recipes for this
     * custom item
     *
     * @param recipes A list of {@link Recipe} entries
     *                representing the associated recipes
     */
    void setRecipes(final @Nullable List<Map.Entry<Recipe, Boolean>> recipes);

    /**
     * Check whether a given item stack is similar to
     * this custom item
     *
     * @param itemStack The {@link ItemStack} to compare
     *                  with this custom item
     * @return True if the item stack is similar to this
     *         custom item
     */
    @Contract("null -> false")
    boolean isSimilar(final @Nullable ItemStack itemStack);

    /**
     * Check whether a given custom item is similar to
     * this custom item
     *
     * @param customItem The {@link CustomItem} to compare
     *                   with this custom item
     * @return True if the custom item is similar to this
     *         custom item
     * @see #isSimilar(ItemStack)
     */
    @Contract("null -> false")
    boolean isSimilar(final @Nullable CustomItem customItem);

    /**
     * Initialize and retrieve a list of associated recipes
     * for this custom item
     *
     * @return A list of {@link Recipe} entries representing
     *         the associated recipes, or null if there are
     *         no recipes
     */
    @Nullable List<Map.Entry<Recipe, Boolean>> initRecipes();

    /**
     * Register the associated recipes of this custom item
     * with the server
     *
     * @see #initRecipes()
     */
    void registerRecipes();

    /**
     * Unregister the associated recipes of this custom item
     * from the server
     */
    void unregisterRecipes();

    /**
     * Create a copy of this custom item
     *
     * @param <T> The type of the custom item to return
     * @return A new instance of the custom item with
     *         identical properties to this one, except
     *         for the namespaced key and recipes
     */
    <T extends CustomItem> @NotNull T copy();
}
