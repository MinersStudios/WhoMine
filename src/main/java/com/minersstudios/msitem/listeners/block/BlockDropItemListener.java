package com.minersstudios.msitem.listeners.block;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.msitem.item.renameable.RenameableItemRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.Tag;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

@MSListener
public class BlockDropItemListener extends AbstractMSListener {

    @EventHandler
    public void onBlockDropItem(@NotNull BlockDropItemEvent event) {
        var items = event.getItems();

        if (items.size() != 1) return;

        Item entity = items.get(0);
        ItemStack item = entity.getItemStack();

        if (!Tag.SHULKER_BOXES.isTagged(item.getType())) return;

        ItemMeta meta = item.getItemMeta();
        Component displayName = meta.displayName();

        if (displayName == null) return;

        String serialized = ChatUtils.serializePlainComponent(displayName);

        RenameableItemRegistry.fromRename(serialized, item)
        .ifPresent(renameableItem -> {
            ItemStack renameableItemStack = renameableItem.craftRenamed(item, serialized);

            if (renameableItemStack != null) {
                entity.setItemStack(renameableItemStack);
            }
        });
    }
}
