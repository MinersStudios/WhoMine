package com.minersstudios.msdecor.listener.event.player;

import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.mscore.location.MSPosition;
import com.minersstudios.mscore.utility.BlockUtils;
import com.minersstudios.mscore.utility.MSDecorUtils;
import com.minersstudios.msdecor.MSDecor;
import com.minersstudios.msdecor.api.CustomDecor;
import com.minersstudios.msdecor.api.CustomDecorData;
import com.minersstudios.msdecor.event.CustomDecorClickEvent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@EventListener
public final class PlayerInteractListener extends AbstractEventListener<MSDecor> {
    private static final Set<UUID> HAND_HANDLER = new HashSet<>();

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(final @NotNull PlayerInteractEvent event) {
        final Block block = event.getClickedBlock();
        final EquipmentSlot hand = event.getHand();

        if (
                block == null
                || hand == null
        ) {
            return;
        }

        final Player player = event.getPlayer();
        final Material blockType = block.getType();
        final GameMode gameMode = player.getGameMode();

        switch (event.getAction()) {
            case LEFT_CLICK_BLOCK -> {
                if (
                        MSDecorUtils.isCustomDecorMaterial(blockType)
                        && gameMode != GameMode.ADVENTURE
                        && gameMode != GameMode.SPECTATOR
                ) {
                    if (
                            (
                                    player.isSneaking()
                                    && gameMode == GameMode.SURVIVAL
                            )
                            || gameMode == GameMode.CREATIVE
                    ) {
                        CustomDecor.destroyInBlock(player, block);
                    } else {
                        final Location interactedLocation = event.getInteractionPoint();

                        if (interactedLocation != null) {
                            callDecorClickEvent(
                                    player,
                                    block,
                                    hand,
                                    interactedLocation.toVector(),
                                    CustomDecorClickEvent.ClickType.LEFT_CLICK
                            );
                        }
                    }
                }
            }
            case RIGHT_CLICK_BLOCK -> {
                if (
                        Tag.SHULKER_BOXES.isTagged(blockType)
                        && block.getBlockData() instanceof final Directional directional
                        && MSDecorUtils.isCustomDecor(block.getRelative(directional.getFacing()))
                ) {
                    event.setCancelled(true);
                }

                if (
                        gameMode == GameMode.ADVENTURE
                        || gameMode == GameMode.SPECTATOR
                ) {
                    return;
                }

                if (
                        hand == EquipmentSlot.OFF_HAND
                        && isDoneForMainHand(player)
                ) {
                    undoneForMainHand(player);
                    return;
                }

                final BlockFace blockFace = event.getBlockFace();
                final ItemStack itemInHand = player.getInventory().getItem(hand);

                if (!MSDecorUtils.isCustomDecor(itemInHand)) {
                    final Location interactedLocation = event.getInteractionPoint();

                    if (interactedLocation != null) {
                        callDecorClickEvent(
                                player,
                                block,
                                hand,
                                interactedLocation.toVector(),
                                CustomDecorClickEvent.ClickType.RIGHT_CLICK
                        );
                    }

                    return;
                }

                final Block blockAtFace = block.getRelative(blockFace);

                if (
                        BlockUtils.isReplaceable(blockAtFace.getType())
                        && (
                                !BlockUtils.isRightClickBlock(blockType)
                                || player.isSneaking()
                                || blockType == Material.NOTE_BLOCK
                        )
                ) {
                    CustomDecorData.fromItemStack(itemInHand)
                    .ifPresent(data -> {
                        data.place(
                                MSPosition.of(
                                        BlockUtils.isReplaceable(blockType)
                                                ? block.getLocation()
                                                : blockAtFace.getLocation()
                                ),
                                player,
                                blockFace,
                                hand,
                                null
                        );
                        doneForMainHand(player);
                    });
                }
            }
        }
    }

    private static void doneForMainHand(final @NotNull Player player) {
        HAND_HANDLER.add(player.getUniqueId());
    }

    private static void undoneForMainHand(final @NotNull Player player) {
        HAND_HANDLER.remove(player.getUniqueId());
    }

    private static boolean isDoneForMainHand(final @NotNull Player player) {
        return HAND_HANDLER.contains(player.getUniqueId());
    }

    private static void callDecorClickEvent(
            final @NotNull Player player,
            final @NotNull Block block,
            final @NotNull EquipmentSlot hand,
            final @NotNull Vector interactedPosition,
            final @NotNull CustomDecorClickEvent.ClickType clickType
    ) {
        final PluginManager pluginManager = player.getServer().getPluginManager();

        for (final var interaction : MSDecorUtils.getNearbyInteractions(block.getLocation().toCenterLocation())) {
            CustomDecor.fromInteraction(interaction)
            .ifPresent(
                    customDecor -> {
                        final CustomDecorClickEvent clickEvent = new CustomDecorClickEvent(
                                customDecor,
                                player,
                                hand,
                                interactedPosition,
                                interaction,
                                clickType
                        );

                        pluginManager.callEvent(clickEvent);

                        if (!clickEvent.isCancelled()) {
                            customDecor.getData().doClickAction(clickEvent);
                            doneForMainHand(player);
                        }
                    }
            );
        }
    }
}