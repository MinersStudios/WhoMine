package com.minersstudios.mscustoms.listener.mechanic;

import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.mscustoms.MSCustoms;
import com.minersstudios.mscustoms.custom.item.CustomItemType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class CocaineMechanic extends AbstractEventListener<MSCustoms> {

    @EventHandler
    public void onInventoryClick(final @NotNull PlayerItemConsumeEvent event) {
        final ItemStack itemStack = event.getItem();

        if (
                !(itemStack.getItemMeta() instanceof PotionMeta)
                || CustomItemType.fromItemStack(itemStack) != CustomItemType.COCAINE
        ) {
            return;
        }

        this.getPlugin().runTask(
                () -> event.getPlayer().getInventory().getItem(event.getHand()).setAmount(0)
        );
    }
}
