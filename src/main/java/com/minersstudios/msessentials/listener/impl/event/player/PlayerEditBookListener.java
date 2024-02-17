package com.minersstudios.msessentials.listener.impl.event.player;

import com.minersstudios.mscore.locale.Translations;
import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

@EventListener
public final class PlayerEditBookListener extends AbstractEventListener<MSEssentials> {

    @EventHandler
    public void onPlayerEditBook(final @NotNull PlayerEditBookEvent event) {
        if (!event.isSigning()) {
            return;
        }

        final PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(this.getPlugin(), event.getPlayer());
        final BookMeta bookMeta = event.getNewBookMeta();
        final String title = bookMeta.getTitle();
        final boolean isAnon =
                title != null
                && title.startsWith("*");

        event.setNewBookMeta(bookMeta
                .author(
                        isAnon
                        ? Translations.BOOK_ANONYMOUS.asTranslatable()
                        : playerInfo.getDefaultName()
                ).title(
                        isAnon
                        ? text(title.substring(1))
                        : bookMeta.title()
                )
        );
    }
}
