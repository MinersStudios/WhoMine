package com.minersstudios.msessentials;

import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.mscore.plugin.config.LanguageFile;
import com.minersstudios.msessentials.chat.ChatType;
import com.minersstudios.msessentials.discord.DiscordHandler;
import com.minersstudios.msessentials.menu.DiscordLinkCodeMenu;
import com.minersstudios.msessentials.menu.ResourcePackMenu;
import com.minersstudios.msessentials.menu.SkinsMenu;
import com.minersstudios.msessentials.player.collection.PlayerInfoMap;
import com.minersstudios.msessentials.tasks.BanListTask;
import com.minersstudios.msessentials.tasks.MuteMapTask;
import com.minersstudios.msessentials.tasks.PlayerListTask;
import com.minersstudios.msessentials.tasks.SeatsTask;
import com.minersstudios.msessentials.world.WorldDark;
import fr.xephi.authme.AuthMe;
import fr.xephi.authme.api.v3.AuthMeApi;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.UnknownNullability;

import java.util.logging.Level;
import java.util.logging.Logger;

import static net.kyori.adventure.text.Component.translatable;

/**
 * The main class of the MSEssentials plugin
 *
 * @see MSPlugin
 */
public final class MSEssentials extends MSPlugin<MSEssentials> {
    private static MSEssentials singleton;

    private Cache cache;
    private Config config;
    private Scoreboard scoreboardHideTags;
    private Team scoreboardHideTagsTeam;

    private static final TranslatableComponent DISABLE_TITLE = translatable("ms.on_disable.message.title");
    private static final TranslatableComponent DISABLE_SUBTITLE = translatable("ms.on_disable.message.subtitle");
    private static final String SERVER_DISABLED = LanguageFile.renderTranslation("ms.discord.server.disabled");

    public static final String NAMESPACE = "msessentials";

    static {
        initClass(DiscordLinkCodeMenu.class);
        initClass(ResourcePackMenu.class);
        initClass(SkinsMenu.class);
    }

    public MSEssentials() {
        singleton = this;
    }

    @Override
    public void load() {
        this.cache = new Cache(this);
        this.config = new Config(this, this.getConfigFile());
    }

    @Override
    public void enable() {
        this.scoreboardHideTags = this.getServer().getScoreboardManager().getNewScoreboard();
        this.scoreboardHideTagsTeam = this.scoreboardHideTags.registerNewTeam("hide_tags");

        this.scoreboardHideTagsTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        this.scoreboardHideTagsTeam.setCanSeeFriendlyInvisibles(false);

        this.runTask(WorldDark::init);

        this.cache.load();
        this.config.reload();

        this.runTaskTimer(new SeatsTask(this), 0L, 1L);
        this.runTaskTimer(new PlayerListTask(this), 6000L, 6000L);
        this.runTaskTimer(new MuteMapTask(this), 0L, 50L);
        this.runTaskTimer(new BanListTask(this), 0L, 6000L);

        this.setupAuthMe();
    }

    @Override
    public void disable() {
        final PlayerInfoMap playerInfoMap = this.cache.getPlayerInfoMap();
        final var onlinePlayers = this.getServer().getOnlinePlayers();

        if (
                !playerInfoMap.isEmpty()
                && !onlinePlayers.isEmpty()
        ) {
            for (final var player : onlinePlayers) {
                playerInfoMap
                .get(player)
                .kickPlayer(player, DISABLE_TITLE, DISABLE_SUBTITLE);
            }
        }

        for (final var task : this.cache.getBukkitTasks()) {
            task.cancel();
        }

        final DiscordHandler discordHandler = this.cache.getDiscordHandler();

        discordHandler.sendMessage(ChatType.GLOBAL, SERVER_DISABLED);
        discordHandler.sendMessage(ChatType.LOCAL, SERVER_DISABLED);

        this.cache.unload();

        this.scoreboardHideTags = null;
        this.scoreboardHideTagsTeam = null;
    }

    /**
     * @return The cache of the plugin,
     *         or null if the plugin is disabled
     */
    public @UnknownNullability Cache getCache() {
        return this.cache;
    }

    /**
     * @return The configuration of the plugin
     *         or null if the plugin is disabled
     */
    public @UnknownNullability Config getConfiguration() {
        return this.config;
    }

    /**
     * @return The player info of the console
     *         or null if the plugin is disabled
     */
    public @UnknownNullability Scoreboard getScoreboardHideTags() {
        return this.scoreboardHideTags;
    }

    /**
     * @return The player info of the console
     *         or null if the plugin is disabled
     */
    public @UnknownNullability Team getScoreboardHideTagsTeam() {
        return this.scoreboardHideTagsTeam;
    }

    /**
     * @return Singleton instance of the plugin
     */
    public static @UnknownNullability MSEssentials singleton() {
        return singleton;
    }

    /**
     * @return The player info of the console
     *         or null if the plugin is disabled
     */
    public static @UnknownNullability Logger logger() {
        return singleton == null ? null : singleton.getLogger();
    }

    /**
     * @return The player info of the console
     *         or null if the plugin is disabled
     */
    public static @UnknownNullability ComponentLogger componentLogger() {
        return singleton == null ? null : singleton.getComponentLogger();
    }

    /**
     * @return The player info of the console
     *         or null if the plugin is disabled
     */
    public static @UnknownNullability Cache cache() {
        return singleton == null ? null : singleton.cache;
    }

    /**
     * @return The player info of the console
     *         or null if the plugin is disabled
     */
    public static @UnknownNullability Config config() {
        return singleton == null ? null : singleton.config;
    }

    private void setupAuthMe() {
        final Logger logger = this.getLogger();
        final PluginManager pluginManager = this.getServer().getPluginManager();

        try {
            final AuthMe authMe = AuthMeApi.getInstance().getPlugin();

            if (!authMe.isEnabled()) {
                logger.log(
                        Level.SEVERE,
                        "AuthMe is not enabled, MSEssentials will not work properly"
                );
                pluginManager.disablePlugin(this);
            }
        } catch (final Throwable e) {
            logger.log(
                    Level.SEVERE,
                    "AuthMe is not installed, MSEssentials will not work properly"
            );
            pluginManager.disablePlugin(this);
        }
    }
}
