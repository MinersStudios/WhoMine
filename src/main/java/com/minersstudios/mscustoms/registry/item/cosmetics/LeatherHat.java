package com.minersstudios.mscustoms.registry.item.cosmetics;

import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscore.utility.Font;
import com.minersstudios.mscustoms.custom.item.CustomItemImpl;
import com.minersstudios.mscustoms.custom.item.Wearable;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
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
import java.util.UUID;

public final class LeatherHat extends CustomItemImpl implements Wearable {
    private static final String KEY;
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
                new AttributeModifier(UUID.randomUUID(), "armor", 1.0f, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD)
        );
        ITEM_STACK.setItemMeta(meta);
    }

    public LeatherHat() {
        super(KEY, ITEM_STACK);
    }

    @Contract(" -> new")
    @Override
    public @NotNull @Unmodifiable List<Map.Entry<Recipe, Boolean>> initRecipes() {
        return Collections.singletonList(Map.entry(
                new ShapedRecipe(this.namespacedKey, this.itemStack)
                .shape(
                        " L ",
                        "LLL"
                ).setIngredient('L', Material.LEATHER),
                Boolean.TRUE
        ));
    }
}
