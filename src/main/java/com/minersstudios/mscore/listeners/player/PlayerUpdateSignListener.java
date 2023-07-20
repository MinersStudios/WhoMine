package com.minersstudios.mscore.listeners.player;

import com.minersstudios.mscore.listener.packet.AbstractMSPacketListener;
import com.minersstudios.mscore.listener.packet.MSPacketListener;
import com.minersstudios.mscore.packet.PacketContainer;
import com.minersstudios.mscore.packet.PacketEvent;
import com.minersstudios.mscore.packet.PacketType;
import com.minersstudios.mscore.utils.SignMenu;
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
        PacketContainer packetContainer = event.getPacketContainer();
        Player player = event.getPlayer();

        if (
                SignMenu.SIGN_MENU_MAP.isEmpty()
                || !SignMenu.SIGN_MENU_MAP.containsKey(player)
        ) return;

        SignMenu menu = SignMenu.SIGN_MENU_MAP.remove(player);

        if (packetContainer.getPacket() instanceof ServerboundSignUpdatePacket packet) {
            if (!menu.getResponse().test(player, packet.getLines())) {
                this.getPlugin().runTaskLater(() -> menu.open(player), 2L);
            } else {
                menu.close(player);
            }
        }
    }
}
