package com.minersstudios.mscustoms.listener.mechanic;

import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.mscore.utility.ItemUtils;
import com.minersstudios.mscustoms.MSCustoms;
import com.minersstudios.mscustoms.custom.item.CustomItem;
import com.minersstudios.mscustoms.registry.item.cards.CardsBicycle;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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

import java.util.List;

@EventListener
public final class CardBoxMechanic extends AbstractEventListener<MSCustoms> {
    private static final List<ItemStack> CARDS = new ObjectArrayList<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryMoveItem(final @NotNull InventoryMoveItemEvent event) {
        if (event.getDestination().getType() != InventoryType.SHULKER_BOX) {
            return;
        }

        CustomItem.fromItemStack(event.getItem())
        .filter(customItem -> customItem instanceof CardsBicycle)
        .ifPresent(
                c -> event.setCancelled(true)
        );
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryDrag(final @NotNull InventoryDragEvent event) {
        if (event.getInventory().getType() != InventoryType.SHULKER_BOX) {
            return;
        }

        CustomItem.fromItemStack(event.getOldCursor())
        .filter(customItem -> customItem instanceof CardsBicycle)
        .ifPresent(
                c -> event.setCancelled(true)
        );
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(final @NotNull InventoryClickEvent event) {
        final ItemStack cursorItem = event.getCursor();
        final ItemStack currentItem = event.getCurrentItem();
        final Inventory clickedInventory = event.getClickedInventory();

        if (
                (
                        clickedInventory != null
                        && clickedInventory.getType() == InventoryType.SHULKER_BOX
                        && CustomItem.fromItemStack(cursorItem).orElse(null) instanceof CardsBicycle
                )
                || (
                        event.isShiftClick()
                        && event.getWhoClicked().getOpenInventory().getType() == InventoryType.SHULKER_BOX
                        && CustomItem.fromItemStack(currentItem).orElse(null) instanceof CardsBicycle
                )
        ) {
            event.setCancelled(true);
        }

        if (
                currentItem == null
                || !event.isRightClick()
        ) {
            return;
        }

        if (
                !cursorItem.getType().isAir()
                && CustomItem.fromItemStack(currentItem).orElse(null) instanceof CardsBicycle
        ) {
            addCardToCardBox(event, currentItem, cursorItem);
        } else if (
                !currentItem.getType().isAir()
                && CustomItem.fromItemStack(cursorItem).orElse(null) instanceof CardsBicycle
        ) {
            addCardToCardBox(event, cursorItem, currentItem);
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    private static void addCardToCardBox(
            final @NotNull InventoryClickEvent event,
            final @NotNull ItemStack cardBoxItem,
            final @NotNull ItemStack cardItem
    ) {
        if (CARDS.isEmpty()) {
            CARDS.addAll(CardsBicycle.Blue.cardItems());
            CARDS.addAll(CardsBicycle.Red.cardItems());
        }

        if (ItemUtils.isContainsItem(CARDS, cardItem)) {
            final BundleMeta bundleMeta = (BundleMeta) cardBoxItem.getItemMeta();
            final var itemStacks = new ObjectArrayList<ItemStack>();

            itemStacks.add(cardItem);
            itemStacks.addAll(bundleMeta.getItems());

            if (itemStacks.size() <= 54) {
                bundleMeta.setItems(itemStacks);
                cardBoxItem.setItemMeta(bundleMeta);
                cardItem.setAmount(0);
            }
        }

        event.setCancelled(true);
    }
}
