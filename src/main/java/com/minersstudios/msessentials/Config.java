package com.minersstudios.msessentials;

import com.minersstudios.mscore.plugin.config.PluginConfig;
import com.minersstudios.mscore.utility.SharedConstants;
import com.minersstudios.msessentials.anomaly.Anomaly;
import com.minersstudios.msessentials.anomaly.task.AnomalyParticleTask;
import com.minersstudios.msessentials.anomaly.task.MainAnomalyActionTask;
import com.minersstudios.msessentials.player.ResourcePack;
import com.minersstudios.msessentials.player.PlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configuration loader class.
 * <br>
 * Use {@link MSEssentials#getConfiguration()} to get configuration instance.
 * Use {@link #reload()} to reload configuration and {@link #save()} to save
 * configuration.
 */
public final class Config extends PluginConfig<MSEssentials> {
    private long anomalyCheckRate;
    private long anomalyParticlesCheckRate;
    private boolean developerMode;
    private long discordServerId;
    private long memberRoleId;
    private long discordGlobalChannelId;
    private long discordLocalChannelId;
    private double localChatRadius;
    private String mineSkinApiKey;
    private Location spawnLocation;

    //<editor-fold desc="File paths" defaultstate="collapsed">
    private static final String YAML_EXTENSION = ".yml";

    /** The player data folder */
    public static final String PLAYERS_FOLDER = "players";

    /** The anomaly configurations folder */
    public static final String ANOMALIES_FOLDER = "anomalies";

    /** The console player data file name */
    public static final String CONSOLE_FILE_NAME = "console" + YAML_EXTENSION;

    /** The example anomaly configuration file name */
    public static final String EXAMPLE_ANOMALY_FILE_NAME = "example" + YAML_EXTENSION;

    /** The path in the plugin folder to the console player data file */
    public static final String CONSOLE_FILE_PATH = PLAYERS_FOLDER + '/' + CONSOLE_FILE_NAME;

    /** The path in the plugin folder to the example anomaly configuration file */
    public static final String EXAMPLE_ANOMALY_FILE_PATH = ANOMALIES_FOLDER + '/' + EXAMPLE_ANOMALY_FILE_NAME;
    //</editor-fold>

    /**
     * Configuration constructor
     *
     * @param plugin The plugin that owns this config
     */
    public Config(final @NotNull MSEssentials plugin) throws IllegalArgumentException {
        super(plugin, plugin.getConfigFile());
    }

    /**
     * Reloads config variables
     */
    @Override
    public void reloadVariables() {
        final MSEssentials plugin = this.getPlugin();
        final Cache cache = plugin.getCache();

        this.developerMode = this.yaml.getBoolean("developer-mode");
        this.anomalyCheckRate = this.yaml.getLong("anomaly-check-rate");
        this.anomalyParticlesCheckRate = this.yaml.getLong("anomaly-particles-check-rate");
        this.localChatRadius = this.yaml.getDouble("chat.local.radius");
        this.discordServerId = this.yaml.getLong("discord.server-id");
        this.memberRoleId = this.yaml.getLong("discord.member-role-id");
        this.discordGlobalChannelId = this.yaml.getLong("chat.global.discord-channel-id");
        this.discordLocalChannelId = this.yaml.getLong("chat.local.discord-channel-id");
        this.mineSkinApiKey = this.yaml.getString("skin.mine-skin-api-key");

        final Server server = plugin.getServer();
        final String spawnLocationWorldName = this.yaml.getString("spawn-location.world", "");
        final World spawnLocationWorld = server.getWorld(spawnLocationWorldName);
        final double spawnLocationX = this.yaml.getDouble("spawn-location.x");
        final double spawnLocationY = this.yaml.getDouble("spawn-location.y");
        final double spawnLocationZ = this.yaml.getDouble("spawn-location.z");
        final float spawnLocationYaw = (float) this.yaml.getDouble("spawn-location.yaw");
        final float spawnLocationPitch = (float) this.yaml.getDouble("spawn-location.pitch");

        if (spawnLocationWorld == null) {
            plugin.getLogger().warning("World \"" + spawnLocationWorldName + "\" not found!\nUsing default spawn location!");
            this.spawnLocation = server.getWorlds().get(0).getSpawnLocation();
        } else {
           this.spawnLocation = new Location(
                   spawnLocationWorld,
                   spawnLocationX,
                   spawnLocationY,
                   spawnLocationZ,
                   spawnLocationYaw,
                   spawnLocationPitch
            );
        }

        if (!cache.getBukkitTasks().isEmpty()) {
            cache.getBukkitTasks().forEach(BukkitTask::cancel);
        }

        cache.getBukkitTasks().clear();
        cache.getPlayerAnomalyActionMap().clear();
        cache.getAnomalies().clear();

        plugin.saveResource(EXAMPLE_ANOMALY_FILE_PATH, true);

        final File consoleDataFile = new File(plugin.getPluginFolder(), CONSOLE_FILE_PATH);

        if (!consoleDataFile.exists()) {
            plugin.saveResource(CONSOLE_FILE_PATH, false);
        }

        cache.consolePlayerInfo = new PlayerInfo(plugin, UUID.randomUUID(), SharedConstants.CONSOLE_NICKNAME);

        plugin.runTaskAsync(() -> ResourcePack.init(plugin));
        plugin.runTaskAsync(this::loadAnomalies);

        cache.getBukkitTasks().add(plugin.runTaskTimer(new MainAnomalyActionTask(plugin), 0L, this.anomalyCheckRate));
        cache.getBukkitTasks().add(plugin.runTaskTimer(new AnomalyParticleTask(plugin), 0L, this.anomalyParticlesCheckRate));

        cache.getDiscordManager().load();
    }

    /**
     * Reloads default config variables
     */
    @Override
    public void reloadDefaultVariables() {
        this.setIfNotExists("developer-mode", false);
        this.setIfNotExists("anomaly-check-rate", 100L);
        this.setIfNotExists("anomaly-particles-check-rate", 10L);
        this.setIfNotExists("chat.local.radius", 25.0d);
        this.setIfNotExists("chat.global.discord-channel-id", -1);
        this.setIfNotExists("chat.local.discord-channel-id", -1);
        this.setIfNotExists("discord.bot-token", "");
        this.setIfNotExists("discord.server-id", -1);
        this.setIfNotExists("discord.member-role-id", -1);
        this.setIfNotExists("skin.mine-skin-api-key", "");

        final Location mainWorldSpawn = Bukkit.getWorlds().get(0).getSpawnLocation();

        this.setIfNotExists("spawn-location.world", mainWorldSpawn.getWorld().getName());
        this.setIfNotExists("spawn-location.x", mainWorldSpawn.x());
        this.setIfNotExists("spawn-location.y", mainWorldSpawn.y());
        this.setIfNotExists("spawn-location.z", mainWorldSpawn.z());
        this.setIfNotExists("spawn-location.yaw", mainWorldSpawn.getYaw());
        this.setIfNotExists("spawn-location.pitch", mainWorldSpawn.getPitch());
    }

    /**
     * @return Anomaly check rate for {@link MainAnomalyActionTask}
     */
    public long getAnomalyCheckRate() {
        return this.anomalyCheckRate;
    }

    /**
     * Sets new anomaly check rate
     *
     * @param rate The new anomaly check rate
     */
    public void setAnomalyCheckRate(final long rate) {
        this.anomalyCheckRate = rate;

        this.yaml.set("anomaly-check-rate", rate);

        this.save();
    }

    /**
     * @return Anomaly particles check rate for {@link AnomalyParticleTask}
     */
    public long getAnomalyParticlesCheckRate() {
        return this.anomalyParticlesCheckRate;
    }

    /**
     * Sets new anomaly particles check rate
     *
     * @param rate The new anomaly particles check rate
     */
    public void setAnomalyParticlesCheckRate(final long rate) {
        this.anomalyParticlesCheckRate = rate;

        this.yaml.set("anomaly-particles-check-rate", rate);

        this.save();
    }

    /**
     * @return True if developer mode is enabled
     */
    public boolean isDeveloperMode() {
        return this.developerMode;
    }

    /**
     * Sets developer mode state
     *
     * @param developerMode The new developer mode state
     */
    public void setDeveloperMode(final boolean developerMode) {
        this.developerMode = developerMode;

        this.yaml.set("developer-mode", developerMode);

        this.save();
    }

    /**
     * @return Discord server id
     */
    public long getDiscordServerId() {
        return this.discordServerId;
    }

    /**
     * Sets new discord server id
     *
     * @param id The new discord server id
     */
    public void setDiscordServerId(final long id) {
        this.discordServerId = id;

        this.yaml.set("discord.server-id", id);

        this.save();
    }

    /**
     * @return Discord member role id
     */
    public long getMemberRoleId() {
        return this.memberRoleId;
    }

    /**
     * Sets new discord member role id
     *
     * @param id The new discord member role id
     */
    public void setMemberRoleId(final long id) {
        this.memberRoleId = id;

        this.yaml.set("discord.member-role-id", id);

        this.save();
    }

    /**
     * @return Discord global channel id
     */
    public long getDiscordGlobalChannelId() {
        return this.discordGlobalChannelId;
    }

    /**
     * Sets new discord global channel id
     *
     * @param id The new discord global channel id
     */
    public void setDiscordGlobalChannelId(final long id) {
        this.discordGlobalChannelId = id;

        this.yaml.set("chat.global.discord-channel-id", id);

        this.save();
    }

    /**
     * @return Discord local channel id
     */
    public long getDiscordLocalChannelId() {
        return this.discordLocalChannelId;
    }

    /**
     * Sets new discord local channel id
     *
     * @param id The new discord local channel id
     */
    public void setDiscordLocalChannelId(final long id) {
        this.discordLocalChannelId = id;

        this.yaml.set("chat.local.discord-channel-id", id);

        this.save();
    }

    /**
     * @return Local chat radius
     */
    public double getLocalChatRadius() {
        return this.localChatRadius;
    }

    /**
     * Sets new local chat radius
     *
     * @param radius The new local chat radius
     */
    public void setLocalChatRadius(final double radius) {
        this.localChatRadius = radius;

        this.yaml.set("chat.local.radius", radius);

        this.save();
    }

    /**
     * @return MineSkin API key
     */
    public @Nullable String getMineSkinApiKey() {
        return this.mineSkinApiKey;
    }

    /**
     * Sets new MineSkin API key
     *
     * @param apiKey The new MineSkin API key
     */
    public void setMineSkinApiKey(final @Nullable String apiKey) {
        this.mineSkinApiKey = apiKey;

        this.yaml.set("skin.mine-skin-api-key", apiKey);

        this.save();
    }

    /**
     * @return Spawn location
     */
    public @NotNull Location getSpawnLocation() {
        return this.spawnLocation;
    }

    /**
     * Sets new spawn location
     *
     * @param location The new spawn location
     */
    public void setSpawnLocation(final @NotNull Location location) {
        this.spawnLocation = location;

        this.yaml.set("spawn-location.world", location.getWorld().getName());
        this.yaml.set("spawn-location.x", location.x());
        this.yaml.set("spawn-location.y", location.y());
        this.yaml.set("spawn-location.z", location.z());
        this.yaml.set("spawn-location.yaw", location.getYaw());
        this.yaml.set("spawn-location.pitch", location.getPitch());

        this.save();
    }

    private void loadAnomalies() {
        final MSEssentials plugin = this.getPlugin();
        final Cache cache = plugin.getCache();
        final Logger logger = plugin.getLogger();

        try (final var path = Files.walk(Paths.get(this.file.getParent() + '/' + ANOMALIES_FOLDER))) {
            path.parallel()
            .filter(file -> {
                final String fileName = file.getFileName().toString();

                return fileName.endsWith(YAML_EXTENSION)
                        && !fileName.equalsIgnoreCase(EXAMPLE_ANOMALY_FILE_NAME);
            })
            .map(Path::toFile)
            .forEach(file -> {
                try {
                    final Anomaly anomaly = Anomaly.fromConfig(plugin, file);

                    cache.getAnomalies().put(anomaly.getNamespacedKey(), anomaly);
                } catch (final IllegalArgumentException e) {
                    logger.log(
                            Level.SEVERE,
                            "An error occurred while loading anomaly \"" + file.getName() + "\"!",
                            e
                    );
                }
            });
        } catch (final IOException e) {
            logger.log(
                    Level.SEVERE,
                    "An error occurred while loading anomalies!",
                    e
            );
        }
    }
}
