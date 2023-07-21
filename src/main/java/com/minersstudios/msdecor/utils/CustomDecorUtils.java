package com.minersstudios.msdecor.utils;

import com.minersstudios.mscore.utils.MSDecorUtils;
import com.minersstudios.msdecor.customdecor.CustomDecorData;
import com.minersstudios.msdecor.customdecor.FaceableByType;
import com.minersstudios.msdecor.customdecor.Typed;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class CustomDecorUtils {

    @Contract(value = " -> fail")
    private CustomDecorUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static @NotNull Optional<CustomDecorData> getCustomDecorDataByLocation(@NotNull Location location) {
        for (var nearbyEntity : location.getWorld().getNearbyEntities(location.clone().add(0.5d, 0.0d, 0.5d), 0.2d, 0.3d, 0.2d)) {
            if (nearbyEntity instanceof ArmorStand armorStand) {
                return getCustomDecorDataByEntity(armorStand);
            }
        }

        for (var nearbyEntity : location.getWorld().getNearbyEntities(location.toCenterLocation(), 0.5d, 0.5d, 0.5d)) {
            if (nearbyEntity instanceof ItemFrame itemFrame) {
                return getCustomDecorDataByEntity(itemFrame);
            }
        }
        return Optional.empty();
    }

    public static @NotNull Optional<CustomDecorData> getCustomDecorDataByEntity(@Nullable Entity entity) {
        return !MSDecorUtils.isCustomDecorEntity(entity)
                ? Optional.empty()
                : entity instanceof ArmorStand armorStand
                ? MSDecorUtils.getCustomDecorData(armorStand.getEquipment().getHelmet())
                : entity instanceof ItemFrame itemFrame
                ? MSDecorUtils.getCustomDecorData(itemFrame.getItem())
                : Optional.empty();
    }

    public static @NotNull Optional<CustomDecorData> getCustomDecorDataWithFace(
            @Nullable ItemStack itemStack,
            @Nullable BlockFace blockFace
    ) {
        var customDecorData = MSDecorUtils.getCustomDecorData(itemStack);

        if (customDecorData.orElse(null) instanceof FaceableByType faceableByType) {
            Typed.Type type = faceableByType.getTypeByFace(blockFace);

            if (type != null) {
                return Optional.of(faceableByType.createCustomDecorData(type));
            }
        }

        return customDecorData;
    }
}
