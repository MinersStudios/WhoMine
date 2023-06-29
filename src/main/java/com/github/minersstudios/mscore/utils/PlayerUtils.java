package com.github.minersstudios.mscore.utils;

import com.github.minersstudios.mscore.MSCore;
import com.github.minersstudios.msessentials.MSEssentials;
import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import net.kyori.adventure.text.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public final class PlayerUtils {
    public static final @NotNull String UUID_URL = "https://api.mojang.com/users/profiles/minecraft/";

    @Contract(value = " -> fail")
    private PlayerUtils() {
        throw new AssertionError("Utility class");
    }

    /**
     * Sets player to a seated position underneath him
     *
     * @param player player
     */
    public static void setSitting(@NotNull Player player) {
        setSitting(player, player.getLocation(), null);
    }

    /**
     * Sets player to a seated position in specified location
     *
     * @param player   Player
     * @param location Location where the player will sit
     */
    public static void setSitting(
            @NotNull Player player,
            @NotNull Location location
    ) {
        setSitting(player, location, null);
    }

    /**
     * Sets player to a seated position in specified location with message
     *
     * @param player   Player
     * @param location Location where the player will sit
     * @param message  Message
     */
    public static void setSitting(
            @NotNull Player player,
            @NotNull Location location,
            @Nullable Component message
    ) {
        MSEssentials.getConfigCache().playerInfoMap.getPlayerInfo(player).setSitting(location, message);
    }

    /**
     * Unsets the sitting position of the player
     *
     * @param player  Player who is currently sitting
     */
    public static void unsetSitting(@NotNull Player player) {
        unsetSitting(player, null);
    }

    /**
     * Unsets the sitting position of the player with message
     *
     * @param player  Player who is currently sitting
     * @param message Message
     */
    public static void unsetSitting(
            @NotNull Player player,
            @Nullable Component message
    ) {
        MSEssentials.getConfigCache().playerInfoMap.getPlayerInfo(player).unsetSitting(message);
    }

    /**
     * @param offlinePlayer Offline player whose data will be loaded
     * @return Online player from offline player
     */
    public static @Nullable Player loadPlayer(@NotNull OfflinePlayer offlinePlayer) {
        if (!offlinePlayer.hasPlayedBefore()) return null;

        GameProfile profile = new GameProfile(
                offlinePlayer.getUniqueId(),
                offlinePlayer.getName() != null
                        ? offlinePlayer.getName()
                        : offlinePlayer.getUniqueId().toString()
        );
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        ServerLevel worldServer = server.getLevel(Level.OVERWORLD);

        if (worldServer == null) return null;

        Player online = new ServerPlayer(server, worldServer, profile).getBukkitEntity();

        online.loadData();
        return online;
    }

    /**
     * Gets UUID from player nickname
     *
     * @param nickname Player nickname
     * @return Player UUID
     */
    public static @Nullable UUID getUUID(@NotNull String nickname) {
        Map<String, UUID> map = MSCore.getCache().playerUUIDs;
        UUID uuid = map.get(nickname);

        if (uuid != null) return uuid;

        if (Bukkit.getOnlineMode()) {
            try {
                URL url = new URL(UUID_URL + nickname);
                String jsonString = IOUtils.toString(url, Charset.defaultCharset());

                if (jsonString.isEmpty()) return null;

                JSONObject jsonObject = (JSONObject) JSONValue.parseWithException(jsonString);
                String uuidString = jsonObject.get("id").toString();
                uuid = UUID.fromString(
                        uuidString.replaceFirst(
                                "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                                "$1-$2-$3-$4-$5"
                        )
                );
            } catch (Exception ignored) {
                return null;
            }
        } else {
            byte[] bytes = ("OfflinePlayer:" + nickname).getBytes(Charsets.UTF_8);
            uuid = UUID.nameUUIDFromBytes(bytes);
        }

        map.put(nickname, uuid);
        return uuid;
    }

    /**
     * Gets offline player by nickname
     *
     * @param nickname Player nickname
     * @return Offline player
     */
    public static @Nullable OfflinePlayer getOfflinePlayerByNick(@NotNull String nickname) {
        UUID uuid = getUUID(nickname);
        return uuid != null
                ? getOfflinePlayer(uuid, nickname)
                : null;
    }

    /**
     * Gets offline player by uuid and nickname
     *
     * @param uuid Player unique id
     * @param name Player nickname
     * @return Offline player
     */
    public static @NotNull OfflinePlayer getOfflinePlayer(
            @NotNull UUID uuid,
            @NotNull String name
    ) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        if (offlinePlayer.getName() == null) {
            CraftServer craftServer = (CraftServer) Bukkit.getServer();
            GameProfile gameProfile = new GameProfile(uuid, name);
            return craftServer.getOfflinePlayer(gameProfile);
        }
        return offlinePlayer;
    }
}
