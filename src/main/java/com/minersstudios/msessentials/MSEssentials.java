package com.minersstudios.msessentials;

import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.msessentials.commands.player.DiscordCommand;
import com.minersstudios.msessentials.listeners.chat.DiscordGuildMessagePreProcessListener;
import com.minersstudios.msessentials.listeners.chat.DiscordPrivateMessageReceivedListener;
import com.minersstudios.msessentials.menu.DiscordLinkCodeMenu;
import com.minersstudios.msessentials.menu.ResourcePackMenu;
import com.minersstudios.msessentials.menu.SkinsMenu;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.collection.PlayerInfoMap;
import com.minersstudios.msessentials.tasks.BanListTask;
import com.minersstudios.msessentials.tasks.MuteMapTask;
import com.minersstudios.msessentials.tasks.PlayerListTask;
import com.minersstudios.msessentials.tasks.SeatsTask;
import com.minersstudios.msessentials.world.WorldDark;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.command.PluginCommand;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

import static net.kyori.adventure.text.Component.translatable;

/**
 * The main class of the MSEssentials plugin
 *
 * @see MSPlugin
 */
public final class MSEssentials extends MSPlugin {
    private static MSEssentials instance;
    private static JDA jda;
    private Cache cache;
    private Config config;
    private Scoreboard scoreboardHideTags;
    private Team scoreboardHideTagsTeam;

    private static final TranslatableComponent DISABLE_TITLE = translatable("ms.on_disable.message.title");
    private static final TranslatableComponent DISABLE_SUBTITLE = translatable("ms.on_disable.message.subtitle");

    static {
        initClass(DiscordLinkCodeMenu.class);
        initClass(ResourcePackMenu.class);
        initClass(SkinsMenu.class);
    }

    public MSEssentials() {
        instance = this;
    }

    @Override
    public void enable() {
        this.cache = new Cache();
        this.scoreboardHideTags = this.getServer().getScoreboardManager().getNewScoreboard();
        this.scoreboardHideTagsTeam = this.scoreboardHideTags.registerNewTeam("hide_tags");

        this.scoreboardHideTagsTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        this.scoreboardHideTagsTeam.setCanSeeFriendlyInvisibles(false);

        final PluginCommand command = DiscordSRV.getPlugin().getCommand("discord");
        if (command != null) {
            command.setExecutor(new DiscordCommand());
        }

        DiscordSRV.api.subscribe(new DiscordGuildMessagePreProcessListener());
        DiscordSRV.api.subscribe(new DiscordPrivateMessageReceivedListener());

        this.runTaskTimer(task -> {
            JDA jda = DiscordSRV.getPlugin().getJda();

            if (jda != null) {
                MSEssentials.jda = jda;
                this.setLoadedCustoms(true);
                task.cancel();
            }
        }, 0L, 1L);

        this.runTask(WorldDark::init);

        this.config = new Config(this, this.getConfigFile());

        this.config.reload();
        this.setLoadedCustoms(jda != null);

        this.runTaskTimer(new SeatsTask(), 0L, 1L);
        this.runTaskTimer(new PlayerListTask(), 6000L, 6000L);
        this.runTaskTimer(new MuteMapTask(), 0L, 50L);
        this.runTaskTimer(new BanListTask(), 0L, 6000L);
    }

    @Override
    public void disable() {
        final PlayerInfoMap playerInfoMap = this.cache.playerInfoMap;
        final var onlinePlayers = this.getServer().getOnlinePlayers();

        if (
                !playerInfoMap.isEmpty()
                && !onlinePlayers.isEmpty()
        ) {
            onlinePlayers.forEach(player -> playerInfoMap.get(player).kickPlayer(player, DISABLE_TITLE, DISABLE_SUBTITLE));
        }

        this.cache.bukkitTasks.forEach(BukkitTask::cancel);
    }

    /**
     * @return The instance of the plugin
     * @throws NullPointerException If the plugin is not enabled
     */
    public static MSEssentials getInstance() throws NullPointerException {
        return instance;
    }

    /**
     * @return The logger of the plugin
     * @throws NullPointerException If the plugin is not enabled
     */
    public static @NotNull Logger logger() throws NullPointerException {
        return instance.getLogger();
    }

    /**
     * @return The component logger of the plugin
     * @throws NullPointerException If the plugin is not enabled
     */
    public static @NotNull ComponentLogger componentLogger() throws NullPointerException {
        return instance.getComponentLogger();
    }

    /**
     * @return The instance of the JDA
     * @throws NullPointerException If the plugin or JDA is not enabled
     */
    public static JDA getJda() throws NullPointerException {
        return jda;
    }

    /**
     * @return The cache of the plugin
     * @throws NullPointerException If the plugin is not enabled
     */
    public static Cache getCache() throws NullPointerException {
        return instance.cache;
    }

    /**
     * @return The configuration of the plugin
     * @throws NullPointerException If the plugin is not enabled
     */
    public static Config getConfiguration() throws NullPointerException {
        return instance.config;
    }

    /**
     * @return The player info of the console
     * @throws NullPointerException If the plugin is not enabled
     */
    public static PlayerInfo getConsolePlayerInfo() throws NullPointerException {
        return instance.cache.consolePlayerInfo;
    }

    /**
     * @return The scoreboard used to hide tags
     * @throws NullPointerException If the plugin is not enabled
     */
    public static Scoreboard getScoreboardHideTags() throws NullPointerException {
        return instance.scoreboardHideTags;
    }

    /**
     * @return The team used to hide tags
     * @throws NullPointerException If the plugin is not enabled
     */
    public static Team getScoreboardHideTagsTeam() throws NullPointerException {
        return instance.scoreboardHideTagsTeam;
    }
}
