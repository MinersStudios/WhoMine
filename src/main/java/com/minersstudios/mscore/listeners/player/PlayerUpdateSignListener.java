package com.minersstudios.mscore.listeners.player;

import com.minersstudios.mscore.listener.packet.AbstractMSPacketListener;
import com.minersstudios.mscore.listener.packet.MSPacketListener;
import com.minersstudios.mscore.packet.PacketEvent;
import com.minersstudios.mscore.packet.PacketType;
import com.minersstudios.mscore.util.SignMenu;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@MSPacketListener
public class PlayerUpdateSignListener extends AbstractMSPacketListener {

    public PlayerUpdateSignListener() {
        super(PacketType.Play.Server.UPDATE_SIGN);
    }

    @Override
    public void onPacketReceive(@NotNull PacketEvent event) {
        Player player = event.getPlayer();
        SignMenu menu = SignMenu.getSignMenu(player);

        if (
                menu != null
                && event.getPacketContainer().getPacket() instanceof ServerboundSignUpdatePacket packet
        ) {
            if (!menu.getResponse().test(player, packet.getLines())) {
                this.getPlugin().runTaskLater(() -> menu.open(player), 2L);
            } else {
                menu.close(player);
            }
        }
    }
}
