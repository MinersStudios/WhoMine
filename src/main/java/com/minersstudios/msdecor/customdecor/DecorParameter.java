package com.minersstudios.msdecor.customdecor;

import com.minersstudios.mscore.util.BlockUtils;
import com.minersstudios.mscore.util.ItemUtils;
import com.minersstudios.mscore.util.PlayerUtils;
import com.minersstudios.msdecor.events.CustomDecorRightClickEvent;
import com.minersstudios.msitem.item.CustomItemType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.bukkit.*;
import org.bukkit.block.data.type.Light;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
                final var data = event.getCustomDecor().getData();

                if (!data.isSittable()) return;

                final Location sitLocation = interaction.getLocation().add(0.0d, data.getSitHeight(), 0.0d);

                for (final var player : sitLocation.getNearbyEntitiesByType(Player.class, 0.5d)) {
                    if (player.getVehicle() != null) {
                        return;
                    }
                }

                final Player player = event.getPlayer();

                PlayerUtils.setSitting(player, sitLocation);
                player.swingHand(event.getHand());
            };

    public static final BiConsumer<@NotNull CustomDecorRightClickEvent, @NotNull Interaction> WRENCHABLE_RIGHT_CLICK_ACTION =
            (event, interaction) -> {
                final CustomDecor customDecor = event.getCustomDecor();
                final var data = customDecor.getData();

                if (!data.isWrenchable()) return;

                final Player player = event.getPlayer();
                final ItemStack itemInUse = player.getInventory().getItem(event.getHand());

                if (CustomItemType.fromItemStack(itemInUse) != CustomItemType.WRENCH) return;

                final ItemDisplay itemDisplay = customDecor.getDisplay();
                final ItemStack displayItem = itemDisplay.getItemStack();
                final var nextType = data.getNextWrenchType(displayItem);

                if (nextType == null) return;

                final ItemStack typeItem = nextType.getItem();
                final ItemMeta itemMeta = typeItem.getItemMeta();

                itemMeta.displayName(displayItem.getItemMeta().displayName());
                typeItem.setItemMeta(itemMeta);

                itemDisplay.setItemStack(typeItem);

                if (player.getGameMode() == GameMode.SURVIVAL) {
                    ItemUtils.damageItem(player, itemInUse);
                }

                final World world = player.getWorld();

                player.swingMainHand();
                world.playSound(
                        event.getClickedPosition().toLocation(world),
                        Sound.ITEM_SPYGLASS_USE,
                        SoundCategory.PLAYERS,
                        1.0f,
                        1.0f
                );
            };

    public static final BiConsumer<@NotNull CustomDecorRightClickEvent, @NotNull Interaction> LIGHTABLE_RIGHT_CLICK_ACTION =
            (event, interaction) -> {
                final CustomDecor customDecor = event.getCustomDecor();
                final var data = customDecor.getData();

                if (!data.isLightable()) return;

                final BoundingBox boundingBox = customDecor.getNMSBoundingBox();
                final int nextLevel = data.getNextLightLevel(
                        interaction.getWorld().getBlockAt(interaction.getLocation()).getBlockData() instanceof Light light
                        ? light.getLevel()
                        : 0
                );

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
                        light.setLevel(nextLevel);
                        block.setBlockData(light);
                    }
                }
            };

    public static final BiConsumer<@NotNull CustomDecorRightClickEvent, @NotNull Interaction> LIGHT_TYPED_RIGHT_CLICK_ACTION =
            (event, interaction) -> {
                final CustomDecor customDecor = event.getCustomDecor();
                final var data = customDecor.getData();

                if (!data.isLightable()) return;

                final BoundingBox boundingBox = customDecor.getNMSBoundingBox();
                final World world = interaction.getWorld();
                final int nextLevel = data.getNextLightLevel(
                        world.getBlockAt(interaction.getLocation()).getBlockData() instanceof Light light
                        ? light.getLevel()
                        : 0
                );

                for (
                        final var block
                        : BlockUtils.getBlocks(
                                world,
                                boundingBox.minX(),
                                boundingBox.minY(),
                                boundingBox.minZ(),
                                boundingBox.maxX(),
                                boundingBox.maxY(),
                                boundingBox.maxZ()
                        )
                ) {
                    if (block.getBlockData() instanceof final Light light) {
                        light.setLevel(nextLevel);
                        block.setBlockData(light);
                    }
                }

                final var nextType = data.getLightTypeOf(nextLevel);

                if (nextType == null) return;

                final ItemStack typeItem = nextType.getItem();
                final ItemMeta itemMeta = typeItem.getItemMeta();

                final ItemDisplay itemDisplay = customDecor.getDisplay();
                final ItemStack displayItem = itemDisplay.getItemStack();
                assert displayItem != null;

                itemMeta.displayName(displayItem.getItemMeta().displayName());
                typeItem.setItemMeta(itemMeta);

                itemDisplay.setItemStack(typeItem);
            };
}
