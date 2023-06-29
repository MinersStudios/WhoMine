package com.github.minersstudios.msutils;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.github.minersstudios.mscore.Cache;
import com.github.minersstudios.mscore.MSCore;
import com.github.minersstudios.mscore.MSPlugin;
import com.github.minersstudios.mscore.inventory.CustomInventoryMap;
import com.github.minersstudios.mscore.utils.MSPluginUtils;
import com.github.minersstudios.msutils.anomalies.tasks.MainAnomalyActionsTask;
import com.github.minersstudios.msutils.anomalies.tasks.ParticleTask;
import com.github.minersstudios.msutils.config.ConfigCache;
import com.github.minersstudios.msutils.listeners.chat.DiscordGuildMessagePreProcessListener;
import com.github.minersstudios.msutils.menu.CraftsMenu;
import com.github.minersstudios.msutils.menu.PronounsMenu;
import com.github.minersstudios.msutils.menu.ResourcePackMenu;
import com.github.minersstudios.msutils.player.MuteMap;
import com.github.minersstudios.msutils.player.PlayerInfo;
import com.github.minersstudios.msutils.player.ResourcePack;
import fr.xephi.authme.api.v3.AuthMeApi;
import github.scarsz.discordsrv.DiscordSRV;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.inventory.Recipe;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.Instant;
import java.util.*;

public final class MSUtils extends MSPlugin {
    private static MSUtils instance;

    private static AuthMeApi authMeApi;
    private static ProtocolManager protocolManager;

    private static World worldDark;
    private static Entity darkEntity;
    private static Location darkSpawnLocation;
    private static World overworld;
    private static ConfigCache configCache;
    private static PlayerInfo consolePlayerInfo;
    private static Scoreboard scoreboardHideTags;
    private static Team scoreboardHideTagsTeam;

    @Override
    public void enable() {
        instance = this;
        protocolManager = ProtocolLibrary.getProtocolManager();
        authMeApi = AuthMeApi.getInstance();

        Bukkit.getScheduler().runTask(this, () -> {
            worldDark = setWorldDark();
            darkEntity = worldDark.getEntitiesByClass(ItemFrame.class).stream().findFirst().orElseGet(() ->
                    worldDark.spawn(new Location(worldDark, 0.0d, 0.0d, 0.0d), ItemFrame.class, (entity) -> {
                        entity.setGravity(false);
                        entity.setFixed(true);
                        entity.setVisible(false);
                        entity.setInvulnerable(true);
                    })
            );
        });
        overworld = ((CraftServer) this.getServer()).getServer().overworld().getWorld();
        darkSpawnLocation = new Location(overworld, 0.0d, 0.0d, 0.0d);

        scoreboardHideTags = Bukkit.getScoreboardManager().getNewScoreboard();
        scoreboardHideTagsTeam = scoreboardHideTags.registerNewTeam("hide_tags");
        scoreboardHideTagsTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        scoreboardHideTagsTeam.setCanSeeFriendlyInvisibles(false);

        reloadConfigs();

        DiscordSRV.api.subscribe(new DiscordGuildMessagePreProcessListener());

        Bukkit.getScheduler().runTaskTimerAsynchronously(
                this,
                () -> configCache.seats.forEach((key, value) -> value.setRotation(key.getLocation().getYaw(), 0.0f)),
                0L, 1L
        );

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            Map<UUID, MuteMap.Params> map = configCache.muteMap.getMap();
            if (map.isEmpty()) return;
            Instant currentInstant = Instant.now();
            map.entrySet().stream()
                    .filter(entry -> entry.getValue().getExpiration().isBefore(currentInstant))
                    .forEach(entry -> {
                        OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());
                        configCache.playerInfoMap.getPlayerInfo(player.getUniqueId(), Objects.requireNonNull(player.getName())).setMuted(false, null);
                    });
        }, 0L, 50L);
    }

    @Override
    public void disable() {
        if (configCache != null) {
            Bukkit.getOnlinePlayers().stream().parallel().forEach(
                    player ->
                            configCache.playerInfoMap.getPlayerInfo(player)
                            .kickPlayer(
                                    Component.translatable("ms.on_disable.message.title"),
                                    Component.translatable("ms.on_disable.message.subtitle")
                            )
            );
            configCache.bukkitTasks.forEach(BukkitTask::cancel);
        }
    }

    private static @NotNull World setWorldDark() {
        String name = "world_dark";
        World world = new WorldCreator(name)
                .type(WorldType.FLAT)
                .environment(World.Environment.NORMAL)
                .biomeProvider(new BiomeProvider() {
                    @Override
                    public @NotNull Biome getBiome(
                            @NotNull WorldInfo worldInfo,
                            int x,
                            int y,
                            int z
                    ) {
                        return Biome.FOREST;
                    }

                    @Override
                    public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
                        return new ArrayList<>();
                    }
                })
                .generator(new ChunkGenerator() {})
                .generateStructures(false)
                .hardcore(false)
                .keepSpawnLoaded(TriState.TRUE)
                .createWorld();

        assert world != null;

        world.setTime(18000L);
        world.setDifficulty(Difficulty.PEACEFUL);
        world.setGameRule(GameRule.FALL_DAMAGE, false);
        world.setGameRule(GameRule.FIRE_DAMAGE, false);
        world.setGameRule(GameRule.DROWNING_DAMAGE, false);
        world.setGameRule(GameRule.FREEZE_DAMAGE, false);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
        world.setGameRule(GameRule.KEEP_INVENTORY, true);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        return world;
    }

    public static void reloadConfigs() {
        instance.saveDefaultConfig();
        instance.reloadConfig();

        if (configCache != null) {
            configCache.bukkitTasks.forEach(BukkitTask::cancel);
        }

        instance.saveResource("anomalies/example.yml", true);
        File consoleDataFile = new File(instance.getPluginFolder(), "players/console.yml");
        if (!consoleDataFile.exists()) {
            instance.saveResource("players/console.yml", false);
        }

        configCache = new ConfigCache();
        consolePlayerInfo = new PlayerInfo(UUID.randomUUID(), "$Console");

        Bukkit.getScheduler().runTaskAsynchronously(MSUtils.getInstance(), ResourcePack::init);

        configCache.bukkitTasks.add(Bukkit.getScheduler().runTaskTimer(
                instance,
                new MainAnomalyActionsTask(),
                0L,
                configCache.anomalyCheckRate
        ));

        configCache.bukkitTasks.add(Bukkit.getScheduler().runTaskTimer(
                instance,
                new ParticleTask(),
                0L,
                configCache.anomalyParticlesCheckRate
        ));

        Cache cache = MSCore.getCache();
        CustomInventoryMap customInventoryMap = cache.customInventoryMap;
        customInventoryMap.put("pronouns", PronounsMenu.create());
        customInventoryMap.put("resourcepack", ResourcePackMenu.create());
        customInventoryMap.put("crafts", CraftsMenu.create());

        List<Recipe> customBlockRecipes = cache.customBlockRecipes;
        List<Recipe> customDecorRecipes = cache.customDecorRecipes;
        List<Recipe> customItemRecipes = cache.customItemRecipes;
        Bukkit.getScheduler().runTaskTimer(instance, task -> {
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
    }

    @Contract(pure = true)
    public static @NotNull MSUtils getInstance() {
        return instance;
    }

    @Contract(pure = true)
    public static @NotNull World getWorldDark() {
        return worldDark;
    }

    @Contract(pure = true)
    public static @NotNull Entity getDarkEntity() {
        return darkEntity;
    }

    @Contract(pure = true)
    public static @NotNull Location getDarkSpawnLocation() {
        return darkSpawnLocation;
    }

    @Contract(pure = true)
    public static @NotNull World getOverworld() {
        return overworld;
    }

    @Contract(pure = true)
    public static @NotNull AuthMeApi getAuthMeApi() {
        return authMeApi;
    }

    @Contract(pure = true)
    public static @NotNull ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    @Contract(pure = true)
    public static @NotNull ConfigCache getConfigCache() {
        return configCache;
    }

    @Contract(pure = true)
    public static @NotNull PlayerInfo getConsolePlayerInfo() {
        return consolePlayerInfo;
    }

    @Contract(pure = true)
    public static @NotNull Scoreboard getScoreboardHideTags() {
        return scoreboardHideTags;
    }

    @Contract(pure = true)
    public static @NotNull Team getScoreboardHideTagsTeam() {
        return scoreboardHideTagsTeam;
    }
}
