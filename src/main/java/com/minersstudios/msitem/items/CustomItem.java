package com.minersstudios.msitem.items;

import com.minersstudios.msitem.MSItem;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.minersstudios.mscore.MSCore.getCache;

public interface CustomItem extends Cloneable {
    @NotNull
    NamespacedKey getNamespacedKey();

    void setNamespacedKey(@NotNull NamespacedKey namespacedKey);

    @NotNull
    ItemStack getItemStack();

    void setItemStack(@NotNull ItemStack itemStack);

    default @Nullable List<Map.Entry<Recipe, Boolean>> getRecipes() {
        return null;
    }

    default void setRecipes(@Nullable List<Map.Entry<Recipe, Boolean>> recipes) {
    }

    default void register() {
        this.register(true);
    }

    default void register(boolean regRecipes) {
        if (this instanceof DamageableItem damageableItem) {
            damageableItem.saveForItemStack(this.getItemStack());
        }

        if (this instanceof Typed typed) {
            for (var type : typed.getTypes()) {
                getCache().customItemMap.put(
                        type.getNamespacedKey().getKey(),
                        type.getCustomModelData(),
                        typed.createCustomItem(type)
                );
            }
        } else if (this instanceof Renameable renameable) {
            for (var item : renameable.getRenameableItems()) {
                ItemStack itemStack = renameable.createRenamedItem(renameable.getItemStack(), item.getRenameText());

                if (itemStack != null) {
                    getCache().customItemMap.put(item.getKey(), item.getCustomModelData(), this);
                    new RenameableItem(
                            new NamespacedKey(MSItem.getInstance(), this.getNamespacedKey().getKey() + "." + item.getKey()),
                            item.getRenameText(),
                            Lists.newArrayList(getItemStack()),
                            itemStack,
                            item.isShowInRenameMenu(),
                            new HashSet<>()
                    );
                }
            }
        } else {
            getCache().customItemMap.put(
                    this.getNamespacedKey().getKey(),
                    this.getItemStack().getItemMeta().getCustomModelData(),
                    this
            );
        }

        if (regRecipes) {
            MSItem.getConfigCache().recipeItems.add(this);
        }
    }

    default @Nullable List<Map.Entry<Recipe, Boolean>> initRecipes() {
        return null;
    }

    default void registerRecipes() {
        var recipes = this.initRecipes();

        if (recipes != null) {
            for (var entry : recipes) {
                Recipe recipe = entry.getKey();

                Bukkit.addRecipe(recipe);
                if (entry.getValue()) {
                    getCache().customItemRecipes.add(recipe);
                }
            }
        }
    }

    CustomItem clone();

    @Contract("null -> false")
    default boolean isSimilar(@Nullable ItemStack itemStack) {
        if (
                itemStack == null
                || itemStack.getType() != this.getItemStack().getType()
                || !itemStack.hasItemMeta()
                || !itemStack.getItemMeta().hasCustomModelData()
                || !this.getItemStack().getItemMeta().hasCustomModelData()
        ) return false;

        if (
                this instanceof Renameable renameable
                && renameable.hasRenameableItem(itemStack)
        ) return true;
        return itemStack.getItemMeta().getCustomModelData() == this.getItemStack().getItemMeta().getCustomModelData();
    }

    @Contract("null -> false")
    default boolean isSimilar(@Nullable CustomItem customItem) {
        if (customItem == null) return false;
        if (customItem == this) return true;
        return this.isSimilar(customItem.getItemStack());
    }
}
