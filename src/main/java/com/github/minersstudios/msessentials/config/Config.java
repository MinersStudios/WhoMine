package com.github.minersstudios.msessentials.config;

import com.github.minersstudios.mscore.MSCore;
import com.github.minersstudios.mscore.inventory.CustomInventoryMap;
import com.github.minersstudios.mscore.utils.MSPluginUtils;
import com.github.minersstudios.msessentials.Cache;
import com.github.minersstudios.msessentials.MSEssentials;
import com.github.minersstudios.msessentials.anomalies.Anomaly;
import com.github.minersstudios.msessentials.anomalies.tasks.MainAnomalyActionsTask;
import com.github.minersstudios.msessentials.anomalies.tasks.ParticleTask;
import com.github.minersstudios.msessentials.menu.CraftsMenu;
import com.github.minersstudios.msessentials.menu.PronounsMenu;
import com.github.minersstudios.msessentials.menu.ResourcePackMenu;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.github.minersstudios.msessentials.player.ResourcePack;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
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

public final class Config {
    private final File configFile;

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

    public Config() {
        this.configFile = MSEssentials.getInstance().getConfigFile();
        this.reload();
    }

    public @NotNull File getFile() {
        return this.configFile;
    }

    /**
     * Reloads the config file
     */
    public void reload() {
        MSEssentials plugin = MSEssentials.getInstance();
        Logger logger = plugin.getLogger();
        File pluginFolder = plugin.getPluginFolder();
        YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(this.configFile);
        Cache cache = MSEssentials.getCache();

        this.developerMode = yamlConfig.getBoolean("developer-mode");
        this.anomalyCheckRate = yamlConfig.getLong("anomaly-check-rate");
        this.anomalyParticlesCheckRate = yamlConfig.getLong("anomaly-particles-check-rate");
        this.localChatRadius = yamlConfig.getDouble("chat.local.radius");
        this.discordGlobalChannelId = yamlConfig.getString("chat.global.discord-channel-id");
        this.discordLocalChannelId = yamlConfig.getString("chat.local.discord-channel-id");
        this.version = yamlConfig.getString("resource-pack.version");
        this.user = yamlConfig.getString("resource-pack.user");
        this.repo = yamlConfig.getString("resource-pack.repo");
        this.fullFileName = yamlConfig.getString("resource-pack.full.file-name");
        this.fullHash = yamlConfig.getString("resource-pack.full.hash");
        this.liteFileName = yamlConfig.getString("resource-pack.lite.file-name");
        this.liteHash = yamlConfig.getString("resource-pack.lite.hash");

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
        CustomInventoryMap customInventoryMap = msCoreCache.customInventoryMap;

        customInventoryMap.remove("pronouns");
        customInventoryMap.remove("resourcepack");
        customInventoryMap.remove("crafts");

        customInventoryMap.put("pronouns", PronounsMenu.create());
        customInventoryMap.put("resourcepack", ResourcePackMenu.create());
        customInventoryMap.put("crafts", CraftsMenu.create());

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
                customInventoryMap.put("crafts_blocks", CraftsMenu.createCraftsInventory(customBlockRecipes));
                customInventoryMap.put("crafts_decors", CraftsMenu.createCraftsInventory(customDecorRecipes));
                customInventoryMap.put("crafts_items", CraftsMenu.createCraftsInventory(customItemRecipes));
                task.cancel();
            }
        }, 0L, 10L);

        plugin.saveDefaultConfig();
        plugin.reloadConfig();
    }

    /**
     * Saves the configuration to the config file
     *
     * @param configuration The configuration to save
     */
    public void save(@NotNull YamlConfiguration configuration) {
        try {
            configuration.save(this.configFile);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "An error occurred while saving the config!", e);
        }
    }
}
