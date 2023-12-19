package com.minersstudios.msessentials.listener.impl.event.player;

import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerFile;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.ResourcePack;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.jetbrains.annotations.NotNull;

import static com.minersstudios.mscore.language.LanguageRegistry.Components.*;
import static net.kyori.adventure.text.Component.text;

@EventListener
public final class AsyncPlayerPreLoginListener extends AbstractEventListener<MSEssentials> {
    private static final TranslatableComponent LEAVE_MESSAGE_FORMAT =
            FORMAT_LEAVE_MESSAGE.color(NamedTextColor.DARK_GRAY);
    private static final TranslatableComponent WHITELIST =
            LEAVE_MESSAGE_FORMAT.args(
                    PRE_LOGIN_WHITELISTED_TITLE.style(Style.style(NamedTextColor.RED, TextDecoration.BOLD)),
                    PRE_LOGIN_WHITELISTED_SUBTITLE.color(NamedTextColor.GRAY)
            );
    private static final TranslatableComponent SERVER_NOT_FULLY_LOADED =
            LEAVE_MESSAGE_FORMAT.args(
                    SERVER_NOT_FULLY_LOADED_TITLE.style(Style.style(NamedTextColor.RED, TextDecoration.BOLD)),
                    SERVER_NOT_FULLY_LOADED_SUBTITLE.color(NamedTextColor.GRAY)
            );
    private static final TranslatableComponent TECH_WORKS =
            LEAVE_MESSAGE_FORMAT.args(
                    PRE_LOGIN_TECH_WORKS_TITLE.style(Style.style(NamedTextColor.RED, TextDecoration.BOLD)),
                    PRE_LOGIN_TECH_WORKS_SUBTITLE.color(NamedTextColor.GRAY)
            );

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAsyncPlayerPreLogin(final @NotNull AsyncPlayerPreLoginEvent event) {
        final String nickname = event.getName();
        final MSEssentials plugin = this.getPlugin();
        final PlayerInfo playerInfo = PlayerInfo.fromProfile(plugin, event.getUniqueId(), nickname);

        if (!playerInfo.isWhiteListed()) {
            event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST,
                    WHITELIST
            );
            return;
        }

        if (playerInfo.isBanned()) {
            event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                    LEAVE_MESSAGE_FORMAT.args(
                            PRE_LOGIN_BANNED_TITLE,
                            PRE_LOGIN_BANNED_SUBTITLE.args(
                                    playerInfo.getBanReason(),
                                    playerInfo.getBannedTo(event.getAddress())
                            )
                    )
            );
            return;
        }

        if (
                !plugin.isLoadedCustoms()
                || !ResourcePack.isResourcePackLoaded()
        ) {
            event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    SERVER_NOT_FULLY_LOADED
            );
            return;
        }

        if (
                plugin.getConfiguration().isDeveloperMode()
                && !playerInfo.getOfflinePlayer().isOp()
        ) {
            event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    TECH_WORKS
            );
            return;
        }

        final String hostAddress = event.getAddress().getHostAddress();
        final PlayerFile playerFile = playerInfo.getPlayerFile();

        if (
                playerFile.exists()
                && !playerFile.getIpList().contains(hostAddress)
        ) {
            playerFile.addIp(hostAddress);
            playerFile.save();

            MSLogger.warning(
                    INFO_PLAYER_ADDED_IP
                    .args(
                            playerInfo.getGrayIDGoldName(),
                            text(nickname),
                            text(hostAddress)
                    )
            );
        }
    }
}
