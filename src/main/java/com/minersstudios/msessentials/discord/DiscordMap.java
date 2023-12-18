package com.minersstudios.msessentials.discord;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.msessentials.player.PlayerInfo;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Discord linking map with discord user id and its player's {@link Params}.
 * All mutes stored in the "config/minersstudios/MSEssentials/discord_links.json" file.
 *
 * @see Params
 */
public final class DiscordMap {
    private final MSPlugin<?> plugin;
    private final File file;
    private final Map<Long, Params> map;
    private final Map<Short, PlayerInfo> codeMap;

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Gson GSON =
            new GsonBuilder()
            .setPrettyPrinting()
            .create();

    /**
     * Discord linking map with discord user id and its player's {@link Params}.
     * Loads mutes from the file.
     */
    public DiscordMap(final @NotNull MSPlugin<?> plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getPluginFolder(), "discord_links.json");
        this.map = new ConcurrentHashMap<>();
        this.codeMap = new ConcurrentHashMap<>();

        this.reloadLinks();
    }

    /**
     * @param params The params of the linked player
     * @return The id of the linked discord user or -1 if not found
     */
    public long getId(final @NotNull Params params) {
        for (final var entry : this.map.entrySet()) {
            if (entry.getValue().equals(params)) {
                return entry.getKey();
            }
        }

        return -1L;
    }

    /**
     * @param id The id of the linked discord user
     * @return The params of the linked player
     */
    public @Nullable Params getParams(final long id) {
        return this.map.get(id);
    }

    /**
     * Links the player with the given id, uuid and nickname
     *
     * @param id     The id of the linking discord player
     * @param player The linking player
     * @see #put(long, UUID, String)
     * @see #put(long, Params)
     */
    public void put(
            final long id,
            final @NotNull Player player
    ) {
        this.put(id, player.getUniqueId(), player.getName());
    }

    /**
     * Links the player with the given id, uuid and nickname
     *
     * @param id       The id of the linking discord player
     * @param uuid     The uuid of the linking player
     * @param nickname The nickname of the linking player
     * @see #put(long, Params)
     */
    public void put(
            final long id,
            final @NotNull UUID uuid,
            final @NotNull String nickname
    ) {
        this.put(id, Params.create(uuid, nickname));
    }

    /**
     * Links the player with the params
     *
     * @param id     The id of the linking discord player
     * @param params The params of the linking player
     */
    public void put(
            final long id,
            final @NotNull Params params
    ) {
        for (final var entry : this.map.entrySet()) {
            if (entry.getValue().equals(params)) {
                this.map.remove(entry.getKey());
            }
        }

        this.map.put(id, params);
        this.saveFile();
    }

    /**
     * Generates a new code for the link request.
     * If the code is already in use, it will generate a new one.
     * The code will be removed after 5 minutes if it is not used.
     *
     * @param playerInfo The player info to generate code for
     * @return The generated code
     */
    public short generateCode(final @NotNull PlayerInfo playerInfo) {
        if (this.codeMap.containsValue(playerInfo)) {
            for (final var entry : this.codeMap.entrySet()) {
                if (entry.getValue().equals(playerInfo)) {
                    this.codeMap.remove(entry.getKey());
                }
            }
        }

        final short code = (short) (RANDOM.nextInt(9000) + 1000);

        if (this.codeMap.containsKey(code)) {
            return this.generateCode(playerInfo);
        }

        this.codeMap.put(code, playerInfo);
        this.plugin.runTaskLater(
                () -> this.codeMap.remove(code, playerInfo), 6000L
        );

        return code;
    }

    /**
     * Removes code from the map
     *
     * @param code The code to remove
     */
    public void removeCode(final short code) {
        this.codeMap.remove(code);
    }

    /**
     * @param code The code to validate
     * @return The linked player if the code is valid, otherwise null
     */
    public @Nullable PlayerInfo validateCode(final short code) {
        return this.codeMap.get(code);
    }

    /**
     * @param id The id to check
     * @return True if the discord user id is linked
     */
    public boolean containsId(final long id) {
        return this.map.containsKey(id);
    }

    /**
     * @param params The params to check
     * @return True if the player is linked
     */
    public boolean containsPlayer(final @NotNull Params params) {
        return this.getId(params) != -1L;
    }

    /**
     * Removes linked player by id
     *
     * @param id The id of the linked player
     */
    public void remove(final long id) {
        this.map.remove(id);
        this.saveFile();
    }

    /**
     * @return The number of linked players
     */
    public int size() {
        return this.map.size();
    }

    /**
     * @return True if this map contains no linked players
     */
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    /**
     * @return An unmodifiable view of the ids contained in this map
     */
    public @NotNull @UnmodifiableView Set<Long> idSet() {
        return Collections.unmodifiableSet(this.map.keySet());
    }

    /**
     * @return An unmodifiable view of the mappings contained in this map
     */
    public @NotNull @UnmodifiableView Set<Map.Entry<Long, Params>> entrySet() {
        return Collections.unmodifiableSet(this.map.entrySet());
    }

    /**
     * Reloads "discord_links.json" file
     */
    public void reloadLinks() {
        this.map.clear();

        if (!this.file.exists()) {
            this.createFile();
        } else {
            try {
                final Type mapType = new TypeToken<Map<Long, Params>>() {}.getType();
                final String json = Files.readString(this.file.toPath(), StandardCharsets.UTF_8);
                final Map<Long, Params> jsonMap = GSON.fromJson(json, mapType);

                if (jsonMap == null) {
                    this.createBackupFile();
                    this.reloadLinks();
                    return;
                }

                jsonMap.forEach((id, params) -> {
                    if (params != null && params.isValidate()) {
                        this.map.put(id, params);
                    } else {
                        this.plugin.getLogger().severe("Failed to read the discord params : " + id.toString() + " in \"discord_links.json\"");
                    }
                });
            } catch (final Exception ignored) {
                this.createBackupFile();
                this.reloadLinks();
            }
        }
    }

    /**
     * Creates a new "discord_links.json" file
     */
    private void createFile() {
        try {
            if (
                    this.file.getParentFile().mkdirs()
                    && this.file.createNewFile()
            ) {
                this.saveFile();
            }
        } catch (final IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to create a new \"discord_links.json\" file", e);
        }
    }

    /**
     * Creates a backup file of the "discord_links.json" file
     */
    private void createBackupFile() {
        final File backupFile = new File(this.file.getParent(), this.file.getName() + ".OLD");

        try {
            Files.move(this.file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            this.saveFile();
        } catch (final IOException e) {
            this.plugin.getLogger().log(
                    Level.SEVERE,
                    "Failed to create \"discord_links.json.OLD\" backup file",
                    e
            );
        }

        this.plugin.getLogger().severe("Failed to read the \"discord_links.json\" file, creating a new file");
    }

    /**
     * Saves the links map to "discord_links.json" file
     */
    private void saveFile() {
        try (final var writer = new OutputStreamWriter(new FileOutputStream(this.file), StandardCharsets.UTF_8)) {
            GSON.toJson(this.map, writer);
        } catch (final IOException e) {
            this.plugin.getLogger().log(
                    Level.SEVERE,
                    "Failed to save \"discord_links.json\" file",
                    e
            );
        }
    }

    /**
     * Discord parameters, used in {@link DiscordMap}
     * <br>
     * Parameters:
     * <ul>
     *     <li>{@link UUID} - player uuid</li>
     *     <li>{@link String} - player nickname</li>
     * </ul>
     *
     * @see DiscordMap
     */
    public static class Params {
        private final UUID uuid;
        private final String nickname;

        private Params(
                final UUID uuid,
                final String nickname
        ) {
            this.uuid = uuid;
            this.nickname = nickname;
        }

        /**
         * Creates a new {@link Params} with the specified parameters
         *
         * @param uuid     Player uuid
         * @param nickname Player nickname
         * @return New {@link Params}
         */
        @Contract("_, _ -> new")
        public static @NotNull Params create(
                final @NotNull UUID uuid,
                final @NotNull String nickname
        ) {
            return new Params(uuid, nickname);
        }

        /**
         * @return Player uuid
         */
        public @NotNull UUID getUuid() {
            return this.uuid;
        }

        /**
         * @return Player nickname
         */
        public @NotNull String getNickname() {
            return this.nickname;
        }

        /**
         * @return True if uuid and nickname are not null
         */
        public boolean isValidate() {
            return this.uuid != null
                    && this.nickname != null;
        }

        /**
         * @param params {@link Params} to compare
         * @return True if uuid and nickname are equals
         */
        public boolean equals(final @NotNull Params params) {
            return this.uuid.equals(params.uuid)
                    && this.nickname.equals(params.nickname);
        }
    }
}
