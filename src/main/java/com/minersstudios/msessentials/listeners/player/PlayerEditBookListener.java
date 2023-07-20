package com.minersstudios.msessentials.listeners.player;

import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.msessentials.player.PlayerInfo;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

@MSListener
public class PlayerEditBookListener extends AbstractMSListener {
    private static final Component ANONYMOUS_AUTHOR = Component.translatable("ms.book.anonymous");

    @EventHandler
    public void onPlayerEditBook(@NotNull PlayerEditBookEvent event) {
        if (!event.isSigning()) return;

        PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(event.getPlayer());
        BookMeta bookMeta = event.getNewBookMeta();
        String title = bookMeta.getTitle();
        boolean isAnon = title != null && title.startsWith("*");

        event.setNewBookMeta(bookMeta
                .author(isAnon
                        ? ANONYMOUS_AUTHOR
                        : playerInfo.getDefaultName()
                ).title(isAnon
                        ? text(title.substring(1))
                        : bookMeta.title()
                )
        );
    }
}
