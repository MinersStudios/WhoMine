package com.github.minersstudios.msutils.config;

import com.github.minersstudios.msutils.MSUtils;
import com.github.minersstudios.msutils.anomalies.Anomaly;
import com.github.minersstudios.msutils.anomalies.AnomalyAction;
import com.github.minersstudios.msutils.player.IDMap;
import com.github.minersstudios.msutils.player.MuteMap;
import com.github.minersstudios.msutils.player.PlayerInfoMap;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public final class ConfigCache {
    public final @NotNull File configFile;
    public final @NotNull YamlConfiguration configYaml;

    public final PlayerInfoMap playerInfoMap;
    public final MuteMap muteMap;
    public final IDMap idMap;

    public final Map<Player, ArmorStand> seats = new HashMap<>();
    public final Map<NamespacedKey, Anomaly> anomalies = new HashMap<>();
    public final Map<Player, Map<AnomalyAction, Long>> playerAnomalyActionMap = new ConcurrentHashMap<>();

    public final Map<UUID, Queue<String>> chatQueue = new HashMap<>();

    public final List<BukkitTask> bukkitTasks = new ArrayList<>();

    public final long anomalyCheckRate;
    public final long anomalyParticlesCheckRate;

    public final boolean developerMode;

    public final String
            discordGlobalChannelId,
            discordLocalChannelId;

    public final String version,
            user,
            repo,
            fullFileName,
            fullHash,
            liteFileName,
            liteHash;
    public final double localChatRadius;

    public ConfigCache() {
        this.configFile = MSUtils.getInstance().getConfigFile();
        this.configYaml = YamlConfiguration.loadConfiguration(this.configFile);

        this.developerMode = this.configYaml.getBoolean("developer-mode");

        this.anomalyCheckRate = this.configYaml.getLong("anomaly-check-rate");
        this.anomalyParticlesCheckRate = this.configYaml.getLong("anomaly-particles-check-rate");

        this.localChatRadius = this.configYaml.getDouble("chat.local.radius");
        this.discordGlobalChannelId = this.configYaml.getString("chat.global.discord-channel-id");
        this.discordLocalChannelId = this.configYaml.getString("chat.local.discord-channel-id");

        this.version = this.configYaml.getString("resource-pack.version");
        this.user = this.configYaml.getString("resource-pack.user");
        this.repo = this.configYaml.getString("resource-pack.repo");
        this.fullFileName = this.configYaml.getString("resource-pack.full.file-name");
        this.fullHash = this.configYaml.getString("resource-pack.full.hash");
        this.liteFileName = this.configYaml.getString("resource-pack.lite.file-name");
        this.liteHash = this.configYaml.getString("resource-pack.lite.hash");

        this.playerInfoMap = new PlayerInfoMap();
        this.muteMap = new MuteMap();
        this.idMap = new IDMap();

        this.loadAnomalies();
    }

    private void loadAnomalies() {
        try (Stream<Path> path = Files.walk(Paths.get(MSUtils.getInstance().getPluginFolder() + "/anomalies"))) {
            path
            .filter(Files::isRegularFile)
            .map(Path::toFile)
            .forEach((file) -> {
                if (file.getName().equals("example.yml")) return;
                Anomaly anomaly = Anomaly.fromConfig(file, YamlConfiguration.loadConfiguration(file));
                this.anomalies.put(anomaly.getNamespacedKey(), anomaly);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save() {
        try {
            this.configYaml.save(MSUtils.getConfigCache().configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
