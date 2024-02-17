package com.minersstudios.mscustoms.custom.item;

import com.minersstudios.mscore.annotation.Key;
import com.minersstudios.mscore.inventory.recipe.entry.RecipeEntry;
import com.minersstudios.mscore.throwable.InvalidRegexException;
import com.minersstudios.mscore.utility.SharedConstants;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.*;

import java.util.Collections;
import java.util.List;

import static com.minersstudios.mscore.plugin.MSPlugin.globalCache;

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
     * @see Key.Validator#matches(String)
     */
    protected CustomItemImpl(
            final @Key @NotNull String key,
            final @NotNull ItemStack itemStack
    ) throws InvalidRegexException, IllegalArgumentException {
        Key.Validator.validate(key);

        if (itemStack.isEmpty()) {
            throw new IllegalArgumentException("Item type cannot be empty! Check " + key);
        }

        this.namespacedKey = new NamespacedKey(SharedConstants.MSITEMS_NAMESPACE, key);
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
            final @NotNull Server server,
            final @Nullable List<RecipeEntry> recipeEntries
    ) {
        this.unregisterRecipes(server);
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
    public final void registerRecipes(final @NotNull Server server) {
        if (this.recipeEntries.isEmpty()) {
            this.setRecipeEntries(server, this.initRecipes());
        }

        for (final var entry : this.recipeEntries) {
            final Recipe recipe = entry.getRecipe();

            server.addRecipe(recipe);

            if (entry.isRegisteredInMenu()) {
                globalCache().customItemRecipes.add(recipe);
            }
        }
    }

    @Override
    public final void unregisterRecipes(final @NotNull Server server) {
        if (this.recipeEntries.isEmpty()) {
            return;
        }

        for (final var entry : this.recipeEntries) {
            final Keyed recipe = (Keyed) entry.getRecipe();

            server.removeRecipe(recipe.getKey());

            if (entry.isRegisteredInMenu()) {
                globalCache().customItemRecipes.remove(recipe);
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
