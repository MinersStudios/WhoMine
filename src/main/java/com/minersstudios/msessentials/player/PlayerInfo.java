package com.minersstudios.msessentials.player;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.minersstudios.mscore.inventory.CustomInventory;
import com.minersstudios.mscore.language.LanguageFile;
import com.minersstudios.mscore.language.LanguageRegistry;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.utility.BlockUtils;
import com.minersstudios.mscore.utility.DateUtils;
import com.minersstudios.mscore.utility.PlayerUtils;
import com.minersstudios.msessentials.Cache;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.discord.BotHandler;
import com.minersstudios.msessentials.discord.DiscordMap;
import com.minersstudios.msessentials.menu.PronounsMenu;
import com.minersstudios.msessentials.menu.ResourcePackMenu;
import com.minersstudios.msessentials.player.collection.MuteMap;
import com.minersstudios.msessentials.player.collection.PlayerInfoMap;
import com.minersstudios.msessentials.player.skin.Skin;
import com.minersstudios.msessentials.utility.IDUtils;
import com.minersstudios.msessentials.utility.MSPlayerUtils;
import com.minersstudios.msessentials.world.WorldDark;
import com.mojang.authlib.GameProfile;
import fr.xephi.authme.api.v3.AuthMeApi;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.server.players.UserWhiteListEntry;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

import static com.minersstudios.mscore.language.LanguageRegistry.Components.*;
import static com.minersstudios.msessentials.utility.MessageUtils.RolePlayActionType.ME;
import static com.minersstudios.msessentials.utility.MessageUtils.RolePlayActionType.TODO;
import static com.minersstudios.msessentials.utility.MessageUtils.*;
import static net.kyori.adventure.text.Component.text;

/**
 * Player info with player file, settings, etc. All player info stored in
 * {@link PlayerInfoMap}. Use {@link #fromOnlinePlayer(MSEssentials, Player)} or
 * {@link #fromProfile(MSEssentials, UUID, String)} to get player info. It will
 * create new player info if it doesn't exist, or get existing player info if it
 * exists and save it to the map if it's not cached.
 *
 * @see PlayerFile
 * @see PlayerInfoMap
 */
public final class PlayerInfo {
    private final MSEssentials plugin;
    private final UUID uuid;
    private final String nickname;
    private final PlayerProfile profile;
    private final OfflinePlayer offlinePlayer;
    private final CraftServer server;
    private PlayerFile playerFile;
    private CompletableFuture<PlayerResourcePackStatusEvent.Status> resourcePackStatus;
    private Component defaultName;
    private Component goldenName;
    private Component grayIDGoldName;
    private Component grayIDGreenName;
    private BukkitTask joinTask;

    /**
     * Player info constructor
     *
     * @param uuid     Player UUID
     * @param nickname Player nickname
     */
    public PlayerInfo(
            final @NotNull MSEssentials plugin,
            final @NotNull UUID uuid,
            final @NotNull String nickname
    ) {
        this.plugin = plugin;
        this.uuid = uuid;
        this.nickname = nickname;
        this.profile = PlayerUtils.craftProfile(uuid, nickname);
        this.playerFile = PlayerFile.loadConfig(plugin, uuid, nickname);
        this.offlinePlayer = PlayerUtils.getOfflinePlayer(uuid, nickname);
        this.server = (CraftServer) Bukkit.getServer();

        this.initNames();
    }

    /**
     * Gets player info from {@link Cache#getPlayerInfoMap()} by nickname and
     * {@link UUID}.
     * <br>
     * If the player info is not cached, new player info is created with the
     * player file and settings if the file exists, or new player information is
     * created with the unsaved player file and default settings, and then saved
     * to the map.
     *
     * @param uuid     The player UUID
     * @param nickname The player nickname
     * @return Player info from {@link Cache#getPlayerInfoMap()}
     */
    public static @NotNull PlayerInfo fromProfile(
            final @NotNull MSEssentials plugin,
            final @NotNull UUID uuid,
            final @NotNull String nickname
    ) {
        return plugin.getCache().getPlayerInfoMap().get(uuid, nickname);
    }

    /**
     * Gets player info from {@link Cache#getPlayerInfoMap()} by {@link Player}
     * object.
     * <br>
     * If the player info is not cached, new player info is created with the
     * player file and settings if the file exists, or new player information is
     * created with the unsaved player file and default settings, and then saved
     * to the map.
     *
     * @param player The player
     * @return Player info from {@link Cache#getPlayerInfoMap()}
     */
    public static @NotNull PlayerInfo fromOnlinePlayer(
            final @NotNull MSEssentials plugin,
            final @NotNull Player player
    ) {
        return plugin.getCache().getPlayerInfoMap().get(player);
    }

    /**
     * Gets player info from {@link Cache#getPlayerInfoMap()} by
     * {@link OfflinePlayer} object.
     * <br>
     * If the player info is not cached, new player info is created with the
     * player file and settings if the file exists, or new player information is
     * created with the unsaved player file and default settings, and then saved
     * to the map.
     *
     * @param offlinePlayer The offline player
     * @return Player info from {@link Cache#getPlayerInfoMap()}, or null if
     *         player nickname is blank
     */
    @Contract("_, null -> null")
    public static @Nullable PlayerInfo fromOfflinePlayer(
            final @NotNull MSEssentials plugin,
            final @Nullable OfflinePlayer offlinePlayer
    ) {
        return plugin.getCache().getPlayerInfoMap().get(offlinePlayer);
    }

    /**
     * Gets player info from {@link Cache#getPlayerInfoMap()} by
     * {@link OfflinePlayer} object, which was retrieved by the specified player
     * ID from {@link Cache#getIdMap()}.
     * <br>
     * If the player info is not cached, new player info is created with the
     * player file and settings if the file exists, or new player information is
     * created with the unsaved player file and default settings, and then saved
     * to the map.
     *
     * @param id The player ID
     * @return Player info from its ID,
     *         or null if the player ID doesn't exist
     * @see #fromOfflinePlayer(MSEssentials, OfflinePlayer)
     */
    public static @Nullable PlayerInfo fromID(
            final @NotNull MSEssentials plugin,
            final int id
    ) {
        return fromOfflinePlayer(plugin, plugin.getCache().getIdMap().getPlayerByID(id));
    }

    /**
     * Gets player info from {@link Cache#getPlayerInfoMap()} by
     * {@link OfflinePlayer} object, which was retrieved by the specified player
     * nickname with {@link Server#getOfflinePlayer(String)}.
     * <br>
     * If the player info is not cached, new player info is created with the
     * player file and settings if the file exists, or new player information is
     * created with the unsaved player file and default settings, and then saved
     * to the map.
     *
     * @param nickname The player nickname
     * @return Player info from its nickname, or null if the nickname is blank
     * @see #fromOfflinePlayer(MSEssentials, OfflinePlayer)
     */
    public static @Nullable PlayerInfo fromNickname(
            final @NotNull MSEssentials plugin,
            final @NotNull String nickname
    ) {
        return nickname.isBlank()
                ? null
                : fromOfflinePlayer(
                        plugin,
                        plugin.getServer().getOfflinePlayer(nickname)
                );
    }

    /**
     * Gets player info from {@link Cache#getPlayerInfoMap()} by
     * {@link OfflinePlayer} object, which was retrieved by the specified player
     * {@link UUID} with {@link Server#getOfflinePlayer(UUID)}.
     * <br>
     * If the player info is not cached, new player info is created with the
     * player file and settings if the file exists, or new player information is
     * created with the unsaved player file and default settings, and then saved
     * to the map.
     *
     * @param uuid The player UUID
     * @return Player info from its {@link UUID}, or null if the player nickname
     *         is blank
     * @see #fromOfflinePlayer(MSEssentials, OfflinePlayer)
     */
    public static @Nullable PlayerInfo fromUUID(
            final @NotNull MSEssentials plugin,
            final @NotNull UUID uuid
    ) {
        return fromOfflinePlayer(
                plugin,
                plugin.getServer().getOfflinePlayer(uuid)
        );
    }

    /**
     * Gets player info from {@link Cache#getPlayerInfoMap()} by
     * {@link OfflinePlayer} object, which was retrieved by the specified player
     * ID or nickname.
     * <br>
     * It first checks if the string matches the ID regex with
     * {@link IDUtils#matchesIDRegex(String)}, and if it does, it gets the
     * player info from its ID, with {@link #fromID(MSEssentials, int)},
     * otherwise it gets the player info from its nickname, with
     * {@link #fromNickname(MSEssentials, String)}.
     * <br>
     * If the player info is not cached, new player info is created with the
     * player file and settings if the file exists, or new player information is
     * created with the unsaved player file and default settings, and then saved
     * to the map.
     *
     * @param string The player ID or nickname
     * @return Player info from its ID or nickname, or null if the specified
     *         string is blank, or the ID doesn't exist
     * @see #fromID(MSEssentials, int)
     * @see #fromNickname(MSEssentials, String)
     */
    public static @Nullable PlayerInfo fromString(
            final @NotNull MSEssentials plugin,
            final @NotNull String string
    ) {
        return string.isBlank()
                ? null
                : IDUtils.matchesIDRegex(string)
                ? fromID(plugin, IDUtils.parseID(string))
                : fromNickname(plugin, string);
    }

    /**
     * Gets player info from {@link Cache#getPlayerInfoMap()} by discord ID with
     * {@link Cache#getDiscordMap()}, then gets the player info from its uuid
     * and nickname with {@link #fromProfile(MSEssentials, UUID, String)}.
     * <br>
     * If the player info is not cached, new player info is created with the
     * player file and settings if the file exists, or new player information is
     * created with the unsaved player file and default settings, and then saved
     * to the map.
     *
     * @param id The player discord ID
     *           (the ID of the discord user who is linked to the player)
     * @return Player info from its discord ID, or null if the discord ID
     *         doesn't exist
     * @see #fromProfile(MSEssentials, UUID, String)
     */
    public static @Nullable PlayerInfo fromDiscord(
            final @NotNull MSEssentials plugin,
            final long id
    ) {
        final DiscordMap.Params params = plugin.getCache().getDiscordMap().getParams(id);
        return params == null
                ? null
                : fromProfile(
                        plugin,
                        params.getUuid(),
                        params.getNickname()
                );
    }

    /**
     * @return Plugin instance
     */
    public @NotNull MSEssentials getPlugin() {
        return this.plugin;
    }

    /**
     * @return The player unique ID
     */
    public @NotNull UUID getUuid() {
        return this.uuid;
    }

    /**
     * @return Player's nickname
     */
    public @NotNull String getNickname() {
        return this.nickname;
    }

    /**
     * @return Player's {@link PlayerProfile}
     */
    public @NotNull PlayerProfile getPlayerProfile() {
        return this.profile;
    }

    /**
     * @return Player's {@link PlayerFile}
     */
    public @NotNull PlayerFile getPlayerFile() {
        return this.playerFile;
    }

    /**
     * @return Player's {@link PlayerSettings}
     */
    public @NotNull OfflinePlayer getOfflinePlayer() {
        return this.offlinePlayer;
    }

    /**
     * @return Online player object if the player is online, or null if the
     *         player is offline
     * @see Server#getPlayer(UUID)
     */
    public @Nullable Player getOnlinePlayer() {
        return this.server.getPlayer(this.uuid);
    }

    /**
     * @return The player's {@link Server}
     */
    public @NotNull Server getServer() {
        return this.server;
    }

    /**
     * @return Player's id, firstname and lastname with default style
     * @see PlayerName#createDefaultName(int)
     */
    public @NotNull Component getDefaultName() {
        return this.defaultName;
    }

    /**
     * @return Player's id, firstname and lastname with golden style
     * @see PlayerName#createGoldenName(int)
     */
    public @NotNull Component getGoldenName() {
        return this.goldenName;
    }

    /**
     * @return Player's id, firstname and lastname with gray id and golden name
     *         style
     * @see PlayerName#createGrayIDGoldName(int)
     */
    public @NotNull Component getGrayIDGoldName() {
        return this.grayIDGoldName;
    }

    /**
     * @return Player's id, firstname and lastname with gray id and green name
     *         style
     * @see PlayerName#createGrayIDGreenName(int)
     */
    public @NotNull Component getGrayIDGreenName() {
        return this.grayIDGreenName;
    }

    /**
     * @return Player's id from {@link Cache#getIdMap()}
     * @see #getID(boolean, boolean)
     */
    public int getID() {
        return this.getID(false, true);
    }

    /**
     * @param addPlayer  If true, the next available ID is added
     * @param zeroIfNull If true, returns 0 if the id is null
     * @return Player's id from {@link Cache#getIdMap()}
     */
    public int getID(
            final boolean addPlayer,
            final boolean zeroIfNull
    ) {
        final Cache cache = this.plugin.getCache();
        return this == cache.getConsolePlayerInfo()
                ? -1
                : cache.getIdMap().getID(
                        this.offlinePlayer.getUniqueId(),
                        addPlayer,
                        zeroIfNull
                );
    }

    /**
     * Sets last leave location to the specified location, only works if the
     * player is online and not in {@link WorldDark}
     *
     * @param location The location to set as the last leave location
     */
    public void setLastLeaveLocation(final @Nullable Location location) {
        if (!this.getPlugin().getCache().getWorldDark().isInWorldDark(location)) {
            this.playerFile.setLastLeaveLocation(location);
            this.playerFile.save();
        }
    }

    /**
     * Sets last death location to the specified location, only works if the
     * world specified in the location is not {@link WorldDark}
     *
     * @param location The location to set as the last death location
     */
    public void setLastDeathLocation(final @Nullable Location location) {
        if (!this.getPlugin().getCache().getWorldDark().isInWorldDark(location)) {
            this.playerFile.setLastDeathLocation(location);
            this.playerFile.save();
        }
    }

    /**
     * Sets the player to sit position
     *
     * @param sitLocation Location of the seat
     */
    public void setSitting(final @NotNull Location sitLocation) {
        this.setSitting(sitLocation, null);
    }

    /**
     * Sets the player to sit position with the given message
     *
     * @param sitLocation Location of the seat
     * @param message     Message to send on sit
     */
    public void setSitting(
            final @NotNull Location sitLocation,
            final @Nullable Component message
    ) {
        final Player player = this.getOnlinePlayer();

        if (player == null) {
            return;
        }

        if (
                (
                        player.getVehicle() != null
                        && player.getVehicle().getType() != EntityType.ARMOR_STAND
                )
                || this.isSitting()
        ) {
            return;
        }

        player.getWorld().spawn(
            sitLocation,
            ArmorStand.class,
            armorStand -> {
                armorStand.setMarker(true);
                armorStand.setCanTick(false);
                armorStand.setBasePlate(false);
                armorStand.setGravity(false);
                armorStand.setVisible(false);
                armorStand.setCollidable(false);
                armorStand.setSmall(true);
                armorStand.addPassenger(player);
                this.plugin.getCache().getSeats().put(player, armorStand);
            }
        );

        if (message == null) {
            sendRPEventMessage(player, this.playerFile.getPronouns().getSitMessage(), ME);
        } else {
            sendRPEventMessage(player, message, text("приседая"), TODO);
        }
    }

    /**
     * Unsets the player from sit position
     */
    public void unsetSitting() {
        this.unsetSitting(null);
    }

    /**
     * Unsets the player from sit position with the given message
     *
     * @param message Message to send on player get up
     */
    public void unsetSitting(final @Nullable Component message) {
        final Player player = this.getOnlinePlayer();

        if (player == null) {
            return;
        }

        if (
                (player.getVehicle() != null
                && player.getVehicle().getType() != EntityType.ARMOR_STAND)
                || !isSitting()
        ) {
            return;
        }

        final ArmorStand armorStand = this.plugin.getCache().getSeats().remove(player);
        final Location playerLoc = player.getLocation();
        final Location getUpLocation = armorStand.getLocation().add(0.0d, 0.25d, 0.0d);

        if (!BlockUtils.isReplaceable(getUpLocation.getBlock().getType())) {
            getUpLocation.add(getUpLocation.getDirection().multiply(0.75d));
        }

        getUpLocation.setYaw(playerLoc.getYaw());
        getUpLocation.setPitch(playerLoc.getPitch());
        armorStand.remove();

        player.teleportAsync(getUpLocation, PlayerTeleportEvent.TeleportCause.PLUGIN).thenAccept(bool -> {
            if (message == null) {
                sendRPEventMessage(player, this.playerFile.getPronouns().getUnSitMessage(), ME);
            } else {
                sendRPEventMessage(player, message, text("вставая"), TODO);
            }
        });
    }

    /**
     * Sets player whitelist status
     *
     * @param value True to add the player to the whitelist, false to remove
     * @return True if the player was added/removed successfully
     */
    public boolean setWhiteListed(final boolean value) {
        final CraftServer craftServer = this.server;
        final UserWhiteList userWhiteList = craftServer.getServer().getPlayerList().getWhiteList();
        final GameProfile gameProfile = new GameProfile(this.uuid, this.nickname);
        final boolean contains = craftServer.getWhitelistedPlayers().contains(this.offlinePlayer);

        if (value) {
            if (contains) {
                return false;
            }

            userWhiteList.add(new UserWhiteListEntry(gameProfile));
        } else {
            if (!contains) {
                return false;
            }

            userWhiteList.remove(gameProfile);
            this.kick(
                    COMMAND_WHITE_LIST_REMOVE_RECEIVER_MESSAGE_TITLE,
                    COMMAND_WHITE_LIST_REMOVE_RECEIVER_MESSAGE_SUBTITLE,
                    PlayerKickEvent.Cause.WHITELIST
            );
        }

        return true;
    }

    /**
     * Sets player skin
     *
     * @param skin Skin to set, null to reset
     */
    public void setSkin(final @Nullable Skin skin) {
        final Player player = this.getOnlinePlayer();

        if (player != null) {
            if (skin == null) {
                PlayerUtils.setSkin(player, null, null);
            } else {
                PlayerUtils.setSkin(
                        player,
                        skin.getValue(),
                        skin.getSignature()
                );
            }
        }

        this.playerFile.getPlayerSettings().setSkin(skin);
        this.playerFile.save();
    }

    /**
     * @return Player current skin or null if not set
     */
    public @Nullable Skin getCurrentSkin() {
        return this.playerFile.getPlayerSettings().getSkin();
    }

    /**
     * @return Player's discord id or -1 if not linked
     * @see DiscordMap#getId(DiscordMap.Params)
     */
    public long getDiscordId() {
        return this.plugin.getCache().getDiscordMap().getId(DiscordMap.Params.create(this.uuid, this.nickname));
    }

    /**
     * Links the player to the Discord account
     *
     * @param id Discord ID
     * @see DiscordMap#put(long, UUID, String)
     */
    public void linkDiscord(final long id) {
        this.plugin.getCache().getDiscordMap().put(id, this.uuid, this.nickname);
    }

    /**
     * Unlinks the player from the Discord account
     *
     * @return Player's Discord ID
     * @see DiscordMap#remove(long)
     */
    public long unlinkDiscord() {
        final Cache cache = this.plugin.getCache();
        final DiscordMap discordMap = cache.getDiscordMap();
        final long id = discordMap.getId(DiscordMap.Params.create(this.uuid, this.nickname));
        final BotHandler botHandler = cache.getBotHandlers().get(id);

        if (botHandler != null) {
            botHandler.setPlayerInfo(null);
            botHandler.setWaitingReplyTask(null);
        }

        discordMap.remove(id);

        return id;
    }

    /**
     * Sets player's resource pack
     *
     * @param url     The URL from which the client will download the resource
     *                pack
     * @param hash    A 40-character hexadecimal and lowercase SHA-1 digest of
     *                the resource pack file
     * @param force   Marks if the client should require the resource pack
     * @param message A Prompt to be displayed in the client request
     * @return Player's resource pack status future, the future completes in
     *         {@link PlayerResourcePackStatusEvent}
     * @see Player#setResourcePack(String, String, boolean, Component)
     * @see #completeResourcePackFuture(PlayerResourcePackStatusEvent.Status)
     */
    public @NotNull CompletableFuture<PlayerResourcePackStatusEvent.Status> setResourcePackAsync(
            final @NotNull String url,
            final @NotNull String hash,
            final boolean force,
            final @Nullable Component message
    ) {
        final Player player = this.getOnlinePlayer();

        if (player == null) {
            return CompletableFuture.completedFuture(null);
        }

        final var resourcePackStatus = new CompletableFuture<PlayerResourcePackStatusEvent.Status>();
        this.resourcePackStatus = resourcePackStatus;

        player.setResourcePack(url, hash, force, message);

        this.plugin.runTaskLater(() -> {
            if (
                    resourcePackStatus.equals(this.resourcePackStatus)
                    && !this.resourcePackStatus.isDone()
            ) {
                this.resourcePackStatus.completeExceptionally(
                        new TimeoutException("PlayerResourcePackStatusEvent not handled within the expected time")
                );
            }
        }, 6000L);

        return this.resourcePackStatus;
    }

    /**
     * Sets player's resource pack status future to the specified status and
     * completes it. If the future is not set or already completed, nothing will
     * happen
     *
     * @param status Player's resource pack status
     * @see #setResourcePackAsync(String, String, boolean, Component)
     */
    public void completeResourcePackFuture(final @NotNull PlayerResourcePackStatusEvent.Status status) {
        if (
                this.resourcePackStatus != null
                && !this.resourcePackStatus.isDone()
        ) {
            this.resourcePackStatus.complete(status);
        }
    }

    /**
     * @return Player's mute params from {@link Cache#getMuteMap()}
     * @see MuteMap#getMuteEntry(OfflinePlayer)
     */
    public @Nullable MuteMap.Entry getMuteEntry() {
        return this.plugin.getCache().getMuteMap().getMuteEntry(this.offlinePlayer);
    }

    /**
     * @return Player's mute reason from {@link Cache#getMuteMap()} params
     * @throws IllegalStateException If the player is not muted, check
     *                               {@link #isMuted()} first
     * @see MuteMap.Entry#getReason()
     */
    public @NotNull String getMuteReason() throws IllegalStateException {
        final MuteMap.Entry muteEntry = this.getMuteEntry();

        if (muteEntry == null) {
            throw new IllegalStateException("Player is not muted");
        }

        return muteEntry.getReason();
    }

    /**
     * @return Player's mute source from {@link Cache#getMuteMap()} params
     * @throws IllegalStateException If the player is not muted, check
     *                               {@link #isMuted()} first
     * @see MuteMap.Entry#getSource()
     */
    public @NotNull String getMutedBy() throws IllegalStateException {
        final MuteMap.Entry muteEntry = this.getMuteEntry();

        if (muteEntry == null) {
            throw new IllegalStateException("Player is not muted");
        }

        return muteEntry.getSource();
    }

    /**
     * @param sender The command sender, used to get the time zone
     * @return Date when the player was muted from {@link Cache#getMuteMap()}
     *         with the sender's time zone or default time zone if the sender's
     *         time zone cannot be obtained
     * @throws IllegalStateException If the player is not muted, check
     *                               {@link #isMuted()} first
     * @see MuteMap.Entry#getCreated()
     */
    public @NotNull Component getMutedFrom(final @NotNull CommandSender sender) throws IllegalStateException {
        return text(DateUtils.getSenderDate(this.getMutedFrom(), sender));
    }

    /**
     * @param address The IP address, used to get the time zone
     * @return Date when the player was muted from {@link Cache#getMuteMap()}
     *         with the time zone of the IP address or default time zone if the
     *         time zone cannot be obtained
     * @throws IllegalStateException If the player is not muted, check
     *                               {@link #isMuted()} first
     * @see MuteMap.Entry#getCreated()
     */
    public @NotNull Component getMutedFrom(final @NotNull InetAddress address) throws IllegalStateException {
        return text(DateUtils.getDate(this.getMutedFrom(), address));
    }

    /**
     * @return Date when the player was muted from {@link Cache#getMuteMap()}
     * @throws IllegalStateException If the player is not muted, check
     *                               {@link #isMuted()} first
     * @see MuteMap.Entry#getCreated()
     */
    public @NotNull Instant getMutedFrom() throws IllegalStateException {
        final MuteMap.Entry muteEntry = this.getMuteEntry();

        if (muteEntry == null) {
            throw new IllegalStateException("Player is not muted");
        }

        return muteEntry.getCreated();
    }

    /**
     * @param sender The command sender, used to get the time zone
     * @return Date when the player will be unmuted from {@link Cache#getMuteMap()}
     *         with the sender's time zone or default time zone if the sender's
     *         time zone cannot be obtained
     * @throws IllegalStateException If the player is not muted, check
     *                               {@link #isMuted()} first
     * @see MuteMap.Entry#getExpiration()
     */
    public @NotNull Component getMutedTo(final @NotNull CommandSender sender) throws IllegalStateException {
        return text(DateUtils.getSenderDate(this.getMutedTo(), sender));
    }

    /**
     * @param address The IP address, used to get the time zone
     * @return Date when the player will be unmuted from {@link Cache#getMuteMap()}
     *         with the time zone of the IP address or default time zone if the
     *         time zone cannot be obtained
     * @throws IllegalStateException If the player is not muted, check
     *                               {@link #isMuted()} first
     * @see MuteMap.Entry#getExpiration()
     */
    public @NotNull Component getMutedTo(final @NotNull InetAddress address) throws IllegalStateException {
        return text(DateUtils.getDate(this.getMutedTo(), address));
    }

    /**
     * @return Date when the player will be unmuted from {@link Cache#getMuteMap()}
     * @throws IllegalStateException If the player is not muted, check
     *                               {@link #isMuted()} first
     * @see MuteMap.Entry#getExpiration()
     */
    public @NotNull Instant getMutedTo() throws IllegalStateException {
        final MuteMap.Entry muteEntry = this.getMuteEntry();

        if (muteEntry == null) {
            throw new IllegalStateException("Player is not muted");
        }

        return muteEntry.getExpiration();
    }

    /**
     * Mutes or unmutes the player in {@link Cache#getMuteMap()}.Also sends a
     * message to the muted player if they are online, and to the sender, who
     * muted the player.
     *
     * @param value  True to mute the player, false to unmute
     * @param date   The date when the player will be unmuted
     * @param reason The reason of the mute
     * @param sender The command sender, who muted the player, can be null, in
     *               this case the console sender will be used
     * @see MuteMap#put(OfflinePlayer, Instant, String, String)
     * @see MuteMap#remove(OfflinePlayer)
     */
    public void setMuted(
            final boolean value,
            final @NotNull Instant date,
            final @NotNull String reason,
            final @NotNull CommandSender sender
    ) {
        final Player player = this.getOnlinePlayer();
        final MuteMap muteMap = this.plugin.getCache().getMuteMap();

        if (value) {
            if (this.isMuted()) {
                MSLogger.warning(
                        sender,
                        COMMAND_MUTE_ALREADY_SENDER
                        .arguments(
                                this.getGrayIDGoldName(),
                                text(this.nickname)
                        )
                );
                return;
            }

            muteMap.put(this.offlinePlayer, date, reason, sender.getName());
            MSLogger.fine(
                    sender,
                    COMMAND_MUTE_MESSAGE_SENDER
                    .arguments(
                            this.getGrayIDGreenName(),
                            text(this.nickname),
                            text(reason),
                            text(DateUtils.getSenderDate(date, sender))
                    )
            );

            if (player != null) {
                MSLogger.warning(
                        player,
                        COMMAND_MUTE_MESSAGE_RECEIVER
                        .arguments(
                                text(reason),
                                text(DateUtils.getSenderDate(date, sender))
                        )
                );
            }
        } else {
            if (!this.isMuted()) {
                MSLogger.warning(
                        sender,
                        COMMAND_UNMUTE_NOT_MUTED
                        .arguments(
                                this.getGrayIDGoldName(),
                                text(this.nickname)
                        )
                );
                return;
            }

            muteMap.remove(this.offlinePlayer);
            MSLogger.fine(
                    sender,
                    COMMAND_UNMUTE_SENDER_MESSAGE
                    .arguments(
                            this.getGrayIDGreenName(),
                            text(this.nickname)
                    )
            );

            if (player != null) {
                MSLogger.warning(
                        player,
                        COMMAND_UNMUTE_RECEIVER_MESSAGE
                );
            }
        }

        if (this.isLinked()) {
            this.sendPrivateDiscordMessage(BotHandler.craftEmbed(
                    LanguageFile.renderTranslation(
                            value
                            ? DISCORD_MUTED.arguments(
                                    this.defaultName,
                                    text(this.nickname),
                                    text(reason),
                                    text(DateUtils.getSenderDate(date, player))
                            )
                            : DISCORD_UNMUTED.arguments(
                                    this.defaultName,
                                    text(this.nickname)
                            )
                    )
            ));
        }
    }

    /**
     * Unmutes the player in {@link Cache#getMuteMap()}. Also sends a message to
     * the muted player if they are online, and to the sender, who unmuted the
     * player.
     *
     * @param commandSender The command sender, who unmuted the player, can be
     *                      null, in this case the console sender will be used
     */
    public void unmute(final @NotNull CommandSender commandSender) {
        this.setMuted(false, Instant.EPOCH, "", commandSender);
    }

    /**
     * @return The ban entry of the player from {@link BanList.Type#PROFILE}
     */
    public @Nullable BanEntry<PlayerProfile> getBanEntry() {
        final BanList<PlayerProfile> banList = this.server.getBanList(BanList.Type.PROFILE);
        return banList.getBanEntry(this.profile);
    }

    /**
     * @return The ban reason of the player from {@link BanList.Type#PROFILE}
     *         or {@link LanguageRegistry.Components#COMMAND_BAN_DEFAULT_REASON}
     *         if the reason is null
     * @throws IllegalStateException If the player is not banned, check
     *                               {@link #isBanned()} first
     * @see BanEntry#getReason()
     */
    public @NotNull Component getBanReason() throws IllegalStateException {
        final var banEntry = this.getBanEntry();

        if (banEntry == null) {
            throw new IllegalStateException("Player is not banned");
        }

        final String reason = banEntry.getReason();
        return reason == null
                ? COMMAND_BAN_DEFAULT_REASON
                : text(reason);
    }

    /**
     * @param reason The ban reason of the player
     * @throws IllegalStateException If the player is not banned, check
     *                               {@link #isBanned()} first
     * @see BanEntry#setReason(String)
     */
    public void setBanReason(final @NotNull String reason) throws IllegalStateException {
        final var banEntry = this.getBanEntry();

        if (banEntry == null) {
            throw new IllegalStateException("Player is not banned");
        }
        
        banEntry.setReason(reason);
        banEntry.save();
    }

    /**
     * @return The ban source of the player from {@link BanList.Type#PROFILE}
     * @throws IllegalStateException If the player is not banned, check
     *                               {@link #isBanned()} first
     * @see BanEntry#getSource()
     */
    public @NotNull String getBannedBy() throws IllegalStateException {
        final var banEntry = this.getBanEntry();

        if (banEntry == null) {
            throw new IllegalStateException("Player is not banned");
        }

        return banEntry.getSource();
    }

    /**
     * @param source The ban source of the player
     * @throws IllegalStateException If the player is not banned, check
     *                               {@link #isBanned()} first
     * @see BanEntry#setSource(String)
     */
    public void setBannedBy(final @NotNull String source) throws IllegalStateException {
        final var banEntry = this.getBanEntry();

        if (banEntry == null) {
            throw new IllegalStateException("Player is not banned");
        }
        
        banEntry.setSource(source);
        banEntry.save();
    }

    /**
     * @param sender The command sender, used to get the time zone
     * @return Date when the player was banned from {@link BanList.Type#PROFILE}
     *         with the sender's time zone or default time zone if the sender's
     *         time zone cannot be obtained
     * @throws IllegalStateException If the player is not banned, check
     *                               {@link #isBanned()} first
     * @see BanEntry#getCreated()
     */
    public @NotNull Component getBannedFrom(final @NotNull CommandSender sender) throws IllegalStateException {
        return text(DateUtils.getSenderDate(this.getBannedFrom(), sender));
    }

    /**
     * @param address The IP address, used to get the time zone
     * @return Date when the player was banned from {@link BanList.Type#PROFILE}
     *         with the time zone of the IP address or default time zone if the
     *         time zone cannot be obtained
     * @throws IllegalStateException If the player is not banned, check
     *                               {@link #isBanned()} first
     * @see BanEntry#getCreated()
     */
    public @NotNull Component getBannedFrom(final @NotNull InetAddress address) throws IllegalStateException {
        return text(DateUtils.getDate(this.getBannedFrom(), address));
    }

    /**
     * @return Date when the player was banned from {@link BanList.Type#PROFILE}
     * @throws IllegalStateException If the player is not banned, check
     *                               {@link #isBanned()} first
     * @see BanEntry#getCreated()
     */
    public @NotNull Instant getBannedFrom() throws IllegalStateException {
        final var banEntry = this.getBanEntry();

        if (banEntry == null) {
            throw new IllegalStateException("Player is not banned");
        }

        return banEntry.getCreated().toInstant();
    }

    /**
     * @param sender The command sender, used to get the time zone
     * @return Date when the player will be unbanned from {@link BanList.Type#PROFILE}
     *         with the sender's time zone or default time zone if the sender's
     *         time zone cannot be obtained
     * @throws IllegalStateException If the player is not banned, check
     *                               {@link #isBanned()} first
     * @see BanEntry#getExpiration()
     */
    public @NotNull Component getBannedTo(final @NotNull CommandSender sender) throws IllegalStateException {
        final var banEntry = this.getBanEntry();

        if (banEntry == null) {
            throw new IllegalStateException("Player is not banned");
        }

        final Date expiration = banEntry.getExpiration();
        return expiration == null
                ? COMMAND_BAN_TIME_FOREVER
                : text(DateUtils.getSenderDate(expiration.toInstant(), sender));
    }

    /**
     * @param address The IP address, used to get the time zone
     * @return Date when the player will be unbanned from {@link BanList.Type#PROFILE}
     *         with the time zone of the IP address or default time zone if the
     *         time zone cannot be obtained
     * @throws IllegalStateException If the player is not banned, check
     *                               {@link #isBanned()} first
     * @see BanEntry#getExpiration()
     */
    public @NotNull Component getBannedTo(final @NotNull InetAddress address) throws IllegalStateException {
        final var banEntry = this.getBanEntry();

        if (banEntry == null) {
            throw new IllegalStateException("Player is not banned");
        }

        final Date expiration = banEntry.getExpiration();
        return expiration == null
                ? COMMAND_BAN_TIME_FOREVER
                : text(DateUtils.getDate(expiration.toInstant(), address));
    }

    /**
     * @return Date when the player will be unbanned from {@link BanList.Type#PROFILE},
     *         or null if the player is banned forever
     * @throws IllegalStateException If the player is not banned, check
     *                               {@link #isBanned()} first
     * @see BanEntry#getExpiration()
     */
    public @Nullable Instant getBannedTo() throws IllegalStateException {
        final var banEntry = this.getBanEntry();

        if (banEntry == null) {
            throw new IllegalStateException("Player is not banned");
        }

        final Date expiration = banEntry.getExpiration();
        return expiration == null ? null : expiration.toInstant();
    }

    /**
     * @param expiration Date when the player will be unbanned from
     *                   {@link BanList.Type#PROFILE}, or null if the player is
     *                   banned forever
     * @throws IllegalStateException If the player is not banned, check
     *                               {@link #isBanned()} first
     * @see BanEntry#setExpiration(Date)
     */
    public void setBannedTo(final @Nullable Date expiration) throws IllegalStateException {
        final var banEntry = this.getBanEntry();

        if (banEntry == null) {
            throw new IllegalStateException("Player is not banned");
        }
        
        banEntry.setExpiration(expiration);
        banEntry.save();
    }

    /**
     * Bans or unbans the player in {@link BanList.Type#PROFILE}. Also kick the
     * player with the specified reason and expiration date if they are online,
     * and to the player's private discord channel, if it is linked. Also sends
     * a message to the sender.
     *
     * @param value  Whether the player should be banned
     * @param date   Date when the player was banned
     * @param reason Reason why the player was banned
     * @param sender The command sender, who banned the player or null if the
     *               player was banned by the console
     */
    public void setBanned(
            final boolean value,
            final @NotNull Instant date,
            final @NotNull String reason,
            final @Nullable CommandSender sender
    ) {
        final BanList<PlayerProfile> banList = this.server.getBanList(BanList.Type.PROFILE);
        final Player player = this.getOnlinePlayer();
        final CommandSender commandSender = sender == null ? this.server.getConsoleSender() : sender;

        if (value) {
            if (this.isBanned()) {
                MSLogger.warning(
                        sender,
                        COMMAND_BAN_ALREADY_SENDER
                        .arguments(
                                this.getGrayIDGoldName(),
                                text(this.nickname)
                        )
                );
                return;
            }

            banList.addBan(this.profile, reason, Date.from(date), commandSender.getName());
            this.kick(
                    COMMAND_BAN_MESSAGE_RECEIVER_TITLE,
                    COMMAND_BAN_MESSAGE_RECEIVER_SUBTITLE
                    .arguments(
                            text(reason),
                            text(DateUtils.getSenderDate(date, player))
                    ),
                    PlayerKickEvent.Cause.BANNED
            );
            MSLogger.fine(
                    sender,
                    COMMAND_BAN_MESSAGE_SENDER
                    .arguments(
                            this.getGrayIDGreenName(),
                            text(this.nickname),
                            text(reason),
                            text(DateUtils.getSenderDate(date, sender))
                    )
            );
        } else {
            if (!this.isBanned()) {
                MSLogger.warning(
                        sender,
                        COMMAND_UNBAN_NOT_BANNED
                        .arguments(
                                this.getGrayIDGoldName(),
                                text(this.nickname)
                        )
                );
                return;
            }

            banList.pardon(this.profile);
            MSLogger.fine(
                    sender,
                    COMMAND_UNBAN_MESSAGE_SENDER
                    .arguments(
                            this.getGrayIDGreenName(),
                            text(this.nickname)
                    )
            );
        }

        if (this.isLinked()) {
            this.sendPrivateDiscordMessage(BotHandler.craftEmbed(
                    LanguageFile.renderTranslation(
                            value
                            ? DISCORD_BANNED.arguments(
                                    this.defaultName,
                                    text(this.nickname),
                                    text(reason),
                                    text(DateUtils.getSenderDate(date, player))
                            )
                            : DISCORD_UNBANNED.arguments(
                                    this.defaultName,
                                    text(this.nickname)
                            )
                    )
            ));
        }
    }

    /**
     * Unbans the player in {@link BanList.Type#PROFILE}
     *
     * @param commandSender The command sender, who unbanned the player, or null
     *                      if the player was unbanned by the console
     */
    public void pardon(final @Nullable CommandSender commandSender) {
        this.setBanned(false, Instant.EPOCH, "", commandSender);
    }

    /**
     * @return True, if the player isn't in dark_world and hasn't vanished
     */
    public boolean isOnline() {
        return this.isOnline(false);
    }

    /**
     * @param ignoreWorld Ignore world_dark check
     * @return True, if the player isn't in dark_world and hasn't vanished
     */
    public boolean isOnline(boolean ignoreWorld) {
        final Player player = this.getOnlinePlayer();
        return player != null
                && (ignoreWorld || !this.isInWorldDark())
                && !this.isVanished();
    }

    /**
     * @return True, if the player is registered,
     *         i.e. the player file exists and has a name
     * @see PlayerFile#exists()
     * @see PlayerFile#isNoName()
     */
    public boolean isRegistered() {
        return this.playerFile.exists() && !this.playerFile.isNoName();
    }

    /**
     * @return True, if the player is vanished
     */
    public boolean isVanished() {
        final Player player = this.getOnlinePlayer();

        if (player == null) {
            return false;
        }

        for (final var metadataValue : player.getMetadata("vanished")) {
            if (metadataValue.asBoolean()) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return True, if the player is sitting
     */
    public boolean isSitting() {
        final Player player = this.getOnlinePlayer();
        return player != null
                && this.plugin.getCache().getSeats().containsKey(player);
    }

    /**
     * @return True, if the player is linked to the Discord account
     * @see DiscordMap#containsPlayer(DiscordMap.Params)
     */
    public boolean isLinked() {
        return this.plugin.getCache().getDiscordMap().containsPlayer(
                DiscordMap.Params.create(this.uuid, this.nickname)
        );
    }

    /**
     * @return True, if the player is whitelisted
     * @see OfflinePlayer#isWhitelisted()
     */
    public boolean isWhiteListed() {
        return this.server.getWhitelistedPlayers().contains(this.offlinePlayer);
    }

    /**
     * @return True, if the player is muted in {@link Cache#getMuteMap()}
     * @see MuteMap#isMuted(OfflinePlayer)
     */
    public boolean isMuted() {
        return this.plugin.getCache().getMuteMap().isMuted(this.offlinePlayer);
    }

    /**
     * @return True, if the player is banned in {@link BanList.Type#PROFILE}
     */
    public boolean isBanned() {
        return this.getBanEntry() != null;
    }

    /**
     * @return True, if the player is online and authenticated
     */
    public boolean isAuthenticated() {
        final Player player = this.getOnlinePlayer();
        return player != null
                && AuthMeApi.getInstance().isAuthenticated(player);
    }

    /**
     * @return True, if the player is online and in the {@link WorldDark}
     */
    public boolean isInWorldDark() {
        return this.getPlugin().getCache().getWorldDark().isInWorldDark(this.getOnlinePlayer());
    }

    /**
     * Initializes the player's names
     *
     * @see PlayerName
     * @see #getDefaultName()
     * @see #getGoldenName()
     * @see #getGrayIDGoldName()
     * @see #getGrayIDGreenName()
     */
    public void initNames() {
        final int id = this.getID();
        final PlayerName playerName = this.playerFile.getPlayerName();

        this.defaultName = playerName.createDefaultName(id);
        this.goldenName = playerName.createGoldenName(id);
        this.grayIDGoldName = playerName.createGrayIDGoldName(id);
        this.grayIDGreenName = playerName.createGrayIDGreenName(id);
    }

    /**
     * Handles the player's join. Starts the join task, which checks if the
     * player is online, authenticated, and not dead. If the player is not
     * online, the task is cancelled. If the player is not authenticated or
     * dead, the task will be repeated.
     *
     * @see #handleJoinTask()
     */
    public void handleJoin() {
        final Player player = this.getOnlinePlayer();

        if (
                player == null
                || (
                        this.joinTask != null
                        && !this.joinTask.isCancelled()
                )
        ) {
            return;
        }

        this.plugin.runTaskTimer(task -> {
            this.joinTask = task;

            if (!player.isOnline()) {
                task.cancel();
            }

            this.handleJoinTask();
        }, 0L, 1L);
    }

    /**
     * Handles the player's quit. Unsets the player's sitting, ejects the player
     * from the vehicle, removes the player from the anomaly action map, and
     * saves the player's data. After that, send the quit message to all players
     * if the player is not located in the dark world.
     */
    public void handleQuit() {
        this.handleQuit(this.getOnlinePlayer());
    }

    /**
     * Handles the player's quit. Unsets the player's sitting, ejects the player
     * from the vehicle, removes the player from the anomaly action map, and
     * saves the player's data. After that, send the quit message to all players
     * if the player is not located in the dark world.
     *
     * @param player The player, who quit
     */
    public void handleQuit(final @Nullable Player player) {
        if (player == null) {
            return;
        }

        this.unsetSitting();

        final Entity vehicle = player.getVehicle();
        if (vehicle != null) {
            vehicle.eject();
        }

        if (this.joinTask != null) {
            this.joinTask.cancel();
        }

        this.plugin.getCache().getPlayerAnomalyActionMap().remove(player);
        this.savePlayerDataParams();

        if (!this.isInWorldDark()) {
            sendQuitMessage(this, player);
        }
    }

    /**
     * @return A future that will be completed with the result of the operation
     * @see #setResourcePackAsync(String, String, boolean, Component)
     */
    public @NotNull CompletableFuture<Boolean> handleResourcePack() {
        final Player player = this.getOnlinePlayer();

        if (player == null) {
            return CompletableFuture.completedFuture(false);
        }

        final PlayerSettings playerSettings = this.playerFile.getPlayerSettings();
        final ResourcePack.Type type = playerSettings.getResourcePackType();
        final ComponentLogger componentLogger = this.plugin.getComponentLogger();

        switch (type) {
            case NONE -> {
                return CompletableFuture.completedFuture(true);
            }
            case NULL -> {
                this.plugin.runTaskTimer(task -> {
                    if (player.getOpenInventory().getTopInventory() instanceof CustomInventory) {
                        task.cancel();
                        return;
                    }

                    this.plugin.openCustomInventory(ResourcePackMenu.class, player);
                }, 0L, 5L);
                return CompletableFuture.completedFuture(false);
            }
            default -> {
                return this.setResourcePackAsync(type.getURL(), type.getHash(), true, null)
                        .thenApply(status -> {
                            switch (status) {
                                case SUCCESSFULLY_LOADED -> {
                                    componentLogger.info(
                                            RESOURCE_PACK_SUCCESSFULLY_LOADED
                                            .arguments(text(this.nickname, NamedTextColor.GREEN))
                                    );
                                    return true;
                                }
                                case FAILED_DOWNLOAD -> {
                                    playerSettings.setResourcePackType(ResourcePack.Type.NONE);
                                    playerSettings.save();

                                    componentLogger.warn(
                                            RESOURCE_PACK_FAILED_DOWNLOAD_CONSOLE
                                            .arguments(text(this.nickname, NamedTextColor.GOLD))
                                    );
                                    this.kick(
                                            RESOURCE_PACK_FAILED_DOWNLOAD_RECEIVER_TITLE,
                                            RESOURCE_PACK_FAILED_DOWNLOAD_RECEIVER_SUBTITLE,
                                            PlayerKickEvent.Cause.RESOURCE_PACK_REJECTION
                                    );
                                }
                                case DECLINED -> {
                                    componentLogger.warn(
                                            RESOURCE_PACK_DECLINED_CONSOLE
                                            .arguments(text(this.nickname, NamedTextColor.GOLD))
                                    );
                                    this.kick(
                                            RESOURCE_PACK_DECLINED_RECEIVER_TITLE,
                                            RESOURCE_PACK_DECLINED_RECEIVER_SUBTITLE,
                                            PlayerKickEvent.Cause.RESOURCE_PACK_REJECTION
                                    );
                                }
                            }
                            return false;
                        })
                        .exceptionally(
                                throwable -> {
                                    componentLogger.error(
                                            "An error occurred while sending the resource pack to " + this.nickname,
                                            throwable
                                    );
                                    this.kick(
                                            SOMETHING_WENT_WRONG_TITLE,
                                            SOMETHING_WENT_WRONG_SUBTITLE
                                    );

                                    return false;
                                }
                        );
            }
        }
    }

    /**
     * Teleports the player to the last leave location or to the bed spawn
     * location if the last leave location is null or to the world spawn
     * location if the bed spawn location is null
     *
     * @return A future that will be completed with the result of the teleport
     */
    public @NotNull CompletableFuture<Boolean> teleportToLastLeaveLocation() {
        final Player player = this.getOnlinePlayer();

        if (player == null) {
            return CompletableFuture.completedFuture(false);
        }

        Location location = this.playerFile.getLastLeaveLocation();

        if (location == null) {
            location = player.getBedSpawnLocation();

            if (location == null) {
                location = this.plugin.getConfiguration().getSpawnLocation();
            }
        }

        return player.teleportAsync(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    /**
     * Teleports the player to the last death location or to the bed spawn
     * location if the last leave location is null or to the world spawn
     * location if the bed spawn location is null
     *
     * @return A future that will be completed with the result of the teleport
     */
    public @NotNull CompletableFuture<Boolean> teleportToLastDeathLocation() {
        final Player player = this.getOnlinePlayer();

        if (player == null) {
            return CompletableFuture.completedFuture(false);
        }

        if (player.getGameMode() == GameMode.SPECTATOR) {
            this.plugin.runTask(() -> player.setSpectatorTarget(null));
        }

        Location location = this.playerFile.getLastDeathLocation();

        if (location == null) {
            location = player.getBedSpawnLocation();

            if (location == null) {
                location = this.plugin.getConfiguration().getSpawnLocation();
            }
        }

        return player.teleportAsync(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    /**
     * Creates the player's file if it doesn't exist. Also sets the player's
     * nickname, IP and first join date if the player is online. And sends the
     * message to the console if the player's file was created successfully.
     */
    public void createPlayerFile() {
        if (this.playerFile.exists()) {
            return;
        }

        this.playerFile.getConfig().set("name.nickname", this.nickname);

        final Player player = this.getOnlinePlayer();

        if (player != null) {
            final InetSocketAddress address = player.getAddress();

            this.playerFile.addIp(
                    address == null
                    ? null
                    : address.getAddress().getHostAddress()
            );
            this.playerFile.setFirstJoin(Instant.now());
        }

        this.playerFile.save();
        this.plugin.getComponentLogger().info(
                INFO_PLAYER_FILE_CREATED
                .arguments(
                        text(this.nickname),
                        text(this.offlinePlayer.getUniqueId().toString())
                )
                .color(NamedTextColor.GREEN)
        );
    }

    /**
     * Kicks the player from the server with the default kick message and
     * {@link PlayerKickEvent.Cause#PLUGIN} cause
     */
    public void kick() {
        this.kick(this.getOnlinePlayer());
    }

    /**
     * Kicks the player from the server with the default kick message and
     * {@link PlayerKickEvent.Cause#PLUGIN} cause
     *
     * @param player Player to kick
     */
    public void kick(final @Nullable Player player) {
        this.kick(
                player,
                PlayerKickEvent.Cause.PLUGIN
        );
    }

    /**
     * Kicks the player from the server with the default kick message
     *
     * @param cause Cause of the kick
     */
    public void kick(final @NotNull PlayerKickEvent.Cause cause) {
        this.kick(
                this.getOnlinePlayer(),
                cause
        );
    }

    /**
     * Kicks the player from the server with
     * {@link PlayerKickEvent.Cause#PLUGIN} cause
     *
     * @param title  Title of the kick message
     * @param reason Reason of the kick message
     */
    public void kick(
            final @NotNull Component title,
            final @NotNull Component reason
    ) {
        this.kick(
                title,
                reason,
                PlayerKickEvent.Cause.PLUGIN
        );
    }

    /**
     * Kicks the player from the server
     *
     * @param title  Title of the kick message
     * @param reason Reason of the kick message
     * @param cause  Cause of the kick
     */
    public void kick(
            final @NotNull Component title,
            final @NotNull Component reason,
            final @NotNull PlayerKickEvent.Cause cause
    ) {
        this.kick(
                this.getOnlinePlayer(),
                title,
                reason,
                cause
        );
    }

    /**
     * Kicks the player from the server with the default kick message
     *
     * @param player Player to kick
     * @param cause  Cause of the kick
     */
    public void kick(
            final @Nullable Player player,
            final @NotNull PlayerKickEvent.Cause cause
    ) {
        this.kick(
                player,
                COMMAND_KICK_MESSAGE_RECEIVER_TITLE,
                COMMAND_KICK_MESSAGE_RECEIVER_SUBTITLE
                        .arguments(COMMAND_KICK_DEFAULT_REASON),
                cause
        );
    }

    /**
     * Kicks the player from the server with
     * {@link PlayerKickEvent.Cause#PLUGIN} cause
     *
     * @param player Player to kick
     * @param title  Title of the kick message
     * @param reason Reason of the kick message
     */
    public void kick(
            final @Nullable Player player,
            final @NotNull Component title,
            final @NotNull Component reason
    ) {
        this.kick(
                player,
                title,
                reason,
                PlayerKickEvent.Cause.PLUGIN
        );
    }

    /**
     * Kicks the player from the server
     *
     * @param player Player to kick
     * @param title  Title of the kick message
     * @param reason Reason of the kick message
     * @param cause  Cause of the kick
     */
    public void kick(
            final @Nullable Player player,
            final @NotNull Component title,
            final @NotNull Component reason,
            final @NotNull PlayerKickEvent.Cause cause
    ) {
        if (player == null) {
            return;
        }

        player.kick(
                FORMAT_LEAVE_MESSAGE
                .arguments(
                        title.color(NamedTextColor.RED).decorate(TextDecoration.BOLD),
                        reason.color(NamedTextColor.GRAY)
                )
                .color(NamedTextColor.DARK_GRAY),
                cause
        );
    }

    /**
     * Generates a new link code for the player
     *
     * @return Generated code
     * @see DiscordMap#generateCode(PlayerInfo)
     */
    public short generateCode() {
        return this.plugin.getCache().getDiscordMap().generateCode(this);
    }

    /**
     * Sends a private message to the player's Discord account
     *
     * @param messageEmbed Embed to send
     * @param other        Other embeds to send
     * @return True if the player has linked their Discord account
     */
    public boolean sendPrivateDiscordMessage(
            final @NotNull MessageEmbed messageEmbed,
            final MessageEmbed @NotNull ... other
    ) {
        final long id = this.getDiscordId();

        if (id == -1) {
            return false;
        }

        this.plugin.getCache().getDiscordManager().sendEmbeds(id, messageEmbed, other);

        return true;
    }

    /**
     * Sends a private message to the player's Discord account
     *
     * @param message Message to send
     * @return True if the player has linked their Discord account
     */
    public boolean sendPrivateDiscordMessage(final @NotNull CharSequence message) {
        final long id = this.getDiscordId();

        if (id == -1) {
            return false;
        }

        this.plugin.getCache().getDiscordManager().sendMessage(id, message);

        return true;
    }

    /**
     * Saves the player's health, air, game mode and last leave location to the
     * player's file if the player is online and not in the {@link WorldDark}
     */
    public void savePlayerDataParams() {
        final Player player = this.getOnlinePlayer();

        if (
                player == null
                || this.isInWorldDark()
        ) {
            return;
        }

        final double health = player.getHealth();
        final int air = player.getRemainingAir();

        this.playerFile.setLastLeaveLocation(
                player.isDead()
                ? player.getBedSpawnLocation() != null
                ? player.getBedSpawnLocation()
                : this.plugin.getConfiguration().getSpawnLocation()
                : player.getLocation()
        );
        this.playerFile.setGameMode(player.getGameMode());
        this.playerFile.setHealth(health == 0.0d ? 20.0d : health);
        this.playerFile.setAir(air == 0 && player.isDead() ? 300 : air);
        this.playerFile.save();
    }

    /**
     * Hides the player's name tag from other players
     * @see MSPlayerUtils#hideNameTag(MSEssentials, Player)
     */
    public void hideNameTag() {
        MSPlayerUtils.hideNameTag(this.plugin, this.getOnlinePlayer());
    }

    /**
     * Updates the player's file and initializes the player's names
     * @see PlayerFile#loadConfig(MSEssentials, UUID, String)
     * @see PlayerInfo#initNames()
     */
    public void update() {
        this.playerFile = PlayerFile.loadConfig(this.plugin, this.uuid, this.nickname);
        this.initNames();
    }

    /**
     * @return The loaded player from offline player
     * @see PlayerUtils#loadPlayer(OfflinePlayer)
     */
    public @Nullable Player loadPlayerData() {
        return PlayerUtils.loadPlayer(this.offlinePlayer);
    }

    /**
     * Sets the player's skin, game mode, health and air. Also teleports the
     * player to the last leave location, and sends the join message to all
     * players. If the player is not registered, then the registration process
     * will be started.
     *
     * @see #handleJoin()
     */
    private void handleJoinTask() {
        final Player player = this.getOnlinePlayer();

        this.joinTask.cancel();

        if (player == null) {
            return;
        }

        if (!this.isRegistered()) {
            new RegistrationProcess(this.plugin).registerPlayer(this);
        } else {
            if (this.playerFile.getConfig().getString("pronouns") == null) {
                this.plugin.openCustomInventory(PronounsMenu.class, player);
            } else {
                final Skin currentSkin = this.getCurrentSkin();

                if (currentSkin != null) {
                    this.setSkin(currentSkin);
                }

                player.setGameMode(this.playerFile.getGameMode());
                player.setHealth(this.playerFile.getHealth());
                player.setRemainingAir(this.playerFile.getAir());

                this.teleportToLastLeaveLocation().thenAccept(result -> {
                    if (result) {
                        sendJoinMessage(this);
                    } else {
                        player.kick();
                        this.plugin.getLogger().warning(
                                "Something went wrong while teleporting " + this.nickname + " to their last leave location"
                        );
                    }
                });
            }
        }
    }
}
