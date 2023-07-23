package com.minersstudios.msessentials.player;

import com.google.common.base.Preconditions;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.skin.Skin;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

import static com.minersstudios.mscore.utils.ChatUtils.serializeLegacyComponent;
import static com.minersstudios.msessentials.MSEssentials.getInstance;
import static net.kyori.adventure.text.Component.translatable;

/**
 * Player file with player data, settings, etc.
 * All files stored in the "config/minersstudios/MSEssentials/players" folder.
 * File name is player {@link UUID}.
 *
 * @see PlayerInfo
 */
@SuppressWarnings("UnusedReturnValue")
public class PlayerFile {
    private final @NotNull File file;
    private final @NotNull YamlConfiguration config;

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

    private PlayerFile(
            @NotNull File file,
            @NotNull YamlConfiguration config
    ) {
        this.file = file;
        this.config = config;

        this.playerName = PlayerName.create(
                config.getString("name.nickname", serializeLegacyComponent(translatable("ms.player.name.nickname"))),
                config.getString("name.first-name", serializeLegacyComponent(translatable("ms.player.name.first_name"))),
                config.getString("name.last-name", serializeLegacyComponent(translatable("ms.player.name.last_name"))),
                config.getString("name.patronymic", serializeLegacyComponent(translatable("ms.player.name.patronymic")))
        );
        this.pronouns = Pronouns.valueOf(config.getString("pronouns", "HE"));
        this.ipList = config.getStringList("ip-list");
        this.gameMode = GameMode.valueOf(config.getString("game-params.game-mode", "SURVIVAL"));
        this.health = config.getDouble("game-params.health", 20.0d);
        this.air = config.getInt("game-params.air", 300);
        this.firstJoin = Instant.ofEpochMilli(config.getLong("first-join", System.currentTimeMillis()));

        ConfigurationSection lastLeaveSection = this.config.getConfigurationSection("locations.last-leave-location");
        String lastLeaveWorldName = this.config.getString("locations.last-leave-location.world", "");
        World lastLeaveWorld = Bukkit.getWorld(lastLeaveWorldName);
        Location spawnLocation = MSEssentials.getConfiguration().spawnLocation;
        World spawnWorld = spawnLocation.getWorld();

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

        ConfigurationSection lastDeathSection = this.config.getConfigurationSection("locations.last-death-location");
        String lastDeathWorldName = this.config.getString("locations.last-death-location.world", "");
        World lastDeathWorld = Bukkit.getWorld(lastDeathWorldName);
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

        this.skins.addAll(this.deserializeSkinsSection());

        this.playerSettings = new PlayerSettings(this);
    }

    public static @NotNull PlayerFile loadConfig(
            @NotNull UUID uniqueId,
            @Nullable String nickname
    ) {
        String filePath = "players/" + ("$Console".equals(nickname) ? "console" : uniqueId) + ".yml";
        File dataFile = new File(getInstance().getPluginFolder(), filePath);
        return new PlayerFile(dataFile, YamlConfiguration.loadConfiguration(dataFile));
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

    public void setPlayerName(@NotNull PlayerName playerName) {
        this.playerName = playerName;
        this.updateName();
    }

    public void updateName() {
        ConfigurationSection section = this.config.createSection("name");
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

    public void setPronouns(@NotNull Pronouns pronouns) {
        this.pronouns = pronouns;
        this.config.set("pronouns", pronouns.name());
    }

    public void addIp(@Nullable String ip) {
        this.ipList.add(ip);
        this.saveIpList();
    }

    public @NotNull List<String> getIpList() {
        return this.ipList;
    }

    public void saveIpList() {
        this.config.set("ip-list", this.ipList);
    }

    public @NotNull List<Skin> getSkins() {
        return List.copyOf(this.skins);
    }

    public @Nullable Skin getSkin(@Range(from = 0, to = Integer.MAX_VALUE) int index) {
        return index >= this.skins.size() ? null : this.skins.get(index);
    }

    @Contract("null -> null")
    public @Nullable Skin getSkin(@Nullable String name) {
        return StringUtils.isBlank(name)
                ? null
                : this.skins.stream()
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
            @Range(from = 0, to = Integer.MAX_VALUE) int index,
            @NotNull Skin skin
    ) throws IndexOutOfBoundsException {
        if (this.containsSkin(skin)) return false;

        this.skins.set(index, skin);
        this.serializeSkinsSection();
        this.save();

        return true;
    }

    public boolean addSkin(@NotNull Skin skin) {
        if (!this.hasAvailableSkinSlot()) return false;

        this.skins.add(skin);
        this.serializeSkinsSection();
        this.save();

        return true;
    }

    public void removeSkin(@Range(from = 0, to = Integer.MAX_VALUE) int index) throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
        Skin skin = this.skins.get(index);

        Preconditions.checkNotNull(skin, "Skin not found");
        this.removeSkin(skin);
    }

    public void removeSkin(@NotNull Skin skin) throws IllegalArgumentException {
        Preconditions.checkArgument(this.containsSkin(skin), "Skin not found");

        Skin currentSkin = this.playerSettings.getSkin();

        if (skin.equals(currentSkin)) {
            PlayerInfo playerInfo = PlayerInfo.fromNickname(this.playerName.getNickname());

            if (playerInfo != null) {
                playerInfo.setSkin(null);
            }
        }

        this.skins.remove(skin);
        this.serializeSkinsSection();
        this.save();
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
        this.config.set("game-params.game-mode", gameMode.name());
    }

    public double getHealth() {
        return this.health;
    }

    public void setHealth(double health) {
        this.health = health;
        this.config.set("game-params.health", health);
    }

    public int getAir() {
        return this.air;
    }

    public void setAir(int air) {
        this.air = air;
        this.config.set("game-params.air", air);
    }

    public @NotNull PlayerSettings getPlayerSettings() {
        return this.playerSettings;
    }

    public @NotNull Instant getFirstJoin() {
        return this.firstJoin;
    }

    public void setFirstJoin(@NotNull Instant firstJoin) {
        this.firstJoin = firstJoin;
        this.config.set("first-join", firstJoin.toEpochMilli());
    }

    public @Nullable Location getLastLeaveLocation() {
        return this.lastLeaveLocation;
    }

    public void setLastLeaveLocation(@Nullable Location leaveLocation) {
        this.lastLeaveLocation = leaveLocation;
        setLocation(
                this.config.createSection("locations.last-leave-location"),
                leaveLocation
        );
    }

    public @Nullable Location getLastDeathLocation() {
        return this.lastDeathLocation;
    }

    public void setLastDeathLocation(@Nullable Location deathLocation) {
        this.lastDeathLocation = deathLocation;
        setLocation(
                this.config.createSection("locations.last-death-location"),
                deathLocation
        );
    }

    private static void setLocation(
            @NotNull ConfigurationSection section,
            @Nullable Location location
    ) {
        boolean isNull = location == null;

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
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void serializeSkinsSection() {
        this.config.set(
                "skins",
                this.skins.stream().map(Skin::serialize).toList()
        );
    }

    public @NotNull List<Skin> deserializeSkinsSection() {
        return this.config
                .getList("skins", Collections.emptyList())
                .stream()
                .filter(Map.class::isInstance)
                .map(skin -> Skin.deserialize(skin.toString()))
                .filter(Objects::nonNull)
                .toList();
    }
}
