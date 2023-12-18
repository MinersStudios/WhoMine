package com.minersstudios.msitem.registry.item;

import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.msitem.api.CustomItemImpl;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class Cocaine extends CustomItemImpl {
    private static final String KEY;
    private static final ItemStack ITEM_STACK;

    static {
        KEY = "cocaine";
        ITEM_STACK = new ItemStack(Material.POTION);
        final PotionMeta meta = (PotionMeta) ITEM_STACK.getItemMeta();

        meta.addCustomEffect(new PotionEffect(PotionEffectType.CONFUSION, 3600, 1), true);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.HUNGER, 3600, 1), true);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 1200, 1), true);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 800, 1), true);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1200, 1), true);
        meta.displayName(ChatUtils.createDefaultStyledText("Кокаин"));
        meta.setCustomModelData(10);
        meta.lore(ChatUtils.convertStringsToComponents(
                ChatUtils.COLORLESS_DEFAULT_STYLE.color(NamedTextColor.GRAY),
                "",
                "Простой путь",
                "в мир чудес и тюрьмы"
        ));
        ITEM_STACK.setItemMeta(meta);
    }

    public Cocaine() {
        super(KEY, ITEM_STACK);
    }

    @Contract(" -> new")
    @Override
    public @NotNull @Unmodifiable List<Map.Entry<Recipe, Boolean>> initRecipes() {
        return Collections.singletonList(Map.entry(
                new ShapedRecipe(this.namespacedKey, this.itemStack)
                .shape(
                        "NNN",
                        "FSF",
                        "LFL"
                ).setIngredient('N', Material.IRON_INGOT)
                .setIngredient('F', Material.FERN)
                .setIngredient('S', Material.SUGAR_CANE)
                .setIngredient('L', Material.OAK_LEAVES),
                true
        ));
    }
}