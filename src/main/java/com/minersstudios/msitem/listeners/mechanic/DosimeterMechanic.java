package com.minersstudios.msitem.listeners.mechanic;

import com.minersstudios.mscore.listener.MSListener;
import com.minersstudios.mscore.utils.MSItemUtils;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.anomalies.Anomaly;
import com.minersstudios.msitem.MSItem;
import com.minersstudios.msitem.items.register.items.Dosimeter;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import com.minersstudios.mscore.listener.AbstractMSListener;
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

@MSListener
public class DosimeterMechanic extends AbstractMSListener {

    @EventHandler
    public void onPlayerSwapHandItems(@NotNull PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        EquipmentSlot equipmentSlot = MSItem.getConfigCache().dosimeterPlayers.get(player);

        if (equipmentSlot != null) {
            MSItem.getConfigCache().dosimeterPlayers.put(player, equipmentSlot == EquipmentSlot.HAND ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND);
        }
    }

    @EventHandler
    public void onPlayerItemHeld(@NotNull PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        EquipmentSlot equipmentSlot = MSItem.getConfigCache().dosimeterPlayers.get(player);

        if (equipmentSlot == EquipmentSlot.HAND) {
            ItemStack dosimeterItem = player.getInventory().getItem(event.getPreviousSlot());

            if (!(MSItemUtils.getCustomItem(dosimeterItem) instanceof Dosimeter dosimeter)) return;

            dosimeter.setItemStack(dosimeterItem);
            dosimeter.setEnabled(false);
            MSItem.getConfigCache().dosimeterPlayers.remove(player);
        }
    }

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        ClickType clickType = event.getClick();

        if (!(inventory instanceof PlayerInventory playerInventory)) return;
        EquipmentSlot equipmentSlot = MSItem.getConfigCache().dosimeterPlayers.get(player);
        if (equipmentSlot == null) return;

        ItemStack dosimeterItem = playerInventory.getItem(equipmentSlot);
        if (!(MSItemUtils.getCustomItem(dosimeterItem) instanceof Dosimeter dosimeter)) return;
        EquipmentSlot newEquipmentSlot = equipmentSlot == EquipmentSlot.HAND ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND;

        if (
                clickType.isShiftClick()
                || (clickType == ClickType.SWAP_OFFHAND
                && equipmentSlot == EquipmentSlot.OFF_HAND
                && event.getSlot() != playerInventory.getHeldItemSlot())
        ) {
            dosimeter.setItemStack(clickType.isShiftClick() ? Objects.requireNonNull(event.getCurrentItem()) : dosimeterItem);
            dosimeter.setEnabled(false);
            MSItem.getConfigCache().dosimeterPlayers.remove(player);
            return;
        }

        this.getPlugin().runTask(() -> {
            if (dosimeterItem.equals(playerInventory.getItem(newEquipmentSlot))) {
                MSItem.getConfigCache().dosimeterPlayers.put(player, newEquipmentSlot);
            } else if (
                    !dosimeterItem.equals(playerInventory.getItem(equipmentSlot))
            ) {
                dosimeter.setItemStack(
                        clickType.isKeyboardClick()
                        ? dosimeterItem
                        : Objects.requireNonNull(event.getCursor()));
                dosimeter.setEnabled(false);
                MSItem.getConfigCache().dosimeterPlayers.remove(player);
            }
        });
    }

    @EventHandler
    public void onPlayerDropItem(@NotNull PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        EquipmentSlot equipmentSlot = MSItem.getConfigCache().dosimeterPlayers.get(player);

        if (equipmentSlot != null) {
            ItemStack drop = event.getItemDrop().getItemStack();
            ItemStack itemStack = player.getInventory().getItem(equipmentSlot);

            if (
                    MSItemUtils.getCustomItem(itemStack) instanceof Dosimeter
                    || !(MSItemUtils.getCustomItem(drop) instanceof Dosimeter dosimeter)
            ) return;

            dosimeter.setItemStack(drop);
            dosimeter.setEnabled(false);
            MSItem.getConfigCache().dosimeterPlayers.remove(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        EquipmentSlot equipmentSlot = MSItem.getConfigCache().dosimeterPlayers.remove(player);
        if (equipmentSlot == null) return;
        ItemStack itemStack = player.getInventory().getItem(equipmentSlot);

        if (MSItemUtils.getCustomItem(itemStack) instanceof Dosimeter dosimeter) {
            dosimeter.setItemStack(itemStack);
            dosimeter.setEnabled(false);
        }
    }

    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) return;
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        EquipmentSlot hand = event.getHand();
        if (hand == null || !hand.isHand()) return;
        ItemStack itemInHand = inventory.getItem(hand);

        if (MSItemUtils.getCustomItem(itemInHand) instanceof Dosimeter dosimeter) {
            event.setCancelled(true);
            dosimeter.setItemStack(itemInHand);
            dosimeter.setEnabled(!dosimeter.isEnabled());

            if (dosimeter.isEnabled()) {
                MSItem.getConfigCache().dosimeterPlayers.put(player, hand);
            } else {
                MSItem.getConfigCache().dosimeterPlayers.remove(player, hand);
            }
        }
    }

    public static class DosimeterTask {

        public static void run() {
            if (MSItem.getConfigCache().dosimeterPlayers.isEmpty()) return;
            MSItem.getConfigCache().dosimeterPlayers.entrySet()
                    .stream()
                    .filter(entry -> entry.getKey().isOnline())
                    .forEach(entry -> {
                        Player player = entry.getKey();
                        ItemStack itemStack = player.getInventory().getItem(entry.getValue());

                        if (MSItemUtils.getCustomItem(itemStack) instanceof Dosimeter dosimeter) {
                            dosimeter.setItemStack(itemStack);

                            if (dosimeter.isEnabled()) {
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

                                dosimeter.setItemStack(itemStack);
                                dosimeter.setScreenTypeByRadius(radii, radius);
                                player.sendActionBar(
                                        Component.text("Уровень радиации : ")
                                                .append(Component.text(radiusToLevel(radii, radius, player.getLocation())))
                                                .append(Component.text(" мк3в/ч"))
                                );
                                return;
                            }
                        }

                        MSItem.getConfigCache().dosimeterPlayers.remove(player);
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

        private static @Nullable Map.Entry<Anomaly, Double> getEntryWithMinValue(@NotNull HashMap<Anomaly, Double> map) {
            return map.entrySet().stream()
                    .min(Map.Entry.comparingByValue())
                    .orElse(null);
        }
    }
}
