package com.minersstudios.msessentials;

import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.msessentials.commands.player.DiscordCommand;
import com.minersstudios.msessentials.listeners.chat.DiscordGuildMessagePreProcessListener;
import com.minersstudios.msessentials.listeners.chat.DiscordPrivateMessageReceivedListener;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.map.PlayerInfoMap;
import com.minersstudios.msessentials.tasks.BanListTask;
import com.minersstudios.msessentials.tasks.MuteListTask;
import com.minersstudios.msessentials.tasks.PlayerListTask;
import com.minersstudios.msessentials.tasks.SeatsTask;
import com.minersstudios.msessentials.world.WorldDark;
import github.scarsz.discordsrv.DiscordSRV;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.translatable;

public final class MSEssentials extends MSPlugin {
    private static MSEssentials instance;
    private Cache cache;
    private Config config;
    private Scoreboard scoreboardHideTags;
    private Team scoreboardHideTagsTeam;

    private static final TranslatableComponent DISABLE_TITLE = translatable("ms.on_disable.message.title");
    private static final TranslatableComponent DISABLE_SUBTITLE = translatable("ms.on_disable.message.subtitle");

    @Override
    public void enable() {
        Server server = this.getServer();
        instance = this;
        this.cache = new Cache();
        this.scoreboardHideTags = server.getScoreboardManager().getNewScoreboard();
        this.scoreboardHideTagsTeam = this.scoreboardHideTags.registerNewTeam("hide_tags");

        this.scoreboardHideTagsTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        this.scoreboardHideTagsTeam.setCanSeeFriendlyInvisibles(false);

        PluginCommand command = DiscordSRV.getPlugin().getCommand("discord");
        if (command != null) {
            command.setExecutor(new DiscordCommand());
        }

        DiscordSRV.api.subscribe(new DiscordGuildMessagePreProcessListener());
        DiscordSRV.api.subscribe(new DiscordPrivateMessageReceivedListener());

        this.runTask(WorldDark::init);

        this.config = new Config(this, this.getConfigFile());

        this.config.reload();
        this.setLoadedCustoms(true);

        this.runTaskTimer(new SeatsTask(), 0L, 1L);
        this.runTaskTimer(new PlayerListTask(), 6000L, 6000L);
        this.runTaskTimerAsync(new MuteListTask(), 0L, 50L);
        this.runTaskTimerAsync(new BanListTask(), 0L, 6000L);
    }

    @Override
    public void disable() {
        PlayerInfoMap playerInfoMap = this.cache.playerInfoMap;
        var onlinePlayers = this.getServer().getOnlinePlayers();

        if (!playerInfoMap.isEmpty() && !onlinePlayers.isEmpty()) {
            onlinePlayers.stream()
            .map(playerInfoMap::get)
            .forEach(playerInfo -> playerInfo.kickPlayer(DISABLE_TITLE, DISABLE_SUBTITLE));
        }

        this.cache.bukkitTasks.forEach(BukkitTask::cancel);
    }

    /**
     * @return The instance of the plugin,
     * @throws NullPointerException If the plugin is not enabled
     */
    public static @NotNull MSEssentials getInstance() throws NullPointerException {
        return instance;
    }

    /**
     * @return The cache of the plugin
     * @throws NullPointerException If the plugin is not enabled
     */
    public static @NotNull Cache getCache() throws NullPointerException {
        return instance.cache;
    }

    /**
     * @return The configuration of the plugin
     * @throws NullPointerException If the plugin is not enabled
     */
    public static @NotNull Config getConfiguration() throws NullPointerException {
        return instance.config;
    }

    /**
     * @return The player info of the console
     * @throws NullPointerException If the plugin is not enabled
     */
    public static @NotNull PlayerInfo getConsolePlayerInfo() throws NullPointerException {
        return instance.cache.consolePlayerInfo;
    }

    /**
     * @return The scoreboard used to hide tags
     * @throws NullPointerException If the plugin is not enabled
     */
    public static @NotNull Scoreboard getScoreboardHideTags() throws NullPointerException {
        return instance.scoreboardHideTags;
    }

    /**
     * @return The team used to hide tags
     * @throws NullPointerException If the plugin is not enabled
     */
    public static @NotNull Team getScoreboardHideTagsTeam() throws NullPointerException {
        return instance.scoreboardHideTagsTeam;
    }
}
