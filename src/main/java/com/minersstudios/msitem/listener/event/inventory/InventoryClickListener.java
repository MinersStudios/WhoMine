package com.minersstudios.msitem.listener.event.inventory;

import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.msitem.MSItem;
import com.minersstudios.msitem.api.CustomItem;
import com.minersstudios.msitem.api.Wearable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class InventoryClickListener extends AbstractEventListener<MSItem> {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(final @NotNull InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final PlayerInventory inventory = player.getInventory();
        final Inventory clickedInventory = event.getClickedInventory();
        final ItemStack cursorItem = event.getCursor();
        final ItemStack currentItem = event.getCurrentItem();

        if (
                event.getSlot() == 39
                && event.getSlotType() == InventoryType.SlotType.ARMOR
                && !cursorItem.getType().isAir()
        ) {
            CustomItem.fromItemStack(currentItem, Wearable.class)
            .ifPresent(w -> {
                assert currentItem != null;

                if (currentItem.getEnchantments().containsKey(Enchantment.BINDING_CURSE)) {
                    return;
                }

                this.getPlugin().runTask(() -> {
                    inventory.setHelmet(cursorItem);
                    player.setItemOnCursor(currentItem);
                });
            });
        }

        if (
                clickedInventory != null
                && currentItem != null
                && event.isShiftClick()
                && clickedInventory.getType() == InventoryType.PLAYER
                && player.getOpenInventory().getType() == InventoryType.CRAFTING
                && inventory.getHelmet() == null
        ) {
            CustomItem.fromItemStack(currentItem, Wearable.class)
            .ifPresent(w -> {
                event.setCancelled(true);
                this.getPlugin().runTask(() -> {
                    inventory.setHelmet(currentItem);
                    currentItem.setAmount(0);
                });
            });
        }
    }
}
