package com.github.minersstudios.msessentials;

import com.comphenix.protocol.ProtocolLibrary;
import com.github.minersstudios.mscore.plugin.MSPlugin;
import com.github.minersstudios.msessentials.commands.other.discord.DiscordCommandHandler;
import com.github.minersstudios.msessentials.listeners.chat.DiscordGuildMessagePreProcessListener;
import com.github.minersstudios.msessentials.listeners.chat.DiscordPrivateMessageReceivedListener;
import com.github.minersstudios.msessentials.listeners.player.PlayerUpdateSignListener;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.github.minersstudios.msessentials.player.map.PlayerInfoMap;
import com.github.minersstudios.msessentials.tasks.BanListTask;
import com.github.minersstudios.msessentials.tasks.MuteListTask;
import com.github.minersstudios.msessentials.tasks.SeatsTask;
import com.github.minersstudios.msessentials.world.WorldDark;
import github.scarsz.discordsrv.DiscordSRV;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

public final class MSEssentials extends MSPlugin {
    private static MSEssentials singleton;
    private static Cache cache;
    private static Config config;
    private static World overworld;
    private static Scoreboard scoreboardHideTags;
    private static Team scoreboardHideTagsTeam;

    private static final TranslatableComponent DISABLE_TITLE = Component.translatable("ms.on_disable.message.title");
    private static final TranslatableComponent DISABLE_SUBTITLE = Component.translatable("ms.on_disable.message.subtitle");

    @Override
    public void enable() {
        singleton = this;
        cache = new Cache();
        Server server = this.getServer();
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

        this.runTask(WorldDark::init);

        config = new Config(this, this.getConfigFile());

        config.reload();
        this.setLoadedCustoms(true);

        this.runTaskTimer(new SeatsTask(), 0L, 1L);
        this.runTaskTimerAsync(new MuteListTask(), 0L, 50L);
        this.runTaskTimerAsync(new BanListTask(), 0L, 6000L);
    }

    @Override
    public void disable() {
        PlayerInfoMap playerInfoMap = cache.playerInfoMap;
        var onlinePlayers = this.getServer().getOnlinePlayers();

        if (!playerInfoMap.isEmpty() && !onlinePlayers.isEmpty()) {
            onlinePlayers.stream()
            .map(playerInfoMap::get)
            .forEach(playerInfo -> playerInfo.kickPlayer(DISABLE_TITLE, DISABLE_SUBTITLE));
        }

        cache.bukkitTasks.forEach(BukkitTask::cancel);
    }

    /**
     * @return The instance of the plugin,
     * @throws NullPointerException If the plugin is not enabled
     */
    public static @NotNull MSEssentials getInstance() throws NullPointerException {
        return singleton;
    }

    /**
     * @return The cache of the plugin
     * @throws NullPointerException If the plugin is not enabled
     */
    public static @NotNull Cache getCache() throws NullPointerException {
        return cache;
    }

    /**
     * @return The configuration of the plugin
     * @throws NullPointerException If the plugin is not enabled
     */
    public static @NotNull Config getConfiguration() throws NullPointerException {
        return config;
    }

    /**
     * @return The overworld of the server
     * @throws NullPointerException If the plugin is not enabled
     */
    public static @NotNull World getOverworld() throws NullPointerException {
        return overworld;
    }

    /**
     * @return The player info of the console
     * @throws NullPointerException If the plugin is not enabled
     */
    public static @NotNull PlayerInfo getConsolePlayerInfo() throws NullPointerException {
        return cache.consolePlayerInfo;
    }

    /**
     * @return The scoreboard used to hide tags
     * @throws NullPointerException If the plugin is not enabled
     */
    public static @NotNull Scoreboard getScoreboardHideTags() throws NullPointerException {
        return scoreboardHideTags;
    }

    /**
     * @return The team used to hide tags
     * @throws NullPointerException If the plugin is not enabled
     */
    public static @NotNull Team getScoreboardHideTagsTeam() throws NullPointerException {
        return scoreboardHideTagsTeam;
    }
}
