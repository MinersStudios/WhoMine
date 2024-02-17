package com.minersstudios.msessentials.listener.impl.event.player;

import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.mscore.locale.TranslationRegistry;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscustoms.MSCustoms;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerFile;
import com.minersstudios.msessentials.player.PlayerInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.jetbrains.annotations.NotNull;

import static com.minersstudios.mscore.locale.Translations.*;
import static net.kyori.adventure.text.Component.text;
import static org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.*;

@EventListener
public final class AsyncPlayerPreLoginListener extends AbstractEventListener<MSEssentials> {
    //<editor-fold desc="Component Messages" defaultstate="collapsed">
    private static final Style TITLE_STYLE = Style.style(NamedTextColor.RED, TextDecoration.BOLD);
    private static final Style SUBTITLE_STYLE = Style.style(NamedTextColor.GRAY);

    private static final TranslatableComponent LEAVE_MESSAGE_FORMAT =
            FORMAT_LEAVE_MESSAGE.asTranslatable().color(NamedTextColor.DARK_GRAY);
    private static final Component MESSAGE_WHITELIST =
            TranslationRegistry.renderComponent(
                    LEAVE_MESSAGE_FORMAT.arguments(
                            PRE_LOGIN_WHITELISTED_TITLE.asTranslatable().style(TITLE_STYLE),
                            PRE_LOGIN_WHITELISTED_SUBTITLE.asTranslatable().style(SUBTITLE_STYLE)
                    )
            );
    private static final Component MESSAGE_SERVER_NOT_FULLY_LOADED =
            TranslationRegistry.renderComponent(
                    LEAVE_MESSAGE_FORMAT.arguments(
                            SERVER_NOT_FULLY_LOADED_TITLE.asTranslatable().style(TITLE_STYLE),
                            SERVER_NOT_FULLY_LOADED_SUBTITLE.asTranslatable().style(SUBTITLE_STYLE)
                    )
            );
    private static final Component MESSAGE_TECH_WORKS =
            TranslationRegistry.renderComponent(
                    LEAVE_MESSAGE_FORMAT.arguments(
                            PRE_LOGIN_TECH_WORKS_TITLE.asTranslatable().style(TITLE_STYLE),
                            PRE_LOGIN_TECH_WORKS_SUBTITLE.asTranslatable().style(SUBTITLE_STYLE)
                    )
            );
    //</editor-fold>

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAsyncPlayerPreLogin(final @NotNull AsyncPlayerPreLoginEvent event) {
        final String nickname = event.getName();
        final MSEssentials plugin = this.getPlugin();
        final PlayerInfo playerInfo = PlayerInfo.fromProfile(plugin, event.getUniqueId(), nickname);

        if (!playerInfo.isWhiteListed()) {
            event.disallow(
                    KICK_WHITELIST,
                    MESSAGE_WHITELIST
            );
        } else if (playerInfo.isBanned()) {
            event.disallow(
                    KICK_BANNED,
                    LEAVE_MESSAGE_FORMAT.arguments(
                            PRE_LOGIN_BANNED_TITLE.asTranslatable(),
                            PRE_LOGIN_BANNED_SUBTITLE.asTranslatable().arguments(
                                    playerInfo.getBanReason(),
                                    playerInfo.getBannedTo(event.getAddress())
                            )
                    )
            );
        } else if (!MSCustoms.singleton().isFullyLoaded()) {
            event.disallow(
                    KICK_OTHER,
                    MESSAGE_SERVER_NOT_FULLY_LOADED
            );
        } else if (
                plugin.getConfiguration().isDeveloperMode()
                && !playerInfo.getOfflinePlayer().isOp()
        ) {
            event.disallow(
                    KICK_OTHER,
                    MESSAGE_TECH_WORKS
            );
        } else {
            final String hostAddress = event.getAddress().getHostAddress();
            final PlayerFile playerFile = playerInfo.getPlayerFile();

            if (
                    playerFile.exists()
                    && !playerFile.getIpList().contains(hostAddress)
            ) {
                playerFile.addIp(hostAddress);
                playerFile.save();
                MSLogger.warning(
                        INFO_PLAYER_ADDED_IP.asTranslatable()
                        .arguments(
                                playerInfo.getGrayIDGoldName(),
                                text(nickname),
                                text(hostAddress)
                        )
                );
            }
        }
    }
}
