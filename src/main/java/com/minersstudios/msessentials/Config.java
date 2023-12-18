package com.minersstudios.msessentials;

import com.minersstudios.mscore.plugin.GlobalCache;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.mscore.plugin.config.PluginConfig;
import com.minersstudios.mscore.util.MSPluginUtils;
import com.minersstudios.msessentials.anomalies.Anomaly;
import com.minersstudios.msessentials.anomalies.tasks.MainAnomalyActionsTask;
import com.minersstudios.msessentials.anomalies.tasks.ParticleTask;
import com.minersstudios.msessentials.menu.CraftsMenu;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.ResourcePack;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
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
 * <br>
 * Use {@link MSEssentials#getConfiguration()} to get configuration instance.
 * Use {@link #reload()} to reload configuration and {@link #save()} to save
 * configuration.
 */
public final class Config extends PluginConfig<MSEssentials> {
    public long anomalyCheckRate;
    public long anomalyParticlesCheckRate;
    public boolean developerMode;
    public long discordServerId;
    public long memberRoleId;
    public long discordGlobalChannelId;
    public long discordLocalChannelId;
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
                plugin.getLogger().log(Level.SEVERE, "An error occurred while loading anomalies!", e);
            }
        });

        cache.getBukkitTasks().add(plugin.runTaskTimer(new MainAnomalyActionsTask(plugin), 0L, this.anomalyCheckRate));
        cache.getBukkitTasks().add(plugin.runTaskTimer(new ParticleTask(plugin), 0L, this.anomalyParticlesCheckRate));

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
}
