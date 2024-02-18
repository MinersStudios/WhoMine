package com.minersstudios.msessentials.utility;

import com.minersstudios.mscore.MSCore;
import com.minersstudios.mscore.locale.TranslationRegistry;
import com.minersstudios.mscore.locale.Translations;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscore.utility.Font;
import com.minersstudios.msessentials.EssentialsConfig;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.chat.ChatType;
import com.minersstudios.msessentials.discord.DiscordManager;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.world.WorldDark;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.minersstudios.msessentials.utility.MessageUtils.Colors.*;
import static net.kyori.adventure.text.Component.*;

public final class MessageUtils {

    @Contract(" -> fail")
    private MessageUtils() throws AssertionError {
        throw new AssertionError("Utility class");
    }

    /**
     * Sends a message to all players except those in world_dark
     *
     * @param message message
     */
    public static void sendGlobalMessage(final @NotNull Component message) {
        final WorldDark worldDark = MSEssentials.singleton().getCache().getWorldDark();

        for (final var player : Bukkit.getOnlinePlayers()) {
            if (!worldDark.isInWorldDark(player)) {
                player.sendMessage(message);
            }
        }
    }

    /**
     * Sends a message to all players within the specified radius
     *
     * @param message  message
     * @param location center location
     * @param radius   radius
     */
    public static void sendLocalMessage(
            final @NotNull Component message,
            final @NotNull Location location,
            final double radius
    ) {
        MSEssentials.singleton().runTask(
                () -> location.getWorld().getNearbyPlayers(location, radius)
                        .forEach(player -> player.sendMessage(message))
        );
    }

    /**
     * Sends a message to chat
     *
     * @param playerInfo player info
     * @param location   sender location
     * @param chatType       chat
     * @param message    message
     */
    public static void sendMessageToChat(
            final @NotNull PlayerInfo playerInfo,
            final @Nullable Location location,
            final @NotNull ChatType chatType,
            final @NotNull Component message
    ) {
        final MSEssentials plugin = MSEssentials.singleton();
        final EssentialsConfig config = plugin.getConfiguration();
        final DiscordManager discordManager = plugin.getCache().getDiscordManager();

        if (chatType == ChatType.LOCAL && location != null) {
            final Component localMessage = space()
                    .append(playerInfo.getDefaultName()
                    .append(text(" : "))
                    .color(CHAT_COLOR_PRIMARY)
                    .hoverEvent(HoverEvent.showText(text("Нажмите, чтобы написать приватное сообщение данному игроку", NamedTextColor.GRAY)))
                    .clickEvent(ClickEvent.suggestCommand("/pm " + playerInfo.getID() + " ")))
                    .append(message)
                    .color(CHAT_COLOR_SECONDARY);
            final String stringLocalMessage =
                    ChatUtils.serializePlainComponent(
                            TranslationRegistry.renderComponent(localMessage)
                    );

            sendLocalMessage(localMessage, location, config.getLocalChatRadius());
            MSEssentials.singleton().runTaskAsync(
                    () -> discordManager.sendMessage(ChatType.LOCAL, stringLocalMessage)
            );
            MSLogger.info(null, localMessage);
            return;
        }

        final Component globalMessage = space()
                .append(text("[WM] ")
                .append(playerInfo.getDefaultName()
                .append(text(" : ")))
                .color(CHAT_COLOR_PRIMARY)
                .hoverEvent(HoverEvent.showText(text("Нажмите, чтобы написать приватное сообщение данному игроку", NamedTextColor.GRAY)))
                .clickEvent(ClickEvent.suggestCommand("/pm " + playerInfo.getID() + " ")))
                .append(message)
                .color(CHAT_COLOR_SECONDARY);
        final String stringGlobalMessage =
                ChatUtils.serializePlainComponent(
                        TranslationRegistry.renderComponent(globalMessage)
                );

        sendGlobalMessage(globalMessage);
        MSCore.singleton().runTaskAsync(() -> {
            discordManager.sendMessage(ChatType.GLOBAL, stringGlobalMessage.replaceFirst("\\[WM]", ""));
            discordManager.sendMessage(ChatType.LOCAL, stringGlobalMessage);
        });
        MSLogger.info(null, globalMessage);
    }

    /**
     * Sends a private message
     *
     * @param sender   private message sender
     * @param receiver private message receiver
     * @param message  private message
     * @return True if sender or receiver == null
     */
    public static boolean sendPrivateMessage(
            final @NotNull PlayerInfo sender,
            final @NotNull PlayerInfo receiver,
            final @NotNull Component message
    ) {
        final MSEssentials plugin = MSEssentials.singleton();
        final DiscordManager discordManager = plugin.getCache().getDiscordManager();
        final CommandSender commandSender = sender == plugin.getCache().getConsolePlayerInfo()
                ? plugin.getServer().getConsoleSender()
                : sender.getOnlinePlayer();
        final Player receiverPlayer = receiver.getOnlinePlayer();

        if (commandSender != null && receiverPlayer != null) {
            final Component privateMessage = space()
                    .append(sender.getDefaultName()
                    .append(text(" -> ")
                    .append(receiver.getDefaultName()
                    .append(text(" : ")))))
                    .color(CHAT_COLOR_PRIMARY)
                    .append(message.color(CHAT_COLOR_SECONDARY));
            final String privateMessageString =
                    ChatUtils.serializePlainComponent(
                            TranslationRegistry.renderComponent(privateMessage)
                    );

            commandSender.sendMessage(
                    Font.Components.SPEECH.append(text()
                    .append(text("Вы -> ")
                    .append(receiver.getDefaultName()
                    .append(text(" : ")))
                    .hoverEvent(HoverEvent.showText(text("Нажмите, чтобы написать приватное сообщение данному игроку", NamedTextColor.GRAY)))
                    .clickEvent(ClickEvent.suggestCommand("/pm " + receiver.getID() + " ")))
                    .color(CHAT_COLOR_PRIMARY))
                    .append(message.color(CHAT_COLOR_SECONDARY))
            );
            receiverPlayer.sendMessage(
                    Font.Components.SPEECH.append(sender.getDefaultName().append(text(" -> Вам : "))
                    .color(CHAT_COLOR_PRIMARY)
                    .hoverEvent(HoverEvent.showText(text("Нажмите, чтобы написать приватное сообщение данному игроку", NamedTextColor.GRAY)))
                    .clickEvent(ClickEvent.suggestCommand("/pm " + sender.getID() + " ")))
                    .append(message.color(CHAT_COLOR_SECONDARY))
            );
            MSCore.singleton().runTaskAsync(
                    () -> discordManager.sendMessage(ChatType.LOCAL, privateMessageString)
            );
            MSLogger.info(null, privateMessage);
            return true;
        }
        return false;
    }

    /**
     * Sends rp event message to chat
     *
     * @param sender             player
     * @param speech             speech
     * @param action             action
     * @param rolePlayActionType rp action type
     */
    public static void sendRPEventMessage(
            final @NotNull Player sender,
            final @NotNull Component speech,
            final @NotNull Component action,
            final @NotNull RolePlayActionType rolePlayActionType
    ) {
        final MSEssentials plugin = MSEssentials.singleton();
        final EssentialsConfig config = plugin.getConfiguration();
        final DiscordManager discordManager = plugin.getCache().getDiscordManager();
        final PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(plugin, sender);
        final Component fullMessage = switch (rolePlayActionType) {
            case DO ->
                    text("* ", RP_MESSAGE_MESSAGE_COLOR_PRIMARY)
                    .append(action.color(RP_MESSAGE_MESSAGE_COLOR_SECONDARY))
                    .append(text(" * | ", RP_MESSAGE_MESSAGE_COLOR_PRIMARY))
                    .append(playerInfo.getGrayIDGoldName());
            case IT ->
                    text("* ", RP_MESSAGE_MESSAGE_COLOR_PRIMARY)
                    .append(action.color(RP_MESSAGE_MESSAGE_COLOR_SECONDARY))
                    .append(text(" *", RP_MESSAGE_MESSAGE_COLOR_PRIMARY));
            case TODO ->
                    text("* ")
                    .color(RP_MESSAGE_MESSAGE_COLOR_PRIMARY)
                    .append(speech
                    .color(RP_MESSAGE_MESSAGE_COLOR_SECONDARY))
                    .append(text(" - ")
                    .append(playerInfo.getPlayerFile().getPronouns().getSaidMessage()))
                    .color(RP_MESSAGE_MESSAGE_COLOR_PRIMARY)
                    .append(space())
                    .append(playerInfo.getGrayIDGoldName())
                    .append(text(", ", RP_MESSAGE_MESSAGE_COLOR_PRIMARY))
                    .append(action
                    .color(RP_MESSAGE_MESSAGE_COLOR_SECONDARY))
                    .append(text(" *", RP_MESSAGE_MESSAGE_COLOR_PRIMARY));
            default ->
                    text("* ", RP_MESSAGE_MESSAGE_COLOR_PRIMARY)
                    .append(playerInfo.getGrayIDGoldName())
                    .append(space()
                    .append(action.color(RP_MESSAGE_MESSAGE_COLOR_SECONDARY)))
                    .append(text(" *", RP_MESSAGE_MESSAGE_COLOR_PRIMARY));
        };

        sendLocalMessage(
                space().append(Font.Components.YELLOW_EXCLAMATION_MARK).append(space()).append(fullMessage),
                sender.getLocation(),
                config.getLocalChatRadius()
        );
        MSCore.singleton().runTaskAsync(
                () -> discordManager.sendMessage(
                        ChatType.LOCAL,
                        ChatUtils.serializePlainComponent(TranslationRegistry.renderComponent(fullMessage))
                )
        );
        MSLogger.info(null, fullMessage);
    }

    public static void sendRPEventMessage(
            final @NotNull Player player,
            final @NotNull Component action,
            final @NotNull RolePlayActionType rolePlayActionType
    ) {
        sendRPEventMessage(player, empty(), action, rolePlayActionType);
    }

    /**
     * Sends a death message
     *
     * @param killed killed player
     * @param killer killer player
     */
    public static void sendDeathMessage(
            final @NotNull Player killed,
            final @Nullable Player killer
    ) {
        final MSEssentials plugin = MSEssentials.singleton();
        final DiscordManager discordManager = plugin.getCache().getDiscordManager();
        final Location deathLocation = killed.getLocation();
        final PlayerInfo killedInfo = PlayerInfo.fromOnlinePlayer(plugin, killed);
        final PlayerInfo killerInfo = killer != null
                        ? PlayerInfo.fromOnlinePlayer(plugin, killer)
                        : null;
        final Component deathMessage = killerInfo != null
                ? space()
                .append(killerInfo.getGoldenName()
                .append(space()))
                .append(killerInfo.getPlayerFile().getPronouns().getKillMessage()
                .color(JOIN_MESSAGE_COLOR_PRIMARY)
                .append(space()))
                .append(killedInfo.getGoldenName())
                : space()
                .append(killedInfo.getGoldenName()
                .append(space()))
                .append(killedInfo.getPlayerFile().getPronouns().getDeathMessage())
                .color(JOIN_MESSAGE_COLOR_PRIMARY);
        final String stringDeathMessage =
                ChatUtils.serializePlainComponent(
                        TranslationRegistry.renderComponent(deathMessage)
                );

        killedInfo.setLastDeathLocation(deathLocation);
        sendGlobalMessage(deathMessage);
        MSCore.singleton().runTaskAsync(() -> {
            discordManager.sendActionMessage(ChatType.GLOBAL, killed.getName(), stringDeathMessage, 16757024);
            discordManager.sendActionMessage(ChatType.LOCAL, killed.getName(), stringDeathMessage, 16757024);
        });
        MSLogger.info(null, deathMessage);

        MSLogger.info(
                null,
                Translations.INFO_PLAYER_DEATH_INFO.asTranslatable()
                .arguments(
                        killedInfo.getDefaultName(),
                        text(killed.getName()),
                        text(deathLocation.getBlock().getWorld().getName()),
                        text(
                                deathLocation.getBlockX() + " "
                                + deathLocation.getBlockY() + " "
                                + deathLocation.getBlockZ()
                        )
                )
        );
    }

    /**
     * Sends a join message
     *
     * @param playerInfo playerInfo
     */
    public static void sendJoinMessage(final @NotNull PlayerInfo playerInfo) {
        final Player player = playerInfo.getOnlinePlayer();

        if (
                !playerInfo.isOnline(true)
                || player == null
        ) {
            return;
        }

        final MSEssentials plugin = MSEssentials.singleton();
        final DiscordManager discordManager = plugin.getCache().getDiscordManager();
        final Component joinMessage = space()
                .append(playerInfo.getGoldenName()
                .append(space()))
                .append(playerInfo.getPlayerFile().getPronouns().getJoinMessage())
                .color(JOIN_MESSAGE_COLOR_PRIMARY);
        final String stringJoinMessage =
                ChatUtils.serializePlainComponent(
                        TranslationRegistry.renderComponent(joinMessage)
                );

        sendGlobalMessage(joinMessage);
        MSCore.singleton().runTaskAsync(() -> {
            discordManager.sendActionMessage(ChatType.GLOBAL, player.getName(), stringJoinMessage, 65280);
            discordManager.sendActionMessage(ChatType.LOCAL, player.getName(), stringJoinMessage, 65280);
        });
        MSLogger.info(null, joinMessage);
    }

    /**
     * Sends a leave message
     *
     * @param playerInfo playerInfo
     * @param player     player
     */
    public static void sendQuitMessage(
            final @NotNull PlayerInfo playerInfo,
            final @NotNull Player player
    ) {
        if (!playerInfo.isOnline()) {
            return;
        }

        final MSEssentials plugin = MSEssentials.singleton();
        final DiscordManager discordManager = plugin.getCache().getDiscordManager();
        final Component quitMessage = space()
                .append(playerInfo.getGoldenName()
                .append(space()))
                .append(playerInfo.getPlayerFile().getPronouns().getQuitMessage())
                .color(JOIN_MESSAGE_COLOR_PRIMARY);
        final String stringQuitMessage =
                ChatUtils.serializePlainComponent(
                        TranslationRegistry.renderComponent(quitMessage)
                );

        sendGlobalMessage(quitMessage);
        MSCore.singleton().runTaskAsync(() -> {
            discordManager.sendActionMessage(ChatType.GLOBAL, player.getName(), stringQuitMessage, 16711680);
            discordManager.sendActionMessage(ChatType.LOCAL, player.getName(), stringQuitMessage, 16711680);
        });
        MSLogger.info(null, quitMessage);
    }

    public enum RolePlayActionType {DO, IT, ME, TODO}

    public static class Colors {
        public static final TextColor
                CHAT_COLOR_PRIMARY = TextColor.color(171, 164, 148),
                CHAT_COLOR_SECONDARY = TextColor.color(241, 240, 227),
                JOIN_MESSAGE_COLOR_PRIMARY = TextColor.color(255, 238, 147),
                JOIN_MESSAGE_COLOR_SECONDARY = TextColor.color(252, 245, 199),
                RP_MESSAGE_MESSAGE_COLOR_PRIMARY = TextColor.color(255, 170, 0),
                RP_MESSAGE_MESSAGE_COLOR_SECONDARY = TextColor.color(255, 195, 105);
    }
}
