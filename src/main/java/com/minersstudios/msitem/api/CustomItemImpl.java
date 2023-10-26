package com.minersstudios.msitem.api;

import com.minersstudios.mscore.util.ChatUtils;
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
import org.jetbrains.annotations.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
    protected ItemStack itemStack;
    protected List<Map.Entry<Recipe, Boolean>> recipes;

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
     * @see ChatUtils#matchesKey(String)
     */
    protected CustomItemImpl(
            final @NotNull String key,
            final @NotNull ItemStack itemStack
    ) throws IllegalArgumentException {
        ChatUtils.validateKey(key);

        if (itemStack.isEmpty()) {
            throw new IllegalArgumentException("Item type cannot be empty! Check " + key);
        }

        this.namespacedKey = new NamespacedKey(CustomItemType.NAMESPACE, key);
        this.itemStack = itemStack;
        this.recipes = new ArrayList<>();

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
    public final @NotNull @UnmodifiableView List<Map.Entry<Recipe, Boolean>> getRecipes() {
        return Collections.unmodifiableList(this.recipes);
    }

    @Override
    public final void setRecipes(final @Nullable List<Map.Entry<Recipe, Boolean>> recipes) {
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
    public final boolean isSimilar(final @Nullable ItemStack itemStack) {
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
    public final boolean isSimilar(final @Nullable CustomItem customItem) {
        return customItem != null
                && (
                        customItem == this
                        || this.isSimilar(customItem.getItem())
                );
    }

    @Override
    public @NotNull @Unmodifiable List<Map.Entry<Recipe, Boolean>> initRecipes() {
        return Collections.emptyList();
    }

    @Override
    public final void registerRecipes() {
        final MSItem plugin = MSItem.getInstance();
        final Server server = plugin.getServer();

        if (this.recipes.isEmpty()) {
            this.setRecipes(this.initRecipes());
        }

        for (final var entry : this.recipes) {
            final Recipe recipe = entry.getKey();

            plugin.runTask(() -> server.addRecipe(recipe));

            if (entry.getValue()) {
                getGlobalCache().customItemRecipes.add(recipe);
            }
        }
    }

    @Override
    public final void unregisterRecipes() {
        for (final var entry : this.recipes) {
            final Recipe recipe = entry.getKey();

            if (recipe instanceof final Keyed keyed) {
                Bukkit.removeRecipe(keyed.getKey());

                if (entry.getValue()) {
                    getGlobalCache().customItemRecipes.remove(recipe);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends CustomItem> @NotNull T copy() {
        try {
            final CustomItemImpl clone = (CustomItemImpl) super.clone();

            clone.itemStack = this.itemStack.clone();
            clone.recipes = new ArrayList<>(this.recipes);

            return (T) clone;
        } catch (final CloneNotSupportedException e) {
            throw new AssertionError("An error occurred while cloning '" + this.namespacedKey + "'", e);
        }
    }
}
