package com.minersstudios.msessentials;

import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.plugin.GlobalCache;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.mscore.plugin.config.MSConfig;
import com.minersstudios.mscore.util.MSPluginUtils;
import com.minersstudios.msessentials.anomalies.Anomaly;
import com.minersstudios.msessentials.anomalies.tasks.MainAnomalyActionsTask;
import com.minersstudios.msessentials.anomalies.tasks.ParticleTask;
import com.minersstudios.msessentials.menu.CraftsMenu;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.ResourcePack;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Configuration loader class.
 * <p>
 * Use {@link MSEssentials#getConfiguration()} to get configuration instance.
 * Use {@link #reload()} to reload configuration and {@link #save()} to save
 * configuration.
 */
public final class Config extends MSConfig {
    private final MSEssentials plugin;

    public long anomalyCheckRate;
    public long anomalyParticlesCheckRate;
    public boolean developerMode;
    public String discordGlobalChannelId;
    public String discordLocalChannelId;
    public String version;
    public String user;
    public String repo;
    public String fullFileName;
    public String fullHash;
    public String liteFileName;
    public String liteHash;
    public double localChatRadius;
    public String mineSkinApiKey;
    public Location spawnLocation;

    /**
     * Configuration constructor
     *
     * @param plugin The plugin instance of the configuration
     * @param file The config file, where the configuration is stored
     * @throws IllegalArgumentException If the given file does not exist
     */
    public Config(
            @NotNull MSEssentials plugin,
            @NotNull File file
    ) throws IllegalArgumentException {
        super(file);
        this.plugin = plugin;
    }

    /**
     * Reloads config variables
     */
    @Override
    public void reloadVariables() {
        Cache cache = MSEssentials.getCache();
        File pluginFolder = this.plugin.getPluginFolder();

        this.developerMode = this.yaml.getBoolean("developer-mode");
        this.anomalyCheckRate = this.yaml.getLong("anomaly-check-rate", 100L);
        this.anomalyParticlesCheckRate = this.yaml.getLong("anomaly-particles-check-rate", 10L);
        this.localChatRadius = this.yaml.getDouble("chat.local.radius", 25.0d);
        this.discordGlobalChannelId = this.yaml.getString("chat.global.discord-channel-id");
        this.discordLocalChannelId = this.yaml.getString("chat.local.discord-channel-id");
        this.version = this.yaml.getString("resource-pack.version");
        this.user = this.yaml.getString("resource-pack.user", "MinersStudios");
        this.repo = this.yaml.getString("resource-pack.repo", "MSTextures");
        this.fullFileName = this.yaml.getString("resource-pack.full.file-name", "FULL-MSTextures-%s.zip");
        this.fullHash = this.yaml.getString("resource-pack.full.hash");
        this.liteFileName = this.yaml.getString("resource-pack.lite.file-name", "LITE-MSTextures-%s.zip");
        this.liteHash = this.yaml.getString("resource-pack.lite.hash");
        this.mineSkinApiKey = this.yaml.getString("skin.mine-skin-api-key");

        String spawnLocationWorldName = this.yaml.getString("spawn-location.world", "");
        World spawnLocationWorld = this.plugin.getServer().getWorld(spawnLocationWorldName);
        double spawnLocationX = this.yaml.getDouble("spawn-location.x");
        double spawnLocationY = this.yaml.getDouble("spawn-location.y");
        double spawnLocationZ = this.yaml.getDouble("spawn-location.z");
        float spawnLocationYaw = (float) this.yaml.getDouble("spawn-location.yaw");
        float spawnLocationPitch = (float) this.yaml.getDouble("spawn-location.pitch");

        if (spawnLocationWorld == null) {
            MSLogger.warning("World \"" + spawnLocationWorldName + "\" not found!\nUsing default spawn location!");
            this.spawnLocation = this.plugin.getServer().getWorlds().get(0).getSpawnLocation();
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

        if (!cache.bukkitTasks.isEmpty()) {
            cache.bukkitTasks.forEach(BukkitTask::cancel);
        }

        cache.bukkitTasks.clear();
        cache.playerAnomalyActionMap.clear();
        cache.anomalies.clear();

        this.plugin.saveResource("anomalies/example.yml", true);
        File consoleDataFile = new File(pluginFolder, "players/console.yml");
        if (!consoleDataFile.exists()) {
            this.plugin.saveResource("players/console.yml", false);
        }

        cache.consolePlayerInfo = new PlayerInfo(UUID.randomUUID(), "$Console");

        this.plugin.runTaskAsync(ResourcePack::init);

        this.plugin.runTaskAsync(() -> {
            try (var path = Files.walk(Paths.get(pluginFolder + "/anomalies"))) {
                path
                .filter(file -> {
                    String fileName = file.getFileName().toString();
                    return Files.isRegularFile(file)
                            && !fileName.equalsIgnoreCase("example.yml")
                            && fileName.endsWith(".yml");
                })
                .map(Path::toFile)
                .forEach(file -> {
                    Anomaly anomaly = Anomaly.fromConfig(file);
                    cache.anomalies.put(anomaly.getNamespacedKey(), anomaly);
                });
            } catch (IOException e) {
                MSLogger.log(Level.SEVERE, "An error occurred while loading anomalies!", e);
            }
        });

        cache.bukkitTasks.add(this.plugin.runTaskTimer(new MainAnomalyActionsTask(), 0L, this.anomalyCheckRate));
        cache.bukkitTasks.add(this.plugin.runTaskTimer(new ParticleTask(), 0L, this.anomalyParticlesCheckRate));

        GlobalCache globalCache = MSPlugin.getGlobalCache();

        var customBlockRecipes = globalCache.customBlockRecipes;
        var customDecorRecipes = globalCache.customDecorRecipes;
        var customItemRecipes = globalCache.customItemRecipes;

        this.plugin.runTaskTimer(task -> {
            if (
                    MSPluginUtils.isLoadedCustoms()
                    && !customBlockRecipes.isEmpty()
                    && !customDecorRecipes.isEmpty()
                    && !customItemRecipes.isEmpty()
            ) {
                CraftsMenu.putCrafts(CraftsMenu.Type.BLOCKS, customBlockRecipes);
                CraftsMenu.putCrafts(CraftsMenu.Type.DECORS, customDecorRecipes);
                CraftsMenu.putCrafts(CraftsMenu.Type.ITEMS, customItemRecipes);
                task.cancel();
            }
        }, 0L, 10L);
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
        this.setIfNotExists("resource-pack.version", "");
        this.setIfNotExists("resource-pack.user", "MinersStudios");
        this.setIfNotExists("resource-pack.repo", "MSTextures");
        this.setIfNotExists("resource-pack.full.file-name", "FULL-MSTextures-%s.zip");
        this.setIfNotExists("resource-pack.full.hash", "");
        this.setIfNotExists("resource-pack.lite.file-name", "LITE-MSTextures-%s.zip");
        this.setIfNotExists("resource-pack.lite.hash", "");
        this.setIfNotExists("skin.mine-skin-api-key", "");

        Location mainWorldSpawn = Bukkit.getWorlds().get(0).getSpawnLocation();

        this.setIfNotExists("spawn-location.world", mainWorldSpawn.getWorld().getName());
        this.setIfNotExists("spawn-location.x", mainWorldSpawn.x());
        this.setIfNotExists("spawn-location.y", mainWorldSpawn.y());
        this.setIfNotExists("spawn-location.z", mainWorldSpawn.z());
        this.setIfNotExists("spawn-location.yaw", mainWorldSpawn.getYaw());
        this.setIfNotExists("spawn-location.pitch", mainWorldSpawn.getPitch());
    }
}
