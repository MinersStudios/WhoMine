package com.minersstudios.mscustoms.registry.item;

import com.minersstudios.mscore.annotation.Key;
import com.minersstudios.mscore.inventory.recipe.builder.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.choice.RecipeChoiceEntry;
import com.minersstudios.mscore.inventory.recipe.entry.RecipeEntry;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscore.utility.Font;
import com.minersstudios.mscustoms.custom.item.CustomItemImpl;
import com.minersstudios.mscustoms.custom.item.damageable.Damageable;
import com.minersstudios.mscustoms.custom.item.damageable.DamageableItem;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;

public final class Wrench extends CustomItemImpl implements Damageable {
    private static final @Key String KEY;
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
    public @NotNull @Unmodifiable List<RecipeEntry> initRecipes() {
        return Collections.singletonList(
                RecipeEntry.fromBuilder(
                        RecipeBuilder.shaped()
                        .namespacedKey(this.namespacedKey)
                        .result(this.itemStack)
                        .shape("I", "I", "I")
                        .ingredients(
                                RecipeChoiceEntry.material('I', Material.IRON_INGOT)
                        ),
                        true
                )
        );
    }

    @Override
    public @NotNull DamageableItem buildDamageable() {
        return new DamageableItem(
                Material.IRON_SHOVEL.getMaxDurability(),
                300
        );
    }
}
