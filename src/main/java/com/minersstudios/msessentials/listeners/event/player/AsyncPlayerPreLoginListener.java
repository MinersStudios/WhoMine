package com.minersstudios.msessentials.listeners.event.player;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
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

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

@MSListener
public final class AsyncPlayerPreLoginListener extends AbstractMSListener<MSEssentials> {
    private static final TranslatableComponent LEAVE_MESSAGE_FORMAT = translatable("ms.format.leave.message").color(NamedTextColor.DARK_GRAY);
    private static final TranslatableComponent WHITELIST_TITLE = translatable("ms.pre_login.whitelisted.title").style(Style.style(NamedTextColor.RED, TextDecoration.BOLD));
    private static final TranslatableComponent WHITELIST_SUBTITLE = translatable("ms.pre_login.whitelisted.subtitle").color(NamedTextColor.GRAY);
    private static final TranslatableComponent BAN_TITLE = translatable("ms.pre_login.banned.title").style(Style.style(NamedTextColor.RED, TextDecoration.BOLD));
    private static final TranslatableComponent BAN_SUBTITLE = translatable("ms.pre_login.banned.subtitle").color(NamedTextColor.GRAY);
    private static final TranslatableComponent SERVER_NOT_LOADED_TITLE = translatable("ms.server_not_fully_loaded.title").style(Style.style(NamedTextColor.RED, TextDecoration.BOLD));
    private static final TranslatableComponent SERVER_NOT_LOADED_SUBTITLE = translatable("ms.server_not_fully_loaded.subtitle").color(NamedTextColor.GRAY);
    private static final TranslatableComponent TECH_WORKS_TITLE = translatable("ms.pre_login.tech_works.title").style(Style.style(NamedTextColor.RED, TextDecoration.BOLD));
    private static final TranslatableComponent TECH_WORKS_SUBTITLE = translatable("ms.pre_login.tech_works.subtitle").color(NamedTextColor.GRAY);
    private static final TranslatableComponent IP_ADDED = translatable("ms.info.player_added_ip");

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAsyncPlayerPreLogin(final @NotNull AsyncPlayerPreLoginEvent event) {
        final String nickname = event.getName();
        final PlayerInfo playerInfo = PlayerInfo.fromProfile(event.getUniqueId(), nickname);

        if (!playerInfo.isWhiteListed()) {
            event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST,
                    LEAVE_MESSAGE_FORMAT.args(WHITELIST_TITLE, WHITELIST_SUBTITLE)
            );
            return;
        }

        if (playerInfo.isBanned()) {
            event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                    LEAVE_MESSAGE_FORMAT.args(
                            BAN_TITLE,
                            BAN_SUBTITLE.args(
                                    playerInfo.getBanReason(),
                                    playerInfo.getBannedTo(event.getAddress())
                            )
                    )
            );
            return;
        }

        if (
                !this.getPlugin().isLoadedCustoms()
                || !ResourcePack.isResourcePackLoaded()
        ) {
            event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    LEAVE_MESSAGE_FORMAT.args(SERVER_NOT_LOADED_TITLE, SERVER_NOT_LOADED_SUBTITLE)
            );
            return;
        }

        if (
                MSEssentials.getConfiguration().developerMode
                && !playerInfo.getOfflinePlayer().isOp()
        ) {
            event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    LEAVE_MESSAGE_FORMAT.args(TECH_WORKS_TITLE, TECH_WORKS_SUBTITLE)
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
                    IP_ADDED.args(
                            playerInfo.getGrayIDGoldName(),
                            text(nickname),
                            text(hostAddress)
                    )
            );
        }
    }
}
