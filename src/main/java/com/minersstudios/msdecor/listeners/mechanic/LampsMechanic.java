package com.minersstudios.msdecor.listeners.mechanic;

import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.msdecor.customdecor.CustomDecorData;
import com.minersstudios.msdecor.customdecor.Lightable;
import com.minersstudios.msdecor.customdecor.register.furniture.lamps.BigLamp;
import com.minersstudios.msdecor.customdecor.register.furniture.lamps.SmallLamp;
import com.minersstudios.msdecor.utils.CustomDecorUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class LampsMechanic extends AbstractMSListener {

    @EventHandler
    public void onInteractWithArmorStand(@NotNull PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof ArmorStand armorStand)) return;

        Block block = armorStand.getLocation().getBlock();
        CustomDecorData customDecorData = CustomDecorUtils.getCustomDecorDataByEntity(armorStand);

        if (
                (customDecorData instanceof SmallLamp
                || customDecorData instanceof BigLamp)
                && block.getBlockData() instanceof Levelled levelled
        ) {
            Lightable lightable = (Lightable) customDecorData;

            if (levelled.getLevel() == lightable.getFirstLightLevel()) {
                levelled.setLevel(lightable.getSecondLightLevel());
                armorStand.getWorld().playSound(armorStand.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_OFF, SoundCategory.PLAYERS, 1.0f, 2.0f);
            } else {
                levelled.setLevel(lightable.getFirstLightLevel());
                armorStand.getWorld().playSound(armorStand.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.PLAYERS, 1.0f, 2.0f);
            }

            block.setType(Material.LIGHT);
            block.setBlockData(levelled, true);
            event.getPlayer().swingHand(event.getHand());
        }
    }
}
