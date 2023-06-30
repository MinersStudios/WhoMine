package com.github.minersstudios.msitem.items.register.items;

import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.mscore.utils.MSItemUtils;
import com.github.minersstudios.msitem.MSItem;
import com.github.minersstudios.msitem.items.CustomItem;
import com.github.minersstudios.msitem.items.DamageableItem;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class Wrench extends DamageableItem implements CustomItem {
    private @NotNull NamespacedKey namespacedKey;
    private @NotNull ItemStack itemStack;
    private @Nullable List<Map.Entry<Recipe, Boolean>> recipes;

    public Wrench() {
        super(Material.IRON_SHOVEL.getMaxDurability(), 300);
        this.namespacedKey = new NamespacedKey(MSItem.getInstance(), "wrench");
        this.itemStack = new ItemStack(Material.IRON_SHOVEL);
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.displayName(ChatUtils.createDefaultStyledText("Гаечный ключ"));
        itemMeta.setCustomModelData(1);
        itemMeta.lore(ChatUtils.convertStringsToComponents(
                ChatUtils.COLORLESS_DEFAULT_STYLE.color(NamedTextColor.GRAY),
                "С его помощью вы можете",
                "изменять вид декораций,",
                "которые помечены как : ",
                "§fꀳ"
        ));
        itemMeta.getPersistentDataContainer().set(
                MSItemUtils.CUSTOM_ITEM_TYPE_NAMESPACED_KEY,
                PersistentDataType.STRING,
                this.getNamespacedKey().getKey()
        );
        this.itemStack.setItemMeta(itemMeta);
    }

    @Override
    public @NotNull List<Map.Entry<Recipe, Boolean>> initRecipes() {
        ShapedRecipe shapedRecipe = new ShapedRecipe(this.namespacedKey, this.itemStack)
                .shape("I", "I", "I")
                .setIngredient('I', Material.IRON_INGOT);
        return this.recipes = List.of(Map.entry(shapedRecipe, true));
    }

    @Override
    public @NotNull NamespacedKey getNamespacedKey() {
        return this.namespacedKey;
    }

    @Override
    public void setNamespacedKey(@NotNull NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
    }

    @Override
    public @NotNull ItemStack getItemStack() {
        return this.itemStack;
    }

    @Override
    public void setItemStack(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public @Nullable List<Map.Entry<Recipe, Boolean>> getRecipes() {
        return this.recipes;
    }

    @Override
    public void setRecipes(@Nullable List<Map.Entry<Recipe, Boolean>> recipes) {
        this.recipes = recipes;
    }

    @Override
    public @NotNull CustomItem clone() {
        try {
            return (CustomItem) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
