package com.github.minersstudios.mscore.utils;

import com.github.minersstudios.mscore.MSCore;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

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
        PlayerInfo.fromMap(player).setSitting(location, message);
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
        PlayerInfo.fromMap(player).unsetSitting(message);
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
     * Sets player's skin with specified value and signature.
     * Use {@link PlayerUtils#reloadPlayer(Player)} to reload the player
     * and make the skin visible.
     *
     * @param player    Player whose skin will be set
     * @param value     Value of the skin
     * @param signature Signature of the skin
     */
    public static void setSkin(
            @NotNull Player player,
            @NotNull String value,
            @NotNull String signature
    ) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        GameProfile gameProfile = craftPlayer.getProfile();
        PropertyMap propertyMap = gameProfile.getProperties();
        Property newProperty = new Property("textures", value, signature);

        if (propertyMap.containsKey("textures")) {
            Property oldProperty = propertyMap.get("textures").iterator().next();
            propertyMap.remove("textures", oldProperty);
        }

        propertyMap.put("textures", newProperty);
    }

    /**
     * Reloads player, like if he was joining the server
     *
     * @param player Player whose will be reloaded
     */
    public static void reloadPlayer(@NotNull Player player) {
        MSCore plugin = MSCore.getInstance();
        Location location = player.getLocation();
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerPlayer serverPlayer = craftPlayer.getHandle();
        ServerGamePacketListenerImpl playerConnection = serverPlayer.connection;
        ServerPlayerGameMode gameMode = serverPlayer.gameMode;
        ServerLevel level = serverPlayer.serverLevel();

        ClientboundPlayerInfoRemovePacket removePacket = new ClientboundPlayerInfoRemovePacket(
                List.of(player.getUniqueId())
        );
        ClientboundPlayerInfoUpdatePacket addPacket = new ClientboundPlayerInfoUpdatePacket(
                ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER,
                serverPlayer
        );
        ClientboundRespawnPacket respawnPacket = new ClientboundRespawnPacket(
                level.dimensionTypeId(),
                level.dimension(),
                BiomeManager.obfuscateSeed(level.getSeed()),
                gameMode.getGameModeForPlayer(),
                gameMode.getPreviousGameModeForPlayer(),
                level.isDebug(),
                level.isFlat(),
                (byte) 0xFF,
                serverPlayer.getLastDeathLocation(),
                serverPlayer.portalCooldown
        );
        ClientboundPlayerPositionPacket positionPacket = new ClientboundPlayerPositionPacket(
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch(),
                Collections.emptySet(),
                0
        );
        ClientboundSetCarriedItemPacket heldItemPacket = new ClientboundSetCarriedItemPacket(
                player.getInventory().getHeldItemSlot()
        );

        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
            if (onlinePlayer.equals(player)) return;
            onlinePlayer.hidePlayer(plugin, player);
            onlinePlayer.showPlayer(plugin, player);
        });

        playerConnection.send(removePacket);
        playerConnection.send(addPacket);
        playerConnection.send(respawnPacket);
        playerConnection.send(positionPacket);
        playerConnection.send(heldItemPacket);

        craftPlayer.updateScaledHealth();
        serverPlayer.containerMenu.sendAllDataToRemote();

        if (player.isOp()) {
           Bukkit.getScheduler().runTask(plugin, () -> {
               player.setOp(false);
               player.setOp(true);
           });
        }
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
