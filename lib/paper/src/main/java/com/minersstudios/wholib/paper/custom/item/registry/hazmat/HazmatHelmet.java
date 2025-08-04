package com.minersstudios.wholib.paper.custom.item.registry.hazmat;

import com.minersstudios.wholib.key.Key;
import com.minersstudios.wholib.paper.inventory.recipe.builder.RecipeBuilder;
import com.minersstudios.wholib.paper.inventory.recipe.entry.RecipeEntry;
import com.minersstudios.wholib.utility.ChatUtils;
import com.minersstudios.wholib.paper.custom.item.CustomItemImpl;
import com.minersstudios.wholib.paper.custom.item.CustomItemType;
import com.minersstudios.wholib.paper.custom.item.damageable.Damageable;
import com.minersstudios.wholib.paper.custom.item.damageable.DamageableItem;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public final class HazmatHelmet extends CustomItemImpl implements Damageable {
    private static final @Key String KEY;
    private static final ItemStack ITEM_STACK;

    /** Max durability of this item */
    public static final int MAX_DURABILITY = 120;

    static {
        KEY = "hazmat_helmet";
        ITEM_STACK = new ItemStack(Material.LEATHER_HELMET);
        final LeatherArmorMeta meta = (LeatherArmorMeta) ITEM_STACK.getItemMeta();

        meta.displayName(ChatUtils.createDefaultStyledText("Антирадиационный шлем"));
        meta.setCustomModelData(1);
        meta.setColor(Color.fromRGB(15712578));
        meta.addItemFlags(ItemFlag.HIDE_DYE);
        meta.addAttributeModifier(
                Attribute.GENERIC_ARMOR,
                new AttributeModifier(
                        NamespacedKey.minecraft("armor"),
                        2.0d,
                        AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlotGroup.HEAD
                )
        );
        meta.addAttributeModifier(
                Attribute.GENERIC_ARMOR_TOUGHNESS,
                new AttributeModifier(
                        NamespacedKey.minecraft("armor_toughness"),
                        1.0d,
                        AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlotGroup.HEAD
                )
        );
        ITEM_STACK.setItemMeta(meta);
    }

    public HazmatHelmet() {
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
                                "TTT",
                                "TPT"
                        )
                        .ingredients(
                                RecipeChoiceEntry.itemStack('T', CustomItemType.ANTI_RADIATION_TEXTILE.getCustomItem().getItem()),
                                RecipeChoiceEntry.itemStack('P', CustomItemType.PLUMBUM_INGOT.getCustomItem().getItem())
                        ),
                        true
                )
        );
    }

    @Override
    public @NotNull DamageableItem buildDamageable() {
        return new DamageableItem(
                Material.LEATHER_HELMET.getMaxDurability(),
                MAX_DURABILITY
        );
    }
}
