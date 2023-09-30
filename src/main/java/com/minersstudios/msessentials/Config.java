package com.minersstudios.msessentials;

import com.minersstudios.mscore.plugin.GlobalCache;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.mscore.plugin.config.LanguageFile;
import com.minersstudios.mscore.plugin.config.MSConfig;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.mscore.util.MSPluginUtils;
import com.minersstudios.msessentials.anomalies.Anomaly;
import com.minersstudios.msessentials.anomalies.tasks.MainAnomalyActionsTask;
import com.minersstudios.msessentials.anomalies.tasks.ParticleTask;
import com.minersstudios.msessentials.chat.ChatType;
import com.minersstudios.msessentials.listeners.event.chat.MessageReceivedListener;
import com.minersstudios.msessentials.listeners.event.chat.SlashCommandInteractionListener;
import com.minersstudios.msessentials.menu.CraftsMenu;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.ResourcePack;
import com.minersstudios.msessentials.util.DiscordUtil;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.managers.Presence;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Configuration loader class.
 * <p>
 * Use {@link MSEssentials#getConfiguration()} to get configuration instance.
 * Use {@link #reload()} to reload configuration and {@link #save()} to save
 * configuration.
 */
public final class Config extends MSConfig {
    private final MSEssentials plugin;

    public long anomalyCheckRate;
    public long anomalyParticlesCheckRate;
    public boolean developerMode;
    public long discordServerId;
    public long memberRoleId;
    public String discordGlobalChannelId;
    public String discordLocalChannelId;
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

    private static final String SERVER_ENABLED = LanguageFile.renderTranslation("ms.discord.server.enabled");
    private static final TranslatableComponent DISCORD_BOT_STATUS = Component.translatable("ms.discord.bot.status");

    /**
     * Configuration constructor
     *
     * @param plugin The plugin instance of the configuration
     * @param file The config file, where the configuration is stored
     * @throws IllegalArgumentException If the given file does not exist
     */
    public Config(
            final @NotNull MSEssentials plugin,
            final @NotNull File file
    ) throws IllegalArgumentException {
        super(file);

        this.plugin = plugin;
    }

    /**
     * Reloads config variables
     */
    @Override
    public void reloadVariables() {
        final Cache cache = MSEssentials.getCache();
        final File pluginFolder = this.plugin.getPluginFolder();

        this.developerMode = this.yaml.getBoolean("developer-mode");
        this.anomalyCheckRate = this.yaml.getLong("anomaly-check-rate");
        this.anomalyParticlesCheckRate = this.yaml.getLong("anomaly-particles-check-rate");
        this.localChatRadius = this.yaml.getDouble("chat.local.radius");
        this.discordServerId = this.yaml.getLong("discord.server-id");
        this.memberRoleId = this.yaml.getLong("discord.member-role-id");
        this.discordGlobalChannelId = this.yaml.getString("chat.global.discord-channel-id");
        this.discordLocalChannelId = this.yaml.getString("chat.local.discord-channel-id");
        this.version = this.yaml.getString("resource-pack.version");
        this.user = this.yaml.getString("resource-pack.user");
        this.repo = this.yaml.getString("resource-pack.repo");
        this.fullFileName = this.yaml.getString("resource-pack.full.file-name");
        this.fullHash = this.yaml.getString("resource-pack.full.hash");
        this.liteFileName = this.yaml.getString("resource-pack.lite.file-name");
        this.liteHash = this.yaml.getString("resource-pack.lite.hash");
        this.mineSkinApiKey = this.yaml.getString("skin.mine-skin-api-key");

        final String spawnLocationWorldName = this.yaml.getString("spawn-location.world", "");
        final World spawnLocationWorld = this.plugin.getServer().getWorld(spawnLocationWorldName);
        final double spawnLocationX = this.yaml.getDouble("spawn-location.x");
        final double spawnLocationY = this.yaml.getDouble("spawn-location.y");
        final double spawnLocationZ = this.yaml.getDouble("spawn-location.z");
        final float spawnLocationYaw = (float) this.yaml.getDouble("spawn-location.yaw");
        final float spawnLocationPitch = (float) this.yaml.getDouble("spawn-location.pitch");

        if (spawnLocationWorld == null) {
            MSEssentials.logger().warning("World \"" + spawnLocationWorldName + "\" not found!\nUsing default spawn location!");
            this.spawnLocation = this.plugin.getServer().getWorlds().get(0).getSpawnLocation();
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

        if (!cache.bukkitTasks.isEmpty()) {
            cache.bukkitTasks.forEach(BukkitTask::cancel);
        }

        cache.bukkitTasks.clear();
        cache.playerAnomalyActionMap.clear();
        cache.anomalies.clear();

        this.plugin.saveResource("anomalies/example.yml", true);
        final File consoleDataFile = new File(pluginFolder, "players/console.yml");
        if (!consoleDataFile.exists()) {
            this.plugin.saveResource("players/console.yml", false);
        }

        cache.consolePlayerInfo = new PlayerInfo(UUID.randomUUID(), "$Console");

        this.plugin.runTaskAsync(ResourcePack::init);

        this.plugin.runTaskAsync(() -> {
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
                    final Anomaly anomaly = Anomaly.fromConfig(file);
                    cache.anomalies.put(anomaly.getNamespacedKey(), anomaly);
                });
            } catch (IOException e) {
                MSEssentials.logger().log(Level.SEVERE, "An error occurred while loading anomalies!", e);
            }
        });

        cache.bukkitTasks.add(this.plugin.runTaskTimer(new MainAnomalyActionsTask(), 0L, this.anomalyCheckRate));
        cache.bukkitTasks.add(this.plugin.runTaskTimer(new ParticleTask(), 0L, this.anomalyParticlesCheckRate));

        final GlobalCache globalCache = MSPlugin.getGlobalCache();
        final var customBlockRecipes = globalCache.customBlockRecipes;
        final var customDecorRecipes = globalCache.customDecorRecipes;
        final var customItemRecipes = globalCache.customItemRecipes;

        this.plugin.runTaskTimer(task -> {
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

        final String botToken = this.yaml.getString("discord.bot-token");

        this.plugin.runTaskAsync(() -> {
            try {
                cache.jda = StringUtils.isNotBlank(botToken)
                        ? JDABuilder
                        .createDefault(botToken)
                        .enableIntents(List.of(
                                GatewayIntent.MESSAGE_CONTENT
                        ))
                        .build()
                        .awaitReady()
                        : null;
            } catch (Exception e) {
                throw new RuntimeException("An error occurred while loading Discord bot!", e);
            }

            if (cache.jda != null) {
                if (this.discordGlobalChannelId != null) {
                    cache.discordGlobalChannel = cache.jda.getTextChannelById(this.discordGlobalChannelId);

                    if (cache.discordGlobalChannel == null) {
                        MSEssentials.logger().warning("Discord global channel not found!");
                    }
                }

                if (this.discordLocalChannelId != null) {
                    cache.discordLocalChannel = cache.jda.getTextChannelById(this.discordLocalChannelId);

                    if (cache.discordLocalChannel == null) {
                        MSEssentials.logger().warning("Discord local channel not found!");
                    }
                }

                final Presence presence = cache.jda.getPresence();

                if (this.developerMode) {
                    presence.setStatus(OnlineStatus.DO_NOT_DISTURB);
                }

                presence.setActivity(
                        Activity.playing(ChatUtils.serializePlainComponent(
                                DISCORD_BOT_STATUS.args(Component.text(this.plugin.getServer().getMinecraftVersion())))
                        )
                );

                cache.jda.addEventListener(new MessageReceivedListener());
                cache.jda.addEventListener(new SlashCommandInteractionListener());

                cache.jda.updateCommands().addCommands(
                        Commands.slash("unlink", "Unlink Discord account"),
                        Commands.slash("skinlist", "Skin list"),
                        Commands.slash("addskin", "Add skin")
                                .addOption(OptionType.STRING, "name", "Skin Name", true)
                                .addOption(OptionType.STRING, "url", "Skin URL")
                                .addOption(OptionType.STRING, "value", "Skin Value")
                                .addOption(OptionType.STRING, "signature", "Skin Signature"),
                        Commands.slash("removeskin", "Remove skin")
                                .addOption(OptionType.INTEGER, "id", "Skin ID", true),
                        Commands.slash("editskin", "Edit skin")
                                .addOption(OptionType.INTEGER, "id", "Skin ID", true)
                                .addOption(OptionType.STRING, "name", "Skin Name")
                                .addOption(OptionType.STRING, "url", "Skin URL")
                                .addOption(OptionType.STRING, "value", "Skin Value")
                                .addOption(OptionType.STRING, "signature", "Skin Signature"),
                        Commands.slash("help", "Help list")
                ).queue();

                cache.mainGuild = cache.jda.getGuildById(this.discordServerId);

                if (cache.mainGuild == null) {
                    MSEssentials.logger().warning("Discord server not found!");
                    return;
                }

                cache.memberRole = cache.mainGuild.getRoleById(this.memberRoleId);

                if (cache.memberRole == null) {
                    MSEssentials.logger().warning("Discord member role not found!");
                }

                DiscordUtil.sendMessage(ChatType.GLOBAL, SERVER_ENABLED);
                DiscordUtil.sendMessage(ChatType.LOCAL, SERVER_ENABLED);
                this.plugin.setLoadedCustoms(true);
            } else {
                this.plugin.setLoadedCustoms(true);
            }
        });
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
