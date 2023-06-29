package com.github.minersstudios.msitems.listeners.inventory;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.mscore.utils.MSBlockUtils;
import com.github.minersstudios.mscore.utils.MSDecorUtils;
import com.github.minersstudios.mscore.utils.MSItemUtils;
import com.github.minersstudios.msitems.items.Renameable;
import com.github.minersstudios.msitems.items.RenameableItem;
import com.github.minersstudios.msitems.utils.CustomItemUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PrepareAnvilListener implements Listener {

	@EventHandler
	public void onPrepareAnvil(@NotNull PrepareAnvilEvent event) {
		ItemStack resultItem = event.getResult();
		ItemStack firstItem = event.getInventory().getFirstItem();
		String renameText = event.getInventory().getRenameText();
		if (resultItem == null || firstItem == null) return;
		if (MSItemUtils.getCustomItem(resultItem) instanceof Renameable renameable) {
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
