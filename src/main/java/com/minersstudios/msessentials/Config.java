package com.minersstudios.msessentials;

import com.minersstudios.mscore.plugin.config.PluginConfig;
import com.minersstudios.mscore.utility.SharedConstants;
import com.minersstudios.msessentials.anomaly.Anomaly;
import com.minersstudios.msessentials.anomaly.task.AnomalyParticleTask;
import com.minersstudios.msessentials.anomaly.task.MainAnomalyActionTask;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.resourcepack.ResourcePack;
import com.minersstudios.msessentials.resourcepack.throwable.FatalPackLoadException;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import static net.kyori.adventure.text.Component.text;

/**
 * Configuration loader class.
 * <br>
 * Use {@link MSEssentials#getConfiguration()} to get configuration instance.
 * Use {@link #reload()} to reload configuration and {@link #save()} to save
 * configuration.
 */
public final class Config extends PluginConfig<MSEssentials> {
    private boolean developerMode;
    private long anomalyCheckRate;
    private long anomalyParticlesCheckRate;
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

    //<editor-fold desc="Config keys" defaultstate="collapsed">
    public static final String KEY_DEVELOPER_MODE =               "developer-mode";
    public static final String KEY_ANOMALY_CHECK_RATE =           "anomaly-check-rate";
    public static final String KEY_ANOMALY_PARTICLES_CHECK_RATE = "anomaly-particles-check-rate";

    public static final String KEY_SKIN_SECTION =                 "skin";
    public static final String KEY_MINE_SKIN_API_KEY =            "mine-skin-api-key";

    public static final String KEY_DISCORD_SECTION =              "discord";
    public static final String KEY_SERVER_ID =                    "server-id";
    public static final String KEY_MEMBER_ROLE_ID =               "member-role-id";
    public static final String KEY_BOT_TOKEN =                    "bot-token";

    public static final String KEY_CHAT_SECTION =                 "chat";
    public static final String KEY_LOCAL_SECTION =                "local";
    public static final String KEY_GLOBAL_SECTION =               "global";
    public static final String KEY_RADIUS =                       "radius";
    public static final String KEY_DISCORD_CHANNEL_ID =           "discord-channel-id";

    public static final String KEY_SPAWN_LOCATION_SECTION =       "spawn-location";
    public static final String KEY_WORLD =                        "world";
    public static final String KEY_X =                            "x";
    public static final String KEY_Y =                            "y";
    public static final String KEY_Z =                            "z";
    public static final String KEY_YAW =                          "yaw";
    public static final String KEY_PITCH =                        "pitch";

    public static final String KEY_RESOURCE_PACKS_SECTION =       "resource-packs";
    //</editor-fold>

    //<editor-fold desc="Config default values" defaultstate="collapsed">
    public static boolean DEFAULT_DEVELOPER_MODE =            false;
    public static long DEFAULT_ANOMALY_CHECK_RATE =           100L;
    public static long DEFAULT_ANOMALY_PARTICLES_CHECK_RATE = 10L;
    public static double DEFAULT_LOCAL_CHAT_RADIUS =          25.0d;
    public static long DEFAULT_DISCORD_CHANNEL_ID =           -1;
    public static char DEFAULT_BOT_TOKEN =                    ' ';
    public static long DEFAULT_SERVER_ID =                    -1;
    public static long DEFAULT_MEMBER_ROLE_ID =               -1;
    public static char DEFAULT_MINE_SKIN_API_KEY =            ' ';
    //</editor-fold>

    Config(final @NotNull MSEssentials plugin) throws IllegalArgumentException {
        super(plugin, plugin.getConfigFile());
    }

    @Override
    public void reloadVariables() {
        final MSEssentials plugin = this.getPlugin();
        final Cache cache = plugin.getCache();

        this.developerMode = this.yaml.getBoolean(KEY_DEVELOPER_MODE);
        this.anomalyCheckRate = this.yaml.getLong(KEY_ANOMALY_CHECK_RATE);
        this.anomalyParticlesCheckRate = this.yaml.getLong(KEY_ANOMALY_PARTICLES_CHECK_RATE);

        final ConfigurationSection chatSection = this.yaml.getConfigurationSection(KEY_CHAT_SECTION);

        if (chatSection != null) {
            final ConfigurationSection localSection = chatSection.getConfigurationSection(KEY_LOCAL_SECTION);

            if (localSection != null) {
                this.localChatRadius = localSection.getDouble(KEY_RADIUS);
                this.discordLocalChannelId = localSection.getLong(KEY_DISCORD_CHANNEL_ID);
            }

            final ConfigurationSection globalSection = chatSection.getConfigurationSection(KEY_GLOBAL_SECTION);

            if (globalSection != null) {
                this.discordGlobalChannelId = globalSection.getLong(KEY_DISCORD_CHANNEL_ID);
            }
        }

        final ConfigurationSection discordSection = this.yaml.getConfigurationSection(KEY_DISCORD_SECTION);

        if (discordSection != null) {
            this.discordServerId = discordSection.getLong(KEY_SERVER_ID);
            this.memberRoleId = discordSection.getLong(KEY_MEMBER_ROLE_ID);
        }

        final ConfigurationSection skinSection = this.yaml.getConfigurationSection(KEY_SKIN_SECTION);

        if (skinSection != null) {
            this.mineSkinApiKey = skinSection.getString(KEY_MINE_SKIN_API_KEY);
        }

        final Server server = plugin.getServer();
        final String spawnLocationWorldName = this.yaml.getString(KEY_SPAWN_LOCATION_SECTION + '.' + KEY_WORLD, "");
        final World spawnLocationWorld = server.getWorld(spawnLocationWorldName);
        final double spawnLocationX = this.yaml.getDouble(KEY_SPAWN_LOCATION_SECTION + '.' + KEY_X);
        final double spawnLocationY = this.yaml.getDouble(KEY_SPAWN_LOCATION_SECTION + '.' + KEY_Y);
        final double spawnLocationZ = this.yaml.getDouble(KEY_SPAWN_LOCATION_SECTION + '.' + KEY_Z);
        final float spawnLocationYaw = (float) this.yaml.getDouble(KEY_SPAWN_LOCATION_SECTION + '.' + KEY_YAW);
        final float spawnLocationPitch = (float) this.yaml.getDouble(KEY_SPAWN_LOCATION_SECTION + '.' + KEY_PITCH);

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

        this.loadResourcePacks();
        plugin.runTaskAsync(this::loadAnomalies);

        cache.getBukkitTasks().add(plugin.runTaskTimer(new MainAnomalyActionTask(plugin), 0L, this.anomalyCheckRate));
        cache.getBukkitTasks().add(plugin.runTaskTimer(new AnomalyParticleTask(plugin), 0L, this.anomalyParticlesCheckRate));

        cache.getDiscordManager().load();
    }

    @Override
    public void reloadDefaultVariables() {
        this.setIfNotExists(KEY_DEVELOPER_MODE, DEFAULT_DEVELOPER_MODE);
        this.setIfNotExists(KEY_ANOMALY_CHECK_RATE, DEFAULT_ANOMALY_CHECK_RATE);
        this.setIfNotExists(KEY_ANOMALY_PARTICLES_CHECK_RATE, DEFAULT_ANOMALY_PARTICLES_CHECK_RATE);

        this.setIfNotExists(KEY_CHAT_SECTION + '.' + KEY_LOCAL_SECTION + '.' + KEY_RADIUS, DEFAULT_LOCAL_CHAT_RADIUS);
        this.setIfNotExists(KEY_CHAT_SECTION + '.' + KEY_LOCAL_SECTION + '.' + KEY_DISCORD_CHANNEL_ID, DEFAULT_DISCORD_CHANNEL_ID);
        this.setIfNotExists(KEY_CHAT_SECTION + '.' + KEY_GLOBAL_SECTION + '.' + KEY_DISCORD_CHANNEL_ID, DEFAULT_DISCORD_CHANNEL_ID);

        this.setIfNotExists(KEY_DISCORD_SECTION + '.' + KEY_BOT_TOKEN, DEFAULT_BOT_TOKEN);
        this.setIfNotExists(KEY_DISCORD_SECTION + '.' + KEY_SERVER_ID, DEFAULT_SERVER_ID);
        this.setIfNotExists(KEY_DISCORD_SECTION + '.' + KEY_MEMBER_ROLE_ID, DEFAULT_MEMBER_ROLE_ID);

        this.setIfNotExists(KEY_SKIN_SECTION + '.' + KEY_MINE_SKIN_API_KEY, DEFAULT_MINE_SKIN_API_KEY);

        final Location mainWorldSpawn = Bukkit.getWorlds().get(0).getSpawnLocation();

        this.setIfNotExists(KEY_SPAWN_LOCATION_SECTION + '.' + KEY_WORLD, mainWorldSpawn.getWorld().getName());
        this.setIfNotExists(KEY_SPAWN_LOCATION_SECTION + '.' + KEY_X, mainWorldSpawn.x());
        this.setIfNotExists(KEY_SPAWN_LOCATION_SECTION + '.' + KEY_Y, mainWorldSpawn.y());
        this.setIfNotExists(KEY_SPAWN_LOCATION_SECTION + '.' + KEY_Z, mainWorldSpawn.z());
        this.setIfNotExists(KEY_SPAWN_LOCATION_SECTION + '.' + KEY_YAW, mainWorldSpawn.getYaw());
        this.setIfNotExists(KEY_SPAWN_LOCATION_SECTION + '.' + KEY_PITCH, mainWorldSpawn.getPitch());
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

        this.yaml.set(KEY_DEVELOPER_MODE, developerMode);

        this.save();
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

        this.yaml.set(KEY_ANOMALY_CHECK_RATE, rate);

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

        this.yaml.set(KEY_ANOMALY_PARTICLES_CHECK_RATE, rate);

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

        this.yaml.set(KEY_DISCORD_SECTION + '.' + KEY_SERVER_ID, id);

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

        this.yaml.set(KEY_DISCORD_SECTION + '.' + KEY_MEMBER_ROLE_ID, id);

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

        this.yaml.set(KEY_CHAT_SECTION + '.' + KEY_GLOBAL_SECTION + '.' + KEY_DISCORD_CHANNEL_ID, id);

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

        this.yaml.set(KEY_CHAT_SECTION + '.' + KEY_LOCAL_SECTION + '.' + KEY_DISCORD_CHANNEL_ID, id);

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

        this.yaml.set(KEY_CHAT_SECTION + '.' + KEY_LOCAL_SECTION + '.' + KEY_RADIUS, radius);

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

        this.yaml.set(KEY_SKIN_SECTION + '.' + KEY_MINE_SKIN_API_KEY, apiKey);

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

        this.yaml.set(KEY_SPAWN_LOCATION_SECTION + '.' + KEY_WORLD, location.getWorld().getName());
        this.yaml.set(KEY_SPAWN_LOCATION_SECTION + '.' + KEY_X, location.x());
        this.yaml.set(KEY_SPAWN_LOCATION_SECTION + '.' + KEY_Y, location.y());
        this.yaml.set(KEY_SPAWN_LOCATION_SECTION + '.' + KEY_Z, location.z());
        this.yaml.set(KEY_SPAWN_LOCATION_SECTION + '.' + KEY_YAW, location.getYaw());
        this.yaml.set(KEY_SPAWN_LOCATION_SECTION + '.' + KEY_PITCH, location.getPitch());

        this.save();
    }

    private void loadResourcePacks() {
        final MSEssentials plugin = this.getPlugin();
        final ConfigurationSection resourcePacksSection = this.yaml.getConfigurationSection(KEY_RESOURCE_PACKS_SECTION);

        plugin.assignStatus(MSEssentials.LOADING_RESOURCE_PACKS);

        if (resourcePacksSection == null) {
            plugin.assignStatus(MSEssentials.LOADED_RESOURCE_PACKS);

            return;
        }

        final ComponentLogger logger = this.getPlugin().getComponentLogger();
        final long start = System.currentTimeMillis();
        final Map<String, CompletableFuture<ResourcePack>> futureMap;

        try {
            futureMap = ResourcePack.loadAll(
                    this.file,
                    this.yaml,
                    resourcePacksSection,
                    entry -> {
                        logger.info(
                                text("Loaded resource pack \"")
                                .append(text(entry.getKey()))
                                .append(text("\" in "))
                                .append(text(System.currentTimeMillis() - start))
                                .append(text("ms"))
                        );

                        return entry;
                    },
                    (entry, throwable) -> {
                        logger.warn(
                                text("Failed to load resource pack \"")
                                .append(text(entry.getKey()))
                                .append(text('"'))
                                .append(text(" this resource pack will be disabled!")),
                                throwable
                        );

                        return entry;
                    }
            );
        } catch (final FatalPackLoadException e) {
            plugin.assignStatus(MSEssentials.FAILED_LOAD_RESOURCE_PACKS);
            logger.error(
                    "Failed to load resource packs due to a fatal error!",
                    e
            );

            return;
        }

        CompletableFuture
        .allOf(
                futureMap
                .values()
                .toArray(CompletableFuture[]::new)
        )
        .thenRun(
                () -> plugin.assignStatus(MSEssentials.LOADED_RESOURCE_PACKS)
        );
    }

    private void loadAnomalies() {
        final MSEssentials plugin = this.getPlugin();
        final Cache cache = plugin.getCache();
        final Logger logger = plugin.getLogger();

        plugin.assignStatus(MSEssentials.LOADING_ANOMALIES);

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

            plugin.assignStatus(MSEssentials.LOADED_ANOMALIES);
        } catch (final IOException e) {
            plugin.assignStatus(MSEssentials.FAILED_LOAD_ANOMALIES);
            logger.log(
                    Level.SEVERE,
                    "An error occurred while loading anomalies!",
                    e
            );
        }
    }
}
