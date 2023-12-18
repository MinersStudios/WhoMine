package com.minersstudios.msitem.listeners.event.inventory;

import com.minersstudios.msblock.api.CustomBlockData;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSEventListener;
import com.minersstudios.mscore.util.MSCustomUtils;
import com.minersstudios.msdecor.api.CustomDecorData;
import com.minersstudios.msitem.MSItem;
import com.minersstudios.msitem.api.CustomItem;
import com.minersstudios.msitem.api.renameable.RenameableItem;
import com.minersstudios.msitem.api.renameable.RenameableItemRegistry;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

@MSEventListener
public final class PrepareAnvilListener extends AbstractMSListener<MSItem> {

    @EventHandler
    public void onPrepareAnvil(final @NotNull PrepareAnvilEvent event) {
        final ItemStack resultItem = event.getResult();
        final ItemStack firstItem = event.getInventory().getFirstItem();
        final String renameText = event.getInventory().getRenameText();

        if (
                resultItem == null
                || firstItem == null
        ) {
            return;
        }

        final RenameableItem renameableItem = RenameableItemRegistry.fromRename(renameText, resultItem).orElse(null);

        if (
                renameableItem != null
                && renameableItem.isWhiteListed((OfflinePlayer) event.getViewers().get(0))
        ) {
            final ItemStack renamedItem = renameableItem.craftRenamed(resultItem, renameText);

            if (renamedItem != null) {
                event.setResult(renamedItem);
            }
        } else {
            final ItemMeta meta = resultItem.getItemMeta();
            final var custom = MSCustomUtils.getCustom(firstItem).orElse(null);
            ItemStack customStack = null;

            if (custom == null) {
                meta.setCustomModelData(null);
                resultItem.setItemMeta(meta);
                event.setResult(resultItem);
                return;
            } else if (custom instanceof final CustomBlockData data) {
                customStack = data.craftItemStack();
            } else if (custom instanceof final CustomItem item) {
                customStack = item.getItem().clone();
            } else if (custom instanceof final CustomDecorData<?> data) {
                customStack = data.getItem().clone();
            }

            assert customStack != null;

            final ItemMeta customMeta = customStack.getItemMeta();
            final PersistentDataContainer container = meta.getPersistentDataContainer();
            final PersistentDataContainer dataContainer = customMeta.getPersistentDataContainer();

            meta.setCustomModelData(customMeta.getCustomModelData());
            meta.lore(customMeta.lore());
            container.getKeys().forEach(container::remove);

            for (final var key : dataContainer.getKeys()) {
                final String keyStr = dataContainer.get(key, PersistentDataType.STRING);

                if (keyStr != null) {
                    container.set(key, PersistentDataType.STRING, keyStr);
                }
            }

            resultItem.setItemMeta(meta);
            event.setResult(resultItem);
        }
    }
}
