package com.minersstudios.msdecor.api;

import com.minersstudios.mscore.location.MSBoundingBox;
import com.minersstudios.mscore.util.ItemUtils;
import com.minersstudios.mscore.util.PlayerUtils;
import com.minersstudios.msdecor.api.action.DecorClickAction;
import com.minersstudios.msdecor.event.CustomDecorClickEvent;
import com.minersstudios.msitem.api.CustomItemType;
import org.bukkit.*;
import org.bukkit.block.data.type.Light;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;

public enum DecorParameter {
    //<editor-fold desc="Parameters" defaultstate="collapsed">
    PAINTABLE,
    SITTABLE(
            event -> {
                if (
                        event.getClickType().isRightClick()
                        && event.getCustomDecor().getData().isSittable()
                ) {
                    doSit(event);
                }
            }
    ),
    WRENCHABLE(
            event -> {
                if (event.getClickType().isLeftClick()) return;

                final ItemStack itemInUse = event.getPlayer().getInventory().getItem(event.getHand());

                if (CustomItemType.fromItemStack(itemInUse) == CustomItemType.WRENCH) {
                    doWrench(event, itemInUse);
                }
            }
    ),
    LIGHTABLE(
            event -> {
                if (event.getClickType().isLeftClick()) return;

                final Interaction interaction = event.getClickedInteraction();

                doLight(
                        event,
                        event.getCustomDecor().getData().getNextLightLevel(
                                interaction.getWorld().getBlockAt(interaction.getLocation()).getBlockData() instanceof final Light light
                                        ? light.getLevel()
                                        : 0
                        )
                );
            }
    ),
    TYPED,
    LIGHT_TYPED(
            event -> {
                if (event.getClickType().isLeftClick()) return;

                final CustomDecor customDecor = event.getCustomDecor();
                final var data = customDecor.getData();
                final Interaction interaction = event.getClickedInteraction();
                final int nextLevel = data.getNextLightLevel(
                        interaction.getWorld().getBlockAt(interaction.getLocation()).getBlockData() instanceof Light light
                                ? light.getLevel()
                                : 0
                );
                final var nextType = data.getLightTypeOf(nextLevel);

                doLight(event, nextLevel);

                if (nextType == null) return;

                final ItemStack typeItem = nextType.getItem();
                final ItemMeta itemMeta = typeItem.getItemMeta();

                final ItemDisplay itemDisplay = customDecor.getDisplay();
                final ItemStack displayItem = itemDisplay.getItemStack();
                assert displayItem != null;

                itemMeta.displayName(displayItem.getItemMeta().displayName());
                typeItem.setItemMeta(itemMeta);

                itemDisplay.setItemStack(typeItem);
            }
    ),
    FACE_TYPED;
    //</editor-fold>

    private final DecorClickAction clickAction;

    //<editor-fold desc="Actions" defaultstate="collapsed">
    private static final DecorClickAction WRENCHABLE_SITTABLE_CLICK_ACTION =
            event -> {
                if (event.getClickType().isLeftClick()) return;

                final ItemStack itemInUse = event.getPlayer().getInventory().getItem(event.getHand());

                if (CustomItemType.fromItemStack(itemInUse) == CustomItemType.WRENCH) {
                    doWrench(event, itemInUse);
                } else {
                    doSit(event);
                }
            };

    private static final DecorClickAction WRENCHABLE_LIGHTABLE_CLICK_ACTION =
            event -> {
                if (event.getClickType().isLeftClick()) return;

                final ItemStack itemInUse = event.getPlayer().getInventory().getItem(event.getHand());

                if (CustomItemType.fromItemStack(itemInUse) == CustomItemType.WRENCH) {
                    doWrench(event, itemInUse);
                } else {
                    final Interaction interaction = event.getClickedInteraction();

                    doLight(
                            event,
                            event.getCustomDecor().getData().getNextLightLevel(
                                    interaction.getWorld().getBlockAt(interaction.getLocation()).getBlockData() instanceof Light light
                                    ? light.getLevel()
                                    : 0
                            )
                    );
                }
            };
    //</editor-fold>

    DecorParameter() {
        this(DecorClickAction.NONE);
    }

    DecorParameter(final @NotNull DecorClickAction clickAction) {
        this.clickAction = clickAction;
    }

    public @NotNull DecorClickAction getClickAction() {
        return this.clickAction;
    }

    public static @NotNull DecorClickAction sittableAction() {
        return SITTABLE.getClickAction();
    }

    public static @NotNull DecorClickAction wrenchableAction() {
        return WRENCHABLE.getClickAction();
    }

    public static @NotNull DecorClickAction lightableAction() {
        return LIGHTABLE.getClickAction();
    }

    public static @NotNull DecorClickAction lightTypedAction() {
        return LIGHT_TYPED.getClickAction();
    }

    public static @NotNull DecorClickAction wrenchableSittableAction() {
        return WRENCHABLE_SITTABLE_CLICK_ACTION;
    }

    public static @NotNull DecorClickAction wrenchableLightableAction() {
        return WRENCHABLE_LIGHTABLE_CLICK_ACTION;
    }

    public static void doSit(final @NotNull CustomDecorClickEvent event) {
        final Player player = event.getPlayer();
        final Location sitLocation = event.getClickedInteraction().getLocation().add(0.0d, event.getCustomDecor().getData().getSitHeight(), 0.0d);

        for (final var nearbyPlayer : sitLocation.getNearbyEntitiesByType(Player.class, 0.5d)) {
            if (nearbyPlayer.getVehicle() != null) {
                return;
            }
        }

        PlayerUtils.setSitting(player, sitLocation);
        player.getWorld().playSound(
                sitLocation,
                Sound.ENTITY_HORSE_SADDLE,
                SoundCategory.PLAYERS,
                1.0f,
                1.0f
        );

        player.swingHand(event.getHand());
    }

    public static void doWrench(
            final @NotNull CustomDecorClickEvent event,
            final @NotNull ItemStack itemInUse
    ) {
        final CustomDecor customDecor = event.getCustomDecor();
        final ItemDisplay itemDisplay = customDecor.getDisplay();
        final ItemStack displayItem = itemDisplay.getItemStack();
        final var nextType = customDecor.getData().getNextType(displayItem);

        if (nextType == null) return;

        final Player player = event.getPlayer();
        final ItemStack typeItem = nextType.getItem();
        final ItemMeta typeMeta = typeItem.getItemMeta();
        final ItemMeta displayMeta = displayItem.getItemMeta();

        if (
                displayMeta instanceof LeatherArmorMeta displayColorable
                && typeMeta instanceof LeatherArmorMeta typeColorable
        ) {
            typeColorable.setColor(displayColorable.getColor());
        }

        typeMeta.displayName(displayMeta.displayName());
        typeItem.setItemMeta(typeMeta);

        itemDisplay.setItemStack(typeItem);

        if (player.getGameMode() == GameMode.SURVIVAL) {
            ItemUtils.damageItem(player, itemInUse);
        }

        player.getWorld().playSound(
                customDecor.getBoundingBox().getCenter().toLocation(),
                Sound.ITEM_SPYGLASS_USE,
                SoundCategory.PLAYERS,
                1.0f,
                1.0f
        );
        player.swingHand(event.getHand());
    }

    public static void doLight(
            final @NotNull CustomDecorClickEvent event,
            final int nextLevel
    ) {
        final World world = event.getClickedInteraction().getWorld();
        final MSBoundingBox msbb = event.getCustomDecor().getBoundingBox();

        for (final var block : msbb.getBlockStates(world)) {
            if (block.getBlockData() instanceof final Light light) {
                light.setLevel(nextLevel);
                block.setBlockData(light);
                block.update();
            }
        }
    }
}
