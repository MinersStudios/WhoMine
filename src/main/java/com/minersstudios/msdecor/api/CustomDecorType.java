package com.minersstudios.msdecor.api;

import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.mscore.util.MSPluginUtils;
import com.minersstudios.msdecor.MSDecor;
import com.minersstudios.msdecor.registry.christmas.*;
import com.minersstudios.msdecor.registry.decoration.home.*;
import com.minersstudios.msdecor.registry.decoration.home.head.DeerHead;
import com.minersstudios.msdecor.registry.decoration.home.head.HoglinHead;
import com.minersstudios.msdecor.registry.decoration.home.head.ZoglinHead;
import com.minersstudios.msdecor.registry.decoration.home.plush.BMOPlush;
import com.minersstudios.msdecor.registry.decoration.home.plush.BrownBearPlush;
import com.minersstudios.msdecor.registry.decoration.home.plush.RacoonPlush;
import com.minersstudios.msdecor.registry.decoration.home.plush.WhocintoshMini;
import com.minersstudios.msdecor.registry.decoration.street.Brazier;
import com.minersstudios.msdecor.registry.decoration.street.FireHydrant;
import com.minersstudios.msdecor.registry.decoration.street.IronTrashcan;
import com.minersstudios.msdecor.registry.decoration.street.Wheelbarrow;
import com.minersstudios.msdecor.registry.furniture.Nightstand;
import com.minersstudios.msdecor.registry.furniture.chair.*;
import com.minersstudios.msdecor.registry.furniture.lamp.BigLamp;
import com.minersstudios.msdecor.registry.furniture.lamp.SmallLamp;
import com.minersstudios.msdecor.registry.furniture.table.BigTable;
import com.minersstudios.msdecor.registry.furniture.table.SmallTable;
import com.minersstudios.msdecor.registry.other.Poop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum CustomDecorType {
    //<editor-fold desc="Types" defaultstate="collapsed">
    CHRISTMAS_BALL(ChristmasBall.class),
    CHRISTMAS_TALL_BALL(ChristmasTallBall.class),
    SNOWMAN_BALL(SnowmanBall.class),
    STAR_ON_STRING(StarOnString.class),
    SNOWFLAKE_ON_STRING(SnowflakeOnString.class),
    SANTA_SOCK(SantaSock.class),
    SNOWMAN(Snowman.class),
    TREE_STAR(TreeStar.class),
    //SKELETON_HAND(),
    WHOCINTOSH(Whocintosh.class),
    DEER_HEAD(DeerHead.class),
    HOGLIN_HEAD(HoglinHead.class),
    ZOGLIN_HEAD(ZoglinHead.class),
    BMO_PLUSH(BMOPlush.class),
    BROWN_BEAR_PLUSH(BrownBearPlush.class),
    RACOON_PLUSH(RacoonPlush.class),
    WHOCINTOSH_MINI(WhocintoshMini.class),
    CELL(Cell.class),
    COOKING_POT(CookingPot.class),
    OLD_CAMERA(OldCamera.class),
    PATEFON(Patefon.class),
    CLAY_PIGGYBANK(Piggybank.Clay.class),
    DIAMOND_PIGGYBANK(Piggybank.Diamond.class),
    EMERALD_PIGGYBANK(Piggybank.Emerald.class),
    GOLD_PIGGYBANK(Piggybank.Gold.class),
    IRON_PIGGYBANK(Piggybank.Iron.class),
    NETHERITE_PIGGYBANK(Piggybank.Netherite.class),
    SMALL_CLOCK(SmallClock.class),
    SMALL_GLOBE(SmallGlobe.class),
    ATM(com.minersstudios.msdecor.registry.decoration.street.ATM.class),
    BRAZIER(Brazier.class),
    FIRE_HYDRANT(FireHydrant.class),
    IRON_TRASHCAN(IronTrashcan.class),
    WHEELBARROW(Wheelbarrow.class),
    BIG_LAMP(BigLamp.class),
    SMALL_LAMP(SmallLamp.class),
    BAR_STOOL(BarStool.class),
    COOL_CHAIR(CoolChair.class),
    COOL_ARMCHAIR(CoolArmchair.class),
    ACACIA_CHAIR(Chair.Acacia.class),
    BIRCH_CHAIR(Chair.Birch.class),
    CHERRY_CHAIR(Chair.Cherry.class),
    CRIMSON_CHAIR(Chair.Crimson.class),
    DARK_OAK_CHAIR(Chair.DarkOak.class),
    JUNGLE_CHAIR(Chair.Jungle.class),
    MANGROVE_CHAIR(Chair.Mangrove.class),
    OAK_CHAIR(Chair.Oak.class),
    SPRUCE_CHAIR(Chair.Spruce.class),
    WARPED_CHAIR(Chair.Warped.class),
    ACACIA_ARMCHAIR(Armchair.Acacia.class),
    BIRCH_ARMCHAIR(Armchair.Birch.class),
    CHERRY_ARMCHAIR(Armchair.Cherry.class),
    CRIMSON_ARMCHAIR(Armchair.Crimson.class),
    DARK_OAK_ARMCHAIR(Armchair.DarkOak.class),
    JUNGLE_ARMCHAIR(Armchair.Jungle.class),
    MANGROVE_ARMCHAIR(Armchair.Mangrove.class),
    OAK_ARMCHAIR(Armchair.Oak.class),
    SPRUCE_ARMCHAIR(Armchair.Spruce.class),
    WARPED_ARMCHAIR(Armchair.Warped.class),
    SMALL_ACACIA_CHAIR(SmallChair.Acacia.class),
    SMALL_BIRCH_CHAIR(SmallChair.Birch.class),
    SMALL_CHERRY_CHAIR(SmallChair.Cherry.class),
    SMALL_CRIMSON_CHAIR(SmallChair.Crimson.class),
    SMALL_DARK_OAK_CHAIR(SmallChair.DarkOak.class),
    SMALL_JUNGLE_CHAIR(SmallChair.Jungle.class),
    SMALL_MANGROVE_CHAIR(SmallChair.Mangrove.class),
    SMALL_OAK_CHAIR(SmallChair.Oak.class),
    SMALL_SPRUCE_CHAIR(SmallChair.Spruce.class),
    SMALL_WARPED_CHAIR(SmallChair.Warped.class),
    SMALL_ACACIA_ARMCHAIR(SmallArmchair.Acacia.class),
    SMALL_BIRCH_ARMCHAIR(SmallArmchair.Birch.class),
    SMALL_CHERRY_ARMCHAIR(SmallArmchair.Cherry.class),
    SMALL_CRIMSON_ARMCHAIR(SmallArmchair.Crimson.class),
    SMALL_DARK_OAK_ARMCHAIR(SmallArmchair.DarkOak.class),
    SMALL_JUNGLE_ARMCHAIR(SmallArmchair.Jungle.class),
    SMALL_MANGROVE_ARMCHAIR(SmallArmchair.Mangrove.class),
    SMALL_OAK_ARMCHAIR(SmallArmchair.Oak.class),
    SMALL_SPRUCE_ARMCHAIR(SmallArmchair.Spruce.class),
    SMALL_WARPED_ARMCHAIR(SmallArmchair.Warped.class),
    ACACIA_ROCKING_CHAIR(RockingChair.Acacia.class),
    BIRCH_ROCKING_CHAIR(RockingChair.Birch.class),
    CHERRY_ROCKING_CHAIR(RockingChair.Cherry.class),
    CRIMSON_ROCKING_CHAIR(RockingChair.Crimson.class),
    DARK_OAK_ROCKING_CHAIR(RockingChair.DarkOak.class),
    JUNGLE_ROCKING_CHAIR(RockingChair.Jungle.class),
    MANGROVE_ROCKING_CHAIR(RockingChair.Mangrove.class),
    OAK_ROCKING_CHAIR(RockingChair.Oak.class),
    SPRUCE_ROCKING_CHAIR(RockingChair.Spruce.class),
    WARPED_ROCKING_CHAIR(RockingChair.Warped.class),
    PAINTABLE_ACACIA_ROCKING_CHAIR(PaintableRockingChair.Acacia.class),
    PAINTABLE_BIRCH_ROCKING_CHAIR(PaintableRockingChair.Birch.class),
    PAINTABLE_CHERRY_ROCKING_CHAIR(PaintableRockingChair.Cherry.class),
    PAINTABLE_CRIMSON_ROCKING_CHAIR(PaintableRockingChair.Crimson.class),
    PAINTABLE_DARK_OAK_ROCKING_CHAIR(PaintableRockingChair.DarkOak.class),
    PAINTABLE_JUNGLE_ROCKING_CHAIR(PaintableRockingChair.Jungle.class),
    PAINTABLE_MANGROVE_ROCKING_CHAIR(PaintableRockingChair.Mangrove.class),
    PAINTABLE_OAK_ROCKING_CHAIR(PaintableRockingChair.Oak.class),
    PAINTABLE_SPRUCE_ROCKING_CHAIR(PaintableRockingChair.Spruce.class),
    PAINTABLE_WARPED_ROCKING_CHAIR(PaintableRockingChair.Warped.class),
    ACACIA_NIGHTSTAND(Nightstand.Acacia.class),
    BIRCH_NIGHTSTAND(Nightstand.Birch.class),
    CHERRY_NIGHTSTAND(Nightstand.Cherry.class),
    CRIMSON_NIGHTSTAND(Nightstand.Crimson.class),
    DARK_OAK_NIGHTSTAND(Nightstand.DarkOak.class),
    JUNGLE_NIGHTSTAND(Nightstand.Jungle.class),
    MANGROVE_NIGHTSTAND(Nightstand.Mangrove.class),
    OAK_NIGHTSTAND(Nightstand.Oak.class),
    SPRUCE_NIGHTSTAND(Nightstand.Spruce.class),
    WARPED_NIGHTSTAND(Nightstand.Warped.class),
    ACACIA_BIG_TABLE(BigTable.Acacia.class),
    BIRCH_BIG_TABLE(BigTable.Birch.class),
    CHERRY_BIG_TABLE(BigTable.Cherry.class),
    CRIMSON_BIG_TABLE(BigTable.Crimson.class),
    DARK_OAK_BIG_TABLE(BigTable.DarkOak.class),
    JUNGLE_BIG_TABLE(BigTable.Jungle.class),
    MANGROVE_BIG_TABLE(BigTable.Mangrove.class),
    OAK_BIG_TABLE(BigTable.Oak.class),
    SPRUCE_BIG_TABLE(BigTable.Spruce.class),
    WARPED_BIG_TABLE(BigTable.Warped.class),
    ACACIA_SMALL_TABLE(SmallTable.Acacia.class),
    BIRCH_SMALL_TABLE(SmallTable.Birch.class),
    CHERRY_SMALL_TABLE(SmallTable.Cherry.class),
    CRIMSON_SMALL_TABLE(SmallTable.Crimson.class),
    DARK_OAK_SMALL_TABLE(SmallTable.DarkOak.class),
    JUNGLE_SMALL_TABLE(SmallTable.Jungle.class),
    MANGROVE_SMALL_TABLE(SmallTable.Mangrove.class),
    OAK_SMALL_TABLE(SmallTable.Oak.class),
    SPRUCE_SMALL_TABLE(SmallTable.Spruce.class),
    WARPED_SMALL_TABLE(SmallTable.Warped.class),
    POOP(Poop.class);
    //</editor-fold>

    private final Class<? extends CustomDecorData<?>> clazz;

    public static final String NAMESPACE = "msdecor";
    public static final String TYPE_TAG_NAME = "type";
    public static final NamespacedKey TYPE_NAMESPACED_KEY = new NamespacedKey(NAMESPACE, TYPE_TAG_NAME);
    public static final String TYPED_KEY_REGEX = "(" + ChatUtils.KEY_REGEX + ")\\.type\\.(" + ChatUtils.KEY_REGEX + ")";
    public static final Pattern TYPED_KEY_PATTERN = Pattern.compile(TYPED_KEY_REGEX);

    static final Map<Class<? extends CustomDecorData<?>>, CustomDecorData<?>> CLASS_TO_DATA_MAP = new ConcurrentHashMap<>();
    private static final Map<String, CustomDecorType> KEY_TO_TYPE_MAP = new ConcurrentHashMap<>();
    private static final Map<Class<? extends CustomDecorData<?>>, CustomDecorType> CLASS_TO_TYPE_MAP = new ConcurrentHashMap<>();

    static {
        final long startTime = System.currentTimeMillis();
        final CustomDecorType[] values = values();
        final var recipesToRegister = new ArrayList<CustomDecorData<?>>();

        for (final var registry : values) {
            final CustomDecorData<?> data;

            try {
                data = registry.getDataClass().getDeclaredConstructor().newInstance();
            } catch (final Exception e) {
                MSDecor.logger().log(
                        Level.SEVERE,
                        "An error occurred while loading custom decor " + registry.name() + "!",
                        e
                );
                continue;
            }

            KEY_TO_TYPE_MAP.put(data.getKey().getKey().toLowerCase(Locale.ENGLISH), registry);
            CLASS_TO_TYPE_MAP.put(registry.clazz, registry);
            CLASS_TO_DATA_MAP.put(registry.clazz, data);
            recipesToRegister.add(data);
        }

        MSDecor.componentLogger().info(
                Component.text(
                        "Loaded " + values.length + " custom decors in " + (System.currentTimeMillis() - startTime) + "ms",
                        NamedTextColor.GREEN
                )
        );

        if (!recipesToRegister.isEmpty()) {
            final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

            executor.scheduleAtFixedRate(() -> {
                if (MSPluginUtils.isLoadedCustoms()) {
                    executor.shutdown();

                    for (final var data : recipesToRegister) {
                        data.registerRecipes();
                    }

                    recipesToRegister.clear();
                }
            }, 0L, 10L, TimeUnit.MILLISECONDS);
        }
    }

    CustomDecorType(final @NotNull Class<? extends CustomDecorData<?>> clazz) {
        this.clazz = clazz;
    }

    public @NotNull Class<? extends CustomDecorData<?>> getDataClass() {
        return this.clazz;
    }

    public @NotNull CustomDecorData<?> getCustomDecorData() {
        return CLASS_TO_DATA_MAP.get(this.clazz);
    }

    public <D extends CustomDecorData<D>> @NotNull D getCustomDecorData(final @NotNull Class<D> clazz) throws IllegalArgumentException {
        try {
            return clazz.cast(CLASS_TO_DATA_MAP.get(this.clazz));
        } catch (final ClassCastException e) {
            throw new IllegalArgumentException("Custom decor " + this.name() + " is not an instance of " + clazz.getName() + "!", e);
        }
    }

    @Contract("null -> null")
    public static @Nullable CustomDecorType fromKey(final @Nullable String key) {
        if (StringUtils.isBlank(key)) return null;

        if (matchesTypedKey(key)) {
            final Matcher matcher = TYPED_KEY_PATTERN.matcher(key);

            if (matcher.find()) {
                return fromKey(matcher.group(1));
            }
        }

        return KEY_TO_TYPE_MAP.get(key.toLowerCase(Locale.ENGLISH));
    }

    @Contract("null -> null")
    public static @Nullable CustomDecorType fromClass(final @Nullable Class<? extends CustomDecorData<?>> clazz) {
        return clazz == null
                ? null
                : CLASS_TO_TYPE_MAP.get(clazz);
    }

    @Contract("null -> null")
    public static @Nullable CustomDecorType fromItemStack(final @Nullable ItemStack itemStack) {
        if (itemStack == null) return null;

        final ItemMeta itemMeta = itemStack.getItemMeta();
        return itemMeta == null
                ? null
                : fromKey(
                        itemMeta.getPersistentDataContainer().get(TYPE_NAMESPACED_KEY, PersistentDataType.STRING)
                );
    }

    public static @NotNull @UnmodifiableView Set<String> keySet() {
        return Collections.unmodifiableSet(KEY_TO_TYPE_MAP.keySet());
    }

    public static @NotNull @UnmodifiableView Set<Class<? extends CustomDecorData<?>>> classSet() {
        return Collections.unmodifiableSet(CLASS_TO_TYPE_MAP.keySet());
    }

    public static @NotNull @UnmodifiableView Collection<CustomDecorData<?>> customDecors() {
        return Collections.unmodifiableCollection(CLASS_TO_DATA_MAP.values());
    }

    @Contract("null -> false")
    public static boolean containsKey(final @Nullable String key) {
        return StringUtils.isNotBlank(key)
                && KEY_TO_TYPE_MAP.containsKey(key.toLowerCase(Locale.ENGLISH));
    }

    @Contract("null -> false")
    public static boolean containsClass(final @Nullable Class<? extends CustomDecorData<?>> clazz) {
        return clazz != null
                && CLASS_TO_TYPE_MAP.containsKey(clazz);
    }

    /**
     * @param key Key to be checked
     * @return True if string matches {@link #TYPED_KEY_REGEX}
     */
    @Contract("null -> false")
    public static boolean matchesTypedKey(final @Nullable NamespacedKey key) {
        return key != null
                && matchesTypedKey(key.getKey());
    }

    /**
     * @param key Key to be checked
     * @return True if string matches {@link #TYPED_KEY_REGEX}
     */
    @Contract("null -> false")
    public static boolean matchesTypedKey(final @Nullable String key) {
        return StringUtils.isNotBlank(key)
                && TYPED_KEY_PATTERN.matcher(key).matches();
    }
}
