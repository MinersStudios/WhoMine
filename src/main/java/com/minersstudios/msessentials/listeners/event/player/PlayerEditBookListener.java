package com.minersstudios.msessentials.listeners.event.player;

import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

@MSListener
public final class PlayerEditBookListener extends AbstractMSListener<MSEssentials> {
    private static final Component ANONYMOUS_AUTHOR = translatable("ms.book.anonymous");

    @EventHandler
    public void onPlayerEditBook(final @NotNull PlayerEditBookEvent event) {
        if (!event.isSigning()) return;

        final PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(event.getPlayer());
        final BookMeta bookMeta = event.getNewBookMeta();
        final String title = bookMeta.getTitle();
        final boolean isAnon = title != null && title.startsWith("*");

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
