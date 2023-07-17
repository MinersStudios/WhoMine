package com.github.minersstudios.msessentials.player.map;

import com.github.minersstudios.mscore.logger.MSLogger;
import com.github.minersstudios.msessentials.MSEssentials;
import com.github.minersstudios.msessentials.utils.IDUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * ID map with {@link UUID} and its ID.
 * All ids stored in the "config/minersstudios/MSEssentials/ids.json" file.
 */
public class IDMap {
    private final File file;
    private final Map<UUID, Integer> map = new ConcurrentHashMap<>();

    private static final Gson GSON =
            new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public IDMap() {
        this.file = new File(MSEssentials.getInstance().getPluginFolder(), "ids.json");
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
     * Gets player ID by uuid
     *
     * @param uuid       Player {@link UUID}
     * @param addPlayer  If true, the new player will be added with the next ID
     * @param zeroIfNull If true and the player is not found, the return value will be 0, not -1
     * @return -1 if the player is not found
     */
    public int getID(
            @NotNull UUID uuid,
            boolean addPlayer,
            boolean zeroIfNull
    ) {
        return this.map.getOrDefault(uuid, addPlayer ? this.addPlayer(uuid) : zeroIfNull ? 0 : -1);
    }

    /**
     * Gets player uuid by id
     *
     * @param id Player ID
     * @return {@link UUID} of player with this ID or null if not found
     */
    public @Nullable UUID getUUID(int id) {
        return this.map.entrySet().stream()
                .filter(entry -> entry.getValue() == id)
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    /**
     * Gets {@link UUID} associated with the ID
     *
     * @param stringId player ID string
     * @return {@link UUID} from ID string
     */
    public @Nullable UUID getUUID(@NotNull String stringId) {
        int id = IDUtils.parseID(stringId);
        return id != -1 ? this.getUUID(id) : null;
    }

    /**
     * Gets {@link OfflinePlayer} associated with the ID
     *
     * @param id player ID
     * @return {@link OfflinePlayer} from ID
     */
    public @Nullable OfflinePlayer getPlayerByID(int id) {
        UUID uuid = this.getUUID(id);
        return uuid == null ? null : Bukkit.getOfflinePlayer(uuid);
    }

    /**
     * Gets {@link OfflinePlayer} associated with the ID
     *
     * @param stringId player ID string
     * @return {@link OfflinePlayer} from ID string
     */
    public @Nullable OfflinePlayer getPlayerByID(@NotNull String stringId) {
        int id = IDUtils.parseID(stringId);
        return id != -1 ? this.getPlayerByID(id) : null;
    }

    /**
     * Sets player ID
     *
     * @param uuid Player {@link UUID}
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
        var usedIDs = new HashSet<>(this.map.values());

        for (int id = 0; id < Integer.MAX_VALUE; id++) {
            if (!usedIDs.contains(id)) return id;
        }

        throw new IllegalStateException("No available ID found.");
    }

    /**
     * Reloads ids.json file
     */
    public void reloadIds() {
        this.map.clear();

        if (!this.file.exists()) {
            this.createFile();
        } else {
            try {
                Type mapType = new TypeToken<Map<UUID, Integer>>() {}.getType();
                String json = Files.readString(this.file.toPath(), StandardCharsets.UTF_8);
                Map<UUID, Integer> jsonMap = GSON.fromJson(json, mapType);

                if (jsonMap == null) {
                    this.createBackupFile();
                    this.reloadIds();
                    return;
                }

                jsonMap.forEach((uuid, id) -> {
                    if (id != null) {
                        this.map.put(uuid, id);
                    } else {
                        MSLogger.severe("Failed to read the player id : " + uuid.toString() + " in \"ids.json\"");
                    }
                });
            } catch (Exception e) {
                this.createBackupFile();
                this.reloadIds();
            }
        }
    }

    /**
     * Creates a new "ids.json" file
     */
    private void createFile() {
        try {
            if (this.file.createNewFile()) {
                this.saveFile();
            }
        } catch (IOException e) {
            MSLogger.log(Level.SEVERE, "Failed to create a new \"ids.json\" file", e);
        }
    }

    /**
     * Creates a backup file of the "ids.json" file
     */
    private void createBackupFile() {
        File backupFile = new File(this.file.getParent(), this.file.getName() + ".OLD");

        try {
            Files.move(this.file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            this.saveFile();
        } catch (IOException e) {
            MSLogger.log(Level.SEVERE, "Failed to create \"ids.json.OLD\" backup file", e);
        }

        MSLogger.log(Level.SEVERE, "Failed to read the \"ids.json\" file, creating a new file");
    }

    /**
     * Saves the mute map to the "ids.json" file
     */
    private void saveFile() {
        try (var writer = new OutputStreamWriter(new FileOutputStream(this.file), StandardCharsets.UTF_8)) {
            GSON.toJson(this.map, writer);
        } catch (IOException e) {
            MSLogger.log(Level.SEVERE, "Failed to save ids", e);
        }
    }
}
