package com.minersstudios.mscustoms.registry.item;

import com.minersstudios.mscore.annotation.Key;
import com.minersstudios.mscore.inventory.recipe.builder.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.choice.RecipeChoiceEntry;
import com.minersstudios.mscore.inventory.recipe.entry.RecipeEntry;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscustoms.custom.item.CustomItemImpl;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;

public final class Cocaine extends CustomItemImpl {
    private static final @Key String KEY;
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
    public @NotNull @Unmodifiable List<RecipeEntry> initRecipes() {
        return Collections.singletonList(
                RecipeEntry.fromBuilder(
                        RecipeBuilder.shaped()
                        .namespacedKey(this.namespacedKey)
                        .result(this.itemStack)
                        .shape(
                                "NNN",
                                "FSF",
                                "LFL"
                        )
                        .ingredients(
                                RecipeChoiceEntry.material('N', Material.IRON_INGOT),
                                RecipeChoiceEntry.material('F', Material.FERN),
                                RecipeChoiceEntry.material('S', Material.SUGAR_CANE),
                                RecipeChoiceEntry.material('L', Material.OAK_LEAVES)
                        ),
                        true
                )
        );
    }
}
