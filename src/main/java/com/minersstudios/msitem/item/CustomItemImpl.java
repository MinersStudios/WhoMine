package com.minersstudios.msitem.item;

import com.google.common.base.Preconditions;
import com.minersstudios.msitem.MSItem;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

import static com.minersstudios.mscore.plugin.MSPlugin.getGlobalCache;

/**
 * The {@code CustomItemImpl} class serves as a base
 * implementation of the {@link CustomItem} interface.
 * It provides methods to manage the properties,
 * recipes, and similarity checks of custom items. It
 * is recommended to extend this class when creating
 * custom items.
 *
 * @see CustomItem
 */
public abstract class CustomItemImpl implements CustomItem, Cloneable {
    protected final NamespacedKey namespacedKey;
    protected final List<Map.Entry<Recipe, Boolean>> recipes;
    protected ItemStack itemStack;

    private static final String KEY_REGEX = "[a-z0-9./_-]+";
    private static final Pattern KEY_PATTERN = Pattern.compile(KEY_REGEX);

    /**
     * Protected constructor to initialize a custom item
     * with the given key and item stack. The constructor
     * enforces key format validation and item stack type
     * validation.
     *
     * @param key       The unique key identifying the custom item
     * @param itemStack The {@link ItemStack} representing the
     *                  custom item
     * @throws IllegalArgumentException If the key format is invalid
     *                                  or the item stack type is air
     * @see #KEY_REGEX
     */
    protected CustomItemImpl(
            @NotNull String key,
            @NotNull ItemStack itemStack
    ) throws IllegalArgumentException {
        Preconditions.checkArgument(KEY_PATTERN.matcher(key).matches(), "Key '" + key + "' does not match regex " + KEY_REGEX);
        Preconditions.checkArgument(!itemStack.getType().isAir(), "Item type cannot be air! Check " + key);

        this.namespacedKey = new NamespacedKey(CustomItemType.NAMESPACE, key);
        this.itemStack = itemStack;
        this.recipes = new LinkedList<>();

        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (!container.has(CustomItemType.TYPE_NAMESPACED_KEY, PersistentDataType.STRING)) {
            container.set(
                    CustomItemType.TYPE_NAMESPACED_KEY,
                    PersistentDataType.STRING,
                    key
            );
            this.itemStack.setItemMeta(meta);
        }
    }

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
    public static @NotNull CustomItem create(
            @NotNull String key,
            @NotNull ItemStack itemStack
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
    public static @NotNull CustomItem create(
            @NotNull String key,
            @NotNull ItemStack itemStack,
            @NotNull Function<CustomItem, List<Map.Entry<Recipe, Boolean>>> initRecipes
    ) throws IllegalArgumentException {
        return new CustomItemImpl(key, itemStack) {

            @Override
            public @NotNull List<Map.Entry<Recipe, Boolean>> initRecipes() {
                return initRecipes.apply(this);
            }
        };
    }

    @Override
    public final @NotNull NamespacedKey getKey() {
        return this.namespacedKey;
    }

    @Override
    public final @NotNull ItemStack getItem() {
        return this.itemStack.clone();
    }

    @Override
    public final void setItem(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public final @NotNull @UnmodifiableView List<Map.Entry<Recipe, Boolean>> getRecipes() {
        return this.recipes;
    }

    @Override
    public final void setRecipes(@Nullable List<Map.Entry<Recipe, Boolean>> recipes) {
        this.unregisterRecipes();
        this.recipes.clear();

        if (
                recipes != null
                && !recipes.isEmpty()
        ) {
            this.recipes.addAll(recipes);
        }
    }

    @Contract("null -> false")
    @Override
    public final boolean isSimilar(@Nullable ItemStack itemStack) {
        if (
                itemStack == null
                || itemStack.getType() != this.itemStack.getType()
                || !itemStack.hasItemMeta()
                || !itemStack.getItemMeta().hasCustomModelData()
                || !this.itemStack.getItemMeta().hasCustomModelData()
        ) return false;

        return itemStack.getItemMeta().getCustomModelData() == this.itemStack.getItemMeta().getCustomModelData();
    }

    @Contract("null -> false")
    @Override
    public final boolean isSimilar(@Nullable CustomItem customItem) {
        return customItem != null
                && (
                        customItem == this
                        || this.isSimilar(customItem.getItem())
                );
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends CustomItem> @NotNull T copy() {
        try {
            CustomItemImpl clone = (CustomItemImpl) super.clone();
            clone.itemStack = this.itemStack.clone();
            return (T) clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("An error occurred while cloning '" + this.namespacedKey + "'", e);
        }
    }

    @Override
    public @Nullable List<Map.Entry<Recipe, Boolean>> initRecipes() {
        return null;
    }

    @Override
    public final void registerRecipes() {
        MSItem plugin = MSItem.getInstance();
        Server server = plugin.getServer();

        this.setRecipes(this.initRecipes());

        for (var entry : this.recipes) {
            Recipe recipe = entry.getKey();

            plugin.runTask(() -> server.addRecipe(recipe));

            if (entry.getValue()) {
                getGlobalCache().customItemRecipes.add(recipe);
            }
        }
    }

    @Override
    public final void unregisterRecipes() {
        for (var entry : this.getRecipes()) {
            Recipe recipe = entry.getKey();

            if (recipe instanceof Keyed keyed) {
                Bukkit.removeRecipe(keyed.getKey());

                if (entry.getValue()) {
                    getGlobalCache().customItemRecipes.remove(recipe);
                }
            }
        }
    }
}
