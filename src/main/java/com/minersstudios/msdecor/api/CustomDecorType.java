package com.minersstudios.msdecor.api;

import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscore.utility.MSPluginUtils;
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
import com.minersstudios.msessentials.menu.CraftsMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * The CustomDecorType enum represents various types of custom decor in the
 * MSDecor plugin. Each enum value is associated with a specific class that
 * implements the CustomDecorData interface. This class provides methods to
 * manage and retrieve custom decor data instances, keys, and types.
 */
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

    public static final String TYPE_TAG_NAME = "type";
    public static final NamespacedKey TYPE_NAMESPACED_KEY = new NamespacedKey(MSDecor.NAMESPACE, TYPE_TAG_NAME);

    public static final String TYPED_KEY_REGEX = "(" + ChatUtils.KEY_REGEX + ")\\.type\\.(" + ChatUtils.KEY_REGEX + ")";
    public static final Pattern TYPED_KEY_PATTERN = Pattern.compile(TYPED_KEY_REGEX);

    private static final CustomDecorType[] VALUES = values();
    private static final Map<String, CustomDecorType> KEY_TO_TYPE_MAP = new HashMap<>();
    private static final Map<Class<? extends CustomDecorData<?>>, CustomDecorType> CLASS_TO_TYPE_MAP = new HashMap<>();
    private static final Map<Class<? extends CustomDecorData<?>>, CustomDecorData<?>> CLASS_TO_DATA_MAP = new HashMap<>();

    /**
     * Constructor for CustomDecorType enum values
     *
     * @param clazz The associated class that implements the CustomDecorData
     *              interface
     */
    CustomDecorType(final @NotNull Class<? extends CustomDecorData<?>> clazz) {
        this.clazz = clazz;
    }

    /**
     * Loads all custom decor types
     *
     * @param plugin The plugin instance
     * @throws IllegalStateException If custom decor types have already been
     *                               loaded
     */
    @ApiStatus.Internal
    public static void load(final @NotNull MSDecor plugin) {
        if (!KEY_TO_TYPE_MAP.isEmpty()) {
            throw new IllegalStateException("Custom decor types have already been loaded!");
        }

        final var recipesToRegister = new ArrayList<CustomDecorData<?>>();
        final long startTime = System.currentTimeMillis();

        Stream.of(VALUES).parallel()
        .forEach(registry -> {
            final CustomDecorData<?> data;

            try {
                data = registry.getDataClass().getDeclaredConstructor().newInstance();
            } catch (final Throwable e) {
                plugin.getLogger().log(
                        Level.SEVERE,
                        "An error occurred while loading custom decor " + registry.name() + "!",
                        e
                );

                return;
            }

            KEY_TO_TYPE_MAP.put(data.getKey().getKey().toLowerCase(Locale.ENGLISH), registry);
            CLASS_TO_TYPE_MAP.put(registry.clazz, registry);
            CLASS_TO_DATA_MAP.put(registry.clazz, data);
            recipesToRegister.add(data);
        });

        plugin.setLoadedCustoms(true);
        plugin.getComponentLogger().info(
                Component.text(
                        "Loaded " + VALUES.length + " custom decors in " + (System.currentTimeMillis() - startTime) + "ms",
                        NamedTextColor.GREEN
                )
        );

        if (!recipesToRegister.isEmpty()) {
            final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            final Server server = plugin.getServer();

            executor.scheduleAtFixedRate(
                    () -> {
                        if (MSPluginUtils.isLoadedCustoms()) {
                            executor.shutdown();

                            plugin.runTask(() -> {
                                for (final var data : recipesToRegister) {
                                    data.registerRecipes(server);
                                }

                                recipesToRegister.clear();
                                CraftsMenu.putCrafts(
                                        CraftsMenu.Type.DECORS,
                                        MSPlugin.globalCache().customDecorRecipes
                                );
                            });
                        }
                    },
                    0L, 10L,
                    TimeUnit.MILLISECONDS
            );
        }
    }

    /**
     * @return The class associated with this custom decor type
     */
    public @NotNull Class<? extends CustomDecorData<?>> getDataClass() {
        return this.clazz;
    }

    /**
     * @return The CustomDecorData instance associated with this custom decor
     *         type
     * @throws IllegalStateException If custom decor types have not been loaded
     *                               yet
     */
    public @NotNull CustomDecorData<?> getCustomDecorData() throws IllegalStateException {
        checkLoaded();
        return CLASS_TO_DATA_MAP.get(this.clazz);
    }

    /**
     * @param clazz The target class to cast the custom decor data instance to
     * @param <D>   The type of the target class
     * @return The custom decor data instance cast to the specified class
     * @throws IllegalArgumentException If the custom decor data instance cannot
     *                                  be cast to the specified class
     * @throws IllegalStateException    If custom decor types have not been
     *                                  loaded yet
     */
    public <D extends CustomDecorData<D>> @NotNull D getCustomDecorData(final @NotNull Class<D> clazz) throws IllegalArgumentException, IllegalStateException {
        checkLoaded();
        try {
            return clazz.cast(CLASS_TO_DATA_MAP.get(this.clazz));
        } catch (final ClassCastException e) {
            throw new IllegalArgumentException(
                    "Custom decor " + this.name() + " is not an instance of " + clazz.getName() + "!",
                    e
            );
        }
    }

    /**
     * Gets the {@link CustomDecorType} from the given key
     *
     * @param key The key to get the custom decor type from, must not be null
     *            (case-insensitive)
     * @return The {@link CustomDecorType} associated with the given key or null
     *         if the given key is not associated with any custom decor type,
     *         or if the given key is null or blank
     * @throws IllegalStateException If custom decor types have not been loaded
     *                               yet
     * @see #KEY_TO_TYPE_MAP
     */
    @Contract("null -> null")
    public static @Nullable CustomDecorType fromKey(final @Nullable String key) throws IllegalStateException {
        checkLoaded();

        if (ChatUtils.isBlank(key)) {
            return null;
        }

        if (TYPED_KEY_PATTERN.matcher(key).matches()) {
            final Matcher matcher = TYPED_KEY_PATTERN.matcher(key);

            if (matcher.find()) {
                return fromKey(matcher.group(1));
            }
        }

        return KEY_TO_TYPE_MAP.get(key.toLowerCase(Locale.ENGLISH));
    }

    /**
     * Gets the {@link CustomDecorType} from the given class
     *
     * @param clazz The class to get the custom decor type from
     * @return The {@link CustomDecorType} associated with the given class or
     *         null if the given class is not associated with any custom decor
     *         type, or if the given class is null
     * @throws IllegalStateException If custom decor types have not been loaded
     *                               yet
     * @see #CLASS_TO_TYPE_MAP
     */
    @Contract("null -> null")
    public static @Nullable CustomDecorType fromClass(final @Nullable Class<? extends CustomDecorData<?>> clazz) throws IllegalStateException {
        checkLoaded();
        return clazz == null
                ? null
                : CLASS_TO_TYPE_MAP.get(clazz);
    }

    /**
     * Gets the {@link CustomDecorType} from the given item stack.
     * <br>
     * It will get the namespaced key from the item stack's persistent data
     * container and then get the custom decor type from {@link #KEY_TO_TYPE_MAP}
     *
     * @param itemStack The item stack to get the custom decor type from
     * @return The {@link CustomDecorType} associated with the given item stack
     *         or null if the given item stack is not associated with any custom
     *         decor type, or if the given item stack is null, or an air item
     * @throws IllegalStateException If custom decor types have not been loaded
     *                               yet
     * @see #fromKey(String)
     */
    @Contract("null -> null")
    public static @Nullable CustomDecorType fromItemStack(final @Nullable ItemStack itemStack) throws IllegalStateException {
        checkLoaded();

        if (itemStack == null) {
            return null;
        }

        final ItemMeta itemMeta = itemStack.getItemMeta();

        return itemMeta == null
                ? null
                : fromKey(
                        itemMeta.getPersistentDataContainer().get(
                                TYPE_NAMESPACED_KEY,
                                PersistentDataType.STRING
                        )
                );
    }

    /**
     * @return An unmodifiable view of the custom decor key set
     * @see #KEY_TO_TYPE_MAP
     */
    public static @NotNull @UnmodifiableView Set<String> keySet() throws IllegalStateException {
        checkLoaded();
        return Collections.unmodifiableSet(KEY_TO_TYPE_MAP.keySet());
    }

    /**
     * @return An unmodifiable view of a set of custom decor classes that
     *         implement the CustomDecorData interface
     * @throws IllegalStateException If custom decor types have not been loaded
     *                               yet
     * @see #CLASS_TO_TYPE_MAP
     */
    public static @NotNull @UnmodifiableView Set<Class<? extends CustomDecorData<?>>> classSet() throws IllegalStateException {
        checkLoaded();
        return Collections.unmodifiableSet(CLASS_TO_TYPE_MAP.keySet());
    }

    /**
     * @return An unmodifiable view of a custom decor data instance collection
     * @throws IllegalStateException If custom decor types have not been loaded
     *                               yet
     * @see #CLASS_TO_DATA_MAP
     */
    public static @NotNull @UnmodifiableView Collection<CustomDecorData<?>> customDecors() throws IllegalStateException {
        checkLoaded();
        return Collections.unmodifiableCollection(CLASS_TO_DATA_MAP.values());
    }

    /**
     * @param key The key to check
     * @return True if the {@link #KEY_TO_TYPE_MAP} contains the given key
     * @throws IllegalStateException If custom decor types have not been loaded
     *                               yet
     */
    @Contract("null -> false")
    public static boolean containsKey(final @Nullable String key) throws IllegalStateException {
        checkLoaded();
        return ChatUtils.isNotBlank(key)
                && KEY_TO_TYPE_MAP.containsKey(key.toLowerCase(Locale.ENGLISH));
    }

    /**
     * @param clazz The class to check
     * @return True if the {@link #CLASS_TO_TYPE_MAP} contains the given class
     * @throws IllegalStateException If custom decor types have not been loaded
     *                               yet
     */
    @Contract("null -> false")
    public static boolean containsClass(final @Nullable Class<? extends CustomDecorData<?>> clazz) throws IllegalStateException {
        checkLoaded();
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
        return ChatUtils.isNotBlank(key)
                && TYPED_KEY_PATTERN.matcher(key).matches();
    }

    /**
     * Checks if custom decor types have been loaded yet
     *
     * @throws IllegalStateException If custom decor types have not been loaded
     *                               yet
     */
    private static void checkLoaded() throws IllegalStateException {
        if (KEY_TO_TYPE_MAP.isEmpty()) {
            throw new IllegalStateException("Custom decor types have not been loaded yet!");
        }
    }
}
