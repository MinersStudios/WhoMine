package com.minersstudios.msitem.api;

import com.minersstudios.msessentials.menu.CraftsMenu;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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
 * <br>
 * It is recommended to extend the {@link CustomItemImpl}
 * class when creating custom items. This class provides
 * a base implementation of the {@code CustomItem} interface
 * and can be used to manage the properties, recipes, and
 * similarity checks of custom items.
 * <br>
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
     * for this custom item. Boolean value in the entry
     * represents whether the recipe should be registered
     * in {@link CraftsMenu}.
     *
     * @return A list of {@link Recipe} entries representing
     *         the associated recipes, or null if there are
     *         no recipes
     * @see #registerRecipes()
     */
    @NotNull @Unmodifiable List<Map.Entry<Recipe, Boolean>> initRecipes();

    /**
     * Register the associated recipes of this custom item
     * with the server
     *
     * @see #initRecipes()
     * @see #unregisterRecipes()
     */
    void registerRecipes();

    /**
     * Unregister the associated recipes of this custom item
     * from the server
     *
     * @see #registerRecipes()
     */
    void unregisterRecipes();

    /**
     * Create a copy of this custom item
     *
     * @param <T> The type of the custom item to return
     * @return A new copy of this custom item
     */
    <T extends CustomItem> @NotNull T copy();

    /**
     * Creates a new instance of {@code CustomItem} with
     * the given key, item stack and empty recipe list
     *
     * @param key       The unique key identifying the custom item
     * @param itemStack The {@link ItemStack} representing the
     *                  custom item
     * @return A new instance of {@code CustomItem} with the given
     *         key and item stack
     * @throws IllegalArgumentException If the key format is invalid
     *                                  or the item stack type is air
     */
    @Contract("_, _ -> new")
    static @NotNull CustomItem create(
            final @NotNull String key,
            final @NotNull ItemStack itemStack
    ) throws IllegalArgumentException {
        return new CustomItemImpl(key, itemStack) {};
    }

    /**
     * Creates a new instance of {@code CustomItem} with
     * the given key, item stack and recipe initialization
     * function
     *
     * @param key         The unique key identifying the custom item
     * @param itemStack   The {@link ItemStack} representing the
     *                    custom item
     * @param initRecipes The function to initialize the recipe list
     *                    of the custom item
     * @return A new instance of {@code CustomItem} with the given
     *         key, item stack and recipe initialization function
     * @throws IllegalArgumentException If the key format is invalid
     *                                  or the item stack type is air
     */
    @Contract("_, _, _ -> new")
    static @NotNull CustomItem create(
            final @NotNull String key,
            final @NotNull ItemStack itemStack,
            final @NotNull Function<CustomItem, List<Map.Entry<Recipe, Boolean>>> initRecipes
    ) throws IllegalArgumentException {
        return new CustomItemImpl(key, itemStack) {

            @Override
            public @NotNull List<Map.Entry<Recipe, Boolean>> initRecipes() {
                return initRecipes.apply(this);
            }
        };
    }

    /**
     * Gets the {@link CustomItem} from the given custom item key. It will
     * get the custom item type from the {@link CustomItemType#KEY_TO_TYPE_MAP} and then
     * get the custom item instance from the returned type using the default
     * {@link CustomItem} class
     *
     * @param key The key to get the custom item type from,
     *            must not be null (case-insensitive)
     * @return An {@link Optional} containing the {@link CustomItem}
     *         or an {@link Optional#empty()} if the given key is not
     *         associated with any custom item
     * @see CustomItemType#KEY_TO_TYPE_MAP
     * @see #fromKey(String, Class)
     */
    static @NotNull Optional<CustomItem> fromKey(final @Nullable String key) {
        return fromKey(key, CustomItem.class);
    }

    /**
     * Gets the {@link CustomItem} from the given custom item key. It will
     * get the custom item type from the {@link CustomItemType#KEY_TO_TYPE_MAP} and then
     * get the custom item instance from the returned type using the given
     * class to cast the custom item instance
     *
     * @param key   The key to get the custom item type from,
     *              must not be null (case-insensitive)
     * @param clazz The target class to cast the custom item instance
     * @param <I>   The type of the target class
     * @return An {@link Optional} containing the {@link CustomItem}
     *         or an {@link Optional#empty()} if the given key is not
     *         associated with any custom item or if the custom item
     *         instance cannot be cast to the specified class
     * @see CustomItemType#getCustomItem(Class)
     * @see CustomItemType#fromKey(String)
     */
    static <I extends CustomItem> @NotNull Optional<I> fromKey(
            final @Nullable String key,
            final @Nullable Class<I> clazz
    ) {
        if (
                key == null
                        || clazz == null
        ) return Optional.empty();

        final CustomItemType type = CustomItemType.fromKey(key);
        return type != null
                && clazz.isInstance(type.getCustomItem())
                ? Optional.of(type.getCustomItem(clazz))
                : Optional.empty();
    }

    /**
     * Gets the {@link CustomItem} from the given class. It will get the
     * custom item instance from the {@link CustomItemType#CLASS_TO_ITEM_MAP} using the
     * given class to cast the custom item instance
     *
     * @param clazz The class to get the custom item type from
     * @param <I>   The type of the target class
     * @return An {@link Optional} containing the {@link CustomItem}
     *         or an {@link Optional#empty()} if the given class is not
     *         associated with any custom item
     * @see CustomItemType#CLASS_TO_ITEM_MAP
     */
    static <I extends CustomItem> @NotNull Optional<I> fromClass(final @Nullable Class<I> clazz) {
        return clazz == null
                ? Optional.empty()
                : Optional.ofNullable(clazz.cast(CustomItemType.CLASS_TO_ITEM_MAP.get(clazz)));
    }

    /**
     * Gets the {@link CustomItem} from the given item stack. It will get
     * the namespaced key from the item stack's persistent data container
     * and then get the custom item instance from the {@link CustomItemType#KEY_TO_TYPE_MAP}
     * using the default {@link CustomItem} class
     *
     * @param itemStack The item stack to get the custom item type from
     * @return An {@link Optional} containing the {@link CustomItem}
     *         or an {@link Optional#empty()} if the given item stack
     *         is not associated with any custom item or is null or an
     *         air item stack
     * @see #fromItemStack(ItemStack, Class)
     */
    static @NotNull Optional<CustomItem> fromItemStack(final @Nullable ItemStack itemStack) {
        return fromItemStack(itemStack, CustomItem.class);
    }

    /**
     * Gets the {@link CustomItem} from the given item stack. It will get
     * the namespaced key from the item stack's persistent data container
     * and then get the custom item instance from the {@link CustomItemType#KEY_TO_TYPE_MAP}
     * using the given class to cast the custom item instance
     *
     * @param itemStack The item stack to get the custom item type from
     * @param clazz     The target class to cast the custom item instance
     * @param <I>       The type of the target class
     * @return An {@link Optional} containing the {@link CustomItem}
     *         or an {@link Optional#empty()} if the given item stack
     *         or class is null, or an air item stack, or if the custom
     *         item instance cannot be cast to the specified class
     * @see #fromKey(String, Class)
     */
    static <I extends CustomItem> @NotNull Optional<I> fromItemStack(
            final @Nullable ItemStack itemStack,
            final @Nullable Class<I> clazz
    ) {
        if (
                itemStack == null
                        || clazz == null
        ) return Optional.empty();

        final ItemMeta itemMeta = itemStack.getItemMeta();
        return itemMeta == null
                ? Optional.empty()
                : fromKey(
                itemMeta.getPersistentDataContainer().get(CustomItemType.TYPE_NAMESPACED_KEY, PersistentDataType.STRING),
                clazz
        );
    }
}
