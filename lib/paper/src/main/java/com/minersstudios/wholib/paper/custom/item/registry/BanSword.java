package com.minersstudios.wholib.paper.custom.item.registry;

import com.minersstudios.wholib.key.Key;
import com.minersstudios.wholib.utility.ChatUtils;
import com.minersstudios.wholib.paper.custom.item.CustomItemImpl;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@SuppressWarnings("UnstableApiUsage")
public final class BanSword extends CustomItemImpl {
    private static final @Key String KEY;
    private static final ItemStack ITEM_STACK;

    static {
        KEY = "ban_sword";
        ITEM_STACK = new ItemStack(Material.NETHERITE_SWORD);
        final ItemMeta meta = ITEM_STACK.getItemMeta();

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
        meta.addEnchant(Enchantment.SHARPNESS, 1, true);
        meta.addAttributeModifier(
                Attribute.GENERIC_ATTACK_DAMAGE,
                new AttributeModifier(
                        NamespacedKey.minecraft("attack_damage"),
                        Double.POSITIVE_INFINITY,
                        AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlotGroup.HAND
                )
        );
        meta.addAttributeModifier(
                Attribute.GENERIC_LUCK,
                new AttributeModifier(
                        NamespacedKey.minecraft("luck"),
                        Double.POSITIVE_INFINITY,
                        AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlotGroup.HAND
                )
        );
        ITEM_STACK.setItemMeta(meta);
    }

    public BanSword() {
        super(KEY, ITEM_STACK);
    }
}
