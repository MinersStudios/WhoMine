package com.minersstudios.msitem.item.damageable;

import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.msitem.item.CustomItemType;
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

/**
 * The {@code DamageableItem} class represents an abstraction
 * for managing damageable items with custom damage values.
 * It provides methods for setting, retrieving, and saving
 * damage-related data to and from {@link ItemStack}s.
 */
public class DamageableItem {
    protected final int defaultDamage;
    protected int maxDamage;
    protected int realDamage;

    public static final NamespacedKey MAX_DAMAGE_NAMESPACED_KEY = new NamespacedKey(CustomItemType.NAMESPACE, "max_damage");
    public static final NamespacedKey REAL_DAMAGE_NAMESPACED_KEY = new NamespacedKey(CustomItemType.NAMESPACE, "real_damage");

    private static final TranslatableComponent DURABILITY = translatable("item.durability");

    /**
     * Constructs a {@code DamageableItem} instance with
     * default, max, and real damage value
     *
     * @param defaultDamage The default damage value of
     *                      the item
     * @param maxDamage     The maximum damage value the
     *                      item can have
     */
    public DamageableItem(
            int defaultDamage,
            int maxDamage
    ) {
        this(defaultDamage, maxDamage, 0);
    }

    /**
     * Constructs a {@code DamageableItem} instance with
     * specified default, max, and real damage values
     *
     * @param defaultDamage The default damage value of
     *                      the item
     * @param maxDamage     The maximum damage value the
     *                      item can have
     * @param realDamage    The current real damage value
     *                      of the item
     */
    public DamageableItem(
            int defaultDamage,
            int maxDamage,
            int realDamage
    ) {
        this.defaultDamage = defaultDamage;
        this.maxDamage = maxDamage;
        this.realDamage = realDamage;
    }

    /**
     * Converts an {@link ItemStack} to a {@code DamageableItem}
     * instance if the item contains necessary data
     *
     * @param itemStack The {@link ItemStack} to convert
     * @return A {@code DamageableItem} instance if conversion
     *         is successful, otherwise null
     */
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

    /**
     * Saves the damage-related data to an {@link ItemStack}'s
     * persistent data container and updates the lore and damage
     * value of the item
     *
     * @param itemStack The {@link ItemStack} to save the data to
     * @return True if the data is successfully saved
     */
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

    /**
     * @return The default damage value of the item
     */
    public int getDefaultDamage() {
        return this.defaultDamage;
    }

    /**
     * @return The maximum damage value the item can have
     */
    public int getMaxDamage() {
        return this.maxDamage;
    }

    /**
     * Sets the maximum damage value the item can have
     *
     * @param damage New maximum damage value the item
     *               can have
     */
    public void setMaxDamage(int damage) {
        this.maxDamage = damage;
    }

    /**
     * @return The current real damage value of the item
     */
    public int getRealDamage() {
        return this.realDamage;
    }

    /**
     * Sets the current real damage value of the item
     *
     * @param damage New real damage value of the item
     */
    public void setRealDamage(int damage) {
        this.realDamage = damage;
    }
}
