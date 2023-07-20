package com.minersstudios.msdecor.listeners.block;

import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.utils.MSDecorUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class BlockPlaceListener extends AbstractMSListener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(@NotNull BlockPlaceEvent event) {
        if (
                MSDecorUtils.isCustomDecorMaterial(event.getBlockReplacedState().getType())
                || MSDecorUtils.isCustomDecor(event.getPlayer().getInventory().getItemInMainHand())
        ) {
            event.setCancelled(true);
        }
    }
}
