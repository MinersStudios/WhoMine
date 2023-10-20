package com.minersstudios.msdecor.customdecor;

import com.minersstudios.mscore.util.BlockUtils;
import com.minersstudios.mscore.util.PlayerUtils;
import com.minersstudios.msdecor.events.CustomDecorRightClickEvent;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.bukkit.Location;
import org.bukkit.block.data.type.Light;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public enum DecorParameter {
    SITTABLE,
    WRENCHABLE,
    LIGHTABLE,
    LIGHT_TYPED,
    FACE_TYPED;

    public static final BiConsumer<@NotNull CustomDecorRightClickEvent, @NotNull Interaction> SITTABLE_RIGHT_CLICK_ACTION =
            (event, interaction) -> {
                final var data = event.getCustomDecorData();

                if (data.isSittable()) {
                    final Location sitLocation = interaction.getLocation().add(0.0d, data.getSitHeight(), 0.0d);

                    for (final var player : sitLocation.getNearbyEntitiesByType(Player.class, 0.5d)) {
                        if (player.getVehicle() != null) {
                            return;
                        }
                    }

                    final Player player = event.getPlayer();

                    PlayerUtils.setSitting(player, sitLocation);
                    player.swingHand(event.getHand());
                }
            };

    public static final BiConsumer<@NotNull CustomDecorRightClickEvent, @NotNull Interaction> LIGHTABLE_RIGHT_CLICK_ACTION =
            (event, interaction) ->
                    DecorHitBox.Elements.fromInteraction(interaction)
                    .ifPresent(
                            elements -> {
                                final var data = event.getCustomDecorData();
                                final BoundingBox boundingBox = elements.getNMSBoundingBox();

                                for (
                                        final var block
                                        : BlockUtils.getBlocks(
                                                interaction.getWorld(),
                                                boundingBox.minX(),
                                                boundingBox.minY(),
                                                boundingBox.minZ(),
                                                boundingBox.maxX(),
                                                boundingBox.maxY(),
                                                boundingBox.maxZ()
                                        )
                                ) {
                                    if (block.getBlockData() instanceof final Light light) {
                                        light.setLevel(data.nextLightLevel(light.getLevel()));
                                        block.setBlockData(light);
                                    }
                                }
                            }
                    );
}
