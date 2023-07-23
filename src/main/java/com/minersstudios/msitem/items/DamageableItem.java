package com.minersstudios.msitem.items;

import com.minersstudios.mscore.utils.ChatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static net.kyori.adventure.text.Component.*;

public class DamageableItem {
    protected final int defaultDamage;
    protected int maxDamage;
    protected int realDamage;

    public static final NamespacedKey MAX_DAMAGE_NAMESPACED_KEY = new NamespacedKey("msitems", "max_damage");
    public static final NamespacedKey REAL_DAMAGE_NAMESPACED_KEY = new NamespacedKey("msitems", "real_damage");

    private static final TranslatableComponent DURABILITY = translatable("item.durability");

    public DamageableItem(
            int defaultDamage,
            int maxDamage
    ) {
        this.defaultDamage = defaultDamage;
        this.maxDamage = maxDamage;
        this.realDamage = 0;
    }

    public DamageableItem(
            int defaultDamage,
            int maxDamage,
            int realDamage
    ) {
        this.defaultDamage = defaultDamage;
        this.maxDamage = maxDamage;
        this.realDamage = realDamage;
    }

    @Contract("null -> null")
    public static @Nullable DamageableItem fromItemStack(@Nullable ItemStack itemStack) {
        if (itemStack == null) return null;
        PersistentDataContainer dataContainer = itemStack.getItemMeta().getPersistentDataContainer();

        if (
                !dataContainer.has(MAX_DAMAGE_NAMESPACED_KEY)
                || !dataContainer.has(REAL_DAMAGE_NAMESPACED_KEY)
        ) return null;

        return new DamageableItem(
                itemStack.getType().getMaxDurability(),
                dataContainer.getOrDefault(MAX_DAMAGE_NAMESPACED_KEY, PersistentDataType.INTEGER, 0),
                dataContainer.getOrDefault(REAL_DAMAGE_NAMESPACED_KEY, PersistentDataType.INTEGER, 0)
        );
    }

    public boolean saveForItemStack(@NotNull ItemStack itemStack) {
        if (
                itemStack.getType().getMaxDurability() != this.defaultDamage
                || !(itemStack.getItemMeta() instanceof Damageable damageable)
        ) return false;

        damageable.getPersistentDataContainer().set(MAX_DAMAGE_NAMESPACED_KEY, PersistentDataType.INTEGER, this.maxDamage);
        damageable.getPersistentDataContainer().set(REAL_DAMAGE_NAMESPACED_KEY, PersistentDataType.INTEGER, this.realDamage);
        damageable.setDamage(Math.round((float) this.realDamage / (float) this.maxDamage * (float) this.defaultDamage));

        var lore = damageable.lore();
        var newLore = new ArrayList<Component>();

        if (lore != null) {
            newLore.addAll(lore);
            if (newLore.get(newLore.size() - 1) instanceof TranslatableComponent) {
                newLore.remove(newLore.size() - 1);
                newLore.remove(newLore.size() - 1);
            }
        }

        newLore.add(empty());
        newLore.add(
                DURABILITY.args(
                        text(this.maxDamage - this.realDamage),
                        text(this.maxDamage)
                ).style(ChatUtils.COLORLESS_DEFAULT_STYLE)
                .color(NamedTextColor.GRAY)
        );
        damageable.lore(newLore);

        return itemStack.setItemMeta(damageable);
    }

    public int getDefaultDamage() {
        return this.defaultDamage;
    }

    public int getMaxDamage() {
        return this.maxDamage;
    }

    public void setMaxDamage(int damage) {
        this.maxDamage = damage;
    }

    public int getRealDamage() {
        return this.realDamage;
    }

    public void setRealDamage(int damage) {
        this.realDamage = damage;
    }
}
