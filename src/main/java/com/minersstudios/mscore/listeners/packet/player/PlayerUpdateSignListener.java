package com.minersstudios.mscore.listeners.packet.player;

import com.minersstudios.mscore.MSCore;
import com.minersstudios.mscore.listener.packet.AbstractMSPacketListener;
import com.minersstudios.mscore.listener.packet.MSPacketListener;
import com.minersstudios.mscore.packet.PacketEvent;
import com.minersstudios.mscore.packet.PacketType;
import com.minersstudios.mscore.inventory.SignMenu;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@MSPacketListener
public final class PlayerUpdateSignListener extends AbstractMSPacketListener<MSCore> {

    public PlayerUpdateSignListener() {
        super(PacketType.Play.Server.UPDATE_SIGN);
    }

    @Override
    public void onPacketReceive(final @NotNull PacketEvent event) {
        final Player player = event.getConnection().getPlayer().getBukkitEntity();
        final SignMenu menu = SignMenu.getSignMenu(player);

        if (
                menu != null
                && event.getPacketContainer().getPacket() instanceof final ServerboundSignUpdatePacket packet
        ) {
            if (!menu.getResponse().test(player, packet.getLines())) {
                this.getPlugin().runTaskLater(() -> menu.open(player), 2L);
            } else {
                menu.close(player);
            }
        }
    }
}
