package com.github.minersstudios.mscore.utils;

import com.github.minersstudios.mscore.MSCore;
import com.github.minersstudios.msdecor.MSDecor;
import com.github.minersstudios.msdecor.customdecor.CustomDecor;
import com.github.minersstudios.msdecor.customdecor.CustomDecorData;
import com.github.minersstudios.msdecor.events.CustomDecorPlaceEvent;
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
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public final class MSDecorUtils {
    public static final NamespacedKey CUSTOM_DECOR_TYPE_NAMESPACED_KEY = new NamespacedKey(MSDecor.getInstance(), "type");
    public static final String NAMESPACED_KEY_REGEX = "msdecor:(\\w+)";
    public static final String ENTITY_TAG_NAME = "customDecor";

    @Contract(value = " -> fail")
    private MSDecorUtils() {
        throw new AssertionError("Utility class");
    }

    /**
     * Places custom decor
     * <br>
     * Calls {@link CustomDecorPlaceEvent} when returns true
     *
     * @param block     Block instead of which decor will be installed
     * @param player    Player who places decor
     * @param key       {@link CustomDecorData} namespaced key string, example - (msdecor:example)
     * @param blockFace Side on which decor will be placed
     * @throws MSCustomNotFoundException If {@link CustomDecorData} is not found
     */
    public static void placeCustomDecor(
            @NotNull Block block,
            @NotNull Player player,
            @NotNull String key,
            @NotNull BlockFace blockFace
    ) {
        placeCustomDecor(block, player, key, blockFace, null, null);
    }

    /**
     * Places custom decor
     * <br>
     * Calls {@link CustomDecorPlaceEvent} when returns true
     *
     * @param block     Block instead of which decor will be installed
     * @param player    Player who places decor
     * @param key       {@link CustomDecorData} namespaced key string, example - (msdecor:example)
     * @param blockFace Side on which decor will be placed
     * @param hand      Hand that was involved
     * @throws MSCustomNotFoundException If {@link CustomDecorData} is not found
     */
    public static void placeCustomDecor(
            @NotNull Block block,
            @NotNull Player player,
            @NotNull String key,
            @NotNull BlockFace blockFace,
            @Nullable EquipmentSlot hand
    ) {
        placeCustomDecor(block, player, key, blockFace, hand, null);
    }

    /**
     * Places custom decor
     * <br>
     * Calls {@link CustomDecorPlaceEvent} when returns true
     *
     * @param block      Block instead of which decor will be installed
     * @param player     Player who places decor
     * @param key        {@link CustomDecorData} namespaced key string, example - (msdecor:example)
     * @param blockFace  Side on which decor will be placed
     * @param hand       Hand that was involved
     * @param customName Custom name of decor
     * @throws MSCustomNotFoundException If {@link CustomDecorData} is not found
     */
    public static void placeCustomDecor(
            @NotNull Block block,
            @NotNull Player player,
            @NotNull String key,
            @NotNull BlockFace blockFace,
            @Nullable EquipmentSlot hand,
            @Nullable Component customName
    ) throws MSCustomNotFoundException {
        CustomDecorData customDecorData = MSDecorUtils.getCustomDecorData(key);
        CustomDecor customDecor = new CustomDecor(block, player, customDecorData);

        customDecor.setCustomDecor(blockFace, hand, customName);
    }

    /**
     * @param material Material of block
     * @return True if the material is used as a decor HitBox
     */
    @Contract("null -> false")
    public static boolean isCustomDecorMaterial(@Nullable Material material) {
        return material != null && switch (material) {
            case BARRIER, STRUCTURE_VOID, LIGHT -> true;
            default -> false;
        };
    }

    /**
     * @param entity The entity
     * @return True if the entity has the "customDecor" tag
     */
    @Contract("null -> false")
    public static boolean isCustomDecorEntity(@Nullable Entity entity) {
        return entity != null && entity.getScoreboardTags().contains(ENTITY_TAG_NAME);
    }

    /**
     * @param itemStack Item
     * @return True if item is {@link CustomDecorData}
     */
    @Contract("null -> false")
    public static boolean isCustomDecor(@Nullable ItemStack itemStack) {
        if (itemStack == null) return false;
        ItemMeta itemMeta = itemStack.getItemMeta();
        return itemMeta != null && itemMeta.getPersistentDataContainer().has(CUSTOM_DECOR_TYPE_NAMESPACED_KEY);
    }

    /**
     * Gets {@link CustomDecorData} item stack from key
     *
     * @param key {@link CustomDecorData} key string
     * @return {@link CustomDecorData} item stack
     * @throws MSCustomNotFoundException If {@link CustomDecorData} is not found
     */
    public static @NotNull ItemStack getCustomDecorItem(@NotNull String key) throws MSCustomNotFoundException {
        return getCustomDecorData(key).getItemStack();
    }

    /**
     * Gets {@link CustomDecorData} from {@link ItemStack}
     *
     * @param itemStack {@link ItemStack}
     * @return {@link CustomDecorData} object
     * @throws MSCustomNotFoundException If {@link CustomDecorData} is not found
     */
    public static @Nullable CustomDecorData getCustomDecorData(@Nullable ItemStack itemStack) throws MSCustomNotFoundException {
        if (itemStack == null) return null;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return null;
        String key = itemMeta.getPersistentDataContainer().get(CUSTOM_DECOR_TYPE_NAMESPACED_KEY, PersistentDataType.STRING);
        return key == null ? null : getCustomDecorData(key);
    }

    /**
     * Gets {@link CustomDecorData} from key
     *
     * @param key {@link CustomDecorData} Key string
     * @return {@link CustomDecorData} object
     * @throws MSCustomNotFoundException If {@link CustomDecorData} is not found
     */
    public static @NotNull CustomDecorData getCustomDecorData(@NotNull String key) throws MSCustomNotFoundException {
        CustomDecorData customDecorData = MSCore.getCache().customDecorMap.getByPrimaryKey(key);
        if (customDecorData == null) {
            throw new MSCustomNotFoundException("Custom decor is not found : " + key);
        }
        return customDecorData;
    }

    /**
     * @param string String to be checked
     * @return True if string matches {@link MSDecorUtils#NAMESPACED_KEY_REGEX} regex
     */
    @Contract(value = "null -> false", pure = true)
    public static boolean matchesNamespacedKey(@Nullable String string) {
        return string != null && string.matches(NAMESPACED_KEY_REGEX);
    }
}
