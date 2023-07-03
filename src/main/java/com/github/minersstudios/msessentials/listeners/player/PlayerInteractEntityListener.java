package com.github.minersstudios.msessentials.listeners.player;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.github.minersstudios.msessentials.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.List;

import static net.kyori.adventure.text.Component.*;

@MSListener
public class PlayerInteractEntityListener implements Listener {
    private final SecureRandom random = new SecureRandom();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteractEntity(@NotNull PlayerInteractEntityEvent event) {
        Player whoClicked = event.getPlayer();

        if (event.getRightClicked() instanceof Player clickedPlayer) {
            float pitch = whoClicked.getEyeLocation().getPitch();

            if (
                    (pitch >= 80 && pitch <= 90)
                    && whoClicked.isSneaking()
                    && !whoClicked.getPassengers().isEmpty()
            ) {
                whoClicked.eject();
            }

            PlayerInfo clickedInfo = PlayerInfo.fromMap(clickedPlayer);

            whoClicked.sendActionBar(
                    empty()
                    .append(clickedInfo.getGoldenName())
                    .append(space())
                    .append(text(clickedInfo.getPlayerFile().getPlayerName().getPatronymic(), MessageUtils.Colors.JOIN_MESSAGE_COLOR_PRIMARY))
            );

            ItemStack helmet = clickedPlayer.getInventory().getHelmet();

            if (
                    !whoClicked.isInsideVehicle()
                    && helmet != null
                    && !whoClicked.isSneaking()
                    && helmet.getType() == Material.SADDLE
            ) {
                List<Entity> passengers = clickedPlayer.getPassengers();
                if (passengers.isEmpty()) {
                    clickedPlayer.addPassenger(whoClicked);
                } else {
                    passengers.get(passengers.size() - 1).addPassenger(whoClicked);
                }
            }
        } else if (event.getRightClicked() instanceof ItemFrame itemFrame) {
            boolean hasScoreboardTag = itemFrame.getScoreboardTags().contains("invisibleItemFrame");
            Material frameMaterial = itemFrame.getItem().getType();
            Material handMaterial = whoClicked.getInventory().getItemInMainHand().getType();

            if (
                    frameMaterial.isAir()
                    && !handMaterial.isAir()
                    && hasScoreboardTag
            ) {
                itemFrame.setVisible(false);
            } else if (
                    (!frameMaterial.isAir() || whoClicked.isSneaking())
                    && handMaterial == Material.SHEARS
                    && !hasScoreboardTag
            ) {
                whoClicked.getWorld().playSound(itemFrame.getLocation(), Sound.ENTITY_SHEEP_SHEAR, SoundCategory.PLAYERS, 1.0f, this.random.nextFloat() * 0.1f + 0.5f);
                itemFrame.addScoreboardTag("invisibleItemFrame");
                itemFrame.setVisible(frameMaterial.isAir());
                event.setCancelled(true);
            }
        }
    }
}
