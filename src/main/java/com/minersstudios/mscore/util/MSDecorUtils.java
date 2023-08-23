package com.minersstudios.mscore.util;

import com.minersstudios.mscore.plugin.GlobalCache;
import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.msdecor.customdecor.CustomDecor;
import com.minersstudios.msdecor.customdecor.CustomDecorData;
import com.minersstudios.msdecor.events.CustomDecorPlaceEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Utility class for {@link CustomDecorData}
 * and {@link CustomDecor}
 */
public final class MSDecorUtils {
    public static final NamespacedKey CUSTOM_DECOR_TYPE_NAMESPACED_KEY = new NamespacedKey("msdecor", "type");
    public static final String NAMESPACED_KEY_REGEX = "(msdecor):([a-z0-9./_-]+)";
    public static final Pattern NAMESPACED_KEY_PATTERN = Pattern.compile(NAMESPACED_KEY_REGEX);
    public static final String ENTITY_TAG_NAME = "customDecor";

    @Contract(value = " -> fail")
    private MSDecorUtils() {
        throw new AssertionError("Utility class");
    }

    /**
     * Gets {@link CustomDecorData} item stack from key from
     * {@link GlobalCache#customDecorMap} by using
     * {@link #getCustomDecorData(String)} method
     *
     * @param key {@link CustomDecorData} key string
     * @return Optional of {@link ItemStack} object
     *         or empty optional if not found
     * @see #getCustomDecorData(String)
     * @see CustomDecorData#getItemStack()
     */
    public static @NotNull Optional<ItemStack> getCustomDecorItem(final @Nullable String key) {
        return getCustomDecorData(key).map(CustomDecorData::getItemStack);
    }

    /**
     * Gets {@link CustomDecorData} from {@link ItemStack} from
     * {@link GlobalCache#customDecorMap} by using persistent data
     * container of item and {@link #getCustomDecorData(String)}
     * method
     *
     * @param itemStack {@link ItemStack} to be checked
     * @return Optional of {@link CustomDecorData} object
     *         or empty optional if not found
     * @see #getCustomDecorData(String)
     */
    public static @NotNull Optional<CustomDecorData> getCustomDecorData(final @Nullable ItemStack itemStack) {
        if (itemStack == null) return Optional.empty();
        final ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return Optional.empty();
        final String key = itemMeta.getPersistentDataContainer().get(CUSTOM_DECOR_TYPE_NAMESPACED_KEY, PersistentDataType.STRING);
        return getCustomDecorData(key);
    }

    /**
     * Gets {@link CustomDecorData} from key from
     * {@link GlobalCache#customDecorMap}
     *
     * @param key {@link CustomDecorData} key string
     * @return Optional of {@link CustomDecorData} object
     *         or empty optional if not found
     */
    public static @NotNull Optional<CustomDecorData> getCustomDecorData(final @Nullable String key) {
        return key == null
                ? Optional.empty()
                : Optional.ofNullable(MSPlugin.getGlobalCache().customDecorMap.getByPrimaryKey(key));
    }

    /**
     * Places custom decor.
     * Calls {@link CustomDecorPlaceEvent} when returns true.
     *
     * @param block     Block instead of which decor will be installed
     * @param player    Player who places decor
     * @param key       {@link CustomDecorData} key string,
     *                  example - (example)
     * @param blockFace Side on which decor will be placed
     * @see CustomDecorPlaceEvent
     * @see #placeCustomDecor(Block, Player, String, BlockFace, EquipmentSlot, Component)
     * @see CustomDecor#setCustomDecor(BlockFace, EquipmentSlot, Component)
     */
    public static void placeCustomDecor(
            final @NotNull Block block,
            final @NotNull Player player,
            final @NotNull String key,
            final @NotNull BlockFace blockFace
    ) {
        placeCustomDecor(block, player, key, blockFace, null, null);
    }

    /**
     * Places custom decor.
     * Calls {@link CustomDecorPlaceEvent} when returns true.
     *
     * @param block     Block instead of which decor will be installed
     * @param player    Player who places decor
     * @param key       {@link CustomDecorData} key string,
     *                  example - (example)
     * @param blockFace Side on which decor will be placed
     * @param hand      Hand that was involved
     * @see CustomDecorPlaceEvent
     * @see #placeCustomDecor(Block, Player, String, BlockFace, EquipmentSlot, Component)
     * @see CustomDecor#setCustomDecor(BlockFace, EquipmentSlot, Component)
     */
    public static void placeCustomDecor(
            final @NotNull Block block,
            final @NotNull Player player,
            final @NotNull String key,
            final @NotNull BlockFace blockFace,
            final @Nullable EquipmentSlot hand
    ) {
        placeCustomDecor(block, player, key, blockFace, hand, null);
    }

    /**
     * Places custom decor.
     * Calls {@link CustomDecorPlaceEvent} when returns true.
     *
     * @param block      Block instead of which decor will be installed
     * @param player     Player who places decor
     * @param key        {@link CustomDecorData} key string,
     *                   example - (example)
     * @param blockFace  Side on which decor will be placed
     * @param hand       Hand that was involved
     * @param customName Custom name of decor
     * @see CustomDecorPlaceEvent
     * @see CustomDecor#setCustomDecor(BlockFace, EquipmentSlot, Component)
     */
    public static void placeCustomDecor(
            final @NotNull Block block,
            final @NotNull Player player,
            final @NotNull String key,
            final @NotNull BlockFace blockFace,
            final @Nullable EquipmentSlot hand,
            final @Nullable Component customName
    ) {
        final var customDecorData = MSDecorUtils.getCustomDecorData(key);

        if (customDecorData.isEmpty()) {
            MSLogger.warning("Custom decor data with key: " + key + " not found");
            return;
        }

        new CustomDecor(block, player, customDecorData.get())
                .setCustomDecor(blockFace, hand, customName);
    }

    /**
     * @param material Material of block to check
     * @return True if the material is used
     *         as a decor {@link CustomDecorData.HitBox}
     */
    @Contract("null -> false")
    public static boolean isCustomDecorMaterial(final @Nullable Material material) {
        return material != null
                && switch (material) {
            case BARRIER, STRUCTURE_VOID, LIGHT -> true;
            default -> false;
        };
    }

    /**
     * Checks if the entity has the {@link #ENTITY_TAG_NAME} tag
     * in {@link Entity#getScoreboardTags()}
     *
     * @param entity The entity to check
     * @return True if the entity has the {@link #ENTITY_TAG_NAME} tag
     */
    @Contract("null -> false")
    public static boolean isCustomDecorEntity(final @Nullable Entity entity) {
        return entity != null && entity.getScoreboardTags().contains(ENTITY_TAG_NAME);
    }

    /**
     * Checks if item has {@link #CUSTOM_DECOR_TYPE_NAMESPACED_KEY}
     * in {@link PersistentDataContainer} of item
     *
     * @param itemStack Item to be checked
     * @return True if item is {@link CustomDecorData} item
     * @see CustomDecorData
     */
    @Contract("null -> false")
    public static boolean isCustomDecor(final @Nullable ItemStack itemStack) {
        if (itemStack == null) return false;
        final ItemMeta itemMeta = itemStack.getItemMeta();
        return itemMeta != null && itemMeta.getPersistentDataContainer().has(CUSTOM_DECOR_TYPE_NAMESPACED_KEY);
    }

    /**
     * @param string String to be checked
     * @return True if string matches {@link #NAMESPACED_KEY_REGEX}
     */
    @Contract(value = "null -> false")
    public static boolean matchesNamespacedKey(final @Nullable String string) {
        return string != null && NAMESPACED_KEY_PATTERN.matcher(string).matches();
    }
}
