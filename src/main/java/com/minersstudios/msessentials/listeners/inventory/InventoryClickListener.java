package com.minersstudios.msessentials.listeners.inventory;

import com.minersstudios.mscore.listener.MSListener;
import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.world.WorldDark;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import com.minersstudios.mscore.listener.AbstractMSListener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

@MSListener
public class InventoryClickListener extends AbstractMSListener {
    private static final TranslatableComponent REMOVED_ITEM = Component.translatable("ms.info.player_item_removed");

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        int slot = event.getSlot();
        ItemStack cursorItem = event.getCursor();
        ItemStack currentItem = event.getCurrentItem();

        if (clickedInventory == null) return;

        if (WorldDark.isInWorldDark(player)) {
            event.setCancelled(true);
        }

        if (
                slot == 39
                && event.getSlotType() == InventoryType.SlotType.ARMOR
                && cursorItem != null
                && currentItem != null
                && currentItem.getType() == Material.AIR
                && cursorItem.getType() != Material.AIR
        ) {
            player.setItemOnCursor(null);
            MSEssentials.getInstance().runTask(() -> player.getInventory().setHelmet(cursorItem));
        }

        if (currentItem != null && currentItem.getType() != Material.AIR) {
            boolean remove = currentItem.getType() == Material.BEDROCK;

            if (!remove) {
                for (var enchantment : currentItem.getEnchantments().keySet()) {
                    remove = currentItem.getEnchantmentLevel(enchantment) > enchantment.getMaxLevel();
                }
            }

            if (remove) {
                clickedInventory.setItem(slot, new ItemStack(Material.AIR));
                MSLogger.warning(
                        REMOVED_ITEM.args(
                                player.name(),
                                text(currentItem.toString())
                        )
                );
                event.setCancelled(true);
            }
        }
    }
}
