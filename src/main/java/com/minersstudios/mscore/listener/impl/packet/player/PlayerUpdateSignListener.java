package com.minersstudios.mscore.listener.impl.packet.player;

import com.minersstudios.mscore.MSCore;
import com.minersstudios.mscore.listener.api.packet.AbstractPacketListener;
import com.minersstudios.mscore.listener.api.packet.PacketListener;
import com.minersstudios.mscore.packet.PacketEvent;
import com.minersstudios.mscore.packet.PacketType;
import com.minersstudios.mscore.inventory.SignMenu;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@PacketListener
public final class PlayerUpdateSignListener extends AbstractPacketListener<MSCore> {

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
