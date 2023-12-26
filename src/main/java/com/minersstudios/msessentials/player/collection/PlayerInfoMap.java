package com.minersstudios.msessentials.player.collection;

import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerFile;
import com.minersstudios.msessentials.player.PlayerInfo;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Player info map with {@link UUID} and its {@link PlayerInfo}. All player
 * files stored in the "config/minersstudios/MSEssentials/players" folder. Use
 * {@link #get(UUID, String)} or {@link #get(Player)} to get player info. It
 * will create new player info if it doesn't exist, or get existing player info
 * if it exists and save it to the map if it's not cached.
 *
 * @see PlayerInfo
 * @see PlayerFile
 */
public final class PlayerInfoMap {
    private final MSEssentials plugin;
    private final Map<UUID, PlayerInfo> map;

    public PlayerInfoMap(final @NotNull MSEssentials plugin) {
        this.plugin = plugin;
        this.map = new ConcurrentHashMap<>();
    }

    /**
     * @return The plugin instance
     */
    public @NotNull MSEssentials getPlugin() {
        return this.plugin;
    }

    /**
     * Gets {@link PlayerInfo} of the player from the map
     *
     * @param uniqueId Player {@link UUID}
     * @param nickname Player nickname
     * @return {@link PlayerInfo} of player
     */
    public @NotNull PlayerInfo get(
            final @NotNull UUID uniqueId,
            final @NotNull String nickname
    ) {
        return this.map.computeIfAbsent(
                uniqueId,
                uuid -> new PlayerInfo(this.plugin, uuid, nickname)
        );
    }

    /**
     * Gets {@link PlayerInfo} of the player from the map
     *
     * @param player The player
     * @return {@link PlayerInfo} of player
     */
    public @NotNull PlayerInfo get(final @NotNull Player player) {
        return this.get(player.getUniqueId(), player.getName());
    }

    /**
     * Gets {@link PlayerInfo} of the player from the map
     *
     * @param offlinePlayer The offline player
     * @return {@link PlayerInfo} of player, or null if we have not seen a name
     *         for this player yet
     */
    @Contract("null -> null")
    public @Nullable PlayerInfo get(final @Nullable OfflinePlayer offlinePlayer) {
        if (offlinePlayer == null) {
            return null;
        }

        final UUID uuid = offlinePlayer.getUniqueId();
        final String nickname = offlinePlayer.getName();

        return ChatUtils.isBlank(nickname)
                ? null
                : this.get(uuid, nickname);
    }

    /**
     * Puts {@link UUID} with its {@link PlayerInfo} to the map
     *
     * @param playerInfo {@link PlayerInfo} for caching
     * @return The previous {@link PlayerInfo} associated with {@link UUID},
     *         or null if there was no mapping for player's {@link UUID}
     */
    public @Nullable PlayerInfo put(final @NotNull PlayerInfo playerInfo) {
        return this.map.put(playerInfo.getUuid(), playerInfo);
    }

    /**
     * Removes {@link UUID} with its {@link PlayerInfo} in the map
     *
     * @param uniqueId Cached {@link UUID} of {@link PlayerInfo}
     * @return The previous {@link PlayerInfo} associated with {@link UUID},
     *         or null if there was no mapping for player's {@link UUID}
     */
    public @Nullable PlayerInfo remove(final @NotNull UUID uniqueId) {
        return this.map.remove(uniqueId);
    }

    /**
     * Removes {@link UUID} with its {@link PlayerInfo} in the map
     *
     * @param playerInfo Cached {@link PlayerInfo}
     * @return The previous {@link PlayerInfo} associated with {@link UUID},
     *         or null if there was no mapping for player's {@link UUID}
     */
    public @Nullable PlayerInfo remove(final @NotNull PlayerInfo playerInfo) {
        return this.remove(playerInfo.getUuid());
    }

    /**
     * @return The number of player info in this map
     */
    public int size() {
        return this.map.size();
    }

    /**
     * @return True if this map contains no player info
     */
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    /**
     * @param uuid {@link UUID} of player
     * @return True if the map contains the uuid of the player
     */
    public boolean containsUUID(final @Nullable UUID uuid) {
        return uuid != null
                && this.map.containsKey(uuid);
    }

    /**
     * @param playerInfo {@link PlayerInfo} of player
     * @return True if the map contains the player info
     */
    public boolean containsPlayerInfo(final @NotNull PlayerInfo playerInfo) {
        return this.map.containsValue(playerInfo);
    }

    /**
     * Removes all the player info from this map. The map will be empty after
     * this call returns.
     */
    public void clear() {
        this.map.clear();
    }

    /**
     * @return An unmodifiable view of the UUIDs contained in this map
     */
    public @NotNull @UnmodifiableView Set<UUID> uuidSet() {
        return Collections.unmodifiableSet(this.map.keySet());
    }

    /**
     * @return An unmodifiable view of the player info contained in this map
     */
    public @NotNull @UnmodifiableView Collection<PlayerInfo> playerInfos() {
        return Collections.unmodifiableCollection(this.map.values());
    }

    /**
     * @return An unmodifiable view of the mappings contained in this map
     */
    public @NotNull @UnmodifiableView Set<Map.Entry<UUID, PlayerInfo>> entrySet() {
        return Collections.unmodifiableSet(this.map.entrySet());
    }
}
