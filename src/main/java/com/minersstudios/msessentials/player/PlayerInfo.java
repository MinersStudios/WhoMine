package com.minersstudios.msessentials.player;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.common.base.Preconditions;
import com.minersstudios.mscore.config.LanguageFile;
import com.minersstudios.mscore.inventory.CustomInventory;
import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.utils.BlockUtils;
import com.minersstudios.mscore.utils.DateUtils;
import com.minersstudios.mscore.utils.PlayerUtils;
import com.minersstudios.msessentials.Cache;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.discord.BotHandler;
import com.minersstudios.msessentials.discord.DiscordMap;
import com.minersstudios.msessentials.menu.PronounsMenu;
import com.minersstudios.msessentials.menu.ResourcePackMenu;
import com.minersstudios.msessentials.player.map.MuteEntry;
import com.minersstudios.msessentials.player.map.MuteMap;
import com.minersstudios.msessentials.player.map.PlayerInfoMap;
import com.minersstudios.msessentials.player.skin.Skin;
import com.minersstudios.msessentials.utils.IDUtils;
import com.minersstudios.msessentials.utils.MSPlayerUtils;
import com.minersstudios.msessentials.utils.MessageUtils;
import com.minersstudios.msessentials.world.WorldDark;
import com.mojang.authlib.GameProfile;
import fr.xephi.authme.api.v3.AuthMeApi;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.util.DiscordUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.server.players.UserWhiteListEntry;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import static com.minersstudios.msessentials.utils.MessageUtils.RolePlayActionType.ME;
import static com.minersstudios.msessentials.utils.MessageUtils.RolePlayActionType.TODO;
import static com.minersstudios.msessentials.utils.MessageUtils.*;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

/**
 * Player info with player file, settings, etc.
 * All player info stored in {@link PlayerInfoMap}.
 * Use {@link #fromOnlinePlayer(Player)} or {@link #fromProfile(UUID, String)} to get player info.
 * It will create new player info if it doesn't exist,
 * or get existing player info if it exists and save it to the map if it's not cached.
 *
 * @see PlayerFile
 * @see PlayerInfoMap
 */
public class PlayerInfo {
    private final @NotNull UUID uuid;
    private final @NotNull String nickname;
    private final @NotNull PlayerProfile profile;
    private final @NotNull OfflinePlayer offlinePlayer;
    private @NotNull PlayerFile playerFile;
    private final @NotNull CraftServer server;
    private CompletableFuture<PlayerResourcePackStatusEvent.Status> resourcePackStatus;

    private Component defaultName;
    private Component goldenName;
    private Component grayIDGoldName;
    private Component grayIDGreenName;

    private BukkitTask joinTask;

    private static final TranslatableComponent RP_SUCCESSFULLY_LOADED = translatable("ms.resource_pack.successfully_loaded");
    private static final TranslatableComponent RP_FAILED_DOWNLOAD_CONSOLE = translatable("ms.resource_pack.failed_download.console");
    private static final TranslatableComponent RP_FAILED_DOWNLOAD_TITLE = translatable("ms.resource_pack.failed_download.receiver.title");
    private static final TranslatableComponent RP_FAILED_DOWNLOAD_SUBTITLE = translatable("ms.resource_pack.failed_download.receiver.subtitle");
    private static final TranslatableComponent RP_DECLINED_CONSOLE = translatable("ms.resource_pack.declined.console");
    private static final TranslatableComponent RP_DECLINED_TITLE = translatable("ms.resource_pack.declined.receiver.title");
    private static final TranslatableComponent RP_DECLINED_SUBTITLE = translatable("ms.resource_pack.declined.receiver.subtitle");
    private static final TranslatableComponent SERVER_NOT_LOADED_TITLE = translatable("ms.server_not_fully_loaded.title");
    private static final TranslatableComponent SERVER_NOT_LOADED_SUBTITLE = translatable("ms.server_not_fully_loaded.subtitle");
    private static final TranslatableComponent SOMETHING_WENT_WRONG_TITLE = translatable("ms.something_went_wrong.title");
    private static final TranslatableComponent SOMETHING_WENT_WRONG_SUBTITLE = translatable("ms.something_went_wrong.subtitle");


    /**
     * Player info constructor
     *
     * @param uuid     Player UUID
     * @param nickname Player nickname
     */
    public PlayerInfo(
            @NotNull UUID uuid,
            @NotNull String nickname
    ) {
        this.uuid = uuid;
        this.nickname = nickname;
        this.profile = PlayerUtils.craftProfile(uuid, nickname);
        this.playerFile = PlayerFile.loadConfig(uuid, nickname);
        this.offlinePlayer = PlayerUtils.getOfflinePlayer(uuid, nickname);
        this.server = (CraftServer) Bukkit.getServer();

        this.initNames();
    }

    /**
     * Gets player info from {@link Cache#playerInfoMap}
     * by nickname and {@link UUID}.
     * If the player info is not cached, a new player info
     * is created with the player file and settings
     * if the file exists, or a new player information
     * is created with the unsaved player file
     * and default settings, and then saved to the map.
     *
     * @param uuid     The player UUID
     * @param nickname The player nickname
     * @return Player info from {@link Cache#playerInfoMap}
     */
    public static @NotNull PlayerInfo fromProfile(
            @NotNull UUID uuid,
            @NotNull String nickname
    ) {
        return MSEssentials.getCache().playerInfoMap.get(uuid, nickname);
    }

    /**
     * Gets player info from {@link Cache#playerInfoMap}
     * by {@link Player} object.
     * If the player info is not cached, a new player info
     * is created with the player file and settings
     * if the file exists, or a new player information
     * is created with the unsaved player file
     * and default settings, and then saved to the map.
     *
     * @param player The player
     * @return Player info from {@link Cache#playerInfoMap}
     */
    public static @NotNull PlayerInfo fromOnlinePlayer(@NotNull Player player) {
        return MSEssentials.getCache().playerInfoMap.get(player);
    }

    /**
     * Gets player info from {@link Cache#playerInfoMap}
     * by {@link OfflinePlayer} object.
     * If the player info is not cached, a new player info
     * is created with the player file and settings
     * if the file exists, or a new player information
     * is created with the unsaved player file
     * and default settings, and then saved to the map.
     *
     * @param offlinePlayer The offline player
     * @return Player info from {@link Cache#playerInfoMap},
     *         or null if player nickname is blank
     */
    @Contract("null -> null")
    public static @Nullable PlayerInfo fromOfflinePlayer(@Nullable OfflinePlayer offlinePlayer) {
        return MSEssentials.getCache().playerInfoMap.get(offlinePlayer);
    }

    /**
     * Gets player info from {@link Cache#playerInfoMap}
     * by {@link OfflinePlayer} object, which was retrieved
     * by the specified player ID from {@link Cache#idMap}.
     * If the player info is not cached, a new player info
     * is created with the player file and settings
     * if the file exists, or a new player information
     * is created with the unsaved player file
     * and default settings, and then saved to the map.
     *
     * @param id The player ID
     * @return Player info from its ID,
     *         or null if the player ID doesn't exist
     * @see #fromOfflinePlayer(OfflinePlayer)
     */
    public static @Nullable PlayerInfo fromID(int id) {
        return fromOfflinePlayer(MSEssentials.getCache().idMap.getPlayerByID(id));
    }

    /**
     * Gets player info from {@link Cache#playerInfoMap}
     * by {@link OfflinePlayer} object, which was retrieved
     * by the specified player nickname with
     * {@link Server#getOfflinePlayer(String)}.
     * If the player info is not cached, a new player info
     * is created with the player file and settings
     * if the file exists, or a new player information
     * is created with the unsaved player file
     * and default settings, and then saved to the map.
     *
     * @param nickname The player nickname
     * @return Player info from its nickname,
     *         or null if the nickname is blank
     * @see #fromOfflinePlayer(OfflinePlayer)
     */
    public static @Nullable PlayerInfo fromNickname(@NotNull String nickname) {
        return nickname.isBlank() ? null : fromOfflinePlayer(Bukkit.getOfflinePlayer(nickname));
    }

    /**
     * Gets player info from {@link Cache#playerInfoMap}
     * by {@link OfflinePlayer} object, which was retrieved
     * by the specified player {@link UUID} with
     * {@link Server#getOfflinePlayer(UUID)}.
     * If the player info is not cached, a new player info
     * is created with the player file and settings
     * if the file exists, or a new player information
     * is created with the unsaved player file
     * and default settings, and then saved to the map.
     *
     * @param uuid The player UUID
     * @return Player info from its {@link UUID},
     *         or null if the player nickname is blank
     * @see #fromOfflinePlayer(OfflinePlayer)
     */
    public static @Nullable PlayerInfo fromUUID(@NotNull UUID uuid) {
        return fromOfflinePlayer(Bukkit.getOfflinePlayer(uuid));
    }

    /**
     * Gets player info from {@link Cache#playerInfoMap}
     * by {@link OfflinePlayer} object, which was retrieved
     * by the specified player ID or nickname.
     * It first checks if the string matches the ID regex
     * with {@link IDUtils#matchesIDRegex(String)},
     * and if it does, it gets the player info from its ID,
     * with {@link #fromID(int)}, otherwise it gets
     * the player info from its nickname, with
     * {@link #fromNickname(String)}.
     * If the player info is not cached, a new player info
     * is created with the player file and settings
     * if the file exists, or a new player information
     * is created with the unsaved player file
     * and default settings, and then saved to the map.
     *
     * @param string The player ID or nickname
     * @return Player info from its ID or nickname,
     *         or null if the specified string is blank,
     *         or the ID doesn't exist
     * @see #fromID(int)
     * @see #fromNickname(String)
     */
    public static @Nullable PlayerInfo fromString(@NotNull String string) {
        return string.isBlank()
                ? null
                : IDUtils.matchesIDRegex(string)
                ? fromID(IDUtils.parseID(string))
                : fromNickname(string);
    }

    /**
     * Gets player info from {@link Cache#playerInfoMap}
     * by discord ID with {@link Cache#discordMap},
     * then gets the player info from its uuid and nickname
     * with {@link #fromProfile(UUID, String)}.
     * If the player info is not cached, a new player info
     * is created with the player file and settings
     * if the file exists, or a new player information
     * is created with the unsaved player file
     * and default settings, and then saved to the map.
     *
     * @param id The player discord ID
     *           (the ID of the discord user who is linked to the player)
     * @return Player info from its discord ID,
     *         or null if the discord ID doesn't exist
     * @see #fromProfile(UUID, String)
     */
    public static @Nullable PlayerInfo fromDiscord(long id) {
        DiscordMap.Params params = MSEssentials.getCache().discordMap.getParams(id);
        return params == null
                ? null
                : fromProfile(params.getUuid(), params.getNickname());
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
     * @return Online player object if the player is online,
     *         or null if the player is offline
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
     * @return Player's id, firstname and lastname
     *         with default style
     * @see PlayerName#createDefaultName(int)
     */
    public @NotNull Component getDefaultName() {
        return this.defaultName;
    }

    /**
     * @return Player's id, firstname and lastname
     *         with golden style
     * @see PlayerName#createGoldenName(int)
     */
    public @NotNull Component getGoldenName() {
        return this.goldenName;
    }

    /**
     * @return Player's id, firstname and lastname
     *         with gray id and golden name style
     * @see PlayerName#createGrayIDGoldName(int)
     */
    public @NotNull Component getGrayIDGoldName() {
        return this.grayIDGoldName;
    }

    /**
     * @return Player's id, firstname and lastname
     *         with gray id and green name style
     * @see PlayerName#createGrayIDGreenName(int)
     */
    public @NotNull Component getGrayIDGreenName() {
        return this.grayIDGreenName;
    }

    /**
     * @return Player's id from {@link Cache#idMap}
     * @see #getID(boolean, boolean)
     */
    public int getID() {
        return this.getID(false, true);
    }

    /**
     * @param addPlayer  If true, the next available ID is added
     * @param zeroIfNull If true, returns 0 if the id is null
     * @return Player's id from {@link Cache#idMap}
     */
    public int getID(
            boolean addPlayer,
            boolean zeroIfNull
    ) {
        return this == MSEssentials.getConsolePlayerInfo()
                ? -1
                : MSEssentials.getCache().idMap.getID(this.offlinePlayer.getUniqueId(), addPlayer, zeroIfNull);
    }

    /**
     * Sets last leave location to the specified location,
     * only works if the player is online and not in
     * {@link WorldDark}
     *
     * @param location The location to set as the last leave location
     */
    public void setLastLeaveLocation(@Nullable Location location) {
        if (!WorldDark.isInWorldDark(location)) {
            this.playerFile.setLastLeaveLocation(location);
            this.playerFile.save();
        }
    }

    /**
     * Sets last death location to the specified location,
     * only works if the world specified in the location
     * is not {@link WorldDark}
     *
     * @param location The location to set as the last death location
     */
    public void setLastDeathLocation(@Nullable Location location) {
        if (!WorldDark.isInWorldDark(location)) {
            this.playerFile.setLastDeathLocation(location);
            this.playerFile.save();
        }
    }

    /**
     * Sets the player to sit position
     *
     * @param sitLocation Location of the seat
     */
    public void setSitting(@NotNull Location sitLocation) {
        this.setSitting(sitLocation, null);
    }

    /**
     * Sets the player to sit position
     * with the given message
     *
     * @param sitLocation Location of the seat
     * @param message     Message to send on sit
     */
    public void setSitting(
            @NotNull Location sitLocation,
            @Nullable Component message
    ) {
        Player player = this.getOnlinePlayer();
        if (player == null) return;

        if (
                (player.getVehicle() != null
                && player.getVehicle().getType() != EntityType.ARMOR_STAND)
                || this.isSitting()
        ) return;

        player.getWorld().spawn(sitLocation.clone().subtract(0.0d, 0.2d, 0.0d), ArmorStand.class, (armorStand) -> {
            armorStand.setMarker(true);
            armorStand.setCanTick(false);
            armorStand.setBasePlate(false);
            armorStand.setGravity(false);
            armorStand.setVisible(false);
            armorStand.setCollidable(false);
            armorStand.setSmall(true);
            armorStand.addPassenger(player);
            armorStand.addScoreboardTag("customDecor");
            MSEssentials.getCache().seats.put(player, armorStand);
        });

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
     * Unsets the player from sit position
     * with the given message
     *
     * @param message Message to send on player get up
     */
    public void unsetSitting(@Nullable Component message) {
        Player player = this.getOnlinePlayer();
        if (player == null) return;

        if (
                (player.getVehicle() != null
                && player.getVehicle().getType() != EntityType.ARMOR_STAND)
                || !isSitting()
        ) return;

        ArmorStand armorStand = MSEssentials.getCache().seats.remove(player);
        Location playerLoc = player.getLocation();
        Location getUpLocation = armorStand.getLocation().add(0.0d, 0.25d, 0.0d);

        if (!BlockUtils.REPLACE.contains(getUpLocation.getBlock().getType())) {
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
     * Sets player white list status
     *
     * @param value True to add the player to the whitelist, false to remove
     * @return True if the player was added/removed successfully
     */
    public boolean setWhiteListed(boolean value) {
        CraftServer craftServer = this.server;
        UserWhiteList userWhiteList = craftServer.getServer().getPlayerList().getWhiteList();
        GameProfile gameProfile = new GameProfile(this.uuid, this.nickname);
        boolean contains = craftServer.getWhitelistedPlayers().contains(this.offlinePlayer);

        if (value) {
            if (contains) return false;
            userWhiteList.add(new UserWhiteListEntry(gameProfile));
        } else {
            if (!contains) return false;
            userWhiteList.remove(gameProfile);
            this.kickPlayer(
                    translatable("ms.command.white_list.remove.receiver.message.title"),
                    translatable("ms.command.white_list.remove.receiver.message.subtitle")
            );
        }
        return true;
    }

    /**
     * Sets player skin
     *
     * @param skin Skin to set, null to reset
     */
    public void setSkin(@Nullable Skin skin) {
        Player player = this.getOnlinePlayer();

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
    public long getDiscordID() {
        return MSEssentials.getCache().discordMap.getId(DiscordMap.Params.create(this.uuid, this.nickname));
    }

    /**
     * Links the player to the Discord account
     *
     * @param id Discord ID
     * @see DiscordMap#put(long, UUID, String)
     */
    public void linkDiscord(long id) {
        MSEssentials.getCache().discordMap.put(id, this.uuid, this.nickname);
    }

    /**
     * Unlinks the player from the Discord account
     *
     * @return Player's Discord ID
     * @see DiscordMap#remove(long)
     */
    public long unlinkDiscord() {
        DiscordMap discordMap = MSEssentials.getCache().discordMap;
        long id = discordMap.getId(DiscordMap.Params.create(this.uuid, this.nickname));
        var botHandlers = MSEssentials.getCache().botHandlers;
        BotHandler botHandler = botHandlers.get(id);

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
     * @param url     The URL from which the client will download the resource pack
     * @param hash    A 40 character hexadecimal and lowercase SHA-1 digest of the resource pack file
     * @param force   Marks if the resource pack should be required by the client
     * @param message A Prompt to be displayed in the client request
     * @return Player's resource pack status future,
     *         the future completes in {@link PlayerResourcePackStatusEvent}
     * @see Player#setResourcePack(String, String, boolean, Component)
     * @see #completeResourcePackFuture(PlayerResourcePackStatusEvent.Status)
     */
    public @NotNull CompletableFuture<PlayerResourcePackStatusEvent.Status> setResourcePackAsync(
            @NotNull String url,
            @NotNull String hash,
            boolean force,
            @Nullable Component message
    ) {
        Player player = this.getOnlinePlayer();
        if (player == null) return CompletableFuture.completedFuture(null);

        var resourcePackStatus = new CompletableFuture<PlayerResourcePackStatusEvent.Status>();
        this.resourcePackStatus = resourcePackStatus;

        player.setResourcePack(url, hash, force, message);

        MSEssentials.getInstance().runTaskLater(() -> {
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
     * Sets player's resource pack status future
     * to the specified status and completes it.
     * If the future is not set or already completed,
     * nothing will happen
     *
     * @param status Player's resource pack status
     * @see #setResourcePackAsync(String, String, boolean, Component)
     */
    public void completeResourcePackFuture(@NotNull PlayerResourcePackStatusEvent.Status status) {
        if (this.resourcePackStatus == null || this.resourcePackStatus.isDone()) return;
        this.resourcePackStatus.complete(status);
    }

    /**
     * @return Player's mute params from {@link Cache#muteMap}
     * @see MuteMap#getMuteEntry(OfflinePlayer)
     */
    public @Nullable MuteEntry getMuteEntry() {
        return MSEssentials.getCache().muteMap.getMuteEntry(this.offlinePlayer);
    }

    /**
     * @return Player's mute reason from {@link Cache#muteMap} params
     * @throws IllegalStateException If the player is not muted,
     *                               check {@link #isMuted()} first
     * @see MuteEntry#getReason()
     */
    public @NotNull String getMuteReason() throws IllegalStateException {
        MuteEntry muteEntry = this.getMuteEntry();

        Preconditions.checkArgument(muteEntry != null, "Player is not muted");
        return muteEntry.getReason();
    }

    /**
     * @return Player's mute source from {@link Cache#muteMap} params
     * @throws IllegalStateException If the player is not muted,
     *                               check {@link #isMuted()} first
     * @see MuteEntry#getSource()
     */
    public @NotNull String getMutedBy() throws IllegalStateException {
        MuteEntry muteEntry = this.getMuteEntry();

        Preconditions.checkArgument(muteEntry != null, "Player is not muted");
        return muteEntry.getSource();
    }

    /**
     * @param sender The command sender, used to get the time zone
     * @return Date when the player was muted from {@link Cache#muteMap}
     *         with the sender's time zone or default time zone
     *         if the sender's time zone cannot be obtained
     * @throws IllegalStateException If the player is not muted,
     *                               check {@link #isMuted()} first
     * @see MuteEntry#getCreated()
     */
    public @NotNull Component getMutedFrom(@NotNull CommandSender sender) throws IllegalStateException {
        return text(DateUtils.getSenderDate(this.getMutedFrom(), sender));
    }

    /**
     * @param address The IP address, used to get the time zone
     * @return Date when the player was muted from {@link Cache#muteMap}
     *         with the time zone of the IP address or default time zone
     *         if the time zone cannot be obtained
     * @throws IllegalStateException If the player is not muted,
     *                               check {@link #isMuted()} first
     * @see MuteEntry#getCreated()
     */
    public @NotNull Component getMutedFrom(@NotNull InetAddress address) throws IllegalStateException {
        return text(DateUtils.getDate(this.getMutedFrom(), address));
    }

    /**
     * @return Date when the player was muted from {@link Cache#muteMap}
     * @throws IllegalStateException If the player is not muted,
     *                               check {@link #isMuted()} first
     * @see MuteEntry#getCreated()
     */
    public @NotNull Instant getMutedFrom() throws IllegalStateException {
        MuteEntry muteEntry = this.getMuteEntry();

        Preconditions.checkArgument(muteEntry != null, "Player is not muted");
        return muteEntry.getCreated();
    }

    /**
     * @param sender The command sender, used to get the time zone
     * @return Date when the player will be unmuted from {@link Cache#muteMap}
     *         with the sender's time zone or default time zone
     *         if the sender's time zone cannot be obtained
     * @throws IllegalStateException If the player is not muted,
     *                               check {@link #isMuted()} first
     * @see MuteEntry#getExpiration()
     */
    public @NotNull Component getMutedTo(@NotNull CommandSender sender) throws IllegalStateException {
        return text(DateUtils.getSenderDate(this.getMutedTo(), sender));
    }

    /**
     * @param address The IP address, used to get the time zone
     * @return Date when the player will be unmuted from {@link Cache#muteMap}
     *         with the time zone of the IP address or default time zone
     *         if the time zone cannot be obtained
     * @throws IllegalStateException If the player is not muted,
     *                               check {@link #isMuted()} first
     * @see MuteEntry#getExpiration()
     */
    public @NotNull Component getMutedTo(@NotNull InetAddress address) throws IllegalStateException {
        return text(DateUtils.getDate(this.getMutedTo(), address));
    }

    /**
     * @return Date when the player will be unmuted from {@link Cache#muteMap}
     * @throws IllegalStateException If the player is not muted,
     *                               check {@link #isMuted()} first
     * @see MuteEntry#getExpiration()
     */
    public @NotNull Instant getMutedTo() throws IllegalStateException {
        MuteEntry muteEntry = this.getMuteEntry();

        Preconditions.checkArgument(muteEntry != null, "Player is not muted");
        return muteEntry.getExpiration();
    }

    /**
     * Mutes or unmutes the player in {@link Cache#muteMap}.
     * Also sends a message to the muted player if they are
     * online, and to the sender, who muted the player.
     *
     * @param value  True to mute the player, false to unmute
     * @param date   The date when the player will be unmuted
     * @param reason The reason of the mute
     * @param sender The command sender, who muted the player,
     *               can be null, in this case the console
     *               sender will be used
     * @see MuteMap#put(OfflinePlayer, Instant, String, String)
     * @see MuteMap#remove(OfflinePlayer)
     */
    public void setMuted(
            boolean value,
            @NotNull Instant date,
            @NotNull String reason,
            @Nullable CommandSender sender
    ) {
        Player player = this.getOnlinePlayer();
        MuteMap muteMap = MSEssentials.getCache().muteMap;

        if (sender == null) {
            sender = this.server.getConsoleSender();
        }

        if (value) {
            if (this.isMuted()) {
                MSLogger.warning(
                        sender,
                        translatable(
                                "ms.command.mute.already.sender",
                                this.getGrayIDGoldName(),
                                text(this.nickname)
                        )
                );
                return;
            }

            muteMap.put(this.offlinePlayer, date, reason, sender.getName());
            MSLogger.fine(
                    sender,
                    translatable(
                            "ms.command.mute.message.sender",
                            this.getGrayIDGreenName(),
                            text(this.nickname),
                            text(reason),
                            text(DateUtils.getSenderDate(date, sender))
                    )
            );

            if (player != null) {
                MSLogger.warning(
                        player,
                        translatable(
                                "ms.command.mute.message.receiver",
                                text(reason),
                                text(DateUtils.getSenderDate(date, sender))
                        )
                );
            }
        } else {
            if (!this.isMuted()) {
                MSLogger.warning(
                        sender,
                        translatable(
                                "ms.command.unmute.not_muted",
                                this.getGrayIDGoldName(),
                                text(this.nickname)
                        )
                );
                return;
            }

            muteMap.remove(this.offlinePlayer);
            MSLogger.fine(
                    sender,
                    translatable(
                            "ms.command.unmute.sender.message",
                            this.getGrayIDGreenName(),
                            text(this.nickname)
                    )
            );

            if (player != null) {
                MSLogger.warning(player, translatable("ms.command.unmute.receiver.message"));
            }
        }

        if (this.isLinked()) {
            this.sendPrivateDiscordMessage(MessageUtils.craftEmbed(
                    LanguageFile.renderTranslation(
                            value
                            ? translatable(
                                    "ms.discord.muted",
                                    this.defaultName,
                                    text(this.nickname),
                                    text(reason),
                                    text(DateUtils.getSenderDate(date, player))
                            )
                            : translatable(
                                    "ms.discord.unmuted",
                                    this.defaultName,
                                    text(this.nickname)
                            )
                    )
            ));
        }
    }

    /**
     * Unmutes the player in {@link Cache#muteMap}.
     * Also sends a message to the muted player if they are
     * online, and to the sender, who unmuted the player.
     *
     * @param commandSender The command sender, who unmuted the player,
     *                      can be null, in this case the console
     *                      sender will be used
     */
    public void unmute(@Nullable CommandSender commandSender) {
        this.setMuted(false, Instant.EPOCH, "", commandSender);
    }

    /**
     * @return The ban entry of the player from {@link BanList.Type#PROFILE}
     */
    public @Nullable BanEntry<PlayerProfile> getBanEntry() {
        BanList<PlayerProfile> banList = this.server.getBanList(BanList.Type.PROFILE);
        return banList.getBanEntry(this.profile);
    }

    /**
     * @return The ban reason of the player from {@link BanList.Type#PROFILE}
     *         or "ms.command.ban.default_reason" if the reason is null
     * @throws IllegalStateException If the player is not banned,
     *                               check {@link #isBanned()} first
     * @see BanEntry#getReason()
     */
    public @NotNull Component getBanReason() throws IllegalStateException {
        var banEntry = this.getBanEntry();

        Preconditions.checkArgument(banEntry != null, "Player is not banned");

        String reason = banEntry.getReason();
        return reason == null ? LanguageFile.renderTranslationComponent("ms.command.ban.default_reason") : text(reason);
    }

    /**
     * @param reason The ban reason of the player
     * @throws IllegalStateException If the player is not banned,
     *                               check {@link #isBanned()} first
     * @see BanEntry#setReason(String)
     */
    public void setBanReason(@NotNull String reason) throws IllegalStateException {
        var banEntry = this.getBanEntry();

        Preconditions.checkArgument(banEntry != null, "Player is not banned");
        
        banEntry.setReason(reason);
        banEntry.save();
    }

    /**
     * @return The ban source of the player from {@link BanList.Type#PROFILE}
     * @throws IllegalStateException If the player is not banned,
     *                               check {@link #isBanned()} first
     * @see BanEntry#getSource()
     */
    public @NotNull String getBannedBy() throws IllegalStateException {
        var banEntry = this.getBanEntry();

        Preconditions.checkArgument(banEntry != null, "Player is not banned");
        return banEntry.getSource();
    }

    /**
     * @param source The ban source of the player
     * @throws IllegalStateException If the player is not banned,
     *                               check {@link #isBanned()} first
     * @see BanEntry#setSource(String)
     */
    public void setBannedBy(@NotNull String source) throws IllegalStateException {
        var banEntry = this.getBanEntry();

        Preconditions.checkArgument(banEntry != null, "Player is not banned");
        
        banEntry.setSource(source);
        banEntry.save();
    }

    /**
     * @param sender The command sender, used to get the time zone
     * @return Date when the player was banned from {@link BanList.Type#PROFILE}
     *         with the sender's time zone or default time zone
     *         if the sender's time zone cannot be obtained
     * @throws IllegalStateException If the player is not banned,
     *                               check {@link #isBanned()} first
     * @see BanEntry#getCreated()
     */
    public @NotNull Component getBannedFrom(@NotNull CommandSender sender) throws IllegalStateException {
        return text(DateUtils.getSenderDate(this.getBannedFrom(), sender));
    }

    /**
     * @param address The IP address, used to get the time zone
     * @return Date when the player was banned from {@link BanList.Type#PROFILE}
     *         with the time zone of the IP address or default time zone
     *         if the time zone cannot be obtained
     * @throws IllegalStateException If the player is not banned,
     *                               check {@link #isBanned()} first
     * @see BanEntry#getCreated()
     */
    public @NotNull Component getBannedFrom(@NotNull InetAddress address) throws IllegalStateException {
        return text(DateUtils.getDate(this.getBannedFrom(), address));
    }

    /**
     * @return Date when the player was banned from {@link BanList.Type#PROFILE}
     * @throws IllegalStateException If the player is not banned,
     *                               check {@link #isBanned()} first
     * @see BanEntry#getCreated()
     */
    public @NotNull Instant getBannedFrom() throws IllegalStateException {
        var banEntry = this.getBanEntry();

        Preconditions.checkArgument(banEntry != null, "Player is not banned");
        return banEntry.getCreated().toInstant();
    }

    /**
     * @param sender The command sender, used to get the time zone
     * @return Date when the player will be unbanned from {@link BanList.Type#PROFILE}
     *         with the sender's time zone or default time zone
     *         if the sender's time zone cannot be obtained
     * @throws IllegalStateException If the player is not banned,
     *                               check {@link #isBanned()} first
     * @see BanEntry#getExpiration()
     */
    public @NotNull Component getBannedTo(@NotNull CommandSender sender) throws IllegalStateException {
        var banEntry = this.getBanEntry();

        Preconditions.checkArgument(banEntry != null, "Player is not banned");

        Date expiration = banEntry.getExpiration();
        return expiration == null
                ? translatable("ms.command.ban.time.forever")
                : text(DateUtils.getSenderDate(expiration.toInstant(), sender));
    }

    /**
     * @param address The IP address, used to get the time zone
     * @return Date when the player will be unbanned from {@link BanList.Type#PROFILE}
     *         with the time zone of the IP address or default time zone
     *         if the time zone cannot be obtained
     * @throws IllegalStateException If the player is not banned,
     *                               check {@link #isBanned()} first
     * @see BanEntry#getExpiration()
     */
    public @NotNull Component getBannedTo(@NotNull InetAddress address) throws IllegalStateException {
        var banEntry = this.getBanEntry();

        Preconditions.checkArgument(banEntry != null, "Player is not banned");

        Date expiration = banEntry.getExpiration();
        return expiration == null
                ? translatable("ms.command.ban.time.forever")
                : text(DateUtils.getDate(expiration.toInstant(), address));
    }

    /**
     * @return Date when the player will be unbanned from {@link BanList.Type#PROFILE},
     *         or null if the player is banned forever
     * @throws IllegalStateException If the player is not banned,
     *                               check {@link #isBanned()} first
     * @see BanEntry#getExpiration()
     */
    public @Nullable Instant getBannedTo() throws IllegalStateException {
        var banEntry = this.getBanEntry();

        Preconditions.checkArgument(banEntry != null, "Player is not banned");

        Date expiration = banEntry.getExpiration();
        return expiration == null ? null : expiration.toInstant();
    }

    /**
     * @param expiration Date when the player will be unbanned from {@link BanList.Type#PROFILE},
     *                   or null if the player is banned forever
     * @throws IllegalStateException If the player is not banned,
     *                               check {@link #isBanned()} first
     * @see BanEntry#setExpiration(Date)
     */
    public void setBannedTo(@Nullable Date expiration) throws IllegalStateException {
        var banEntry = this.getBanEntry();

        Preconditions.checkArgument(banEntry != null, "Player is not banned");
        
        banEntry.setExpiration(expiration);
        banEntry.save();
    }

    /**
     * Bans or unbans the player in {@link BanList.Type#PROFILE}.
     * Also kicks the player with the specified reason and expiration date
     * if they are online, and to the player's private discord channel,
     * if it is linked. Also sends a message to the sender.
     *
     * @param value  Whether the player should be banned
     * @param date   Date when the player was banned
     * @param reason Reason why the player was banned
     * @param sender The command sender, who banned the player
     *               or null if the player was banned by the console
     */
    public void setBanned(
            boolean value,
            @NotNull Instant date,
            @NotNull String reason,
            @Nullable CommandSender sender
    ) {
        BanList<PlayerProfile> banList = this.server.getBanList(BanList.Type.PROFILE);
        Player player = this.getOnlinePlayer();
        CommandSender commandSender = sender == null ? this.server.getConsoleSender() : sender;

        if (value) {
            if (this.isBanned()) {
                MSLogger.warning(
                        sender,
                        translatable(
                                "ms.command.ban.already.sender",
                                this.getGrayIDGoldName(),
                                text(this.nickname)
                        )
                );
                return;
            }

            banList.addBan(this.profile, reason, Date.from(date), commandSender.getName());
            this.kickPlayer(
                    translatable("ms.command.ban.message.receiver.title"),
                    translatable(
                            "ms.command.ban.message.receiver.subtitle",
                            text(reason),
                            text(DateUtils.getSenderDate(date, player))
                    )
            );
            MSLogger.fine(
                    sender,
                    translatable(
                            "ms.command.ban.message.sender",
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
                        translatable(
                                "ms.command.unban.not_banned",
                                this.getGrayIDGoldName(),
                                text(this.nickname)
                        )
                );
                return;
            }

            banList.pardon(this.profile);
            MSLogger.fine(
                    sender,
                    translatable(
                            "ms.command.unban.message.sender",
                            this.getGrayIDGreenName(),
                            text(this.nickname)
                    )
            );
        }

        if (this.isLinked()) {
            this.sendPrivateDiscordMessage(MessageUtils.craftEmbed(
                    LanguageFile.renderTranslation(
                            value
                            ? translatable(
                                    "ms.discord.banned",
                                    this.defaultName,
                                    text(this.nickname),
                                    text(reason),
                                    text(DateUtils.getSenderDate(date, player))
                            )
                            : translatable(
                                    "ms.discord.unbanned",
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
     * @param commandSender The command sender, who unbanned the player,
     *                      or null if the player was unbanned by the console
     */
    public void pardon(@Nullable CommandSender commandSender) {
        this.setBanned(false, Instant.EPOCH, "", commandSender);
    }

    /**
     * @return True if the player isn't in dark_world and hasn't vanished
     */
    public boolean isOnline() {
        return this.isOnline(false);
    }

    /**
     * @param ignoreWorld Ignore world_dark check
     * @return True if the player isn't in dark_world and hasn't vanished
     */
    public boolean isOnline(boolean ignoreWorld) {
        Player player = this.getOnlinePlayer();
        return player != null
                && (ignoreWorld || !this.isInWorldDark())
                && !this.isVanished();
    }

    /**
     * @return True if the player is registered,
     *         i.e. the player file exists and has a name
     * @see PlayerFile#exists()
     * @see PlayerFile#isNoName()
     */
    public boolean isRegistered() {
        return this.playerFile.exists() && !this.playerFile.isNoName();
    }

    /**
     * @return True if the player is vanished
     */
    public boolean isVanished() {
        Player player = this.getOnlinePlayer();
        return player != null && player.getMetadata("vanished").stream().anyMatch(MetadataValue::asBoolean);
    }

    /**
     * @return True if the player is sitting
     */
    public boolean isSitting() {
        Player player = this.getOnlinePlayer();
        return player != null && MSEssentials.getCache().seats.containsKey(player);
    }

    /**
     * @return True if the player is linked to the Discord account
     * @see DiscordMap#containsPlayer(DiscordMap.Params)
     */
    public boolean isLinked() {
        return MSEssentials.getCache().discordMap.containsPlayer(DiscordMap.Params.create(this.uuid, this.nickname));
    }

    /**
     * @return True if the player is whitelisted
     * @see OfflinePlayer#isWhitelisted()
     */
    public boolean isWhiteListed() {
        return this.server.getWhitelistedPlayers().contains(this.offlinePlayer);
    }

    /**
     * @return True if the player is muted in {@link Cache#muteMap}
     * @see MuteMap#isMuted(OfflinePlayer)
     */
    public boolean isMuted() {
        return MSEssentials.getCache().muteMap.isMuted(this.offlinePlayer);
    }

    /**
     * @return True if the player is banned in {@link BanList.Type#PROFILE}
     */
    public boolean isBanned() {
        return this.getBanEntry() != null;
    }

    /**
     * @return True if the player is online and authenticated
     */
    public boolean isAuthenticated() {
        Player player = this.getOnlinePlayer();
        return player != null && AuthMeApi.getInstance().isAuthenticated(player);
    }

    /**
     * @return True if the player is online
     *         and in the {@link WorldDark}
     */
    public boolean isInWorldDark() {
        return WorldDark.isInWorldDark(this.getOnlinePlayer());
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
        int id = this.getID();
        PlayerName playerName = this.playerFile.getPlayerName();
        this.defaultName = playerName.createDefaultName(id);
        this.goldenName = playerName.createGoldenName(id);
        this.grayIDGoldName = playerName.createGrayIDGoldName(id);
        this.grayIDGreenName = playerName.createGrayIDGreenName(id);
    }

    /**
     * Handles the player's join.
     * Starts the join task, which checks if the player
     * is online, authenticated, and not dead. If the player
     * is not online, the task is cancelled. If the player
     * is not authenticated or dead, the task will be repeated.
     *
     * @see #handleJoinTask()
     */
    public void handleJoin() {
        Player player = this.getOnlinePlayer();

        if (
                player == null
                || this.joinTask != null && !this.joinTask.isCancelled()
        ) return;

        MSEssentials.getInstance().runTaskTimer(task -> {
            this.joinTask = task;

            if (!player.isOnline()) task.cancel();

            this.handleJoinTask();
        }, 0L, 1L);
    }

    /**
     * Handles the player's quit.
     * Unsets the player's sitting, ejects the player from
     * the vehicle, removes the player from the anomaly action map,
     * and saves the player's data. After that, sends the quit message
     * to all players, if the player is not located in the dark world.
     */
    public void handleQuit() {
        Player player = this.getOnlinePlayer();
        if (player == null) return;

        this.unsetSitting();

        Entity vehicle = player.getVehicle();
        if (vehicle != null) {
            vehicle.eject();
        }

        if (this.joinTask != null) {
            this.joinTask.cancel();
        }

        MSEssentials.getCache().playerAnomalyActionMap.remove(player);
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
        Player player = this.getOnlinePlayer();
        if (player == null) return CompletableFuture.completedFuture(false);

        PlayerSettings playerSettings = this.playerFile.getPlayerSettings();
        ResourcePack.Type type = playerSettings.getResourcePackType();

        if (!ResourcePack.isResourcePackLoaded()) {
            this.kickPlayer(SERVER_NOT_LOADED_TITLE, SERVER_NOT_LOADED_SUBTITLE);
            return CompletableFuture.completedFuture(false);
        }

        switch (type) {
            case NONE -> {
                return CompletableFuture.completedFuture(true);
            }
            case NULL -> {
                MSEssentials.getInstance().runTaskTimer(task -> {
                    if (player.getOpenInventory().getTopInventory() instanceof CustomInventory) {
                        task.cancel();
                        return;
                    }

                    ResourcePackMenu.open(player);
                }, 0L, 5L);
                return CompletableFuture.completedFuture(false);
            }
            default -> {
                return this.setResourcePackAsync(type.getURL(), type.getHash(), true, null)
                        .thenApply(status -> {
                            switch (status) {
                                case SUCCESSFULLY_LOADED -> {
                                    MSLogger.fine(RP_SUCCESSFULLY_LOADED.args(text(this.nickname)));
                                    return true;
                                }
                                case FAILED_DOWNLOAD -> {
                                    playerSettings.setResourcePackType(ResourcePack.Type.NONE);
                                    playerSettings.save();

                                    MSLogger.warning(RP_FAILED_DOWNLOAD_CONSOLE.args(text(this.nickname)));
                                    this.kickPlayer(RP_FAILED_DOWNLOAD_TITLE, RP_FAILED_DOWNLOAD_SUBTITLE);
                                }
                                case DECLINED -> {
                                    MSLogger.warning(RP_DECLINED_CONSOLE.args(text(this.nickname)));
                                    this.kickPlayer(RP_DECLINED_TITLE, RP_DECLINED_SUBTITLE);
                                }
                            }
                            return false;
                        })
                        .exceptionally(
                                throwable -> {
                                    MSLogger.log(Level.SEVERE, "An error occurred while sending the resource pack to " + this.nickname, throwable);
                                    this.kickPlayer(SOMETHING_WENT_WRONG_TITLE, SOMETHING_WENT_WRONG_SUBTITLE);
                                    return false;
                                }
                        );
            }
        }
    }

    /**
     * Teleports the player to the last leave location
     * or to the bed spawn location if the last leave location is null
     * or to the world spawn location if the bed spawn location is null
     *
     * @return A future that will be completed with the result of the teleport
     */
    public @NotNull CompletableFuture<Boolean> teleportToLastLeaveLocation() {
        Player player = this.getOnlinePlayer();
        if (player == null) return CompletableFuture.completedFuture(false);

        if (player.getGameMode() == GameMode.SPECTATOR) {
            MSEssentials.getInstance().runTask(() -> player.setSpectatorTarget(null));
        }

        Location location = this.playerFile.getLastLeaveLocation();

        if (location == null) {
            location = player.getBedSpawnLocation();

            if (location == null) {
                location = MSEssentials.getConfiguration().spawnLocation;
            }
        }

        return player.teleportAsync(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    /**
     * Teleports the player to the last death location
     * or to the bed spawn location if the last leave location is null
     * or to the world spawn location if the bed spawn location is null
     *
     * @return A future that will be completed with the result of the teleport
     */
    public @NotNull CompletableFuture<Boolean> teleportToLastDeathLocation() {
        Player player = this.getOnlinePlayer();
        if (player == null) return CompletableFuture.completedFuture(false);

        if (player.getGameMode() == GameMode.SPECTATOR) {
            MSEssentials.getInstance().runTask(() -> player.setSpectatorTarget(null));
        }

        Location location = this.playerFile.getLastDeathLocation();

        if (location == null) {
            location = player.getBedSpawnLocation();

            if (location == null) {
                location = MSEssentials.getConfiguration().spawnLocation;
            }
        }

        return player.teleportAsync(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    /**
     * Creates the player's file, if it doesn't exist.
     * Also sets the player's nickname, IP and first join date,
     * if the player is online.
     * And sends the message to the console, if the player's file
     * was created successfully.
     */
    public void createPlayerFile() {
        if (this.playerFile.exists()) return;

        this.playerFile.getConfig().set("name.nickname", this.nickname);

        if (this.getOnlinePlayer() != null) {
            this.playerFile.addIp(
                    this.getOnlinePlayer().getAddress() == null
                    ? null
                    : this.getOnlinePlayer().getAddress().getAddress().getHostAddress()
            );
            this.playerFile.setFirstJoin(Instant.now());
        }

        this.playerFile.save();
        MSLogger.fine(
                translatable(
                        "ms.info.player_file_created",
                        text(this.nickname),
                        text(this.offlinePlayer.getUniqueId().toString())
                )
        );
    }

    /**
     * Kicks the player from the server
     *
     * @param title  Title of the kick message
     * @param reason Reason of the kick message
     */
    public void kickPlayer(
            @NotNull Component title,
            @NotNull Component reason
    ) {
        Player player = this.getOnlinePlayer();
        if (player == null) return;

        this.handleQuit();
        player.kick(
                translatable(
                        "ms.format.leave.message",
                        title.color(NamedTextColor.RED).decorate(TextDecoration.BOLD),
                        reason.color(NamedTextColor.GRAY)
                ).color(NamedTextColor.DARK_GRAY)
        );
    }

    /**
     * Generates a new link code for the player
     *
     * @return Generated code
     * @see DiscordMap#generateCode(PlayerInfo)
     */
    public short generateCode() {
        return MSEssentials.getCache().discordMap.generateCode(this);
    }

    /**
     * Sends a private message to the player's Discord account
     *
     * @param messageEmbed Embed to send
     * @param other        Other embeds to send
     * @return True if the player has linked their Discord account
     */
    public boolean sendPrivateDiscordMessage(
            @NotNull MessageEmbed messageEmbed,
            MessageEmbed @NotNull ... other
    ) {
        long id = this.getDiscordID();

        if (id == -1) return false;

        MSEssentials.getInstance().runTaskAsync(() -> {
            User user = DiscordUtil.getJda().getUserById(id);

            if (user != null) {
                user.openPrivateChannel().complete().sendMessageEmbeds(messageEmbed, other).queue();
            }
        });
        return true;
    }

    /**
     * Sends a private message to the player's Discord account
     *
     * @param message Message to send
     * @return True if the player has linked their Discord account
     */
    public boolean sendPrivateDiscordMessage(@NotNull CharSequence message) {
        long id = this.getDiscordID();

        if (id == -1) return false;

        MSEssentials.getInstance().runTaskAsync(() -> {
            User user = DiscordUtil.getJda().getUserById(id);

            if (user != null) {
                user.openPrivateChannel().complete().sendMessage(message).queue();
            }
        });
        return true;
    }

    /**
     * Saves the player's health, air, game mode
     * and last leave location to the player's file,
     * if the player is online and not in the
     * {@link WorldDark}
     */
    public void savePlayerDataParams() {
        Player player = this.getOnlinePlayer();

        if (
                player == null
                || this.isInWorldDark()
        ) return;

        double health = player.getHealth();
        int air = player.getRemainingAir();

        this.playerFile.setLastLeaveLocation(
                player.isDead()
                ? player.getBedSpawnLocation() != null
                ? player.getBedSpawnLocation()
                : MSEssentials.getConfiguration().spawnLocation
                : player.getLocation()
        );
        this.playerFile.setGameMode(player.getGameMode());
        this.playerFile.setHealth(health == 0.0d ? 20.0d : health);
        this.playerFile.setAir(air == 0 && player.isDead() ? 300 : air);
        this.playerFile.save();
    }

    /**
     * Hides the player's name tag from other players
     * @see MSPlayerUtils#hideNameTag(Player)
     */
    public void hideNameTag() {
        MSPlayerUtils.hideNameTag(this.getOnlinePlayer());
    }

    /**
     * Updates the player's file and initializes the player's names
     * @see PlayerFile#loadConfig(UUID, String)
     * @see PlayerInfo#initNames()
     */
    public void update() {
        this.playerFile = PlayerFile.loadConfig(this.uuid, this.nickname);
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
     * Sets the player's skin, game mode, health and air.
     * Also teleports the player to the last leave location,
     * and sends the join message to all players. If the player
     * is not registered, then the registration process will be
     * started.
     *
     * @see #handleJoin()
     */
    private void handleJoinTask() {
        Player player = this.getOnlinePlayer();

        this.joinTask.cancel();

        if (player == null) return;
        if (!this.isRegistered()) {
            new RegistrationProcess().registerPlayer(this);
        } else {
            if (this.playerFile.getConfig().getString("pronouns") == null) {
                PronounsMenu.open(player);
            } else {
                Skin currentSkin = this.getCurrentSkin();

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
                        MSLogger.warning("Something went wrong while teleporting " + this.nickname + " to their last leave location");
                    }
                });
            }
        }
    }
}
