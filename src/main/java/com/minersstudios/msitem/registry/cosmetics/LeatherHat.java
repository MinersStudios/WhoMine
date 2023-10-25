package com.minersstudios.msitem.registry.cosmetics;

import com.minersstudios.mscore.util.Badges;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.msitem.api.CustomItemImpl;
import com.minersstudios.msitem.api.Wearable;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

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
        meta.lore(Badges.PAINTABLE_LORE_LIST);
        meta.addAttributeModifier(
                Attribute.GENERIC_ARMOR,
                new AttributeModifier(UUID.randomUUID(), "armor", 1.0f, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD)
        );
        ITEM_STACK.setItemMeta(meta);
    }

    public LeatherHat() {
        super(KEY, ITEM_STACK);
    }

    @Override
    public @NotNull List<Map.Entry<Recipe, Boolean>> initRecipes() {
        return Collections.singletonList(Map.entry(
                new ShapedRecipe(this.namespacedKey, this.itemStack)
                .shape(
                        " L ",
                        "LLL"
                ).setIngredient('L', Material.LEATHER),
                true
        ));
    }
}
