package com.minersstudios.mscore.util;

import com.minersstudios.msdecor.customdecor.CustomDecorData;
import com.minersstudios.msdecor.customdecor.CustomDecorType;
import com.minersstudios.msdecor.customdecor.DecorHitBox;
import com.minersstudios.msitem.item.CustomItem;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Utility class for {@link CustomDecorData}
 */
public final class MSDecorUtils {
    public static final String NAMESPACED_KEY_REGEX = '(' + CustomDecorType.NAMESPACE + "):([a-z0-9./_-]+)";
    public static final Pattern NAMESPACED_KEY_PATTERN = Pattern.compile(NAMESPACED_KEY_REGEX);

    @Contract(value = " -> fail")
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
        final var entities = LocationUtils.getNearbyNMSEntities(
                ((CraftWorld) location.getWorld()).getHandle(),
                LocationUtils.bukkitToAABB(org.bukkit.util.BoundingBox.of(location, 0.5d, 0.5d, 0.5d)),
                entity -> entity instanceof net.minecraft.world.entity.Interaction
        );
        final int size = entities.size();
        final Interaction[] interactions = new Interaction[size];

        for (int i = 0; i < size; ++i) {
            interactions[i] = (Interaction) entities.get(i).getBukkitEntity();
        }

        return interactions;
    }

    public static @Nullable Interaction getNearbyInteraction(final @NotNull Location location) {
        final var entities = LocationUtils.getNearbyNMSEntities(
                ((CraftWorld) location.getWorld()).getHandle(),
                LocationUtils.bukkitToAABB(org.bukkit.util.BoundingBox.of(location, 0.5d, 0.5d, 0.5d)),
                entity -> entity instanceof net.minecraft.world.entity.Interaction
        );

        return entities.isEmpty() ? null : (Interaction) entities.get(0).getBukkitEntity();
    }

    /**
     * @param material Material to be checked
     * @return True if material is custom decor material
     *         (barrier, structure_void, light)
     */
    @Contract("null -> false")
    public static boolean isCustomDecorMaterial(final @Nullable Material material) {
        return material != null
                && switch (material) {
                    case BARRIER, STRUCTURE_VOID, LIGHT -> true;
                    default -> false;
                };
    }

    @Contract("null -> false")
    public static boolean isCustomDecor(final @Nullable ItemStack itemStack) {
        return CustomDecorData.fromItemStack(itemStack).isPresent();
    }

    @Contract("null -> false")
    public static boolean isCustomDecor(final @Nullable Entity entity) {
        if (!(entity instanceof final Interaction interaction)) return false;

        final PersistentDataContainer container = interaction.getPersistentDataContainer();

        return !container.isEmpty()
                && (
                        (
                                container.has(CustomDecorType.TYPE_NAMESPACED_KEY)
                                && container.has(DecorHitBox.HITBOX_DISPLAY_NAMESPACED_KEY)
                                && container.has(DecorHitBox.HITBOX_INTERACTIONS_NAMESPACED_KEY)
                                && container.has(DecorHitBox.HITBOX_BOUNDING_BOX_NAMESPACED_KEY)
                        )
                        || container.has(DecorHitBox.HITBOX_CHILD_NAMESPACED_KEY)
                );
    }

    @Contract("null -> false")
    public static boolean isCustomDecor(final @Nullable Block block) {
        if (
                block == null
                || isCustomDecorMaterial(block.getType())
        ) return false;

        for (final var entity : MSDecorUtils.getNearbyInteractions(block.getLocation().toCenterLocation())) {
            if (isCustomDecor(entity)) return true;
        }

        return false;
    }

    /**
     * @param string String to be checked
     * @return True if string matches {@link #NAMESPACED_KEY_REGEX}
     */
    @Contract(value = "null -> false")
    public static boolean matchesNamespacedKey(final @Nullable String string) {
        return StringUtils.isNotBlank(string)
                && NAMESPACED_KEY_PATTERN.matcher(string).matches();
    }
}
