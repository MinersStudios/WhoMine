package com.minersstudios.msdecor.listeners.event.player;

import com.minersstudios.msblock.customblock.CustomBlockRegistry;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.util.BlockUtils;
import com.minersstudios.mscore.util.MSDecorUtils;
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
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerInteractListener extends AbstractMSListener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlaceArmorStand(final @NotNull PlayerInteractEvent event) {
        if (
                event.getAction() != Action.RIGHT_CLICK_BLOCK
                || event.getClickedBlock() == null
                || event.getHand() == null
                || !MSDecorUtils.isCustomDecor(event.getItem())
        ) return;

        event.setUseItemInHand(Event.Result.DENY);
    }

    @EventHandler
    public void onPlayerInteract(final @NotNull PlayerInteractEvent event) {
        if (
                event.getClickedBlock() == null
                || event.getHand() == null
        ) return;

        final Block block = event.getClickedBlock();
        final Material blockType = block.getType();
        final Player player = event.getPlayer();
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

                final PlayerInventory inventory = player.getInventory();
                final ItemStack itemInMainHand = inventory.getItemInMainHand();
                EquipmentSlot hand = event.getHand();

                if (CustomBlockRegistry.isCustomBlock(itemInMainHand)) return;

                if (hand != EquipmentSlot.HAND && MSDecorUtils.isCustomDecor(itemInMainHand)) {
                    hand = EquipmentSlot.HAND;
                }

                final BlockFace blockFace = event.getBlockFace();
                final ItemStack itemInHand = inventory.getItem(hand);

                if (
                        (event.getHand() == EquipmentSlot.HAND || hand == EquipmentSlot.OFF_HAND)
                        && gameMode != GameMode.ADVENTURE
                        && gameMode != GameMode.SPECTATOR
                ) {
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
                                                event.getPlayer(),
                                                event.getHand(),
                                                interactedPosition
                                        );

                                        pluginManager.callEvent(rightClickEvent);

                                        if (rightClickEvent.isCancelled()) return;

                                        customDecor.getData().doRightClickAction(rightClickEvent, interaction);
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
                        final EquipmentSlot finalHand = hand;

                        CustomDecorData.fromItemStack(itemInHand)
                        .ifPresent(data ->
                                data.place(
                                        BlockUtils.isReplaceable(block)
                                                ? block
                                                : block.getRelative(blockFace),
                                        player,
                                        blockFace,
                                        finalHand,
                                        null
                                )
                        );
                    }
                }
            }
        }
    }
}
