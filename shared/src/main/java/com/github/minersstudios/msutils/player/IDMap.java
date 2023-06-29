package com.github.minersstudios.msutils.player;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.github.minersstudios.msutils.MSUtils.getInstance;

@SuppressWarnings("unused")
public class IDMap {
    private final File file;
    private final Map<UUID, Integer> map = new HashMap<>();

    public IDMap() {
        this.file = new File(getInstance().getPluginFolder(), "ids.yml");
        this.reloadIds();
    }

    /**
     * Gets ID map
     *
     * @return map with {@link UUID} and its ID
     */
    public @NotNull Map<UUID, Integer> getMap() {
        return Collections.unmodifiableMap(this.map);
    }

    /**
     * Gets player ID
     *
     * @param uuid       player {@link UUID}
     * @param addPlayer  if true, the new player will be added with the next ID
     * @param zeroIfNull if true and the player is not found, the return value will be 0, not -1
     * @return -1 if the player is not found
     */
    public int get(
            @NotNull UUID uuid,
            boolean addPlayer,
            boolean zeroIfNull
    ) {
        return this.map.getOrDefault(uuid, addPlayer ? this.addPlayer(uuid) : zeroIfNull ? 0 : -1);
    }

    /**
     * Sets player ID
     *
     * @param uuid player {@link UUID}
     * @param id   ID to set
     */
    public void put(
            @NotNull UUID uuid,
            int id
    ) {
        this.map.put(uuid, id);
        this.saveFile();
    }

    /**
     * Adds player with next ID
     *
     * @param uuid player {@link UUID}
     * @return next player ID, or -1 if the player already has an ID
     */
    public int addPlayer(@NotNull UUID uuid) {
        return this.map.computeIfAbsent(uuid, unused -> this.nextID());
    }

    /**
     * Gets next ID, by the size of the ID map
     * <br>
     * If the ID with that size already exists, returns the next ID
     *
     * @return next player ID
     */
    public int nextID() {
        Set<Integer> usedIDs = new HashSet<>(this.map.values());
        for (int id = 0; id < Integer.MAX_VALUE; id++) {
            if (!usedIDs.contains(id)) return id;
        }
        throw new IllegalStateException("No available ID found.");
    }

    /**
     * Gets {@link UUID} associated with the ID
     *
     * @param id player ID
     * @return {@link UUID} from ID
     */
    public @Nullable UUID getUUIDByID(int id) {
        return this.map.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(id))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets {@link UUID} associated with the ID
     *
     * @param stringId player ID string
     * @return {@link UUID} from ID string
     */
    public @Nullable UUID getUUIDByID(@NotNull String stringId) {
        int id = this.parseID(stringId);
        return id != -1 ? this.getUUIDByID(id) : null;
    }

    /**
     * Gets {@link OfflinePlayer} associated with the ID
     *
     * @param id player ID
     * @return {@link OfflinePlayer} from ID
     */
    public @Nullable OfflinePlayer getPlayerByID(int id) {
        UUID uuid = this.getUUIDByID(id);
        return uuid == null ? null : Bukkit.getOfflinePlayer(uuid);
    }

    /**
     * Gets {@link OfflinePlayer} associated with the ID
     *
     * @param stringId player ID string
     * @return {@link OfflinePlayer} from ID string
     */
    public @Nullable OfflinePlayer getPlayerByID(@NotNull String stringId) {
        int id = this.parseID(stringId);
        return id != -1 ? this.getPlayerByID(id) : null;
    }

    /**
     * Reloads ids.yml
     */
    public void reloadIds() {
        this.map.clear();
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(this.file);
        for (String key : configuration.getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            int id = configuration.getInt(key);
            this.map.put(uuid, id);
        }
    }

    /**
     * Parses ID from the string
     *
     * @param stringId ID string
     * @return int ID from string or -1 if
     */
    public int parseID(@NotNull String stringId) {
        try {
            return Integer.parseInt(stringId);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void saveFile() {
        YamlConfiguration configuration = new YamlConfiguration();

        for (Map.Entry<UUID, Integer> entry : this.map.entrySet()) {
            configuration.set(entry.getKey().toString(), entry.getValue());
        }

        try {
            configuration.save(this.file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save ID map", e);
        }
    }
}
