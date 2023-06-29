package com.github.minersstudios.msutils.listeners.player;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.msutils.MSUtils;
import com.github.minersstudios.msutils.player.PlayerInfo;
import com.github.minersstudios.msutils.player.PlayerInfoMap;
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

        PlayerInfoMap playerInfoMap = MSUtils.getConfigCache().playerInfoMap;
        PlayerInfo playerInfo = playerInfoMap.getPlayerInfo(event.getPlayer());
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
