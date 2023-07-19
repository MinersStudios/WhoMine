package com.minersstudios.mscore.listeners.player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.mscore.utils.SignMenu;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerUpdateSignListener extends PacketAdapter {
    private final MSPlugin plugin;

    public PlayerUpdateSignListener(@NotNull MSPlugin plugin) {
        super(plugin, PacketType.Play.Client.UPDATE_SIGN);
        this.plugin = plugin;
    }

    @Override
    public void onPacketReceiving(@NotNull PacketEvent event) {
        Player player = event.getPlayer();
        if (SignMenu.SIGN_MENU_MAP.isEmpty() || !SignMenu.SIGN_MENU_MAP.containsKey(player)) return;
        SignMenu menu = SignMenu.SIGN_MENU_MAP.remove(player);

        event.setCancelled(true);

        if (!menu.getResponse().test(player, event.getPacket().getStringArrays().read(0))) {
            this.plugin.runTaskLater(() -> menu.open(player), 2L);
        } else {
            menu.close(player);
        }
    }
}
