package com.minersstudios.msitem.items.register.items;

import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.mscore.util.MSItemUtils;
import com.minersstudios.msitem.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Dosimeter implements CustomItem {
    private @NotNull NamespacedKey namespacedKey;
    private @NotNull ItemStack itemStack;
    private @Nullable List<Map.Entry<Recipe, Boolean>> recipes;

    public static final @NotNull NamespacedKey DOSIMETER = new NamespacedKey("msitems", "dosimeter");

    public Dosimeter() {
        this.namespacedKey = new NamespacedKey("msitems", "dosimeter");
        this.itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.displayName(ChatUtils.createDefaultStyledText("Дозиметр радиации"));
        itemMeta.setCustomModelData(1372);
        itemMeta.getPersistentDataContainer().set(
                MSItemUtils.CUSTOM_ITEM_TYPE_NAMESPACED_KEY,
                PersistentDataType.STRING,
                this.getNamespacedKey().getKey()
        );
        this.setEnabled(false);
        this.itemStack.setItemMeta(itemMeta);
    }

    public boolean isEnabled() {
        return this.itemStack.getItemMeta().getPersistentDataContainer().getOrDefault(DOSIMETER, PersistentDataType.BYTE, (byte) 0) == 1;
    }

    public void setEnabled(boolean enabled) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta == null) return;
        itemMeta.getPersistentDataContainer().set(DOSIMETER, PersistentDataType.BYTE, (byte) (enabled ? 1 : 0));
        if (!enabled) {
            itemMeta.setCustomModelData(ScreenType.OFF.customModelData);
        } else {
            itemMeta.setCustomModelData(ScreenType.GREEN.customModelData);
        }
        this.itemStack.setItemMeta(itemMeta);
    }

    public @NotNull ScreenType getScreenType() {
        ScreenType screenType = ScreenType.getScreenType(this.itemStack.getItemMeta().getCustomModelData());
        return screenType == null ? ScreenType.OFF : screenType;
    }

    public void setScreenType(@NotNull ScreenType screenType) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.setCustomModelData(screenType.customModelData);
        this.itemStack.setItemMeta(itemMeta);
    }

    public void setScreenTypeByRadius(@NotNull List<Double> radii, @Nullable Double radius) {
        if (radius == null || radii.isEmpty()) {
            this.setScreenType(ScreenType.GREEN);
            return;
        }

        List<Double> firstHalf = radii.subList(0, radii.size() / 2);
        if (firstHalf.contains(radius)) {
            this.setScreenType(ScreenType.RED);
        } else {
            this.setScreenType(ScreenType.YELLOW);
        }
    }

    @Override
    public @NotNull List<Map.Entry<Recipe, Boolean>> initRecipes() {
        var plumbumIngot = MSItemUtils.getCustomItemItemStack("plumbum_ingot");

        if (plumbumIngot.isEmpty()) {
            MSLogger.warning("Can't find custom item with key: plumbum_ingot");
            return this.recipes = Collections.emptyList();
        }

        ShapedRecipe shapedRecipe = new ShapedRecipe(this.namespacedKey, this.itemStack)
                .shape(
                        "III",
                        "ILI",
                        "IRI"
                )
                .setIngredient('I', plumbumIngot.get())
                .setIngredient('L', Material.REDSTONE_LAMP)
                .setIngredient('R', Material.REDSTONE_TORCH);
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

    public enum ScreenType {
        OFF(1372),
        GREEN(1373),
        YELLOW(1374),
        RED(1375);

        public static final List<ScreenType> VALUES = Arrays.stream(values()).toList();

        private final int customModelData;

        ScreenType(int customModelData) {
            this.customModelData = customModelData;
        }

        public int getCustomModelData() {
            return this.customModelData;
        }

        public static @Nullable ScreenType getScreenType(int customModelData) {
            for (ScreenType screenType : VALUES) {
                if (screenType.customModelData == customModelData) {
                    return screenType;
                }
            }
            return null;
        }
    }
}
