package com.minersstudios.msdecor.listener.event.player;

import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.mscore.utility.MSDecorUtils;
import com.minersstudios.msdecor.MSDecor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class PlayerBucketEmptyListener extends AbstractEventListener<MSDecor> {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerBucketEmpty(final @NotNull PlayerBucketEmptyEvent event) {
        if (
                event.getBucket() == Material.LAVA_BUCKET
                && MSDecorUtils.isCustomDecor(event.getBlock())
        ) {
            event.setCancelled(true);
        }
    }
}
