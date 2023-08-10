package com.minersstudios.msitem.listeners.mechanic;

import com.google.common.collect.Lists;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.util.ItemUtils;
import com.minersstudios.msitem.item.CustomItemType;
import com.minersstudios.msitem.item.registry.items.cards.CardsBicycle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

@MSListener
public class CardBoxMechanic extends AbstractMSListener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryMoveItem(@NotNull InventoryMoveItemEvent event) {
        if (event.getDestination().getType() != InventoryType.SHULKER_BOX) return;

        CustomItemType.fromItemStack(event.getItem())
        .filter(customItem -> customItem instanceof CardsBicycle)
        .ifPresent(
                c -> event.setCancelled(true)
        );
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryDrag(@NotNull InventoryDragEvent event) {
        if (event.getInventory().getType() != InventoryType.SHULKER_BOX) return;

        CustomItemType.fromItemStack(event.getOldCursor())
        .filter(customItem -> customItem instanceof CardsBicycle)
        .ifPresent(
                c -> event.setCancelled(true)
        );
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        ItemStack cursorItem = event.getCursor();
        ItemStack currentItem = event.getCurrentItem();
        Inventory clickedInventory = event.getClickedInventory();

        if (
                (clickedInventory != null
                && clickedInventory.getType() == InventoryType.SHULKER_BOX
                && CustomItemType.fromItemStack(cursorItem).orElse(null) instanceof CardsBicycle)
                || (event.isShiftClick()
                && event.getWhoClicked().getOpenInventory().getType() == InventoryType.SHULKER_BOX
                && CustomItemType.fromItemStack(currentItem).orElse(null) instanceof CardsBicycle)
        ) {
            event.setCancelled(true);
        }

        if (cursorItem == null || currentItem == null || !event.isRightClick()) return;
        if (
                !cursorItem.getType().isAir()
                && CustomItemType.fromItemStack(currentItem).orElse(null) instanceof CardsBicycle
        ) {
            addCardToCardBox(event, currentItem, cursorItem);
        } else if (
                !currentItem.getType().isAir()
                && CustomItemType.fromItemStack(cursorItem).orElse(null) instanceof CardsBicycle
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

        if (ItemUtils.isContainsItem(cards, cardItem)) {
            BundleMeta bundleMeta = (BundleMeta) cardBoxItem.getItemMeta();
            var itemStacks = Lists.newArrayList(cardItem);
            itemStacks.addAll(bundleMeta.getItems());

            if (!(itemStacks.size() > 54)) {
                bundleMeta.setItems(itemStacks);
                cardBoxItem.setItemMeta(bundleMeta);
                cardItem.setAmount(0);
            }
        }

        event.setCancelled(true);
    }
}
