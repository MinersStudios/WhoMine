package com.github.minersstudios.msessentials;

import com.github.minersstudios.mscore.MSCore;
import com.github.minersstudios.mscore.config.MSConfig;
import com.github.minersstudios.mscore.utils.MSPluginUtils;
import com.github.minersstudios.msessentials.anomalies.Anomaly;
import com.github.minersstudios.msessentials.anomalies.tasks.MainAnomalyActionsTask;
import com.github.minersstudios.msessentials.anomalies.tasks.ParticleTask;
import com.github.minersstudios.msessentials.menu.CraftsMenu;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.github.minersstudios.msessentials.player.ResourcePack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

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
 * Use {@link MSEssentials#getConfiguration()} to get configuration instance.
 * Use {@link #reload()} to reload configuration and {@link #save()} to save configuration.
 */
public final class Config extends MSConfig {
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

    /**
     * Configuration constructor, automatically loads the yaml configuration
     * from the specified file and initializes the variables of the class
     *
     * @param file The config file, where the configuration is stored
     * @throws IllegalArgumentException If the given file does not exist
     */
    public Config(@NotNull File file) throws IllegalArgumentException {
        super(file);
    }

    /**
     * Reloads config variables
     */
    @Override
    public void reloadVariables() {
        MSEssentials plugin = MSEssentials.getInstance();
        Cache cache = MSEssentials.getCache();
        Logger logger = plugin.getLogger();
        File pluginFolder = plugin.getPluginFolder();

        this.developerMode = this.config.getBoolean("developer-mode", false);
        this.anomalyCheckRate = this.config.getLong("anomaly-check-rate", 100L);
        this.anomalyParticlesCheckRate = this.config.getLong("anomaly-particles-check-rate", 10L);
        this.localChatRadius = this.config.getDouble("chat.local.radius", 25);
        this.discordGlobalChannelId = this.config.getString("chat.global.discord-channel-id");
        this.discordLocalChannelId = this.config.getString("chat.local.discord-channel-id");
        this.version = this.config.getString("resource-pack.version");
        this.user = this.config.getString("resource-pack.user");
        this.repo = this.config.getString("resource-pack.repo");
        this.fullFileName = this.config.getString("resource-pack.full.file-name");
        this.fullHash = this.config.getString("resource-pack.full.hash");
        this.liteFileName = this.config.getString("resource-pack.lite.file-name");
        this.liteHash = this.config.getString("resource-pack.lite.hash");
        this.mineSkinApiKey = this.config.getString("skin.mine-skin-api-key", "");

        if (!cache.bukkitTasks.isEmpty()) {
            cache.bukkitTasks.forEach(BukkitTask::cancel);
        }

        cache.bukkitTasks.clear();
        cache.playerAnomalyActionMap.clear();
        cache.anomalies.clear();

        plugin.saveResource("anomalies/example.yml", true);
        File consoleDataFile = new File(pluginFolder, "players/console.yml");
        if (!consoleDataFile.exists()) {
            plugin.saveResource("players/console.yml", false);
        }

        cache.consolePlayerInfo = new PlayerInfo(UUID.randomUUID(), "$Console");

        plugin.runTaskAsync(ResourcePack::init);

        plugin.runTaskAsync(() -> {
            try (var path = Files.walk(Paths.get(pluginFolder + "/anomalies"))) {
                path
                .filter(file -> {
                    String fileName = file.getFileName().toString();
                    return Files.isRegularFile(file)
                            && !fileName.equals("example.yml")
                            && fileName.endsWith(".yml");
                })
                .map(Path::toFile)
                .forEach(file -> {
                    Anomaly anomaly = Anomaly.fromConfig(file);
                    cache.anomalies.put(anomaly.getNamespacedKey(), anomaly);
                });
            } catch (IOException e) {
                logger.log(Level.SEVERE, "An error occurred while loading anomalies!", e);
            }
        });

        cache.bukkitTasks.add(plugin.runTaskTimer(
                new MainAnomalyActionsTask(),
                0L,
                this.anomalyCheckRate
        ));

        cache.bukkitTasks.add(plugin.runTaskTimer(
                new ParticleTask(),
                0L,
                this.anomalyParticlesCheckRate
        ));

        com.github.minersstudios.mscore.Cache msCoreCache = MSCore.getCache();

        var customBlockRecipes = msCoreCache.customBlockRecipes;
        var customDecorRecipes = msCoreCache.customDecorRecipes;
        var customItemRecipes = msCoreCache.customItemRecipes;

        plugin.runTaskTimer(task -> {
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

        plugin.saveDefaultConfig();
        plugin.reloadConfig();
    }
}
