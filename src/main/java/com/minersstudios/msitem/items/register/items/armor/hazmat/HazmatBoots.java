package com.minersstudios.msitem.items.register.items.armor.hazmat;

import com.minersstudios.mscore.utils.ChatUtils;
import com.minersstudios.mscore.utils.MSItemUtils;
import com.minersstudios.msitem.MSItem;
import com.minersstudios.msitem.items.CustomItem;
import com.minersstudios.msitem.items.DamageableItem;
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

public class HazmatBoots extends DamageableItem implements CustomItem {
    private @NotNull NamespacedKey namespacedKey;
    private @NotNull ItemStack itemStack;
    private @Nullable List<Map.Entry<Recipe, Boolean>> recipes;

    public HazmatBoots() {
        super(Material.LEATHER_BOOTS.getMaxDurability(), 140);
        this.namespacedKey = new NamespacedKey(MSItem.getInstance(), "hazmat_boots");
        this.itemStack = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta itemMeta = (LeatherArmorMeta) this.itemStack.getItemMeta();
        itemMeta.displayName(ChatUtils.createDefaultStyledText("Антирадиационные ботинки"));
        itemMeta.setCustomModelData(1);
        itemMeta.setColor(Color.fromRGB(15712578));
        itemMeta.addItemFlags(ItemFlag.HIDE_DYE);
        itemMeta.addAttributeModifier(
                Attribute.GENERIC_ARMOR,
                new AttributeModifier(UUID.randomUUID(), "armor", 2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET)
        );
        itemMeta.addAttributeModifier(
                Attribute.GENERIC_ARMOR_TOUGHNESS,
                new AttributeModifier(UUID.randomUUID(), "armor_toughness", 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET)
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
        ItemStack plumbumIngot = MSItemUtils.getCustomItemItemStack("plumbum_ingot");
        ShapedRecipe shapedRecipe = new ShapedRecipe(this.namespacedKey, this.itemStack)
                .shape(
                        "T T",
                        "P P"
                )
                .setIngredient('T', textile)
                .setIngredient('P', plumbumIngot);
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
