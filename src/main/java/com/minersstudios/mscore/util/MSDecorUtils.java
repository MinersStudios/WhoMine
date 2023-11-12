package com.minersstudios.mscore.util;

import com.minersstudios.mscore.location.MSBoundingBox;
import com.minersstudios.msdecor.api.CustomDecorData;
import com.minersstudios.msdecor.api.CustomDecorType;
import com.minersstudios.msdecor.api.DecorHitBox;
import com.minersstudios.msitem.api.CustomItem;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Utility class for {@link CustomDecorData}
 */
public final class MSDecorUtils {
    public static final String NAMESPACED_KEY_REGEX = '(' + CustomDecorType.NAMESPACE + "):(" + ChatUtils.KEY_REGEX + ")";
    public static final Pattern NAMESPACED_KEY_PATTERN = Pattern.compile(NAMESPACED_KEY_REGEX);

    @Contract(" -> fail")
    private MSDecorUtils() {
        throw new AssertionError("Utility class");
    }

    /**
     * Gets {@link CustomDecorData} item stack from key from
     * {@link CustomDecorType} by using
     * {@link CustomDecorData#fromKey(String)} method
     *
     * @param key {@link CustomDecorData} key string
     * @return Optional of {@link ItemStack} object
     *         or empty optional if not found
     * @see CustomDecorData#fromKey(String)
     * @see CustomDecorData#getItem()
     */
    public static @NotNull Optional<ItemStack> getItemStack(final @Nullable String key) {
        return CustomDecorData.fromKey(key).map(CustomDecorData::getItem);
    }

    /**
     * Gets {@link CustomDecorData} item stack from class from
     * {@link CustomDecorType} by using
     * {@link CustomDecorData#fromClass(Class)} method
     *
     * @param clazz {@link CustomItem} class
     * @return Optional of {@link ItemStack} object
     *         or empty optional if not found
     * @see CustomDecorData#fromClass(Class)
     * @see CustomDecorData#getItem()
     */
    public static @NotNull Optional<ItemStack> getItemStack(final @NotNull Class<? extends CustomDecorData<?>> clazz) {
        return CustomDecorData.fromClass(clazz).map(CustomDecorData::getItem);
    }

    public static Interaction @NotNull [] getNearbyInteractions(final @NotNull Location location) {
        final var entities = MSBoundingBox.ofSize(location, 1.0d, 1.0d, 1.0d)
                .getNMSEntities(
                        ((CraftWorld) location.getWorld()).getHandle(),
                        entity -> entity instanceof net.minecraft.world.entity.Interaction
                );
        final int size = entities.size();
        final Interaction[] interactions = new Interaction[size];

        for (int i = 0; i < size; i++) {
            interactions[i] = (Interaction) entities.get(i).getBukkitEntity();
        }

        return interactions;
    }

    public static @Nullable Interaction getNearbyInteraction(final @NotNull Location location) {
        final var entities = MSBoundingBox.ofSize(location, 1.0d, 1.0d, 1.0d)
                .getNMSEntities(
                        ((CraftWorld) location.getWorld()).getHandle(),
                        entity -> entity instanceof net.minecraft.world.entity.Interaction
                );
        return entities.isEmpty()
                ? null
                : (Interaction) entities.get(0).getBukkitEntity();
    }

    /**
     * @param material Material to be checked
     * @return True if material is custom decor material
     *         (barrier, structure_void, light)
     */
    @Contract("null -> false")
    public static boolean isCustomDecorMaterial(final @Nullable Material material) {
        return material == Material.BARRIER
                || material == Material.LIGHT;
    }

    @Contract("null -> false")
    public static boolean isCustomDecor(final @Nullable ItemStack itemStack) {
        return CustomDecorData.fromItemStack(itemStack).isPresent();
    }

    @Contract("null -> false")
    public static boolean isCustomDecor(final @Nullable net.minecraft.world.entity.Entity entity) {
        return entity != null
                && isCustomDecor(entity.getBukkitEntity());
    }

    @Contract("null -> false")
    public static boolean isCustomDecor(final @Nullable Entity entity) {
        return entity instanceof final Interaction interaction
                && (
                        DecorHitBox.isChild(interaction)
                        || DecorHitBox.isParent(interaction)
                );
    }

    @Contract("null -> false")
    public static boolean isCustomDecor(final @Nullable Block block) {
        return block != null
                && MSBoundingBox.of(block)
                .hasNMSEntity(
                        ((CraftWorld) block.getWorld()).getHandle(),
                        MSDecorUtils::isCustomDecor
                );
    }

    /**
     * @param string String to be checked
     * @return True if string matches {@link #NAMESPACED_KEY_REGEX}
     */
    @Contract("null -> false")
    public static boolean matchesNamespacedKey(final @Nullable String string) {
        return StringUtils.isNotBlank(string)
                && NAMESPACED_KEY_PATTERN.matcher(string).matches();
    }
}
