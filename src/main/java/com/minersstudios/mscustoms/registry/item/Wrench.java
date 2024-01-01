package com.minersstudios.mscustoms.registry.item;

import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscore.utility.Font;
import com.minersstudios.mscustoms.custom.item.CustomItemImpl;
import com.minersstudios.mscustoms.custom.item.damageable.Damageable;
import com.minersstudios.mscustoms.custom.item.damageable.DamageableItem;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class Wrench extends CustomItemImpl implements Damageable {
    private static final String KEY;
    private static final ItemStack ITEM_STACK;

    static {
        KEY = "wrench";
        ITEM_STACK = new ItemStack(Material.IRON_SHOVEL);
        final ItemMeta meta = ITEM_STACK.getItemMeta();

        meta.displayName(ChatUtils.createDefaultStyledText("Гаечный ключ"));
        meta.setCustomModelData(1);
        meta.lore(ChatUtils.convertStringsToComponents(
                ChatUtils.COLORLESS_DEFAULT_STYLE.color(NamedTextColor.GRAY),
                "С его помощью вы можете",
                "изменять вид декораций,",
                "которые помечены как : ",
                "§f" + Font.Chars.WRENCHABLE
        ));
        ITEM_STACK.setItemMeta(meta);
    }

    public Wrench() {
        super(KEY, ITEM_STACK);
    }

    @Contract(" -> new")
    @Override
    public @NotNull @Unmodifiable List<Map.Entry<Recipe, Boolean>> initRecipes() {
        return Collections.singletonList(Map.entry(
                new ShapedRecipe(this.namespacedKey, this.itemStack)
                        .shape("I", "I", "I")
                        .setIngredient('I', Material.IRON_INGOT),
                Boolean.TRUE
        ));
    }

    @Override
    public @NotNull DamageableItem buildDamageable() {
        return new DamageableItem(Material.IRON_SHOVEL.getMaxDurability(), 300);
    }
}
