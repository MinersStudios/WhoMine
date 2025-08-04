package com.minersstudios.wholib.paper.custom.item.registry.cosmetics;

import com.minersstudios.wholib.key.Key;
import com.minersstudios.wholib.paper.inventory.recipe.builder.RecipeBuilder;
import com.minersstudios.wholib.paper.inventory.recipe.entry.RecipeEntry;
import com.minersstudios.wholib.utility.ChatUtils;
import com.minersstudios.wholib.utility.Font;
import com.minersstudios.wholib.paper.custom.item.CustomItemImpl;
import com.minersstudios.wholib.paper.custom.item.Wearable;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public final class LeatherHat extends CustomItemImpl implements Wearable {
    private static final @Key String KEY;
    private static final ItemStack ITEM_STACK;

    static {
        KEY = "leather_hat";
        ITEM_STACK = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta meta = ITEM_STACK.getItemMeta();

        meta.displayName(ChatUtils.createDefaultStyledText("Кожаная шляпа"));
        meta.setCustomModelData(999);
        meta.lore(Collections.singletonList(Font.Components.PAINTABLE));
        meta.addAttributeModifier(
                Attribute.GENERIC_ARMOR,
                new AttributeModifier(
                        NamespacedKey.minecraft("armor"),
                        1.0d,
                        AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlotGroup.HEAD
                )
        );
        ITEM_STACK.setItemMeta(meta);
    }

    public LeatherHat() {
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
                                " L ",
                                "LLL"
                        )
                        .ingredients(
                                RecipeChoiceEntry.material('L', Material.LEATHER)
                        ),
                        true
                )
        );
    }
}
