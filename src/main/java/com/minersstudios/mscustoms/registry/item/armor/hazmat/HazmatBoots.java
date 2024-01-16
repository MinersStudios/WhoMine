package com.minersstudios.mscustoms.registry.item.armor.hazmat;

import com.minersstudios.mscore.inventory.recipe.builder.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.builder.ShapedRecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.entry.RecipeEntry;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscustoms.custom.item.CustomItemImpl;
import com.minersstudios.mscustoms.custom.item.CustomItemType;
import com.minersstudios.mscustoms.custom.item.damageable.Damageable;
import com.minersstudios.mscustoms.custom.item.damageable.DamageableItem;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class HazmatBoots extends CustomItemImpl implements Damageable {
    private static final String KEY;
    private static final ItemStack ITEM_STACK;

    /** Max durability of this item */
    public static final int MAX_DURABILITY = 140;

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
    public @NotNull @Unmodifiable List<RecipeEntry> initRecipes() {
        return Collections.singletonList(
                RecipeEntry.of(
                        RecipeBuilder.shapedBuilder()
                        .namespacedKey(this.namespacedKey)
                        .result(this.itemStack)
                        .shape(
                                "T T",
                                "P P"
                        )
                        .ingredients(
                                ShapedRecipeBuilder.itemStack('T', CustomItemType.ANTI_RADIATION_TEXTILE.getCustomItem().getItem()),
                                ShapedRecipeBuilder.itemStack('P', CustomItemType.PLUMBUM_INGOT.getCustomItem().getItem())
                        ),
                        true
                )
        );
    }

    @Override
    public @NotNull DamageableItem buildDamageable() {
        return new DamageableItem(
                Material.LEATHER_BOOTS.getMaxDurability(),
                MAX_DURABILITY
        );
    }
}
