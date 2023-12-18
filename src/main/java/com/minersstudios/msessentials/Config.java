package com.minersstudios.msessentials;

import com.minersstudios.mscore.plugin.GlobalCache;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.mscore.plugin.config.PluginConfig;
import com.minersstudios.mscore.utility.MSPluginUtils;
import com.minersstudios.msessentials.anomaly.Anomaly;
import com.minersstudios.msessentials.anomaly.task.MainAnomalyActionTask;
import com.minersstudios.msessentials.anomaly.task.AnomalyParticleTask;
import com.minersstudios.msessentials.menu.CraftsMenu;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.ResourcePack;
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
    private String version;
    private String user;
    private String repo;
    private String fullFileName;
    private String fullHash;
    private String liteFileName;
    private String liteHash;
    private double localChatRadius;
    private String mineSkinApiKey;
    private Location spawnLocation;

    /**
     * Configuration constructor
     *
     * @param plugin The plugin that owns this config
     * @param file The config file, where the configuration is stored
     * @throws IllegalArgumentException If the given file does not exist
     */
    public Config(
            final @NotNull MSEssentials plugin,
            final @NotNull File file
    ) throws IllegalArgumentException {
        super(plugin, file);
    }

    /**
     * Reloads config variables
     */
    @Override
    public void reloadVariables() {
        final MSEssentials plugin = this.getPlugin();
        final Cache cache = plugin.getCache();
        final File pluginFolder = plugin.getPluginFolder();

        this.developerMode = this.yaml.getBoolean("developer-mode");
        this.anomalyCheckRate = this.yaml.getLong("anomaly-check-rate");
        this.anomalyParticlesCheckRate = this.yaml.getLong("anomaly-particles-check-rate");
        this.localChatRadius = this.yaml.getDouble("chat.local.radius");
        this.discordServerId = this.yaml.getLong("discord.server-id");
        this.memberRoleId = this.yaml.getLong("discord.member-role-id");
        this.discordGlobalChannelId = this.yaml.getLong("chat.global.discord-channel-id");
        this.discordLocalChannelId = this.yaml.getLong("chat.local.discord-channel-id");
        this.version = this.yaml.getString("resource-pack.version");
        this.user = this.yaml.getString("resource-pack.user");
        this.repo = this.yaml.getString("resource-pack.repo");
        this.fullFileName = this.yaml.getString("resource-pack.full.file-name");
        this.fullHash = this.yaml.getString("resource-pack.full.hash");
        this.liteFileName = this.yaml.getString("resource-pack.lite.file-name");
        this.liteHash = this.yaml.getString("resource-pack.lite.hash");
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

        plugin.saveResource("anomalies/example.yml", true);

        final File consoleDataFile = new File(pluginFolder, "players/console.yml");

        if (!consoleDataFile.exists()) {
            plugin.saveResource("players/console.yml", false);
        }

        cache.consolePlayerInfo = new PlayerInfo(plugin, UUID.randomUUID(), "$Console");

        plugin.runTaskAsync(() -> ResourcePack.init(plugin));
        plugin.runTaskAsync(() -> {
            try (final var path = Files.walk(Paths.get(pluginFolder + "/anomalies"))) {
                path.parallel()
                .filter(file -> {
                    final String fileName = file.getFileName().toString();

                    return Files.isRegularFile(file)
                            && !fileName.equalsIgnoreCase("example.yml")
                            && fileName.endsWith(".yml");
                })
                .map(Path::toFile)
                .forEach(file -> {
                    final Anomaly anomaly = Anomaly.fromConfig(plugin, file);

                    cache.getAnomalies().put(anomaly.getNamespacedKey(), anomaly);
                });
            } catch (final IOException e) {
                plugin.getLogger().log(
                        Level.SEVERE,
                        "An error occurred while loading anomalies!",
                        e
                );
            }
        });

        cache.getBukkitTasks().add(plugin.runTaskTimer(new MainAnomalyActionTask(plugin), 0L, this.anomalyCheckRate));
        cache.getBukkitTasks().add(plugin.runTaskTimer(new AnomalyParticleTask(plugin), 0L, this.anomalyParticlesCheckRate));

        final GlobalCache globalCache = MSPlugin.globalCache();
        final var customBlockRecipes = globalCache.customBlockRecipes;
        final var customDecorRecipes = globalCache.customDecorRecipes;
        final var customItemRecipes = globalCache.customItemRecipes;

        plugin.runTaskTimer(task -> {
            if (
                    MSPluginUtils.isLoadedCustoms()
                    && !customBlockRecipes.isEmpty()
                    && !customDecorRecipes.isEmpty()
                    && !customItemRecipes.isEmpty()
            ) {
                task.cancel();
                CraftsMenu.putCrafts(CraftsMenu.Type.BLOCKS, customBlockRecipes);
                CraftsMenu.putCrafts(CraftsMenu.Type.DECORS, customDecorRecipes);
                CraftsMenu.putCrafts(CraftsMenu.Type.ITEMS, customItemRecipes);
            }
        }, 0L, 10L);

        cache.getDiscordHandler().load();
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
        this.setIfNotExists("resource-pack.version", "");
        this.setIfNotExists("resource-pack.user", "MinersStudios");
        this.setIfNotExists("resource-pack.repo", "WMTextures");
        this.setIfNotExists("resource-pack.full.file-name", "FULL-WMTextures-%s.zip");
        this.setIfNotExists("resource-pack.full.hash", "");
        this.setIfNotExists("resource-pack.lite.file-name", "LITE-WMTextures-%s.zip");
        this.setIfNotExists("resource-pack.lite.hash", "");
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
     * @return Resource pack version
     */
    public @Nullable String getResourcePackVersion() {
        return this.version;
    }

    /**
     * Sets new resource pack version
     *
     * @param version The new resource pack version
     */
    public void setResourcePackVersion(final @Nullable String version) {
        this.version = version;

        this.yaml.set("resource-pack.version", version);

        this.save();
    }

    /**
     * @return Resource pack user
     */
    public @Nullable String getResourcePackUser() {
        return this.user;
    }

    /**
     * Sets new resource pack user
     *
     * @param user The new resource pack user
     */
    public void setResourcePackUser(final @Nullable String user) {
        this.user = user;

        this.yaml.set("resource-pack.user", user);

        this.save();
    }

    /**
     * @return Resource pack repo
     */
    public @Nullable String getResourcePackRepo() {
        return this.repo;
    }

    /**
     * Sets new resource pack repo
     *
     * @param repo The new resource pack repo
     */
    public void setResourcePackRepo(final @Nullable String repo) {
        this.repo = repo;

        this.yaml.set("resource-pack.repo", repo);

        this.save();
    }

    /**
     * Sets new resource pack version, user and repo
     *
     * @param version The new resource pack version
     * @param user    The new resource pack user
     * @param repo    The new resource pack repo
     */
    public void setResourcePack(
            final @Nullable String version,
            final @Nullable String user,
            final @Nullable String repo
    ) {
        this.version = version;
        this.user = user;
        this.repo = repo;

        this.yaml.set("resource-pack.version", version);
        this.yaml.set("resource-pack.user", user);
        this.yaml.set("resource-pack.repo", repo);

        this.save();
    }

    /**
     * @return Resource pack full file name
     */
    public @Nullable String getResourcePackFullFileName() {
        return this.fullFileName;
    }

    /**
     * Sets new resource pack full file name
     *
     * @param fileName The new resource pack full file name
     */
    public void setResourcePackFullFileName(final @Nullable String fileName) {
        this.fullFileName = fileName;

        this.yaml.set("resource-pack.full.file-name", fileName);

        this.save();
    }

    /**
     * @return Resource pack full hash
     */
    public @Nullable String getResourcePackFullHash() {
        return this.fullHash;
    }

    /**
     * Sets new resource pack full hash
     *
     * @param hash The new resource pack full hash
     */
    public void setResourcePackFullHash(final @Nullable String hash) {
        this.fullHash = hash;

        this.yaml.set("resource-pack.full.hash", hash);

        this.save();
    }

    /**
     * Sets new resource pack full file name and hash
     *
     * @param fileName The new resource pack full file name
     * @param hash     The new resource pack full hash
     */
    public void setFullResourcePack(
            final @Nullable String fileName,
            final @Nullable String hash
    ) {
        this.fullFileName = fileName;
        this.fullHash = hash;

        this.yaml.set("resource-pack.full.file-name", fileName);
        this.yaml.set("resource-pack.full.hash", hash);

        this.save();
    }

    /**
     * @return Resource pack lite file name
     */
    public @Nullable String getResourcePackLiteFileName() {
        return this.liteFileName;
    }

    /**
     * Sets new resource pack lite file name
     *
     * @param fileName The new resource pack lite file name
     */
    public void setResourcePackLiteFileName(final @Nullable String fileName) {
        this.liteFileName = fileName;

        this.yaml.set("resource-pack.lite.file-name", fileName);

        this.save();
    }

    /**
     * @return Resource pack lite hash
     */
    public @Nullable String getResourcePackLiteHash() {
        return this.liteHash;
    }

    /**
     * Sets new resource pack hash
     *
     * @param hash The new resource pack hash
     */
    public void setResourcePackLiteHash(final @Nullable String hash) {
        this.liteHash = hash;

        this.yaml.set("resource-pack.lite.hash", hash);

        this.save();
    }

    /**
     * Sets new resource pack lite file name and hash
     *
     * @param fileName The new resource pack lite file name
     * @param hash     The new resource pack lite hash
     */
    public void setLiteResourcePack(
            final @Nullable String fileName,
            final @Nullable String hash
    ) {
        this.liteFileName = fileName;
        this.liteHash = hash;

        this.yaml.set("resource-pack.lite.file-name", fileName);
        this.yaml.set("resource-pack.lite.hash", hash);

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
}
