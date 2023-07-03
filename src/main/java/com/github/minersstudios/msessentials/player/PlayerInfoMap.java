package com.github.minersstudios.msessentials.player;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Player info map with {@link UUID} and its {@link PlayerInfo}.
 * All player files stored in the "config/minersstudios/MSEssentials/players" folder.
 * Use {@link #get(UUID, String)} or {@link #get(Player)} to get player info.
 * It will create new player info if it doesn't exist,
 * or get existing player info if it exists and save it to the map if it's not cached.
 *
 * @see PlayerInfo
 * @see PlayerFile
 */
public class PlayerInfoMap {
    private final Map<UUID, PlayerInfo> map = new ConcurrentHashMap<>();

    /**
     * Gets {@link PlayerInfo} of the player from the map,
     * or creates new {@link PlayerInfo} and puts it in the map if it's not cached
     *
     * @param uniqueId player {@link UUID}
     * @param nickname player nickname
     * @return {@link PlayerInfo} of player
     */
    public @NotNull PlayerInfo get(
            @NotNull UUID uniqueId,
            @NotNull String nickname
    ) {
        return this.map.computeIfAbsent(
                uniqueId,
                uuid -> new PlayerInfo(uuid, nickname)
        );
    }

    /**
     * Gets {@link PlayerInfo} of the player from the map,
     * or creates new {@link PlayerInfo} and puts it in the map if it's not cached
     *
     * @param player the player
     * @return {@link PlayerInfo} of player
     */
    public @NotNull PlayerInfo get(@NotNull Player player) {
        return this.map.computeIfAbsent(
                player.getUniqueId(),
                uuid -> new PlayerInfo(player)
        );
    }

    /**
     * Puts {@link UUID} with its {@link PlayerInfo} to the map
     *
     * @param playerInfo {@link PlayerInfo} for caching
     * @return The previous {@link PlayerInfo} associated with {@link UUID},
     *         or null if there was no mapping for player's {@link UUID}
     */
    public @Nullable PlayerInfo put(@NotNull PlayerInfo playerInfo) {
        return this.map.put(playerInfo.getUuid(), playerInfo);
    }

    /**
     * Removes {@link UUID} with its {@link PlayerInfo} in the map
     *
     * @param uniqueId cached {@link UUID} of {@link PlayerInfo}
     * @return The previous {@link PlayerInfo} associated with {@link UUID},
     *         or null if there was no mapping for player's {@link UUID}
     */
    public @Nullable PlayerInfo remove(@NotNull UUID uniqueId) {
        return this.map.remove(uniqueId);
    }

    /**
     * Removes {@link UUID} with its {@link PlayerInfo} in the map
     *
     * @param playerInfo cached {@link PlayerInfo}
     * @return The previous {@link PlayerInfo} associated with {@link UUID},
     *         or null if there was no mapping for player's {@link UUID}
     */
    public @Nullable PlayerInfo remove(@NotNull PlayerInfo playerInfo) {
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
    public boolean containsUUID(@Nullable UUID uuid) {
        return this.map.containsKey(uuid);
    }

    /**
     * @param playerInfo {@link PlayerInfo} of player
     * @return True if the map contains the player info
     */
    public boolean containsPlayerInfo(@NotNull PlayerInfo playerInfo) {
        return this.map.containsValue(playerInfo);
    }

    /**
     * Removes all the player info from this map.
     * The map will be empty after this call returns.
     */
    public void clear() {
        this.map.clear();
    }

    /**
     * @return An unmodifiable view of the UUIDs contained in this map
     */
    public @NotNull @UnmodifiableView Set<UUID> uuidSet() {
        return Set.copyOf(this.map.keySet());
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
        return Set.copyOf(this.map.entrySet());
    }
}
