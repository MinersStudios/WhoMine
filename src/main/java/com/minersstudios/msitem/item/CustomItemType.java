package com.minersstudios.msitem.item;

import com.minersstudios.mscore.util.MSPluginUtils;
import com.minersstudios.msitem.MSItem;
import com.minersstudios.msitem.item.damageable.Damageable;
import com.minersstudios.msitem.item.registry.cosmetics.LeatherHat;
import com.minersstudios.msitem.item.registry.items.*;
import com.minersstudios.msitem.item.registry.items.armor.hazmat.HazmatBoots;
import com.minersstudios.msitem.item.registry.items.armor.hazmat.HazmatChestplate;
import com.minersstudios.msitem.item.registry.items.armor.hazmat.HazmatHelmet;
import com.minersstudios.msitem.item.registry.items.armor.hazmat.HazmatLeggings;
import com.minersstudios.msitem.item.registry.items.cards.CardsBicycle;
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

    private static final Map<String, CustomItemType> KEY_TO_TYPE_MAP = new ConcurrentHashMap<>();
    private static final Map<Class<? extends CustomItem>, CustomItemType> CLASS_TO_TYPE_MAP = new ConcurrentHashMap<>();
    private static final Map<Class<? extends CustomItem>, CustomItem> CLASS_TO_ITEM_MAP = new ConcurrentHashMap<>();

    static {
        final var recipesToRegister = new ArrayList<CustomItem>();

        for (final var registry : values()) {
            final CustomItem customItem;

            try {
                customItem = registry.getClazz().getDeclaredConstructor().newInstance();
            } catch (Exception e) {
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
     * @param <T>   The type of the target class
     * @return The custom item instance cast to the specified class
     * @throws IllegalArgumentException If the custom item instance cannot
     *                                  be cast to the specified class
     */
    public <T extends CustomItem> @NotNull T getCustomItem(final @NotNull Class<T> clazz) throws IllegalArgumentException {
        final CustomItem customItem = CLASS_TO_ITEM_MAP.get(this.clazz);

        try {
            return clazz.cast(customItem);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Custom item " + this.name() + " is not an instance of " + clazz.getName() + "!");
        }
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
     * Gets the {@link CustomItem} from the given custom item key. It will
     * get the custom item type from the {@link #KEY_TO_TYPE_MAP} and then
     * get the custom item instance from the returned type using the default
     * {@link CustomItem} class
     *
     * @param key The key to get the custom item type from,
     *            must not be null (case-insensitive)
     * @return An {@link Optional} containing the {@link CustomItem}
     *         or an {@link Optional#empty()} if the given key is not
     *         associated with any custom item
     * @see #KEY_TO_TYPE_MAP
     * @see #fromKey(String, Class)
     */
    public static @NotNull Optional<CustomItem> fromKey(final @Nullable String key) {
        return fromKey(key, CustomItem.class);
    }

    /**
     * Gets the {@link CustomItem} from the given custom item key. It will
     * get the custom item type from the {@link #KEY_TO_TYPE_MAP} and then
     * get the custom item instance from the returned type using the given
     * class to cast the custom item instance
     *
     * @param key   The key to get the custom item type from,
     *              must not be null (case-insensitive)
     * @param clazz The target class to cast the custom item instance
     * @param <T>   The type of the target class
     * @return An {@link Optional} containing the {@link CustomItem}
     *         or an {@link Optional#empty()} if the given key is not
     *         associated with any custom item or if the custom item
     *         instance cannot be cast to the specified class
     * @see #getCustomItem(Class)
     * @see #typeOf(String)
     */
    public static <T extends CustomItem> @NotNull Optional<T> fromKey(
            final @Nullable String key,
            final @Nullable Class<T> clazz
    ) {
        if (
                key == null
                || clazz == null
        ) return Optional.empty();

        final CustomItemType type = typeOf(key);
        return type != null
                && clazz.isInstance(type.getCustomItem())
                ? Optional.of(type.getCustomItem(clazz))
                : Optional.empty();
    }

    /**
     * Gets the {@link CustomItem} from the given class. It will get the
     * custom item instance from the {@link #CLASS_TO_ITEM_MAP} using the
     * given class to cast the custom item instance
     *
     * @param clazz The class to get the custom item type from
     * @param <T>   The type of the target class
     * @return An {@link Optional} containing the {@link CustomItem}
     *         or an {@link Optional#empty()} if the given class is not
     *         associated with any custom item
     * @see #CLASS_TO_ITEM_MAP
     */
    public static <T extends CustomItem> @NotNull Optional<T> fromClass(final @Nullable Class<T> clazz) {
        return clazz == null
                ? Optional.empty()
                : Optional.ofNullable(clazz.cast(CLASS_TO_ITEM_MAP.get(clazz)));
    }

    /**
     * Gets the {@link CustomItem} from the given item stack. It will get
     * the namespaced key from the item stack's persistent data container
     * and then get the custom item instance from the {@link #KEY_TO_TYPE_MAP}
     * using the default {@link CustomItem} class
     *
     * @param itemStack The item stack to get the custom item type from
     * @return An {@link Optional} containing the {@link CustomItem}
     *         or an {@link Optional#empty()} if the given item stack
     *         is not associated with any custom item or is null or an
     *         air item stack
     * @see #fromItemStack(ItemStack, Class)
     */
    public static @NotNull Optional<CustomItem> fromItemStack(final @Nullable ItemStack itemStack) {
        return fromItemStack(itemStack, CustomItem.class);
    }

    /**
     * Gets the {@link CustomItem} from the given item stack. It will get
     * the namespaced key from the item stack's persistent data container
     * and then get the custom item instance from the {@link #KEY_TO_TYPE_MAP}
     * using the given class to cast the custom item instance
     *
     * @param itemStack The item stack to get the custom item type from
     * @param clazz     The target class to cast the custom item instance
     * @param <T>       The type of the target class
     * @return An {@link Optional} containing the {@link CustomItem}
     *         or an {@link Optional#empty()} if the given item stack
     *         or class is null, or an air item stack, or if the custom
     *         item instance cannot be cast to the specified class
     * @see #fromKey(String, Class)
     */
    public static <T extends CustomItem> @NotNull Optional<T> fromItemStack(
            final @Nullable ItemStack itemStack,
            final @Nullable Class<T> clazz
    ) {
        if (
                itemStack == null
                || clazz == null
        ) return Optional.empty();

        final ItemMeta itemMeta = itemStack.getItemMeta();
        return itemMeta == null
                ? Optional.empty()
                : fromKey(
                        itemMeta.getPersistentDataContainer().get(TYPE_NAMESPACED_KEY, PersistentDataType.STRING),
                        clazz
                );
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
    public static @Nullable CustomItemType typeOf(final @Nullable String key) {
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
    public static @Nullable CustomItemType typeOf(final @Nullable Class<? extends CustomItem> clazz) {
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
     * @see #typeOf(String)
     */
    @Contract("null -> null")
    public static @Nullable CustomItemType typeOf(final @Nullable ItemStack itemStack) {
        if (itemStack == null) return null;

        final ItemMeta itemMeta = itemStack.getItemMeta();
        return itemMeta == null
                ? null
                : typeOf(
                        itemMeta.getPersistentDataContainer().get(TYPE_NAMESPACED_KEY, PersistentDataType.STRING)
                );
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

    /**
     * Checks if the item stack is a custom item by verifying
     * if it has a valid key associated with it
     *
     * @param itemStack The item stack to check
     * @return True if the item stack is a custom item
     * @see #fromItemStack(ItemStack)
     */
    @Contract("null -> false")
    public static boolean isCustomItem(final @Nullable ItemStack itemStack) {
        return fromItemStack(itemStack).isPresent();
    }
}
