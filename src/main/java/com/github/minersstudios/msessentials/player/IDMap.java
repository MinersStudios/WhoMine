package com.github.minersstudios.msessentials.player;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.github.minersstudios.msessentials.MSEssentials.getInstance;

/**
 * ID map with {@link UUID} and its ID.
 * All ids stored in the "config/minersstudios/MSEssentials/ids.yml" file.
 */
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
    public @NotNull @UnmodifiableView Map<UUID, Integer> getMap() {
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
     * @return The number of ids in this map
     */
    public int size() {
        return this.map.size();
    }

    /**
     * @return True if this map contains no ids
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
     * @param id ID of player
     * @return True if the map contains the id of the player
     */
    public boolean containsID(@NotNull Integer id) {
        return this.map.containsValue(id);
    }

    /**
     * @return An unmodifiable view of the UUIDs contained in this map
     */
    public @NotNull @UnmodifiableView Set<UUID> uuidSet() {
        return Set.copyOf(this.map.keySet());
    }

    /**
     * @return An unmodifiable view of the ids contained in this map
     */
    public @NotNull @UnmodifiableView Collection<Integer> ids() {
        return Collections.unmodifiableCollection(this.map.values());
    }

    /**
     * @return An unmodifiable view of the mappings contained in this map
     */
    public @NotNull @UnmodifiableView Set<Map.Entry<UUID, Integer>> entrySet() {
        return Set.copyOf(this.map.entrySet());
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
     * Gets next ID, by the size of the ID map.
     * If the ID with that size already exists, returns the next ID.
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
        for (var key : configuration.getKeys(false)) {
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

    /**
     * Saves ids.yml file with the current map values
     */
    private void saveFile() {
        YamlConfiguration configuration = new YamlConfiguration();

        for (var entry : this.map.entrySet()) {
            configuration.set(entry.getKey().toString(), entry.getValue());
        }

        try {
            configuration.save(this.file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save ID map", e);
        }
    }
}
