package com.minersstudios.msitem.listeners.inventory;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.util.MSItemUtils;
import com.minersstudios.msitem.items.Wearable;
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

@MSListener
public class InventoryClickListener extends AbstractMSListener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        PlayerInventory inventory = player.getInventory();
        Inventory clickedInventory = event.getClickedInventory();
        ItemStack cursorItem = event.getCursor(),
                currentItem = event.getCurrentItem();
        int slot = event.getSlot();

        if (
                slot == 39
                && event.getSlotType() == InventoryType.SlotType.ARMOR
                && cursorItem != null
                && !cursorItem.getType().isAir()
                && MSItemUtils.getCustomItem(cursorItem).orElse(null) instanceof Wearable
        ) {
            if (
                    currentItem != null
                    && currentItem.getEnchantments().containsKey(Enchantment.BINDING_CURSE)
            ) return;

            this.getPlugin().runTask(() -> {
                inventory.setHelmet(cursorItem);
                player.setItemOnCursor(currentItem);
            });
        }

        if (
                clickedInventory != null
                && currentItem != null
                && event.isShiftClick()
                && clickedInventory.getType() == InventoryType.PLAYER
                && player.getOpenInventory().getType() == InventoryType.CRAFTING
                && inventory.getHelmet() == null
                && MSItemUtils.getCustomItem(currentItem).orElse(null) instanceof Wearable
        ) {
            event.setCancelled(true);
            this.getPlugin().runTask(() -> {
                inventory.setHelmet(currentItem);
                currentItem.setAmount(0);
            });
        }
    }
}
