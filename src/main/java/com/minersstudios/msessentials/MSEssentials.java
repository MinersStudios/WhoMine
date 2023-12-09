package com.minersstudios.msessentials;

import com.google.common.collect.ImmutableList;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.mscore.plugin.config.LanguageFile;
import com.minersstudios.msessentials.chat.ChatType;
import com.minersstudios.msessentials.discord.command.SlashCommand;
import com.minersstudios.msessentials.discord.command.SlashCommandExecutor;
import com.minersstudios.msessentials.menu.DiscordLinkCodeMenu;
import com.minersstudios.msessentials.menu.ResourcePackMenu;
import com.minersstudios.msessentials.menu.SkinsMenu;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.collection.PlayerInfoMap;
import com.minersstudios.msessentials.tasks.BanListTask;
import com.minersstudios.msessentials.tasks.MuteMapTask;
import com.minersstudios.msessentials.tasks.PlayerListTask;
import com.minersstudios.msessentials.tasks.SeatsTask;
import com.minersstudios.msessentials.util.DiscordUtil;
import com.minersstudios.msessentials.world.WorldDark;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
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
        this.cache = new Cache();
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

        this.runTaskTimer(new SeatsTask(), 0L, 1L);
        this.runTaskTimer(new PlayerListTask(), 6000L, 6000L);
        this.runTaskTimer(new MuteMapTask(), 0L, 50L);
        this.runTaskTimer(new BanListTask(), 0L, 6000L);
    }

    @Override
    public void disable() {
        final PlayerInfoMap playerInfoMap = this.cache.getPlayerInfoMap();
        final var onlinePlayers = this.getServer().getOnlinePlayers();

        if (this.cache.jda != null) {
            this.cache.jda.shutdown();
        }

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

        DiscordUtil.sendMessage(ChatType.GLOBAL, SERVER_DISABLED);
        DiscordUtil.sendMessage(ChatType.LOCAL, SERVER_DISABLED);

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

    /**
     * @return The player info of the console
     *         or null if the plugin is disabled
     */
    public static @UnknownNullability PlayerInfo consolePlayerInfo() {
        if (singleton == null) return null;

        final Cache cache = singleton.cache;
        return cache == null ? null : cache.consolePlayerInfo;
    }

    /**
     * @return The player info of the console
     *         or null if the plugin is disabled
     */
    public static @UnknownNullability Scoreboard scoreboardHideTags() {
        return singleton == null ? null : singleton.scoreboardHideTags;
    }

    /**
     * @return The player info of the console
     *         or null if the plugin is disabled
     */
    public static @UnknownNullability Team scoreboardHideTagsTeam() {
        return singleton == null ? null : singleton.scoreboardHideTagsTeam;
    }

    void loadSlashCommands() {
        final Logger logger = this.getLogger();
        final ClassLoader classLoader = this.getClassLoader();
        final var builder = new ImmutableList.Builder<SlashCommandExecutor>();

        this.getClassNames().parallelStream()
        .forEach(className -> {
            try {
                final var clazz = classLoader.loadClass(className);

                if (clazz.isAnnotationPresent(SlashCommand.class)) {
                    if (clazz.getDeclaredConstructor().newInstance() instanceof final SlashCommandExecutor executor) {
                        builder.add(executor);
                    } else {
                        logger.warning(
                                "Annotated class with SlashCommand is not instance of SlashCommandExecutor (" + className + ")"
                        );
                    }
                }
            } catch (final Exception e) {
                logger.log(
                        Level.SEVERE,
                        "Failed to load slash command",
                        e
                );
            }
        });

        this.cache.slashCommands = builder.build();
    }
}
