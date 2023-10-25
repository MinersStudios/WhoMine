package com.minersstudios.msitem.api;

import com.minersstudios.mscore.util.MSPluginUtils;
import com.minersstudios.msitem.MSItem;
import com.minersstudios.msitem.api.damageable.Damageable;
import com.minersstudios.msitem.registry.cosmetics.LeatherHat;
import com.minersstudios.msitem.registry.items.*;
import com.minersstudios.msitem.registry.items.armor.hazmat.HazmatBoots;
import com.minersstudios.msitem.registry.items.armor.hazmat.HazmatChestplate;
import com.minersstudios.msitem.registry.items.armor.hazmat.HazmatHelmet;
import com.minersstudios.msitem.registry.items.armor.hazmat.HazmatLeggings;
import com.minersstudios.msitem.registry.items.cards.CardsBicycle;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * The CustomItemType enum represents various types of custom items in
 * the MSItem plugin. Each enum value is associated with a specific
 * class that implements the CustomItem interface. This class provides
 * methods to manage and retrieve custom item instances, keys, and types.
 */
public enum CustomItemType {
    LEATHER_HAT(LeatherHat.class),
    PLUMBUM_INGOT(PlumbumIngot.class),
    RAW_PLUMBUM(RawPlumbum.class),
    ANTI_RADIATION_TEXTILE(AntiRadiationTextile.class),
    DOSIMETER(Dosimeter.class),
    HAZMAT_HELMET(HazmatHelmet.class),
    HAZMAT_CHESTPLATE(HazmatChestplate.class),
    HAZMAT_LEGGINGS(HazmatLeggings.class),
    HAZMAT_BOOTS(HazmatBoots.class),
    COCAINE(Cocaine.class),
    WRENCH(Wrench.class),
    CARDS_BICYCLE_BLUE_1(CardsBicycle.Blue1.class),
    CARDS_BICYCLE_BLUE_2(CardsBicycle.Blue2.class),
    CARDS_BICYCLE_RED_1(CardsBicycle.Red1.class),
    CARDS_BICYCLE_RED_2(CardsBicycle.Red2.class),
    BAN_SWORD(BanSword.class);

    private final Class<? extends CustomItem> clazz;

    public static final String NAMESPACE = "msitems";
    public static final NamespacedKey TYPE_NAMESPACED_KEY = new NamespacedKey(NAMESPACE, "type");

    static final Map<String, CustomItemType> KEY_TO_TYPE_MAP = new ConcurrentHashMap<>();
    static final Map<Class<? extends CustomItem>, CustomItem> CLASS_TO_ITEM_MAP = new ConcurrentHashMap<>();
    private static final Map<Class<? extends CustomItem>, CustomItemType> CLASS_TO_TYPE_MAP = new ConcurrentHashMap<>();

    static {
        final var recipesToRegister = new ArrayList<CustomItem>();

        for (final var registry : values()) {
            final CustomItem customItem;

            try {
                customItem = registry.getClazz().getDeclaredConstructor().newInstance();
            } catch (final Exception e) {
                MSItem.logger().log(Level.SEVERE, "Error while initializing custom item " + registry.name() + "!", e);
                continue;
            }

            if (customItem instanceof final Damageable damageable) {
                damageable.buildDamageable().saveForItemStack(customItem.getItem());
            }

            KEY_TO_TYPE_MAP.put(customItem.getKey().getKey().toLowerCase(Locale.ENGLISH), registry);
            CLASS_TO_TYPE_MAP.put(registry.clazz, registry);
            CLASS_TO_ITEM_MAP.put(registry.clazz, customItem);
            recipesToRegister.add(customItem);
        }

        final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        executor.scheduleAtFixedRate(() -> {
            if (MSPluginUtils.isLoadedCustoms()) {
                executor.shutdown();
                recipesToRegister.forEach(CustomItem::registerRecipes);
                recipesToRegister.clear();
            }
        }, 0L, 10L, TimeUnit.MILLISECONDS);
    }

    /**
     * Constructor for CustomItemType enum values
     *
     * @param clazz The associated class that implements
     *              the CustomItem interface
     */
    CustomItemType(final @NotNull Class<? extends CustomItem> clazz) {
        this.clazz = clazz;
    }

    /**
     * @return The class associated with this custom item type
     */
    public @NotNull Class<? extends CustomItem> getClazz() {
        return this.clazz;
    }

    /**
     * @return The CustomItem instance associated with this custom
     *         item type
     */
    public @NotNull CustomItem getCustomItem() {
        return CLASS_TO_ITEM_MAP.get(this.clazz);
    }

    /**
     * @param clazz The target class to cast the custom item instance
     * @param <I>   The type of the target class
     * @return The custom item instance cast to the specified class
     * @throws IllegalArgumentException If the custom item instance cannot
     *                                  be cast to the specified class
     */
    public <I extends CustomItem> @NotNull I getCustomItem(final @NotNull Class<I> clazz) throws IllegalArgumentException {
        final CustomItem customItem = CLASS_TO_ITEM_MAP.get(this.clazz);

        try {
            return clazz.cast(customItem);
        } catch (final ClassCastException e) {
            throw new IllegalArgumentException("Custom item " + this.name() + " is not an instance of " + clazz.getName() + "!");
        }
    }

    /**
     * Gets the {@link CustomItem} from the given key
     *
     * @param key The key to get the custom item type from,
     *            must not be null (case-insensitive)
     * @return The {@link CustomItemType} associated with the given key
     *         or null if the given key is not associated with any custom
     *         item type, or if the given key is null or blank
     * @see #KEY_TO_TYPE_MAP
     */
    @Contract("null -> null")
    public static @Nullable CustomItemType fromKey(final @Nullable String key) {
        return StringUtils.isBlank(key)
                ? null
                : KEY_TO_TYPE_MAP.get(key.toLowerCase(Locale.ENGLISH));
    }

    /**
     * Gets the {@link CustomItem} from the given class
     *
     * @param clazz The class to get the custom item type from
     * @return The {@link CustomItemType} associated with the given class
     *         or null if the given class is not associated with any custom
     *         item type, or if the given class is null
     * @see #CLASS_TO_TYPE_MAP
     */
    @Contract("null -> null")
    public static @Nullable CustomItemType fromClass(final @Nullable Class<? extends CustomItem> clazz) {
        return clazz == null
                ? null
                : CLASS_TO_TYPE_MAP.get(clazz);
    }

    /**
     * Gets the {@link CustomItem} from the given item stack. It will get
     * the namespaced key from the item stack's persistent data container
     * and then get the custom item type from the {@link #KEY_TO_TYPE_MAP}
     *
     * @param itemStack The item stack to get the custom item type from
     * @return The {@link CustomItemType} associated with the given item
     *         stack or null if the given item stack is not associated
     *         with any custom item type, or if the given item stack is
     *         null, or an air item stack
     * @see #fromKey(String)
     */
    @Contract("null -> null")
    public static @Nullable CustomItemType fromItemStack(final @Nullable ItemStack itemStack) {
        if (itemStack == null) return null;

        final ItemMeta itemMeta = itemStack.getItemMeta();
        return itemMeta == null
                ? null
                : fromKey(
                        itemMeta.getPersistentDataContainer().get(TYPE_NAMESPACED_KEY, PersistentDataType.STRING)
                );
    }

    /**
     * @return An unmodifiable view of the set of custom item keys
     * @see #KEY_TO_TYPE_MAP
     */
    public static @NotNull @UnmodifiableView Set<String> keySet() {
        return Collections.unmodifiableSet(KEY_TO_TYPE_MAP.keySet());
    }

    /**
     * @return An unmodifiable view of the set of custom item classes
     *         that implement the CustomItem interface
     * @see #CLASS_TO_TYPE_MAP
     */
    public static @NotNull @UnmodifiableView Set<Class<? extends CustomItem>> classSet() {
        return Collections.unmodifiableSet(CLASS_TO_TYPE_MAP.keySet());
    }

    /**
     * @return An unmodifiable view of the collection of custom item
     *         instances
     * @see #CLASS_TO_ITEM_MAP
     */
    public static @NotNull @UnmodifiableView Collection<CustomItem> customItems() {
        return Collections.unmodifiableCollection(CLASS_TO_ITEM_MAP.values());
    }

    /**
     * @param key The key to check
     * @return True if the {@link #KEY_TO_TYPE_MAP} contains the given key
     */
    @Contract("null -> false")
    public static boolean containsKey(final @Nullable String key) {
        return StringUtils.isNotBlank(key)
                && KEY_TO_TYPE_MAP.containsKey(key.toLowerCase(Locale.ENGLISH));
    }

    /**
     * @param clazz The class to check
     * @return True if the {@link #CLASS_TO_TYPE_MAP} contains the given class
     */
    @Contract("null -> false")
    public static boolean containsClass(final @Nullable Class<? extends CustomItem> clazz) {
        return clazz != null
                && CLASS_TO_TYPE_MAP.containsKey(clazz);
    }
}
