package com.minersstudios.msitem.registry.item.armor.hazmat;

import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.msitem.api.CustomItemImpl;
import com.minersstudios.msitem.api.CustomItemType;
import com.minersstudios.msitem.api.damageable.Damageable;
import com.minersstudios.msitem.api.damageable.DamageableItem;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class HazmatBoots extends CustomItemImpl implements Damageable {
    private static final String KEY;
    private static final ItemStack ITEM_STACK;

    static {
        KEY = "hazmat_boots";
        ITEM_STACK = new ItemStack(Material.LEATHER_BOOTS);
        final LeatherArmorMeta meta = (LeatherArmorMeta) ITEM_STACK.getItemMeta();

        meta.displayName(ChatUtils.createDefaultStyledText("Антирадиационные ботинки"));
        meta.setCustomModelData(1);
        meta.setColor(Color.fromRGB(15712578));
        meta.addItemFlags(ItemFlag.HIDE_DYE);
        meta.addAttributeModifier(
                Attribute.GENERIC_ARMOR,
                new AttributeModifier(UUID.randomUUID(), "armor", 2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET)
        );
        meta.addAttributeModifier(
                Attribute.GENERIC_ARMOR_TOUGHNESS,
                new AttributeModifier(UUID.randomUUID(), "armor_toughness", 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET)
        );
        ITEM_STACK.setItemMeta(meta);
    }

    public HazmatBoots() {
        super(KEY, ITEM_STACK);
    }

    @Contract(" -> new")
    @Override
    public @NotNull @Unmodifiable List<Map.Entry<Recipe, Boolean>> initRecipes() {
        return Collections.singletonList(Map.entry(
                new ShapedRecipe(this.namespacedKey, this.itemStack)
                .shape(
                        "T T",
                        "P P"
                )
                .setIngredient('T', CustomItemType.ANTI_RADIATION_TEXTILE.getCustomItem().getItem())
                .setIngredient('P', CustomItemType.PLUMBUM_INGOT.getCustomItem().getItem()),
                Boolean.TRUE
        ));
    }

    @Override
    public @NotNull DamageableItem buildDamageable() {
        return new DamageableItem(Material.LEATHER_BOOTS.getMaxDurability(), 140);
    }
}
