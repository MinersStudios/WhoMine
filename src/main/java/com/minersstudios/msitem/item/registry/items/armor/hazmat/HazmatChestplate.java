package com.minersstudios.msitem.item.registry.items.armor.hazmat;

import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.msitem.item.CustomItemImpl;
import com.minersstudios.msitem.item.CustomItemType;
import com.minersstudios.msitem.item.damageable.Damageable;
import com.minersstudios.msitem.item.damageable.DamageableItem;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HazmatChestplate extends CustomItemImpl implements Damageable {
    private static final String KEY;
    private static final ItemStack ITEM_STACK;

    static {
        KEY = "hazmat_chestplate";
        ITEM_STACK = new ItemStack(Material.LEATHER_CHESTPLATE);
        final LeatherArmorMeta meta = (LeatherArmorMeta) ITEM_STACK.getItemMeta();

        meta.displayName(ChatUtils.createDefaultStyledText("Антирадиационная куртка"));
        meta.setCustomModelData(1);
        meta.setColor(Color.fromRGB(15712578));
        meta.addItemFlags(ItemFlag.HIDE_DYE);
        meta.addAttributeModifier(
                Attribute.GENERIC_ARMOR,
                new AttributeModifier(UUID.randomUUID(), "armor", 4, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST)
        );
        meta.addAttributeModifier(
                Attribute.GENERIC_ARMOR_TOUGHNESS,
                new AttributeModifier(UUID.randomUUID(), "armor_toughness", 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST)
        );
        ITEM_STACK.setItemMeta(meta);
    }

    public HazmatChestplate() {
        super(KEY, ITEM_STACK);
    }

    @Override
    public @Nullable List<Map.Entry<Recipe, Boolean>> initRecipes() {
        return Collections.singletonList(Map.entry(
                new ShapedRecipe(this.namespacedKey, this.itemStack)
                .shape(
                        "T T",
                        "TPT",
                        "TTT"
                )
                .setIngredient('T', CustomItemType.ANTI_RADIATION_TEXTILE.getCustomItem().getItem())
                .setIngredient('P', CustomItemType.PLUMBUM_INGOT.getCustomItem().getItem()),
                true
        ));
    }

    @Override
    public @NotNull DamageableItem buildDamageable() {
        return new DamageableItem(Material.LEATHER_CHESTPLATE.getMaxDurability(), 170);
    }
}