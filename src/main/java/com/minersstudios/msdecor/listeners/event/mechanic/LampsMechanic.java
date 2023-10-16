package com.minersstudios.msdecor.listeners.event.mechanic;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class LampsMechanic extends AbstractMSListener {

    @EventHandler
    public void onInteractWithArmorStand(@NotNull PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof final ArmorStand armorStand)) return;

        //TODO
        /*
        final Block block = armorStand.getLocation().getBlock();
        final var customDecorData = MSDecorUtils.getCustomDecorDataByEntity(armorStand).orElse(null);


        if (
                (customDecorData instanceof SmallLamp
                || customDecorData instanceof BigLamp)
                && block.getBlockData() instanceof final Levelled levelled
        ) {
            final LightableDecorData lightableDecorData = (LightableDecorData) customDecorData;

            if (levelled.getLevel() == lightableDecorData.getInitialLightLevel()) {
                levelled.setLevel(lightableDecorData.getFinalLightLevel());
                armorStand.getWorld().playSound(armorStand.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_OFF, SoundCategory.PLAYERS, 1.0f, 2.0f);
            } else {
                levelled.setLevel(lightableDecorData.getInitialLightLevel());
                armorStand.getWorld().playSound(armorStand.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.PLAYERS, 1.0f, 2.0f);
            }

            block.setType(Material.LIGHT);
            block.setBlockData(levelled, true);
            event.getPlayer().swingHand(event.getHand());
        }

         */
    }
}
