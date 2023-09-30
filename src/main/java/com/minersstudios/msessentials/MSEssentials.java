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
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static net.kyori.adventure.text.Component.translatable;

/**
 * The main class of the MSEssentials plugin
 *
 * @see MSPlugin
 */
public final class MSEssentials extends MSPlugin {
    private static MSEssentials instance;
    private Cache cache;
    private Config config;
    private Scoreboard scoreboardHideTags;
    private Team scoreboardHideTagsTeam;
    private List<SlashCommandExecutor> slashCommands;

    private static final TranslatableComponent DISABLE_TITLE = translatable("ms.on_disable.message.title");
    private static final TranslatableComponent DISABLE_SUBTITLE = translatable("ms.on_disable.message.subtitle");
    private static final String SERVER_DISABLED = LanguageFile.renderTranslation("ms.discord.server.disabled");

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

        this.runTask(WorldDark::init);

        this.config = new Config(this, this.getConfigFile());

        this.config.reload();

        this.runTaskTimer(new SeatsTask(), 0L, 1L);
        this.runTaskTimer(new PlayerListTask(), 6000L, 6000L);
        this.runTaskTimer(new MuteMapTask(), 0L, 50L);
        this.runTaskTimer(new BanListTask(), 0L, 6000L);
    }

    @Override
    public void disable() {
        final PlayerInfoMap playerInfoMap = this.cache.playerInfoMap;
        final var onlinePlayers = this.getServer().getOnlinePlayers();

        if (this.cache.jda != null) {
            this.cache.jda.shutdown();
        }

        if (
                !playerInfoMap.isEmpty()
                && !onlinePlayers.isEmpty()
        ) {
            onlinePlayers.forEach(player -> playerInfoMap.get(player).kickPlayer(player, DISABLE_TITLE, DISABLE_SUBTITLE));
        }

        this.cache.bukkitTasks.forEach(BukkitTask::cancel);

        DiscordUtil.sendMessage(ChatType.GLOBAL, SERVER_DISABLED);
        DiscordUtil.sendMessage(ChatType.LOCAL, SERVER_DISABLED);
    }

    void loadSlashCommands() {
        final var builder = new ImmutableList.Builder<SlashCommandExecutor>();

        this.getClassNames().stream().parallel().forEach(className -> {
            try {
                final var clazz = this.getClassLoader().loadClass(className);

                if (clazz.isAnnotationPresent(SlashCommand.class)) {
                    if (clazz.getDeclaredConstructor().newInstance() instanceof final SlashCommandExecutor executor) {
                        builder.add(executor);
                    } else {
                        this.getLogger().warning("Annotated class with SlashCommand is not instance of SlashCommandExecutor (" + className + ")");
                    }
                }
            } catch (Exception e) {
                this.getLogger().log(Level.SEVERE, "Failed to load slash command", e);
            }
        });

        this.slashCommands = builder.build();
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

    /**
     * @return The list of slash commands or an empty list
     *         if the list is not loaded
     */
    public static @NotNull @Unmodifiable List<SlashCommandExecutor> getSlashCommands() {
        return instance == null || instance.slashCommands == null
                ? Collections.emptyList()
                : instance.slashCommands;
    }
}
