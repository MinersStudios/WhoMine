package com.github.minersstudios.msitem.listeners.mechanic;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.mscore.utils.ItemUtils;
import com.github.minersstudios.mscore.utils.MSItemUtils;
import com.github.minersstudios.msitem.items.register.items.cards.CardsBicycle;
import com.google.common.collect.Lists;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

@SuppressWarnings("UnstableApiUsage")
@MSListener
public class CardBoxMechanic implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryMoveItem(@NotNull InventoryMoveItemEvent event) {
        if (
                event.getDestination().getType() == InventoryType.SHULKER_BOX
                && MSItemUtils.getCustomItem(event.getItem()) instanceof CardsBicycle
        ) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryDrag(@NotNull InventoryDragEvent event) {
        if (
                event.getInventory().getType() == InventoryType.SHULKER_BOX
                && MSItemUtils.getCustomItem(event.getOldCursor()) instanceof CardsBicycle
        ) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        ItemStack cursorItem = event.getCursor();
        ItemStack currentItem = event.getCurrentItem();
        Inventory clickedInventory = event.getClickedInventory();

        if (
                (clickedInventory != null
                && clickedInventory.getType() == InventoryType.SHULKER_BOX
                && MSItemUtils.getCustomItem(cursorItem) instanceof CardsBicycle)
                || (event.isShiftClick()
                && event.getWhoClicked().getOpenInventory().getType() == InventoryType.SHULKER_BOX
                && MSItemUtils.getCustomItem(currentItem) instanceof CardsBicycle)
        ) {
            event.setCancelled(true);
        }

        if (cursorItem == null || currentItem == null || !event.isRightClick()) return;
        if (
                !cursorItem.getType().isAir()
                && MSItemUtils.getCustomItem(currentItem) instanceof CardsBicycle
        ) {
            addCardToCardBox(event, currentItem, cursorItem);
        } else if (
                !currentItem.getType().isAir()
                && MSItemUtils.getCustomItem(cursorItem) instanceof CardsBicycle
        ) {
            addCardToCardBox(event, cursorItem, currentItem);
        }
    }

    private static void addCardToCardBox(
            @NotNull InventoryClickEvent event,
            @NotNull ItemStack cardBoxItem,
            @NotNull ItemStack cardItem
    ) {
        var cards = new ArrayList<ItemStack>();
        cards.addAll(CardsBicycle.BLUE_CARD_ITEMS);
        cards.addAll(CardsBicycle.RED_CARD_ITEMS);

        if (ItemUtils.isListContainsItem(cards, cardItem)) {
            BundleMeta bundleMeta = (BundleMeta) cardBoxItem.getItemMeta();
            var itemStacks = Lists.newArrayList(cardItem);
            itemStacks.addAll(bundleMeta.getItems());

            if (!(itemStacks.size() > 54)) {
                bundleMeta.setItems(itemStacks);
                cardBoxItem.setItemMeta(bundleMeta);
                cardItem.setAmount(0);
            }
        }

        ((Player) event.getWhoClicked()).updateInventory();
        event.setCancelled(true);
    }
}
