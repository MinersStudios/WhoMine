package com.minersstudios.msitem.item;

import com.google.common.base.Preconditions;
import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.util.MSPluginUtils;
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
    CARDS_BICYCLE(CardsBicycle.class),
    BAN_SWORD(BanSword.class);

    private final Class<? extends CustomItem> clazz;

    public static final String NAMESPACE = "msitems";
    public static final NamespacedKey TYPE_NAMESPACED_KEY = new NamespacedKey(NAMESPACE, "type");

    private static final Map<String, CustomItemType> KEY_TO_TYPE_MAP = new ConcurrentHashMap<>();
    private static final Map<Class<? extends CustomItem>, CustomItemType> CLASS_TO_TYPE_MAP = new ConcurrentHashMap<>();
    private static final Map<Class<? extends CustomItem>, CustomItem> CLASS_TO_ITEM_MAP = new ConcurrentHashMap<>();

    static {
        var recipesToRegister = new LinkedList<CustomItem>();

        for (var registry : values()) {
            CustomItem customItem;

            try {
                customItem = registry.getClazz().getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                MSLogger.log(Level.SEVERE, "Error while initializing custom item " + registry.name() + "!", e);
                continue;
            }

            if (customItem instanceof Damageable damageable) {
                damageable.buildDamageable().saveForItemStack(customItem.getItem());
            }

            if (customItem instanceof Typed typed) {
                for (var type : typed.getTypes()) {
                    String key = type.getKey().getKey();

                    Preconditions.checkArgument(!containsKey(key), "Duplicate key " + key + " for type " + type.getClass().getName() + "!");
                    KEY_TO_TYPE_MAP.put(key, registry);
                }
            }

            KEY_TO_TYPE_MAP.put(customItem.getKey().getKey().toLowerCase(Locale.ENGLISH), registry);
            CLASS_TO_TYPE_MAP.put(registry.clazz, registry);
            CLASS_TO_ITEM_MAP.put(registry.clazz, customItem);
            recipesToRegister.add(customItem);
        }

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        executor.scheduleAtFixedRate(() -> {
            if (MSPluginUtils.isLoadedCustoms()) {
                executor.shutdown();
                recipesToRegister.forEach(CustomItem::registerRecipes);
                recipesToRegister.clear();
            }
        }, 0L, 10L, TimeUnit.MILLISECONDS);
    }

    CustomItemType(@NotNull Class<? extends CustomItem> clazz) {
        this.clazz = clazz;
    }

    public @NotNull Class<? extends CustomItem> getClazz() {
        return this.clazz;
    }

    public @NotNull CustomItem getCustomItem() {
        return CLASS_TO_ITEM_MAP.get(this.clazz);
    }

    public <T extends CustomItem> @NotNull T getCustomItem(@NotNull Class<T> clazz) throws IllegalArgumentException {
        CustomItem customItem = CLASS_TO_ITEM_MAP.get(this.clazz);

        try {
            return clazz.cast(customItem);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Custom item " + this.name() + " is not an instance of " + clazz.getName() + "!");
        }
    }

    public static @NotNull @UnmodifiableView Set<String> keySet() {
        return Collections.unmodifiableSet(KEY_TO_TYPE_MAP.keySet());
    }

    public static @NotNull @UnmodifiableView Set<Class<? extends CustomItem>> classSet() {
        return Collections.unmodifiableSet(CLASS_TO_TYPE_MAP.keySet());
    }

    public static @NotNull @UnmodifiableView Collection<CustomItem> customItems() {
        return Collections.unmodifiableCollection(CLASS_TO_ITEM_MAP.values());
    }

    @Contract("null -> false")
    public static boolean containsKey(@Nullable String key) {
        return !StringUtils.isBlank(key)
                && KEY_TO_TYPE_MAP.containsKey(key);
    }

    @Contract("null -> false")
    public static boolean containsClass(@Nullable Class<? extends CustomItem> clazz) {
        return clazz != null
                && CLASS_TO_TYPE_MAP.containsKey(clazz);
    }

    @Contract("null -> false")
    public static boolean isCustomItem(@Nullable ItemStack itemStack) {
        return fromItemStack(itemStack).isPresent();
    }

    public static @NotNull Optional<CustomItem> fromKey(@Nullable String key) {
        return fromKey(key, CustomItem.class);
    }

    public static <T extends CustomItem> @NotNull Optional<T> fromKey(
            @Nullable String key,
            @Nullable Class<T> clazz
    ) {
        if (
                key == null
                || clazz == null
        ) return Optional.empty();

        CustomItemType type = typeOf(key);
        return type != null
                && clazz.isInstance(type.getCustomItem())
                ? Optional.of(type.getCustomItem(clazz))
                : Optional.empty();
    }

    public static <T extends CustomItem> @NotNull Optional<T> fromClass(@Nullable Class<T> clazz) {
        if (clazz == null) return Optional.empty();

        CustomItem customItem = CLASS_TO_ITEM_MAP.get(clazz);

        try {
            return Optional.ofNullable(clazz.cast(customItem));
        } catch (ClassCastException e) {
            return Optional.empty();
        }
    }

    public static @NotNull Optional<CustomItem> fromItemStack(@Nullable ItemStack itemStack) {
        return fromItemStack(itemStack, CustomItem.class);
    }

    public static <T extends CustomItem> @NotNull Optional<T> fromItemStack(
            @Nullable ItemStack itemStack,
            @Nullable Class<T> clazz
    ) {
        if (
                itemStack == null
                || clazz == null
        ) return Optional.empty();

        ItemMeta itemMeta = itemStack.getItemMeta();
        return itemMeta == null
                ? Optional.empty()
                : fromKey(
                        itemMeta.getPersistentDataContainer().get(TYPE_NAMESPACED_KEY, PersistentDataType.STRING),
                        clazz
                );
    }

    @Contract("null -> null")
    public static @Nullable CustomItemType typeOf(@Nullable String key) {
        return StringUtils.isBlank(key)
                ? null
                : KEY_TO_TYPE_MAP.get(key.toLowerCase(Locale.ENGLISH));
    }

    @Contract("null -> null")
    public static @Nullable CustomItemType typeOf(@Nullable Class<? extends CustomItem> clazz) {
        return clazz == null
                ? null
                : CLASS_TO_TYPE_MAP.get(clazz);
    }

    @Contract("null -> null")
    public static @Nullable CustomItemType typeOf(@Nullable ItemStack itemStack) {
        if (itemStack == null) return null;

        ItemMeta itemMeta = itemStack.getItemMeta();
        return itemMeta == null
                ? null
                : typeOf(
                        itemMeta.getPersistentDataContainer().get(TYPE_NAMESPACED_KEY, PersistentDataType.STRING)
                );
    }
}
