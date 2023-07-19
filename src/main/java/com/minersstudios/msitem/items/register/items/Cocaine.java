package com.minersstudios.msitem.items.register.items;

import com.minersstudios.mscore.utils.ChatUtils;
import com.minersstudios.mscore.utils.MSItemUtils;
import com.minersstudios.msitem.MSItem;
import com.minersstudios.msitem.items.CustomItem;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class Cocaine implements CustomItem {
    private @NotNull NamespacedKey namespacedKey;
    private @NotNull ItemStack itemStack;
    private @Nullable List<Map.Entry<Recipe, Boolean>> recipes;

    public Cocaine() {
        this.namespacedKey = new NamespacedKey(MSItem.getInstance(), "cocaine");
        this.itemStack = new ItemStack(Material.POTION);
        PotionMeta potionMeta = (PotionMeta) this.itemStack.getItemMeta();
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.CONFUSION, 3600, 1), true);
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.HUNGER, 3600, 1), true);
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 1200, 1), true);
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 800, 1), true);
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1200, 1), true);
        potionMeta.displayName(ChatUtils.createDefaultStyledText("Кокаин"));
        potionMeta.setCustomModelData(10);
        potionMeta.lore(ChatUtils.convertStringsToComponents(
                ChatUtils.COLORLESS_DEFAULT_STYLE.color(NamedTextColor.GRAY),
                "",
                "Простой путь",
                "в мир чудес и тюрьмы"
        ));
        potionMeta.getPersistentDataContainer().set(
                MSItemUtils.CUSTOM_ITEM_TYPE_NAMESPACED_KEY,
                PersistentDataType.STRING,
                this.getNamespacedKey().getKey()
        );
        this.itemStack.setItemMeta(potionMeta);
    }

    @Override
    public @NotNull List<Map.Entry<Recipe, Boolean>> initRecipes() {
        ShapedRecipe shapedRecipe = new ShapedRecipe(this.namespacedKey, this.itemStack)
                .shape(
                        "NNN",
                        "FSF",
                        "LFL"
                ).setIngredient('N', Material.IRON_INGOT)
                .setIngredient('F', Material.FERN)
                .setIngredient('S', Material.SUGAR_CANE)
                .setIngredient('L', Material.OAK_LEAVES);
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
