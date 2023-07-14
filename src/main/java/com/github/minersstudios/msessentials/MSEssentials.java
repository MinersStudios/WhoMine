package com.github.minersstudios.msessentials;

import com.comphenix.protocol.ProtocolLibrary;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.github.minersstudios.mscore.MSPlugin;
import com.github.minersstudios.msessentials.commands.other.discord.DiscordCommandHandler;
import com.github.minersstudios.msessentials.listeners.chat.DiscordGuildMessagePreProcessListener;
import com.github.minersstudios.msessentials.listeners.chat.DiscordPrivateMessageReceivedListener;
import com.github.minersstudios.msessentials.listeners.player.PlayerUpdateSignListener;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.github.minersstudios.msessentials.player.map.PlayerInfoMap;
import github.scarsz.discordsrv.DiscordSRV;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import org.bukkit.*;
import org.bukkit.ban.ProfileBanList;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.*;

public final class MSEssentials extends MSPlugin {
    private static MSEssentials singleton;
    private static Cache cache;
    private static Config config;
    private static World worldDark;
    private static Entity darkEntity;
    private static Location darkSpawnLocation;
    private static World overworld;
    private static Scoreboard scoreboardHideTags;
    private static Team scoreboardHideTagsTeam;

    @Override
    public void enable() {
        singleton = this;
        cache = new Cache();
        Server server = this.getServer();
        var ignoreBanSet = new HashSet<BanEntry<PlayerProfile>>();
        overworld = server.getWorlds().get(0);
        scoreboardHideTags = server.getScoreboardManager().getNewScoreboard();
        scoreboardHideTagsTeam = scoreboardHideTags.registerNewTeam("hide_tags");

        scoreboardHideTagsTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        scoreboardHideTagsTeam.setCanSeeFriendlyInvisibles(false);

        PluginCommand command = DiscordSRV.getPlugin().getCommand("discord");
        if (command != null) {
            command.setExecutor(new DiscordCommandHandler());
        }

        ProtocolLibrary.getProtocolManager().addPacketListener(new PlayerUpdateSignListener(this));
        DiscordSRV.api.subscribe(new DiscordGuildMessagePreProcessListener());
        DiscordSRV.api.subscribe(new DiscordPrivateMessageReceivedListener());

        this.runTask(() -> {
            worldDark = setWorldDark();
            darkSpawnLocation = new Location(worldDark, 0.0d, 0.0d, 0.0d);
            darkEntity = worldDark.getEntitiesByClass(ItemFrame.class).stream().findFirst().orElseGet(() ->
                    worldDark.spawn(darkSpawnLocation, ItemFrame.class, (entity) -> {
                        entity.setGravity(false);
                        entity.setFixed(true);
                        entity.setVisible(false);
                        entity.setInvulnerable(true);
                    })
            );
        });

        config = new Config(this.getConfigFile());

        config.reload();
        this.setLoadedCustoms(true);

        this.runTaskTimerAsync(
                () -> cache.seats.entrySet().stream().parallel().forEach(
                        entry -> entry.getValue().setRotation(entry.getKey().getLocation().getYaw(), 0.0f)
                ),
                0L, 1L
        );

        this.runTaskTimerAsync(() -> {
            if (cache.muteMap.isEmpty()) return;
            Instant currentInstant = Instant.now();

            cache.muteMap.entrySet().stream()
            .filter(entry -> entry.getValue().getExpiration().isBefore(currentInstant))
            .forEach(entry -> {
                OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());
                PlayerInfo.fromProfile(player.getUniqueId(), Objects.requireNonNull(player.getName())).unmute(null);
            });
        }, 0L, 50L);

        this.runTaskTimerAsync(() -> {
            ProfileBanList banList = server.getBanList(BanList.Type.PROFILE);
            Set<BanEntry<PlayerProfile>> entries = banList.getEntries();
            Instant currentInstant = Instant.now();

            entries.stream()
                    .filter(entry -> {
                        Date expiration = entry.getExpiration();
                        return !ignoreBanSet.contains(entry)
                                && expiration != null
                                && expiration.toInstant().isBefore(currentInstant);
                    })
                    .forEach(entry -> {
                        PlayerProfile profile = entry.getBanTarget();
                        UUID uuid = profile.getId();
                        String name = profile.getName();

                        if (uuid == null || name == null) {
                            ignoreBanSet.add(entry);
                            return;
                        }

                        PlayerInfo.fromProfile(uuid, name).pardon(null);
                    });
        }, 0L, 6000L);
    }

    @Override
    public void disable() {
        PlayerInfoMap playerInfoMap = cache.playerInfoMap;
        var onlinePlayers = this.getServer().getOnlinePlayers();

        if (!playerInfoMap.isEmpty() && !onlinePlayers.isEmpty()) {
            Component title = Component.translatable("ms.on_disable.message.title");
            Component subtitle = Component.translatable("ms.on_disable.message.subtitle");

            onlinePlayers.stream()
            .map(playerInfoMap::get)
            .forEach(playerInfo -> playerInfo.kickPlayer(title, subtitle));
        }

        cache.bukkitTasks.forEach(BukkitTask::cancel);
    }

    private static @NotNull World setWorldDark() {
        World world = new WorldCreator("world_dark")
                .type(WorldType.FLAT)
                .environment(World.Environment.NORMAL)
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

    @Contract(pure = true)
    public static @NotNull MSEssentials getInstance() {
        return singleton;
    }

    @Contract(pure = true)
    public static @NotNull Cache getCache() {
        return cache;
    }

    @Contract(pure = true)
    public static @NotNull Config getConfiguration() {
        return config;
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
    public static @NotNull PlayerInfo getConsolePlayerInfo() {
        return cache.consolePlayerInfo;
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
