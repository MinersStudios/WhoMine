package com.github.minersstudios.msessentials.player;

import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.msessentials.MSEssentials;
import com.github.minersstudios.msessentials.player.skin.Skin;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.github.minersstudios.msessentials.MSEssentials.getInstance;

/**
 * Player file with player data, settings, etc.
 * All files stored in the "config/minersstudios/MSEssentials/players" folder.
 * File name is player {@link UUID}.
 *
 * @see PlayerInfo
 */
public class PlayerFile {
    private final @NotNull File dataFile;
    private final @NotNull YamlConfiguration yamlConfiguration;

    private @NotNull PlayerName playerName;
    private @NotNull Pronouns pronouns;
    private final @NotNull List<String> ipList;
    private final @NotNull List<Skin> skins = new ArrayList<>(18);
    private @NotNull GameMode gameMode;
    private double health;
    private int air;
    private final @NotNull PlayerSettings playerSettings;
    private @NotNull Instant firstJoin;
    private @Nullable Location lastLeaveLocation;
    private @Nullable Location lastDeathLocation;

    public PlayerFile(
            @NotNull File dataFile,
            @NotNull YamlConfiguration yamlConfiguration
    ) {
        this.dataFile = dataFile;
        this.yamlConfiguration = yamlConfiguration;

        this.playerName = PlayerName.create(
                yamlConfiguration.getString("name.nickname", ChatUtils.serializeLegacyComponent(Component.translatable("ms.player.name.nickname"))),
                yamlConfiguration.getString("name.first-name", ChatUtils.serializeLegacyComponent(Component.translatable("ms.player.name.first_name"))),
                yamlConfiguration.getString("name.last-name", ChatUtils.serializeLegacyComponent(Component.translatable("ms.player.name.last_name"))),
                yamlConfiguration.getString("name.patronymic", ChatUtils.serializeLegacyComponent(Component.translatable("ms.player.name.patronymic")))
        );
        this.pronouns = Pronouns.valueOf(yamlConfiguration.getString("pronouns", "HE"));
        this.ipList = yamlConfiguration.getStringList("ip-list");
        this.gameMode = GameMode.valueOf(yamlConfiguration.getString("game-params.game-mode", "SURVIVAL"));
        this.health = yamlConfiguration.getDouble("game-params.health", 20.0d);
        this.air = yamlConfiguration.getInt("game-params.air", 300);
        this.playerSettings = new PlayerSettings(this);
        this.firstJoin = Instant.ofEpochMilli(yamlConfiguration.getLong("first-join", System.currentTimeMillis()));

        World overworld = MSEssentials.getOverworld();
        Location spawnLocation = overworld.getSpawnLocation();

        ConfigurationSection lastLeaveSection = this.yamlConfiguration.getConfigurationSection("locations.last-leave-location");
        String lastLeaveWorldName = this.yamlConfiguration.getString("locations.last-leave-location.world", "");
        World lastLeaveWorld = Bukkit.getWorld(lastLeaveWorldName);
        this.lastLeaveLocation = lastLeaveSection == null
                ? null
                : new Location(
                lastLeaveWorld == null ? overworld : lastLeaveWorld,
                lastLeaveSection.getDouble("x", spawnLocation.getX()),
                lastLeaveSection.getDouble("y", spawnLocation.getY()),
                lastLeaveSection.getDouble("z", spawnLocation.getZ()),
                (float) lastLeaveSection.getDouble("yaw", spawnLocation.getYaw()),
                (float) lastLeaveSection.getDouble("pitch", spawnLocation.getPitch())
        );

        ConfigurationSection lastDeathSection = this.yamlConfiguration.getConfigurationSection("locations.last-death-location");
        String lastDeathWorldName = this.yamlConfiguration.getString("locations.last-death-location.world", "");
        World lastDeathWorld = Bukkit.getWorld(lastDeathWorldName);
        this.lastDeathLocation = lastDeathSection == null
                ? null
                : new Location(
                lastDeathWorld == null ? overworld : lastDeathWorld,
                lastDeathSection.getDouble("x"),
                lastDeathSection.getDouble("y"),
                lastDeathSection.getDouble("z"),
                (float) lastDeathSection.getDouble("yaw"),
                (float) lastDeathSection.getDouble("pitch")
        );

        ConfigurationSection skinsSection = this.yamlConfiguration.getConfigurationSection("skins");
        if (skinsSection != null) {
            for (var key : skinsSection.getKeys(false)) {
                ConfigurationSection skinSection = skinsSection.getConfigurationSection(key);

                if (skinSection == null) continue;

                String name = skinSection.getName();
                String value = skinSection.getString("value");
                String signature = skinSection.getString("signature");

                if (value == null || signature == null) continue;

                this.skins.add(Skin.create(name, value, signature));
            }
        }
    }

    public static @NotNull PlayerFile loadConfig(
            @NotNull UUID uniqueId,
            @Nullable String nickname
    ) {
        String child = "players/" + ("$Console".equals(nickname) ? "console" : uniqueId) + ".yml";
        File dataFile = new File(getInstance().getPluginFolder(), child);
        return new PlayerFile(dataFile, YamlConfiguration.loadConfiguration(dataFile));
    }

    public @NotNull File getFile() {
        return this.dataFile;
    }

    public @NotNull YamlConfiguration getYamlConfiguration() {
        return this.yamlConfiguration;
    }

    public @NotNull PlayerName getPlayerName() {
        return this.playerName;
    }

    public void setPlayerName(@NotNull PlayerName playerName) {
        this.playerName = playerName;
        this.updateName();
    }

    public void updateName() {
        ConfigurationSection section = this.yamlConfiguration.createSection("name");
        section.set("nickname", this.playerName.getNickname());
        section.set("first-name", this.playerName.getFirstName());
        section.set("last-name", this.playerName.getLastName());
        section.set("patronymic", this.playerName.getPatronymic());
    }

    public boolean isNoName() {
        return this.yamlConfiguration.getString("name.first-name") == null
                || this.yamlConfiguration.getString("name.last-name") == null
                || this.yamlConfiguration.getString("name.patronymic") == null;
    }

    public @NotNull Pronouns getPronouns() {
        return this.pronouns;
    }

    public void setPronouns(@NotNull Pronouns pronouns) {
        this.pronouns = pronouns;
        this.yamlConfiguration.set("pronouns", pronouns.name());
    }

    public void addIp(@Nullable String ip) {
        this.ipList.add(ip);
        this.saveIpList();
    }

    public @NotNull List<String> getIpList() {
        return this.ipList;
    }

    public void saveIpList() {
        this.yamlConfiguration.set("ip-list", this.ipList);
    }

    public @NotNull List<Skin> getSkins() {
        return List.copyOf(this.skins);
    }

    public @Nullable Skin getSkin(int index) {
        return this.skins.get(index);
    }

    public @Nullable Skin getSkin(@NotNull String name) {
        return this.skins.stream()
                .filter(skin -> skin.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public int getSkinIndex(@NotNull Skin skin) {
        return this.skins.indexOf(skin);
    }

    public int getSkinIndex(@NotNull String name) {
        return this.skins.indexOf(this.getSkin(name));
    }

    public boolean setSkin(
            int index,
            @NotNull Skin skin
    ) {
        String sectionName = "skins." + index;

        this.skins.set(index, skin);
        this.yamlConfiguration.set(sectionName + ".value", skin.getValue());
        this.yamlConfiguration.set(sectionName + ".signature", skin.getSignature());
        this.save();

        return true;
    }

    public boolean addSkin(@NotNull Skin skin) {
        if (!this.hasAvailableSkinSlot()) return false;

        String sectionName = "skins." + skin.getName();

        this.skins.add(skin);
        this.yamlConfiguration.set(sectionName + ".value", skin.getValue());
        this.yamlConfiguration.set(sectionName + ".signature", skin.getSignature());
        this.save();

        return true;
    }

    public void removeSkin(int index) {
        Skin skin = this.skins.get(index);
        String sectionName = "skins." + skin.getName();

        this.skins.remove(skin);
        this.yamlConfiguration.set(sectionName + ".value", null);
        this.yamlConfiguration.set(sectionName + ".signature", null);
        this.yamlConfiguration.set(sectionName, null);
        this.save();
    }

    public boolean renameSkin(
            @NotNull Skin skin,
            @NotNull String newName
    ) {
        if (!this.skins.contains(skin)) return false;

        this.yamlConfiguration.set("skins." + skin.getName(), newName);
        this.save();

        return true;
    }

    public boolean hasAvailableSkinSlot() {
        return this.skins.size() < 18;
    }

    public boolean containsSkin(@NotNull Skin skin) {
        return this.skins.contains(skin);
    }

    public boolean containsSkin(@NotNull String name) {
        return this.skins.stream().anyMatch(skin -> skin.getName().equalsIgnoreCase(name));
    }

    public @NotNull GameMode getGameMode() {
        return this.gameMode;
    }

    public void setGameMode(@NotNull GameMode gameMode) {
        this.gameMode = gameMode;
        this.yamlConfiguration.set("game-params.game-mode", gameMode.name());
    }

    public double getHealth() {
        return this.health;
    }

    public void setHealth(double health) {
        this.health = health;
        this.yamlConfiguration.set("game-params.health", health);
    }

    public int getAir() {
        return this.air;
    }

    public void setAir(int air) {
        this.air = air;
        this.yamlConfiguration.set("game-params.air", air);
    }

    public @NotNull PlayerSettings getPlayerSettings() {
        return this.playerSettings;
    }

    public @NotNull Instant getFirstJoin() {
        return this.firstJoin;
    }

    public void setFirstJoin(@NotNull Instant firstJoin) {
        this.firstJoin = firstJoin;
        this.yamlConfiguration.set("first-join", firstJoin.toEpochMilli());
    }

    public @Nullable Location getLastLeaveLocation() {
        return this.lastLeaveLocation;
    }

    public void setLastLeaveLocation(@Nullable Location leaveLocation) {
        this.lastLeaveLocation = leaveLocation;
        setLocation(
                this.yamlConfiguration.createSection("locations.last-leave-location"),
                leaveLocation
        );
    }

    public @Nullable Location getLastDeathLocation() {
        return this.lastDeathLocation;
    }

    public void setLastDeathLocation(@Nullable Location deathLocation) {
        this.lastDeathLocation = deathLocation;
        setLocation(
                this.yamlConfiguration.createSection("locations.last-death-location"),
                deathLocation
        );
    }

    private static void setLocation(
            @NotNull ConfigurationSection section,
            @Nullable Location location
    ) {
        boolean isNull = location == null;

        if (!isNull) {
            if (location.getWorld().equals(MSEssentials.getWorldDark())) {
                throw new IllegalArgumentException("The world cannot be world_dark");
            }
        }

        section.set("world", isNull ? null : location.getWorld().getName());
        section.set("x", isNull ? null : location.getX());
        section.set("y", isNull ? null : location.getY());
        section.set("z", isNull ? null : location.getZ());
        section.set("yaw", isNull ? null : location.getYaw());
        section.set("pitch", isNull ? null : location.getPitch());
    }

    public boolean exists() {
        return this.dataFile.exists();
    }

    public void save() {
        try {
            this.yamlConfiguration.save(this.dataFile);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
