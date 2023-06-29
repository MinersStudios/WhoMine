package com.github.minersstudios.msessentials.listeners.player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.github.minersstudios.msessentials.MSEssentials;
import com.github.minersstudios.msessentials.utils.SignMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class PlayerUpdateSignListener extends PacketAdapter {

    public PlayerUpdateSignListener() {
        super(MSEssentials.getInstance(), PacketType.Play.Client.UPDATE_SIGN);
    }

    @Override
    public void onPacketReceiving(@NotNull PacketEvent event) {
        Player player = event.getPlayer();
        Map<Player, SignMenu> signs = MSEssentials.getConfigCache().signs;
        if (signs.isEmpty() || !signs.containsKey(player)) return;
        SignMenu menu = signs.remove(player);

        event.setCancelled(true);

        if (!menu.getResponse().test(player, event.getPacket().getStringArrays().read(0))) {
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> menu.open(player), 2L);
        } else {
            menu.close(player);
        }
    }
}
