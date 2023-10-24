package com.minersstudios.msdecor.listeners.event.player;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.util.BlockUtils;
import com.minersstudios.mscore.util.MSDecorUtils;
import com.minersstudios.msdecor.MSDecor;
import com.minersstudios.msdecor.customdecor.CustomDecor;
import com.minersstudios.msdecor.customdecor.CustomDecorData;
import com.minersstudios.msdecor.events.CustomDecorRightClickEvent;
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

@MSListener
public class PlayerInteractListener extends AbstractMSListener<MSDecor> {
    private static final Set<UUID> HAND_HANDLER = new HashSet<>();

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(final @NotNull PlayerInteractEvent event) {
        final Block block = event.getClickedBlock();
        final EquipmentSlot hand = event.getHand();

        if (
                block == null
                || hand == null
        ) return;

        final Player player = event.getPlayer();
        final Material blockType = block.getType();
        final GameMode gameMode = player.getGameMode();

        switch (event.getAction()) {
            case LEFT_CLICK_BLOCK -> {
                if (
                        MSDecorUtils.isCustomDecorMaterial(blockType)
                        && (
                                (
                                        player.isSneaking()
                                        && gameMode == GameMode.SURVIVAL
                                )
                                || gameMode == GameMode.CREATIVE
                        )
                ) {
                    CustomDecorData.destroyInBlock(player, block);
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
                ) return;

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
                    if (blockType != Material.BARRIER) return;

                    final Location interactedLocation = event.getInteractionPoint();

                    if (interactedLocation == null) return;

                    final PluginManager pluginManager = player.getServer().getPluginManager();
                    final Vector interactedPosition = interactedLocation.toVector();

                    for (final var interaction : MSDecorUtils.getNearbyInteractions(block.getLocation().toCenterLocation())) {
                        CustomDecor.fromInteraction(interaction)
                        .ifPresent(
                                customDecor -> {
                                    final CustomDecorRightClickEvent rightClickEvent = new CustomDecorRightClickEvent(
                                            customDecor,
                                            player,
                                            hand,
                                            interactedPosition
                                    );

                                    pluginManager.callEvent(rightClickEvent);

                                    if (rightClickEvent.isCancelled()) return;

                                    customDecor.getData().doRightClickAction(rightClickEvent, interaction);
                                    doneForeMainHand(player);
                                }
                        );
                    }
                } else if (
                        (
                                (
                                        !blockType.isInteractable()
                                        || Tag.STAIRS.isTagged(blockType)
                                        || Tag.FENCES.isTagged(blockType)
                                )
                                || (player.isSneaking() && blockType.isInteractable())
                                || blockType == Material.NOTE_BLOCK
                        )
                        && BlockUtils.isReplaceable(block.getRelative(blockFace).getType())
                ) {
                    CustomDecorData.fromItemStack(itemInHand)
                    .ifPresent(data -> {
                        data.place(
                                BlockUtils.isReplaceable(block)
                                        ? block
                                        : block.getRelative(blockFace),
                                player,
                                blockFace,
                                hand,
                                null
                        );
                        doneForeMainHand(player);
                    });
                }
            }
        }
    }

    private static void doneForeMainHand(final @NotNull Player player) {
        HAND_HANDLER.add(player.getUniqueId());
    }

    private static void undoneForMainHand(final @NotNull Player player) {
        HAND_HANDLER.remove(player.getUniqueId());
    }

    private static boolean isDoneForMainHand(final @NotNull Player player) {
        return HAND_HANDLER.contains(player.getUniqueId());
    }
}
