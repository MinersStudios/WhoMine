package com.minersstudios.msitem.listeners.inventory;

import com.minersstudios.msblock.customblock.CustomBlockData;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.util.MSCustomUtils;
import com.minersstudios.msdecor.customdecor.CustomDecorData;
import com.minersstudios.msitem.item.CustomItem;
import com.minersstudios.msitem.item.renameable.RenameableItem;
import com.minersstudios.msitem.item.renameable.RenameableItemRegistry;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PrepareAnvilListener extends AbstractMSListener {

    @EventHandler
    public void onPrepareAnvil(@NotNull PrepareAnvilEvent event) {
        ItemStack resultItem = event.getResult();
        ItemStack firstItem = event.getInventory().getFirstItem();
        String renameText = event.getInventory().getRenameText();

        if (
                resultItem == null
                || firstItem == null
        ) return;

        RenameableItem renameableItem = RenameableItemRegistry.fromRename(renameText, resultItem).orElse(null);

        if (
                renameableItem != null
                && renameableItem.isWhiteListed((OfflinePlayer) event.getViewers().get(0))
        ) {
            ItemStack renamedItem = renameableItem.craftRenamed(resultItem, renameText);

            if (renamedItem != null) {
                event.setResult(renamedItem);
            }
        } else {
            ItemMeta meta = resultItem.getItemMeta();
            var custom = MSCustomUtils.getCustom(firstItem).orElse(null);
            ItemStack customStack = null;

            if (custom == null) {
                meta.setCustomModelData(null);
                resultItem.setItemMeta(meta);
                event.setResult(resultItem);
                return;
            } else if (custom instanceof CustomBlockData data) {
                customStack = data.craftItemStack();
            } else if (custom instanceof CustomItem item) {
                customStack = item.getItem().clone();
            } else if (custom instanceof CustomDecorData data) {
                customStack = data.getItemStack().clone();
            }

            assert customStack != null;

            ItemMeta customMeta = customStack.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            PersistentDataContainer dataContainer = customMeta.getPersistentDataContainer();

            meta.setCustomModelData(customMeta.getCustomModelData());
            meta.lore(customMeta.lore());
            container.getKeys().forEach(container::remove);

            for (var key : dataContainer.getKeys()) {
                String keyStr = dataContainer.get(key, PersistentDataType.STRING);

                if (keyStr != null) {
                    container.set(key, PersistentDataType.STRING, keyStr);
                }
            }

            resultItem.setItemMeta(meta);
            event.setResult(resultItem);
        }
    }
}
