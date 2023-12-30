package com.minersstudios.msessentials.listener.impl.event.inventory;

import com.minersstudios.mscore.language.LanguageRegistry;
import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.msessentials.MSEssentials;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

@EventListener
public final class InventoryClickListener extends AbstractEventListener<MSEssentials> {
    private static final int HELMET_SLOT = 39;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(final @NotNull InventoryClickEvent event) {
        final Inventory clickedInventory = event.getClickedInventory();

        if (clickedInventory == null) {
            return;
        }

        final MSEssentials plugin = this.getPlugin();
        final Player player = (Player) event.getWhoClicked();
        final ItemStack currentItem = event.getCurrentItem();

        if (plugin.getCache().getWorldDark().isInWorldDark(player)) {
            event.setCancelled(true);
        }

        if (currentItem == null) {
            return;
        }

        final int slot = event.getSlot();
        final ItemStack cursorItem = event.getCursor();

        if (
                slot == HELMET_SLOT
                && event.getSlotType() == InventoryType.SlotType.ARMOR
                && currentItem.isEmpty()
                && !cursorItem.isEmpty()
        ) {
            player.setItemOnCursor(null);
            plugin.runTask(
                    () -> player.getInventory().setHelmet(cursorItem)
            );
        }

        if (!currentItem.isEmpty()) {
            boolean remove = currentItem.getType() == Material.BEDROCK;

            if (!remove) {
                for (final var enchantment : currentItem.getEnchantments().keySet()) {
                    remove = currentItem.getEnchantmentLevel(enchantment) > enchantment.getMaxLevel();
                }
            }

            if (remove) {
                clickedInventory.setItem(slot, new ItemStack(Material.AIR));
                MSLogger.warning(
                        LanguageRegistry.Components.INFO_PLAYER_ITEM_REMOVED
                        .arguments(
                                player.name(),
                                text(currentItem.toString())
                        )
                );
                event.setCancelled(true);
            }
        }
    }
}
