package com.minersstudios.msitem.listeners.event.mechanic;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.msitem.MSItem;
import com.minersstudios.msitem.api.CustomItemType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;

@MSListener
public final class CocaineMechanic extends AbstractMSListener<MSItem> {

    @EventHandler
    public void onInventoryClick(final @NotNull PlayerItemConsumeEvent event) {
        final ItemStack itemStack = event.getItem();

        if (
                !(itemStack.getItemMeta() instanceof PotionMeta)
                || CustomItemType.fromItemStack(itemStack) != CustomItemType.COCAINE
        ) return;

        this.getPlugin().runTask(
                () -> event.getPlayer().getInventory().getItem(event.getHand()).setAmount(0)
        );
    }
}