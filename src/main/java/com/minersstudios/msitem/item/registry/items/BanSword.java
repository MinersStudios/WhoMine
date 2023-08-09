package com.minersstudios.msitem.item.registry.items;

import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.msitem.item.CustomItemImpl;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class BanSword extends CustomItemImpl {
    private static final String KEY;
    private static final ItemStack ITEM_STACK;

    static {
        KEY = "ban_sword";
        ITEM_STACK = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = ITEM_STACK.getItemMeta();

        meta.displayName(ChatUtils.createDefaultStyledText("Бан-меч"));
        meta.lore(ChatUtils.convertStringsToComponents(
                ChatUtils.COLORLESS_DEFAULT_STYLE.color(NamedTextColor.GRAY),
                "Ходят легенды, про его мощь...",
                "Лишь в руках избранного",
                "он раскрывает свой потенциал"
        ));
        meta.setCustomModelData(20);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
        meta.addAttributeModifier(
                Attribute.GENERIC_ATTACK_DAMAGE,
                new AttributeModifier(UUID.randomUUID(), "attack_damage", Double.POSITIVE_INFINITY, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND)
        );
        meta.addAttributeModifier(
                Attribute.GENERIC_LUCK,
                new AttributeModifier(UUID.randomUUID(), "luck", Double.POSITIVE_INFINITY, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND)
        );
        ITEM_STACK.setItemMeta(meta);
    }

    public BanSword() {
        super(KEY, ITEM_STACK);
    }
}
