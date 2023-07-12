package com.github.minersstudios.msessentials.listeners.player;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

@MSListener
public class PlayerEditBookListener implements Listener {

    @EventHandler
    public void onPlayerEditBook(@NotNull PlayerEditBookEvent event) {
        if (!event.isSigning()) return;

        PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(event.getPlayer());
        BookMeta bookMeta = event.getNewBookMeta();
        String title = bookMeta.getTitle();
        boolean isAnon = title != null && title.startsWith("*");

        event.setNewBookMeta(bookMeta
                .author(isAnon
                        ? Component.translatable("ms.book.anonymous")
                        : playerInfo.getDefaultName()
                ).title(isAnon
                        ? text(title.substring(1))
                        : bookMeta.title()
                )
        );
    }
}
