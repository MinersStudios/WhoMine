package com.minersstudios.wholib.paper.custom.block;

import com.minersstudios.wholib.key.Resource;
import com.minersstudios.wholib.module.MainModule;
import com.minersstudios.wholib.paper.custom.block.params.NoteBlockData;
import com.minersstudios.wholib.paper.custom.block.params.PlacingType;
import com.minersstudios.wholib.utility.ChatUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

/**
 * The CustomBlockRegistry class is responsible for managing and storing custom
 * block data for {@link MainModule} plugin.
 * <br>
 * It provides various methods to register, unregister, and retrieve custom
 * block data based on different criteria, such as the custom block's key, hash
 * code, or block data.
 * <br>
 * The CustomBlockRegistry uses one concurrent map to store all the registered
 * custom block data associated with the corresponding hash code of the
 * {@link NoteBlockData} of the custom block. And another concurrent map to
 * store the registered keys associated with the corresponding hash code of the
 * {@link NoteBlockData} of the custom block. The {@link #HASH_CODE_MAP} is a
 * main map that stores all the registered custom block data. The
 * {@link #KEY_MAP} is used to store the registered keys and associated hash
 * codes.
 * <br>
 * All recipes by default are registered after all custom blocks, items, and
 * decorations are registered. This is to avoid problems related to dependencies
 * between other plugins and custom items, decorations, and blocks.
 * <br>
 * Make sure to use the provided methods and their respective Optional return
 * types to handle cases where the desired custom block data might not be
 * present.
 * <br>
 * Example usage:
 * <pre>{@code
 * // Register a custom block data
 * CustomBlockRegistry.registerData(customBlockData);
 *
 * // Retrieve custom block data using key
 * Optional<CustomBlockData> customBlockData =
 *          CustomBlockRegistry.fromKey("my_custom_block");
 * if (customBlockData.isPresent()) {
 *     // Custom block data found, do something with it
 *     CustomBlockData data = customBlockData.get();
 * }
 *
 * // Check if an item stack is a custom block
 * ItemStack itemStack = ...;
 * if (CustomBlockRegistry.isCustomBlock(itemStack)) {
 *     // Handle the case when the item stack is a custom block
 * }
 *
 * // Check if a block is a custom block
 * Block block = ...;
 * if (CustomBlockRegistry.isCustomBlock(block)) {
 *    // Handle the case when the block is a custom block
 * }
 * }</pre>
 */
public final class CustomBlockRegistry {
    public static final NamespacedKey TYPE_NAMESPACED_KEY =
            new NamespacedKey(Resource.WMBLOCK, "type");

    private static final Int2ObjectMap<CustomBlockData> HASH_CODE_MAP = new Int2ObjectOpenHashMap<>();
    private static final Map<String, IntSet> KEY_MAP = new Object2ObjectOpenHashMap<>();

    static {
        register(CustomBlockData.defaultData());
    }

    @Contract(" -> fail")
    private CustomBlockRegistry() throws AssertionError {
        throw new AssertionError("Utility class");
    }

    public static @NotNull @UnmodifiableView Set<Integer> hashCodeSet() {
        return Collections.unmodifiableSet(HASH_CODE_MAP.keySet());
    }

    public static @NotNull @UnmodifiableView Set<String> keySet() {
        return Collections.unmodifiableSet(KEY_MAP.keySet());
    }

    public static @NotNull @UnmodifiableView Collection<CustomBlockData> customBlockDataCollection() {
        return Collections.unmodifiableCollection(HASH_CODE_MAP.values());
    }

    public static @NotNull Optional<CustomBlockData> fromHashCode(final int hashCode) {
        return Optional.ofNullable(HASH_CODE_MAP.get(hashCode));
    }

    public static @NotNull Optional<CustomBlockData> fromKey(final @Nullable String key) {
        return ChatUtils.isBlank(key)
                ? Optional.empty()
                : Optional.ofNullable(KEY_MAP.get(key.toLowerCase(Locale.ENGLISH)))
                .flatMap(
                        hashCodes -> hashCodes.isEmpty()
                                ? Optional.empty()
                                : fromHashCode(hashCodes.iterator().nextInt())
                );
    }

    public static @NotNull Optional<CustomBlockData> fromBlockData(final @NotNull BlockData blockData) {
        return blockData instanceof final NoteBlock noteBlock
                ? fromNoteBlock(noteBlock)
                : Optional.empty();
    }

    public static @NotNull Optional<CustomBlockData> fromNoteBlock(final @NotNull NoteBlock noteBlock) {
        return fromNoteBlockData(NoteBlockData.from(noteBlock));
    }

    public static @NotNull Optional<CustomBlockData> fromNoteBlockData(final @NotNull NoteBlockData noteBlockData) {
        return fromHashCode(noteBlockData.hashCode());
    }

    public static @NotNull Optional<CustomBlockData> fromItemStack(final @Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return Optional.empty();
        }

        final ItemMeta itemMeta = itemStack.getItemMeta();
        return itemMeta == null
                ? Optional.empty()
                : fromKey(
                        itemMeta.getPersistentDataContainer().get(TYPE_NAMESPACED_KEY, PersistentDataType.STRING)
                );
    }

    public static boolean containsHashCode(final int hashCode) {
        return HASH_CODE_MAP.containsKey(hashCode);
    }

    @Contract("null -> false")
    public static boolean containsKey(final @Nullable String key) {
        return ChatUtils.isNotBlank(key)
                && KEY_MAP.containsKey(key.toLowerCase(Locale.ENGLISH));
    }

    @Contract("null -> false")
    public static boolean containsCustomBlockData(final @Nullable CustomBlockData customBlockData) {
        if (customBlockData == null) {
            return false;
        }

        final PlacingType placingType = customBlockData.getBlockSettings().getPlacing().getType();

        switch (placingType) {
            case final PlacingType.Default normal -> {
                return containsHashCode(normal.getNoteBlockData().hashCode());
            }
            case final PlacingType.Directional directional -> {
                for (final var noteBlockData : directional.getMap().values()) {
                    if (containsHashCode(noteBlockData.hashCode())) {
                        return true;
                    }
                }
            }
            case final PlacingType.Orientable orientable -> {
                for (final var noteBlockData : orientable.getMap().values()) {
                    if (containsHashCode(noteBlockData.hashCode())) {
                        return true;
                    }
                }
            }
            default -> throw new IllegalArgumentException("Unknown placing type: " + placingType.getClass().getName());
        }

        return false;
    }

    @Contract("null -> false")
    public static boolean isCustomBlock(final @Nullable ItemStack itemStack) {
        return itemStack != null
                && fromItemStack(itemStack).isPresent();
    }

    @Contract("null -> false")
    public static boolean isCustomBlock(final @Nullable Block block) {
        return block != null
                && isCustomBlock(block.getBlockData());
    }

    @Contract("null -> false")
    public static boolean isCustomBlock(final @Nullable BlockData blockData) {
        return blockData instanceof NoteBlock noteBlock
                && fromNoteBlock(noteBlock).isPresent();
    }

    public static boolean isEmpty() {
        return HASH_CODE_MAP.isEmpty();
    }

    public static int size() {
        return HASH_CODE_MAP.size();
    }

    public static synchronized void register(final @NotNull CustomBlockData customBlockData) throws IllegalArgumentException {
        final String key = customBlockData.getKey();
        final PlacingType placingType = customBlockData.getBlockSettings().getPlacing().getType();

        switch (placingType) {
            case final PlacingType.Default normal ->
                    register(
                            customBlockData, normal.getNoteBlockData().hashCode(), key
                    );
            case final PlacingType.Directional directional ->
                    directional.getMap().forEach(
                            (blockFace, data) -> register(customBlockData, data.hashCode(), key)
                    );
            case final PlacingType.Orientable orientable ->
                    orientable.getMap().forEach(
                            (blockAxis, data) -> register(customBlockData, data.hashCode(), key)
                    );
            default -> throw new IllegalArgumentException("Unknown placing type: " + placingType.getClass().getName());
        }
    }

    public static synchronized void unregister(final @NotNull CustomBlockData customBlockData) throws IllegalArgumentException {
        final String key = customBlockData.getKey().toLowerCase(Locale.ENGLISH);
        final int hashCode = customBlockData.hashCode();

        if (!containsKey(key)) {
            throw new IllegalArgumentException("The key " + key + " is not registered! See " + key + " custom block data!");
        }

        if (!containsHashCode(hashCode)) {
            throw new IllegalArgumentException("The hash code " + hashCode + " is not registered! See " + key + " custom block data!");
        }

        KEY_MAP.remove(key);
        HASH_CODE_MAP.remove(hashCode);
    }

    public static synchronized void unregisterAll() {
        KEY_MAP.clear();
        HASH_CODE_MAP.clear();
    }

    private static synchronized void register(
            final @NotNull CustomBlockData customBlockData,
            final int hashCode,
            final String key
    ) throws IllegalArgumentException {
        if (containsCustomBlockData(customBlockData)) {
            throw new IllegalArgumentException("The custom block data is already registered! See " + key + " custom block data!");
        }

        if (
                customBlockData.getBlockSettings().getPlacing().getType() instanceof PlacingType.Default
                && containsHashCode(hashCode)
        ) {
            throw new IllegalArgumentException("The hash code " + hashCode + " is already registered! See " + key + " custom block data!");
        }

        final var hashKeys = KEY_MAP.computeIfAbsent(key, k -> new IntOpenHashSet());

        hashKeys.add(hashCode);
        HASH_CODE_MAP.put(hashCode, customBlockData);
        KEY_MAP.put(key, hashKeys);
    }
}
