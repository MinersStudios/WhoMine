package com.github.minersstudios.msessentials.player;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public class PlayerInfoMap {
    private final Map<UUID, PlayerInfo> map = new ConcurrentHashMap<>();

    /**
     * Gets player info map
     *
     * @return map with {@link UUID} and its {@link PlayerInfo}
     */
    public @NotNull @UnmodifiableView  Map<UUID, PlayerInfo> getMap() {
        return Collections.unmodifiableMap(this.map);
    }

    /**
     * Puts {@link UUID} with its {@link PlayerInfo} to the map
     *
     * @param playerInfo {@link PlayerInfo} for caching
     */
    public void put(@NotNull PlayerInfo playerInfo) {
        this.map.put(playerInfo.getUuid(), playerInfo);
    }

    /**
     * Removes {@link UUID} with its {@link PlayerInfo} in the map
     *
     * @param uniqueId cached {@link UUID} of {@link PlayerInfo}
     */
    public void remove(@NotNull UUID uniqueId) {
        this.map.remove(uniqueId);
    }

    /**
     * Removes {@link UUID} with its {@link PlayerInfo} in the map
     *
     * @param playerInfo cached {@link PlayerInfo}
     */
    public void remove(@NotNull PlayerInfo playerInfo) {
        this.remove(playerInfo.getUuid());
    }

    /**
     * Gets {@link PlayerInfo} of the player from the map
     * <br>
     * or creates new {@link PlayerInfo} and puts it in the map if it's not cached
     *
     * @param uniqueId player {@link UUID}
     * @param nickname player nickname
     * @return {@link PlayerInfo} of player
     */
    public @NotNull PlayerInfo getPlayerInfo(
            @NotNull UUID uniqueId,
            @NotNull String nickname
    ) {
        return this.map.computeIfAbsent(
                uniqueId,
                uuid -> new PlayerInfo(uuid, nickname)
        );
    }


    /**
     * Gets {@link PlayerInfo} of the player from the map
     * <br>
     * or creates new {@link PlayerInfo} and puts it in the map if it's not cached
     *
     * @param player the player
     * @return {@link PlayerInfo} of player
     */
    public @NotNull PlayerInfo getPlayerInfo(@NotNull Player player) {
        return this.map.computeIfAbsent(
                player.getUniqueId(),
                uuid -> new PlayerInfo(player)
        );
    }
}
