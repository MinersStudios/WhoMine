package com.github.minersstudios.msitem.items.register.items.armor.hazmat;

import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.mscore.utils.MSItemUtils;
import com.github.minersstudios.msitem.MSItem;
import com.github.minersstudios.msitem.items.CustomItem;
import com.github.minersstudios.msitem.items.DamageableItem;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HazmatLeggings extends DamageableItem implements CustomItem {
    private @NotNull NamespacedKey namespacedKey;
    private @NotNull ItemStack itemStack;
    private @Nullable List<Map.Entry<Recipe, Boolean>> recipes;

    public HazmatLeggings() {
        super(Material.LEATHER_LEGGINGS.getMaxDurability(), 150);
        this.namespacedKey = new NamespacedKey(MSItem.getInstance(), "hazmat_leggings");
        this.itemStack = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta itemMeta = (LeatherArmorMeta) this.itemStack.getItemMeta();
        itemMeta.displayName(ChatUtils.createDefaultStyledText("Антирадиационные штаны"));
        itemMeta.setCustomModelData(1);
        itemMeta.setColor(Color.fromRGB(15712578));
        itemMeta.addItemFlags(ItemFlag.HIDE_DYE);
        itemMeta.addAttributeModifier(
                Attribute.GENERIC_ARMOR,
                new AttributeModifier(UUID.randomUUID(), "armor", 3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS)
        );
        itemMeta.addAttributeModifier(
                Attribute.GENERIC_ARMOR_TOUGHNESS,
                new AttributeModifier(UUID.randomUUID(), "armor_toughness", 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS)
        );
        itemMeta.getPersistentDataContainer().set(
                MSItemUtils.CUSTOM_ITEM_TYPE_NAMESPACED_KEY,
                PersistentDataType.STRING,
                this.getNamespacedKey().getKey()
        );
        this.itemStack.setItemMeta(itemMeta);
    }

    @Override
    public @NotNull List<Map.Entry<Recipe, Boolean>> initRecipes() {
        ItemStack textile = MSItemUtils.getCustomItemItemStack("anti_radiation_textile");
        ShapedRecipe shapedRecipe = new ShapedRecipe(this.namespacedKey, this.itemStack)
                .shape(
                        "TTT",
                        "T T",
                        "T T"
                )
                .setIngredient('T', textile);
        return this.recipes = List.of(Map.entry(shapedRecipe, true));
    }

    @Override
    public @NotNull CustomItem clone() {
        try {
            return (CustomItem) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull NamespacedKey getNamespacedKey() {
        return this.namespacedKey;
    }

    @Override
    public void setNamespacedKey(@NotNull NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
    }

    @Override
    public @NotNull ItemStack getItemStack() {
        return this.itemStack;
    }

    @Override
    public void setItemStack(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public @Nullable List<Map.Entry<Recipe, Boolean>> getRecipes() {
        return this.recipes;
    }

    @Override
    public void setRecipes(@Nullable List<Map.Entry<Recipe, Boolean>> recipes) {
        this.recipes = recipes;
    }
}
