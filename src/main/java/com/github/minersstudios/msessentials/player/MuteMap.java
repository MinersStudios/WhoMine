package com.github.minersstudios.msessentials.player;

import com.github.minersstudios.mscore.utils.ChatUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.util.InstantTypeAdapter;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.minersstudios.msessentials.MSEssentials.getInstance;

/**
 * Mute map with {@link UUID} and its {@link Params}.
 * All mutes stored in the "config/minersstudios/MSEssentials/muted_players.json" file.
 *
 * @see Params
 */
public class MuteMap {
    private final File file;
    private final Map<UUID, Params> map = new ConcurrentHashMap<>();
    private final Gson gson;

    public MuteMap() {
        this.file = new File(getInstance().getPluginFolder(), "muted_players.json");
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                .setPrettyPrinting()
                .create();
        this.reloadMutes();
    }

    /**
     * Gets mute map
     *
     * @return map with {@link UUID} and its {@link Params}
     */
    public @NotNull @UnmodifiableView Map<UUID, Params> getMap() {
        return Collections.unmodifiableMap(this.map);
    }

    /**
     * Gets mute parameters
     *
     * @param player probably muted player
     * @return creation and expiration date, reason and source of mute
     */
    public @Nullable Params getParams(@NotNull OfflinePlayer player) {
        return this.map.get(player.getUniqueId());
    }

    /**
     * @param player probably muted player
     * @return True if the player is muted
     */
    @Contract("null -> false")
    public boolean isMuted(@Nullable OfflinePlayer player) {
        return player != null && this.map.containsKey(player.getUniqueId());
    }

    /**
     * Adds mute for the player
     *
     * @param player     player who will be muted
     * @param expiration date when the player will be unmuted
     * @param reason     mute reason
     * @param source     mute source, could be a player's nickname or CONSOLE
     */
    public void put(
            @NotNull OfflinePlayer player,
            @NotNull Instant expiration,
            @NotNull String reason,
            @NotNull String source
    ) {
        Instant created = Instant.now();
        UUID uuid = player.getUniqueId();

        this.map.put(uuid, Params.create(created, expiration, reason, source));
        this.saveFile();
    }

    /**
     * Removes mute from player
     *
     * @param player muted player
     */
    public void remove(@Nullable OfflinePlayer player) {
        if (player == null) return;
        this.map.remove(player.getUniqueId());
        this.saveFile();
    }

    /**
     * Reloads muted_players.json
     *
     * @throws RuntimeException if the file "muted_players.json" was not created successfully
     */
    public void reloadMutes() throws RuntimeException {
        this.map.clear();

        if (!this.file.exists()) {
            this.createFile();
        } else {
            try {
                Type mapType = new TypeToken<Map<UUID, Params>>() {}.getType();
                String json = Files.readString(this.file.toPath(), StandardCharsets.UTF_8);
                Map<UUID, Params> jsonMap = this.gson.fromJson(json, mapType);

                if (jsonMap == null) {
                    this.createBackupFile();
                    this.reloadMutes();
                    return;
                }

                jsonMap.forEach((uuid, params) -> {
                    if (params != null && params.isValidate()) {
                        this.map.put(uuid, params);
                    } else {
                        ChatUtils.sendError("Failed to read the player params : " + uuid.toString() + " in \"muted_players.json\"");
                    }
                });
            } catch (Exception e) {
                this.createBackupFile();
                this.reloadMutes();
            }
        }
    }

    private void createFile() throws RuntimeException {
        try {
            if (this.file.createNewFile()) {
                this.saveFile();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create a new \"muted_players.json\" file", e);
        }
    }

    private void createBackupFile() {
        File backupFile = new File(this.file.getParent(), this.file.getName() + ".OLD");
        try {
            Files.move(this.file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            this.saveFile();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create \"muted_players.json.OLD\" backup file", e);
        }
        ChatUtils.sendError("Failed to read the \"muted_players.json\" file, creating a new file");
    }

    private void saveFile() {
        try (var writer = new OutputStreamWriter(new FileOutputStream(this.file), StandardCharsets.UTF_8)) {
            this.gson.toJson(this.map, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save muted players", e);
        }
    }

    /**
     * Mute parameters, used in {@link MuteMap}
     * <br>
     * Parameters:
     * <ul>
     *     <li>created - date when the player was muted</li>
     *     <li>expiration - date when the player will be unmuted</li>
     *     <li>reason - mute reason</li>
     *     <li>source - mute source, could be a player's nickname or CONSOLE</li>
     * </ul>
     *
     * @see MuteMap
     */
    public static class Params {
        private final Instant created;
        private final Instant expiration;
        private final String reason;
        private final String source;

        private Params(
                Instant created,
                Instant expiration,
                String reason,
                String source
        ) {
            this.created = created;
            this.expiration = expiration;
            this.reason = reason;
            this.source = source;
        }

        /**
         * Creates a new {@link Params} with the specified parameters
         *
         * @param created    date when the player was muted
         * @param expiration date when the player will be unmuted
         * @param reason     mute reason
         * @param source     mute source, could be a player's nickname or CONSOLE
         * @return new {@link Params}
         */
        @Contract(value = "_, _, _, _ -> new")
        public static @NotNull Params create(
                @NotNull Instant created,
                @NotNull Instant expiration,
                @NotNull String reason,
                @NotNull String source
        ) {
            return new Params(created, expiration, reason, source);
        }

        /**
         * @return True if created, expiration, reason, source != null
         */
        public boolean isValidate() {
            return this.created != null
                    && this.expiration != null
                    && this.reason != null
                    && this.source != null;
        }

        /**
         * @return date when the player was muted
         */
        public Instant getCreated() {
            return this.created;
        }

        /**
         * @return date when the player will be unmuted
         */
        public Instant getExpiration() {
            return this.expiration;
        }

        /**
         * @return mute reason
         */
        public String getReason() {
            return this.reason;
        }

        /**
         * @return mute source, could be a player's nickname or CONSOLE
         */
        public String getSource() {
            return this.source;
        }
    }
}
