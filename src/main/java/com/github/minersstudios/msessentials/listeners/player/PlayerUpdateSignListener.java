package com.github.minersstudios.msessentials.listeners.player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.github.minersstudios.mscore.MSCore;
import com.github.minersstudios.mscore.utils.SignMenu;
import com.github.minersstudios.msessentials.MSEssentials;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerUpdateSignListener extends PacketAdapter {
    private final MSEssentials plugin;

    public PlayerUpdateSignListener(@NotNull MSEssentials plugin) {
        super(plugin, PacketType.Play.Client.UPDATE_SIGN);
        this.plugin = plugin;
    }

    @Override
    public void onPacketReceiving(@NotNull PacketEvent event) {
        Player player = event.getPlayer();
        var signs = MSCore.getCache().signMenuMap;
        if (signs.isEmpty() || !signs.containsKey(player)) return;
        SignMenu menu = signs.remove(player);

        event.setCancelled(true);

        if (!menu.getResponse().test(player, event.getPacket().getStringArrays().read(0))) {
            this.plugin.runTaskLater(() -> menu.open(player), 2L);
        } else {
            menu.close(player);
        }
    }
}
