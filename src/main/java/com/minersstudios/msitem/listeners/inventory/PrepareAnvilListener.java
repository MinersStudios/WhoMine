package com.minersstudios.msitem.listeners.inventory;

import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.util.MSBlockUtils;
import com.minersstudios.mscore.util.MSDecorUtils;
import com.minersstudios.mscore.util.MSItemUtils;
import com.minersstudios.msitem.items.Renameable;
import com.minersstudios.msitem.items.RenameableItem;
import com.minersstudios.msitem.utils.CustomItemUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PrepareAnvilListener extends AbstractMSListener {

    @EventHandler
    public void onPrepareAnvil(@NotNull PrepareAnvilEvent event) {
        ItemStack resultItem = event.getResult();
        ItemStack firstItem = event.getInventory().getFirstItem();
        String renameText = event.getInventory().getRenameText();

        if (resultItem == null || firstItem == null) return;
        if (MSItemUtils.getCustomItem(resultItem).orElse(null) instanceof Renameable renameable) {
            ItemStack renameableItem = renameable.createRenamedItem(resultItem, renameText);

            if (renameableItem != null) {
                event.setResult(renameableItem);
            }
        } else {
            RenameableItem renameableItem = CustomItemUtils.getRenameableItem(resultItem, renameText);

            if (
                    renameableItem != null
                    && renameableItem.isWhiteListed((OfflinePlayer) event.getViewers().get(0))
            ) {
                ItemStack renamedItem = renameableItem.createRenamedItem(resultItem, renameText);

                if (renamedItem != null) {
                    event.setResult(renamedItem);
                }
            } else if (
                    !MSBlockUtils.isCustomBlock(firstItem)
                    && !MSDecorUtils.isCustomDecor(firstItem)
                    && !MSItemUtils.isCustomItem(firstItem)
            ) {
                ItemMeta itemMeta = resultItem.getItemMeta();
                itemMeta.setCustomModelData(null);
                resultItem.setItemMeta(itemMeta);
                event.setResult(resultItem);
            }
        }
    }
}
