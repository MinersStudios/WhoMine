package com.minersstudios.msessentials.player;

import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.skin.Skin;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.*;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.logging.Level;

import static com.minersstudios.mscore.language.LanguageRegistry.Strings.*;

/**
 * Player file with player data, settings, etc.
 * All files stored in the "config/minersstudios/MSEssentials/players" folder.
 * File name is player {@link UUID}.
 *
 * @see PlayerInfo
 */
@SuppressWarnings("UnusedReturnValue")
public final class PlayerFile {
    private final @NotNull MSEssentials plugin;
    private final @NotNull File file;
    private final @NotNull YamlConfiguration config;

    private @NotNull PlayerName playerName;
    private @NotNull Pronouns pronouns;
    private final @NotNull List<String> ipList;
    private final @NotNull List<Skin> skins;
    private @NotNull GameMode gameMode;
    private double health;
    private int air;
    private final @NotNull PlayerSettings playerSettings;
    private @NotNull Instant firstJoin;
    private @Nullable Location lastLeaveLocation;
    private @Nullable Location lastDeathLocation;

    private static final int MAX_SKINS = 18;

    private PlayerFile(
            final @NotNull MSEssentials plugin,
            final @NotNull File file,
            final @NotNull YamlConfiguration config
    ) {
        this.plugin = plugin;
        this.file = file;
        this.config = config;

        this.playerName = PlayerName.create(
                config.getString("name.nickname", PLAYER_NAME_NICKNAME),
                config.getString("name.first-name", PLAYER_NAME_FIRST_NAME),
                config.getString("name.last-name", PLAYER_NAME_LAST_NAME),
                config.getString("name.patronymic", PLAYER_NAME_PATRONYMIC)
        );
        this.pronouns = Pronouns.valueOf(config.getString("pronouns", "HE"));
        this.ipList = config.getStringList("ip-list");
        this.gameMode = GameMode.valueOf(config.getString("game-params.game-mode", "SURVIVAL"));
        this.health = config.getDouble("game-params.health", 20.0d);
        this.air = config.getInt("game-params.air", 300);
        this.firstJoin = Instant.ofEpochMilli(config.getLong("first-join", System.currentTimeMillis()));

        final ConfigurationSection lastLeaveSection = this.config.getConfigurationSection("locations.last-leave-location");
        final String lastLeaveWorldName = this.config.getString("locations.last-leave-location.world", "");
        final World lastLeaveWorld = Bukkit.getWorld(lastLeaveWorldName);
        final Location spawnLocation = plugin.getConfiguration().getSpawnLocation();
        final World spawnWorld = spawnLocation.getWorld();

        this.lastLeaveLocation = lastLeaveSection == null
                ? null
                : new Location(
                lastLeaveWorld == null ? spawnWorld : lastLeaveWorld,
                lastLeaveSection.getDouble("x", spawnLocation.getX()),
                lastLeaveSection.getDouble("y", spawnLocation.getY()),
                lastLeaveSection.getDouble("z", spawnLocation.getZ()),
                (float) lastLeaveSection.getDouble("yaw", spawnLocation.getYaw()),
                (float) lastLeaveSection.getDouble("pitch", spawnLocation.getPitch())
        );

        final ConfigurationSection lastDeathSection = this.config.getConfigurationSection("locations.last-death-location");
        final String lastDeathWorldName = this.config.getString("locations.last-death-location.world", "");
        final World lastDeathWorld = Bukkit.getWorld(lastDeathWorldName);
        this.lastDeathLocation = lastDeathSection == null
                ? null
                : new Location(
                lastDeathWorld == null ? spawnWorld : lastDeathWorld,
                lastDeathSection.getDouble("x"),
                lastDeathSection.getDouble("y"),
                lastDeathSection.getDouble("z"),
                (float) lastDeathSection.getDouble("yaw"),
                (float) lastDeathSection.getDouble("pitch")
        );

        this.skins = new ObjectArrayList<>(MAX_SKINS);
        this.skins.addAll(this.deserializeSkinsSection());

        this.playerSettings = new PlayerSettings(this);
    }

    public static @NotNull PlayerFile loadConfig(
            final @NotNull MSEssentials plugin,
            final @NotNull UUID uniqueId,
            final @Nullable String nickname
    ) {
        final File dataFile = new File(
                plugin.getPluginFolder(),
                "players/" + ("$Console".equals(nickname) ? "console" : uniqueId) + ".yml"
        );
        return new PlayerFile(
                plugin,
                dataFile,
                YamlConfiguration.loadConfiguration(dataFile)
        );
    }

    public @NotNull MSEssentials getPlugin() {
        return this.plugin;
    }

    public @NotNull File getFile() {
        return this.file;
    }

    public @NotNull YamlConfiguration getConfig() {
        return this.config;
    }

    public @NotNull PlayerName getPlayerName() {
        return this.playerName;
    }

    public void setPlayerName(final @NotNull PlayerName playerName) {
        this.playerName = playerName;
        this.updateName();
    }

    public void updateName() {
        final ConfigurationSection section = this.config.createSection("name");

        section.set("nickname", this.playerName.getNickname());
        section.set("first-name", this.playerName.getFirstName());
        section.set("last-name", this.playerName.getLastName());
        section.set("patronymic", this.playerName.getPatronymic());
    }

    public boolean isNoName() {
        return this.config.getString("name.first-name") == null
                || this.config.getString("name.last-name") == null
                || this.config.getString("name.patronymic") == null;
    }

    public @NotNull Pronouns getPronouns() {
        return this.pronouns;
    }

    public void setPronouns(final @NotNull Pronouns pronouns) {
        this.pronouns = pronouns;
        this.config.set("pronouns", pronouns.name());
    }

    public void addIp(final @Nullable String ip) {
        this.ipList.add(ip);
        this.saveIpList();
    }

    public @NotNull List<String> getIpList() {
        return this.ipList;
    }

    public void saveIpList() {
        this.config.set("ip-list", this.ipList);
    }

    public @NotNull @UnmodifiableView List<Skin> getSkins() {
        return Collections.unmodifiableList(this.skins);
    }

    public @Nullable Skin getSkin(final @Range(from = 0, to = Integer.MAX_VALUE) int index) {
        return index >= this.skins.size() ? null : this.skins.get(index);
    }

    @Contract("null -> null")
    public @Nullable Skin getSkin(final @Nullable String name) {
        if (ChatUtils.isBlank(name)) {
            return null;
        }

        for (final var skin : this.skins) {
            if (skin.getName().equalsIgnoreCase(name)) {
                return skin;
            }
        }

        return null;
    }

    public int getSkinIndex(final @NotNull Skin skin) {
        return this.skins.indexOf(skin);
    }

    public int getSkinIndex(final @NotNull String name) {
        return this.skins.indexOf(this.getSkin(name));
    }

    public boolean setSkin(
            final int index,
            final @NotNull Skin skin
    ) {
        if (
                index < 0
                || index >= this.skins.size()
                || this.containsSkin(skin)
        ) {
            return false;
        }

        this.skins.set(index, skin);
        this.serializeSkinsSection();
        this.save();

        return true;
    }

    public boolean addSkin(final @NotNull Skin skin) {
        if (!this.hasAvailableSkinSlot()) {
            return false;
        }

        this.skins.add(skin);
        this.serializeSkinsSection();
        this.save();

        return true;
    }

    public boolean removeSkin(final @Range(from = 0, to = Integer.MAX_VALUE) int index) {
        return index < this.skins.size()
                && this.removeSkin(this.skins.get(index));
    }

    public boolean removeSkin(final @Nullable Skin skin) {
        if (!this.containsSkin(skin)) {
            return false;
        }

        final Skin currentSkin = this.playerSettings.getSkin();

        if (skin.equals(currentSkin)) {
            final PlayerInfo playerInfo = PlayerInfo.fromNickname(this.plugin, this.playerName.getNickname());

            if (playerInfo != null) {
                playerInfo.setSkin(null);
            }
        }

        this.skins.remove(skin);
        this.serializeSkinsSection();
        this.save();

        return true;
    }

    public boolean hasAvailableSkinSlot() {
        return this.skins.size() < MAX_SKINS;
    }

    @Contract("null -> false")
    public boolean containsSkin(final @Nullable Skin skin) {
        return skin != null
                && this.skins.contains(skin);
    }

    @Contract("null -> false")
    public boolean containsSkin(final @Nullable String name) {
        if (ChatUtils.isBlank(name)) {
            return false;
        }

        for (final var skin : this.skins) {
            if (skin.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    public @NotNull GameMode getGameMode() {
        return this.gameMode;
    }

    public void setGameMode(final @NotNull GameMode gameMode) {
        this.gameMode = gameMode;
        this.config.set("game-params.game-mode", gameMode.name());
    }

    public double getHealth() {
        return this.health;
    }

    public void setHealth(final double health) {
        this.health = health;
        this.config.set("game-params.health", health);
    }

    public int getAir() {
        return this.air;
    }

    public void setAir(final int air) {
        this.air = air;
        this.config.set("game-params.air", air);
    }

    public @NotNull PlayerSettings getPlayerSettings() {
        return this.playerSettings;
    }

    public @NotNull Instant getFirstJoin() {
        return this.firstJoin;
    }

    public void setFirstJoin(final @NotNull Instant firstJoin) {
        this.firstJoin = firstJoin;
        this.config.set("first-join", firstJoin.toEpochMilli());
    }

    public @Nullable Location getLastLeaveLocation() {
        return this.lastLeaveLocation;
    }

    public void setLastLeaveLocation(final @Nullable Location leaveLocation) {
        this.lastLeaveLocation = leaveLocation;
        setLocation(
                this.config.createSection("locations.last-leave-location"),
                leaveLocation
        );
    }

    public @Nullable Location getLastDeathLocation() {
        return this.lastDeathLocation;
    }

    public void setLastDeathLocation(final @Nullable Location deathLocation) {
        this.lastDeathLocation = deathLocation;
        setLocation(
                this.config.createSection("locations.last-death-location"),
                deathLocation
        );
    }

    private static void setLocation(
            final @NotNull ConfigurationSection section,
            final @Nullable Location location
    ) {
        final boolean isNull = location == null;

        section.set("world", isNull ? null : location.getWorld().getName());
        section.set("x", isNull ? null : location.getX());
        section.set("y", isNull ? null : location.getY());
        section.set("z", isNull ? null : location.getZ());
        section.set("yaw", isNull ? null : location.getYaw());
        section.set("pitch", isNull ? null : location.getPitch());
    }

    public boolean exists() {
        return this.file.exists();
    }

    public void save() {
        try {
            this.config.save(this.file);
        } catch (final IOException e) {
            this.plugin.getLogger().log(
                    Level.SEVERE,
                    "Failed to save player file : " + this.file.getName(),
                    e
            );
        }
    }

    public void serializeSkinsSection() {
        final var list = new ObjectArrayList<>(MAX_SKINS);

        for (final var skin : this.skins) {
            list.add(skin.serialize());
        }

        this.config.set("skins", list);
    }

    public @NotNull List<Skin> deserializeSkinsSection() {
        final var names = this.config.getList("skins", Collections.emptyList());
        final int size = names.size();

        if (size == 0) {
            return Collections.emptyList();
        }

        final var skins = new ObjectArrayList<Skin>(size);

        for (final var skin : names) {
            if (!(skin instanceof Map)) {
                continue;
            }

            final Skin deserialized = Skin.deserialize(skin.toString());

            if (deserialized != null) {
                skins.add(deserialized);
            }
        }

        return skins;
    }
}
