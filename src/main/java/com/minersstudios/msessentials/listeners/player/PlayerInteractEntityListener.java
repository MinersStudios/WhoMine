package com.minersstudios.msessentials.listeners.player;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.util.MessageUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;

@MSListener
public class PlayerInteractEntityListener extends AbstractMSListener {
    private final SecureRandom random = new SecureRandom();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteractEntity(final @NotNull PlayerInteractEntityEvent event) {
        final Player whoClicked = event.getPlayer();

        if (event.getRightClicked() instanceof final Player clickedPlayer) {
            final PlayerInfo clickedInfo = PlayerInfo.fromOnlinePlayer(clickedPlayer);
            final ItemStack helmet = clickedPlayer.getInventory().getHelmet();
            final float pitch = whoClicked.getEyeLocation().getPitch();

            if (
                    (pitch >= 80 && pitch <= 90)
                    && whoClicked.isSneaking()
                    && !whoClicked.getPassengers().isEmpty()
            ) {
                whoClicked.eject();
            }

            whoClicked.sendActionBar(
                    clickedInfo.getPlayerFile().getPlayerName()
                    .createFullName(
                            clickedInfo.getID(),
                            MessageUtils.Colors.JOIN_MESSAGE_COLOR_SECONDARY,
                            MessageUtils.Colors.JOIN_MESSAGE_COLOR_PRIMARY
                    )
            );

            if (
                    !whoClicked.isInsideVehicle()
                    && helmet != null
                    && !whoClicked.isSneaking()
                    && helmet.getType() == Material.SADDLE
            ) {
                final var passengers = clickedPlayer.getPassengers();

                if (passengers.isEmpty()) {
                    clickedPlayer.addPassenger(whoClicked);
                } else {
                    passengers.get(passengers.size() - 1).addPassenger(whoClicked);
                }
            }
        } else if (event.getRightClicked() instanceof final ItemFrame itemFrame) {
            final boolean hasScoreboardTag = itemFrame.getScoreboardTags().contains("invisibleItemFrame");
            final Material frameMaterial = itemFrame.getItem().getType();
            final Material handMaterial = whoClicked.getInventory().getItemInMainHand().getType();

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
