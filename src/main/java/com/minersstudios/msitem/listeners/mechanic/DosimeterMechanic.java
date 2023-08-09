package com.minersstudios.msitem.listeners.mechanic;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.anomalies.Anomaly;
import com.minersstudios.msitem.MSItem;
import com.minersstudios.msitem.item.CustomItemType;
import com.minersstudios.msitem.item.registry.items.Dosimeter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.kyori.adventure.text.Component.text;

@MSListener
public class DosimeterMechanic extends AbstractMSListener {

    @EventHandler
    public void onPlayerSwapHandItems(@NotNull PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        var players = MSItem.getCache().dosimeterPlayers;
        EquipmentSlot equipmentSlot = players.get(player);

        if (equipmentSlot != null) {
            players.put(player, equipmentSlot == EquipmentSlot.HAND ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND);
        }
    }

    @EventHandler
    public void onPlayerItemHeld(@NotNull PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        var players = MSItem.getCache().dosimeterPlayers;
        EquipmentSlot equipmentSlot = players.get(player);

        if (equipmentSlot == EquipmentSlot.HAND) {
            ItemStack dosimeterItem = player.getInventory().getItem(event.getPreviousSlot());

            CustomItemType.fromItemStack(dosimeterItem, Dosimeter.class)
            .ifPresent(dosimeter -> {
                Dosimeter copy = dosimeter.copy();

                assert dosimeterItem != null;

                copy.setItem(dosimeterItem);
                copy.setEnabled(false);
                players.remove(player);
            });
        }
    }

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        ClickType clickType = event.getClick();

        if (!(inventory instanceof PlayerInventory playerInventory)) return;

        var players = MSItem.getCache().dosimeterPlayers;
        EquipmentSlot equipmentSlot = players.get(player);

        if (equipmentSlot == null) return;

        ItemStack dosimeterItem = playerInventory.getItem(equipmentSlot);

        CustomItemType.fromItemStack(dosimeterItem, Dosimeter.class)
        .ifPresent(dosimeter -> {
            Dosimeter copy = dosimeter.copy();
            EquipmentSlot newEquipmentSlot = equipmentSlot == EquipmentSlot.HAND ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND;

            if (
                    clickType.isShiftClick()
                    || (clickType == ClickType.SWAP_OFFHAND
                    && equipmentSlot == EquipmentSlot.OFF_HAND
                    && event.getSlot() != playerInventory.getHeldItemSlot())
            ) {
                copy.setItem(clickType.isShiftClick() ? Objects.requireNonNull(event.getCurrentItem()) : dosimeterItem);
                copy.setEnabled(false);
                players.remove(player);
                return;
            }

            this.getPlugin().runTask(() -> {
                if (dosimeterItem.equals(playerInventory.getItem(newEquipmentSlot))) {
                    players.put(player, newEquipmentSlot);
                } else if (!dosimeterItem.equals(playerInventory.getItem(equipmentSlot))) {
                    copy.setItem(
                            clickType.isKeyboardClick()
                            ? dosimeterItem
                            : Objects.requireNonNull(event.getCursor())
                    );
                    copy.setEnabled(false);
                    players.remove(player);
                }
            });
        });
    }

    @EventHandler
    public void onPlayerDropItem(@NotNull PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        var players = MSItem.getCache().dosimeterPlayers;
        EquipmentSlot equipmentSlot = players.get(player);

        if (equipmentSlot != null) {
            ItemStack drop = event.getItemDrop().getItemStack();
            ItemStack itemStack = player.getInventory().getItem(equipmentSlot);

            CustomItemType.fromItemStack(itemStack, Dosimeter.class)
            .ifPresent(dosimeter -> {
                if (CustomItemType.fromItemStack(drop, Dosimeter.class).isEmpty()) return;

                Dosimeter copy = dosimeter.copy();

                copy.setItem(drop);
                copy.setEnabled(false);
                players.remove(player);
            });
        }
    }

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        EquipmentSlot equipmentSlot = MSItem.getCache().dosimeterPlayers.remove(player);

        if (equipmentSlot != null) {
            ItemStack itemStack = player.getInventory().getItem(equipmentSlot);

            CustomItemType.fromItemStack(itemStack, Dosimeter.class)
            .ifPresent(dosimeter -> {
                Dosimeter copy = dosimeter.copy();

                copy.setItem(itemStack);
                copy.setEnabled(false);
            });
        }
    }

    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) return;

        Player player = event.getPlayer();
        EquipmentSlot hand = event.getHand();

        if (
                hand == null
                || !hand.isHand()
        ) return;

        ItemStack itemInHand = player.getInventory().getItem(hand);

        CustomItemType.fromItemStack(itemInHand, Dosimeter.class)
        .ifPresent(dosimeter -> {
            Dosimeter copy = dosimeter.copy();

            event.setCancelled(true);
            copy.setItem(itemInHand);
            copy.setEnabled(!copy.isEnabled());

            if (copy.isEnabled()) {
                MSItem.getCache().dosimeterPlayers.put(player, hand);
            } else {
                MSItem.getCache().dosimeterPlayers.remove(player, hand);
            }
        });
    }

    public static class DosimeterTask {
        private static final Map<Player, EquipmentSlot> PLAYERS = MSItem.getCache().dosimeterPlayers;

        public static void run() {
            if (PLAYERS.isEmpty()) return;
            PLAYERS.entrySet()
            .stream()
            .filter(entry -> entry.getKey().isOnline())
            .forEach(entry -> {
                Player player = entry.getKey();
                ItemStack itemStack = player.getInventory().getItem(entry.getValue());

                if (CustomItemType.fromItemStack(itemStack).orElse(null) instanceof Dosimeter dosimeter) {
                    Dosimeter copy = dosimeter.copy();

                    copy.setItem(itemStack);

                    if (copy.isEnabled()) {
                        var radiiPlayerInside = new HashMap<Anomaly, Double>();

                        for (var anomaly : MSEssentials.getCache().anomalies.values()) {
                            double radiusInside = anomaly.getBoundingBox().getRadiusInside(player);

                            if (radiusInside != -1.0d) {
                                radiiPlayerInside.put(anomaly, radiusInside);
                            }
                        }

                        var anomalyEntry = getEntryWithMinValue(radiiPlayerInside);
                        List<Double> radii = anomalyEntry == null
                                ? Collections.emptyList()
                                : anomalyEntry.getKey().getBoundingBox().getRadii();
                        Double radius = anomalyEntry == null
                                ? null
                                : anomalyEntry.getValue();

                        copy.setItem(itemStack);
                        copy.setScreenTypeByRadius(radii, radius);
                        player.sendActionBar(
                                text("Уровень радиации : ")
                                .append(text(radiusToLevel(radii, radius, player.getLocation())))
                                .append(text(" мк3в/ч"))
                        );
                        return;
                    }
                }

                PLAYERS.remove(player);
            });
        }

        private static @NotNull String radiusToLevel(
                @NotNull List<Double> radii,
                @Nullable Double radius,
                @NotNull Location loc
        ) {
            var reversedRadii = new ArrayList<>(radii);

            Collections.reverse(reversedRadii);

            double indexOfRadius = reversedRadii.indexOf(radius);
            double afterComma = Math.round(((Math.abs(loc.getX()) + Math.abs(loc.getY()) + Math.abs(loc.getZ())) % 1.0d) * 10.0d) / 10.0d;
            return (indexOfRadius == -1.0d ? 0.0d : indexOfRadius + 1.0d) + Math.min(afterComma, 0.9d) + String.valueOf(Math.min(Math.round(Math.random() * 10.0d), 9));
        }

        private static @Nullable Map.Entry<Anomaly, Double> getEntryWithMinValue(@NotNull Map<Anomaly, Double> map) {
            return map.entrySet().stream()
                    .min(Map.Entry.comparingByValue())
                    .orElse(null);
        }
    }
}
