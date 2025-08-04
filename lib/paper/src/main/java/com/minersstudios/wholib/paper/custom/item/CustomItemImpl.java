package com.minersstudios.wholib.paper.custom.item;

import com.minersstudios.wholib.paper.WhoMine;
import com.minersstudios.wholib.key.Key;
import com.minersstudios.wholib.key.Resource;
import com.minersstudios.wholib.paper.inventory.recipe.entry.RecipeEntry;
import com.minersstudios.wholib.throwable.InvalidRegexException;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.*;

import java.util.Collections;
import java.util.List;

/**
 * The CustomItemImpl class serves as a base implementation of the
 * {@link CustomItem} interface. It provides methods to manage the properties,
 * recipes, and similarity checks of custom items. It is recommended to extend
 * this class when creating custom items.
 *
 * @see CustomItem
 */
public abstract class CustomItemImpl implements CustomItem, Cloneable {
    protected final NamespacedKey namespacedKey;
    protected ItemStack itemStack;
    protected List<RecipeEntry> recipeEntries;

    /**
     * Protected constructor to initialize a custom item with the given key and
     * item stack. The constructor enforces key format validation and item stack
     * type validation.
     *
     * @param key       The unique key identifying the custom item
     * @param itemStack The {@link ItemStack} representing the custom item
     * @throws IllegalArgumentException If the key format is invalid or the item
     *                                  stack type is air
     * @see Key.Validator#matchesPattern(String)
     */
    protected CustomItemImpl(
            final @Key @NotNull String key,
            final @NotNull ItemStack itemStack
    ) throws InvalidRegexException, IllegalArgumentException {
        Key.Validator.validatePattern(key);

        if (itemStack.isEmpty()) {
            throw new IllegalArgumentException("Item type cannot be empty! Check " + key);
        }

        this.namespacedKey = new NamespacedKey(Resource.WMITEM, key);
        this.itemStack = itemStack;
        this.recipeEntries = new ObjectArrayList<>();

        final ItemMeta meta = itemStack.getItemMeta();
        final PersistentDataContainer container = meta.getPersistentDataContainer();

        if (!container.has(CustomItemType.TYPE_NAMESPACED_KEY, PersistentDataType.STRING)) {
            container.set(
                    CustomItemType.TYPE_NAMESPACED_KEY,
                    PersistentDataType.STRING,
                    key
            );
            this.itemStack.setItemMeta(meta);
        }
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
    public final void setItem(final @NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public final @NotNull @UnmodifiableView List<RecipeEntry> getRecipeEntries() {
        return Collections.unmodifiableList(this.recipeEntries);
    }

    @Override
    public final void setRecipeEntries(
            final @NotNull WhoMine plugin,
            final @Nullable List<RecipeEntry> recipeEntries
    ) {
        this.unregisterRecipes(plugin);
        this.recipeEntries.clear();

        if (
                recipeEntries != null
                && !recipeEntries.isEmpty()
        ) {
            this.recipeEntries.addAll(recipeEntries);
        }
    }

    @Contract("null -> false")
    @Override
    public final boolean isSimilar(final @Nullable ItemStack itemStack) {
        if (
                itemStack == null
                || itemStack.getType() != this.itemStack.getType()
                || !itemStack.hasItemMeta()
                || !itemStack.getItemMeta().hasCustomModelData()
                || !this.itemStack.getItemMeta().hasCustomModelData()
        ) {
            return false;
        }

        return itemStack.getItemMeta().getCustomModelData() == this.itemStack.getItemMeta().getCustomModelData();
    }

    @Contract("null -> false")
    @Override
    public final boolean isSimilar(final @Nullable CustomItem customItem) {
        return customItem != null
                && (
                        customItem == this
                        || this.isSimilar(customItem.getItem())
                );
    }

    @Override
    public @NotNull @Unmodifiable List<RecipeEntry> initRecipes() {
        return Collections.emptyList();
    }

    @Override
    public final void registerRecipes(final @NotNull WhoMine plugin) {
        if (this.recipeEntries.isEmpty()) {
            this.setRecipeEntries(plugin, this.initRecipes());
        }

        for (final var entry : this.recipeEntries) {
            final Recipe recipe = entry.getRecipe();

            plugin.getServer().addRecipe(recipe);

            if (entry.isRegisteredInMenu()) {
                plugin.getCache().getCustomItemRecipes().add(recipe);
            }
        }
    }

    @Override
    public final void unregisterRecipes(final @NotNull WhoMine plugin) {
        if (this.recipeEntries.isEmpty()) {
            return;
        }

        for (final var entry : this.recipeEntries) {
            final Keyed recipe = (Keyed) entry.getRecipe();

            plugin.getServer().removeRecipe(recipe.getKey());

            if (entry.isRegisteredInMenu()) {
                plugin.getCache().getCustomItemRecipes().remove(recipe);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends CustomItem> @NotNull T copy() {
        try {
            final CustomItemImpl clone = (CustomItemImpl) super.clone();

            clone.itemStack = this.itemStack.clone();
            clone.recipeEntries = new ObjectArrayList<>(this.recipeEntries);

            return (T) clone;
        } catch (final CloneNotSupportedException e) {
            throw new AssertionError("An error occurred while cloning '" + this.namespacedKey + "'", e);
        }
    }
}
