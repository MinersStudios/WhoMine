package com.minersstudios.msessentials.discord;

import com.minersstudios.mscore.plugin.config.LanguageFile;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.msessentials.Config;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.chat.ChatType;
import com.minersstudios.msessentials.discord.command.SlashCommand;
import com.minersstudios.msessentials.discord.command.SlashCommandExecutor;
import com.minersstudios.msessentials.discord.listener.AbstractMSDiscordListener;
import com.minersstudios.msessentials.discord.listener.MSDiscordListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.Presence;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class handles the Discord integration for the MSEssentials plugin. It
 * provides methods for interacting with the Discord API using JDA
 * (Java Discord API).
 *
 * @see <a href="https://jda.wiki">JDA Wiki</a>
 */
public final class DiscordHandler {
    private final MSEssentials plugin;
    private final Map<String, SlashCommandExecutor<MSEssentials>> slashCommandMap;
    private final List<AbstractMSDiscordListener<MSEssentials>> listeners;
    private JDA jda;
    private Guild mainGuild;
    private TextChannel globalChannel;
    private TextChannel localChannel;
    private Role memberRole;

    private static final String SERVER_ENABLED = LanguageFile.renderTranslation("ms.discord.server.enabled");
    private static final TranslatableComponent DISCORD_BOT_STATUS = Component.translatable("ms.discord.bot.status");

    /**
     * Constructor for the DiscordHandler class
     *
     * @param plugin The MSEssentials plugin instance
     */
    public DiscordHandler(final @NotNull MSEssentials plugin) {
        this.plugin = plugin;
        this.slashCommandMap = new HashMap<>();
        this.listeners = new ArrayList<>();
    }

    /**
     * @return The MSEssentials plugin instance
     */
    public @NotNull MSEssentials getPlugin() {
        return this.plugin;
    }

    /**
     * @return An Optional containing the JDA instance if it exists, or an empty
     *         Optional otherwise
     */
    public @NotNull Optional<JDA> getJda() {
        return Optional.ofNullable(this.jda);
    }

    /**
     * @return An Optional containing the main guild if it exists, or an empty
     *         Optional otherwise
     */
    public @NotNull Optional<Guild> getMainGuild() {
        return Optional.ofNullable(this.mainGuild);
    }

    /**
     * @return An Optional containing the global channel if it exists, or an
     *         empty Optional otherwise
     */
    public @NotNull Optional<TextChannel> getGlobalChannel() {
        return Optional.ofNullable(this.globalChannel);
    }

    /**
     * @return An Optional containing the local channel if it exists, or an
     *         empty Optional otherwise
     */
    public @NotNull Optional<TextChannel> getLocalChannel() {
        return Optional.ofNullable(this.localChannel);
    }

    /**
     * @return An Optional containing the member role if it exists, or an empty
     *         Optional otherwise.
     *         <br>
     *         The member role is the role that is given to verified members.
     */
    public @NotNull Optional<Role> getMemberRole() {
        return Optional.ofNullable(this.memberRole);
    }

    /**
     * @return An unmodifiable view of the slash commands map
     */
    public @UnknownNullability @UnmodifiableView Map<String, SlashCommandExecutor<MSEssentials>> slashCommandMap() {
        return Collections.unmodifiableMap(this.slashCommandMap);
    }

    /**
     * @return An unmodifiable view of the discord listeners list
     */
    public @UnknownNullability @UnmodifiableView List<AbstractMSDiscordListener<MSEssentials>> listeners() {
        return Collections.unmodifiableList(this.listeners);
    }

    /**
     * @param userId The id of the user to get
     * @return An Optional containing the user, or an
     *         empty Optional if not available
     */
    public @NotNull Optional<User> getUser(final long userId) {
        return this.jda == null
                ? Optional.empty()
                : Optional.ofNullable(this.jda.getUserById(userId));
    }

    /**
     * @param userId The id of the user to retrieve
     * @return An Optional containing the user, or an
     *         empty Optional if not available
     */
    public @NotNull Optional<User> retrieveUser(final long userId) {
        return this.jda == null
                ? Optional.empty()
                : Optional.ofNullable(this.jda.retrieveUserById(userId).complete());
    }

    /**
     * @param user The user to get the member of
     * @return An Optional containing the member, or an
     *         empty Optional if not available
     */
    public @NotNull Optional<Member> getMember(final @Nullable User user) {
        return this.mainGuild == null
                || user == null
                ? Optional.empty()
                : Optional.ofNullable(this.mainGuild.getMember(user));
    }

    /**
     * @param userId The id of the user to get the member of
     * @return An Optional containing the member, or an
     *         empty Optional if not available
     */
    public @NotNull Optional<Member> getMember(final long userId) {
        return this.mainGuild == null
                ? Optional.empty()
                : Optional.ofNullable(this.mainGuild.getMemberById(userId));
    }

    /**
     * @param user The user to retrieve the member of
     * @return An Optional containing the member, or an
     *         empty Optional if not available
     */
    public @NotNull Optional<Member> retrieveMember(final @Nullable User user) {
        return this.mainGuild == null
                || user == null
                ? Optional.empty()
                : Optional.ofNullable(this.mainGuild.retrieveMember(user).complete());
    }

    /**
     * @param userId The id of the user to retrieve the member of
     * @return An Optional containing the member, or an
     *         empty Optional if not available
     */
    public @NotNull Optional<Member> retrieveMember(final long userId) {
        return this.mainGuild == null
                ? Optional.empty()
                : Optional.ofNullable(this.mainGuild.retrieveMemberById(userId).complete());
    }

    /**
     * @param channelId The id of the channel to get
     * @return An Optional containing the channel, or an
     *         empty Optional if not available
     */
    public @NotNull Optional<TextChannel> getTextChannel(final long channelId) {
        return this.isLoaded()
                ? Optional.ofNullable(this.jda.getTextChannelById(channelId))
                : Optional.empty();
    }

    /**
     * @return True if the Discord bot is loaded, false otherwise
     */
    public boolean isLoaded() {
        return this.jda != null
                && this.jda.getStatus() == JDA.Status.CONNECTED;
    }

    /**
     * @param user The user to check
     * @return True if the user is a member of the main guild, false otherwise
     * @see #getMember(User)
     */
    public boolean isMember(final @Nullable User user) {
        return this.getMember(user).isPresent();
    }

    /**
     * @param userId The id of the user to check
     * @return True if the user is a member of the main guild, false otherwise
     * @see #getMember(long)
     */
    public boolean isMember(final long userId) {
        return this.getMember(userId).isPresent();
    }

    /**
     * @param user The user to check
     * @return True if the user is a member of the main guild and has the member
     *         role, false otherwise
     * @see #getMember(User)
     * @see #getMemberRole()
     */
    public boolean isVerified(final @Nullable User user) {
        return user != null
                && this.memberRole != null
                && this.getMember(user)
                .map(member -> member.getRoles().contains(this.memberRole))
                .orElse(false);
    }

    /**
     * @param userId The id of the user to check
     * @return True if the user is a member of the main guild and has the member
     *         role, false otherwise
     * @see #getMember(long)
     * @see #getMemberRole()
     */
    public boolean isVerified(final long userId) {
        return this.memberRole != null
                && this.getMember(userId)
                .map(member -> member.getRoles().contains(this.memberRole))
                .orElse(false);
    }

    /**
     * Sends a message to the specified chat type
     *
     * @param chatType The chat type to send the message to
     * @param message  The message to send
     * @see ChatType
     * @see #getGlobalChannel()
     * @see #getLocalChannel()
     */
    public void sendMessage(
            final @NotNull ChatType chatType,
            final @NotNull CharSequence message
    ) {
        if (!this.isLoaded()) {
            return;
        }

        switch (chatType) {
            case GLOBAL -> {
                if (this.globalChannel != null) {
                    this.globalChannel.sendMessage(message).queue();
                }
            }
            case LOCAL -> {
                if (this.localChannel != null) {
                    this.localChannel.sendMessage(message).queue();
                }
            }
        }
    }

    /**
     * Sends a message to the private channel of the specified user
     *
     * @param user    The user to send the message to
     * @param message The message to send
     */
    public void sendMessage(
            final @NotNull User user,
            final @NotNull CharSequence message
    ) {
        if (this.isLoaded()) {
            user.openPrivateChannel().queue(
                    privateChannel -> privateChannel.sendMessage(message).queue()
            );
        }
    }

    /**
     * Sends a message to the private channel of the specified user
     *
     * @param userId  The id of the user to send the message to
     * @param message The message to send
     * @see #retrieveUser(long)
     * @see #sendMessage(User, CharSequence)
     */
    public void sendMessage(
            final long userId,
            final @NotNull CharSequence message
    ) {
        this.retrieveUser(userId).ifPresent(
                user -> this.sendMessage(user, message)
        );
    }

    /**
     * Sends an embeds to the specified chat type
     *
     * @param chatType The chat type to send the message to
     * @param first    The first embed to send
     * @param rest     The rest of the embeds to send
     * @see ChatType
     * @see #getGlobalChannel()
     * @see #getLocalChannel()
     */
    public void sendEmbeds(
            final @NotNull ChatType chatType,
            final @NotNull MessageEmbed first,
            final MessageEmbed @NotNull ... rest
    ) {
        if (!this.isLoaded()) {
            return;
        }

        switch (chatType) {
            case GLOBAL -> {
                if (this.globalChannel != null) {
                    this.globalChannel.sendMessageEmbeds(first, rest).queue();
                }
            }
            case LOCAL -> {
                if (this.localChannel != null) {
                    this.localChannel.sendMessageEmbeds(first, rest).queue();
                }
            }
        }
    }

    /**
     * Sends an embeds to the private channel of the specified user
     *
     * @param user  The user to send the message to
     * @param first The first embed to send
     * @param rest  The rest of the embeds to send
     */
    public void sendEmbeds(
            final @NotNull User user,
            final @NotNull MessageEmbed first,
            final MessageEmbed @NotNull ... rest
    ) {
        if (this.isLoaded()) {
            user.openPrivateChannel().queue(
                    privateChannel -> privateChannel.sendMessageEmbeds(first, rest).queue()
            );
        }
    }

    /**
     * Sends an embeds to the private channel of the specified user
     *
     * @param userId The id of the user to send the message to
     * @param first  The first embed to send
     * @param rest   The rest of the embeds to send
     * @see #retrieveUser(long)
     * @see #sendEmbeds(User, MessageEmbed, MessageEmbed...)
     */
    public void sendEmbeds(
            final long userId,
            final @NotNull MessageEmbed first,
            final MessageEmbed @NotNull ... rest
    ) {
        this.retrieveUser(userId).ifPresent(
                user -> this.sendEmbeds(user, first, rest)
        );
    }

    /**
     * Sends an embed action message with the specified color, player head and
     * action message to the specified chat type
     *
     * @param chatType      The chat type to send the message to
     * @param playerName    The name of the player who did the action
     * @param actionMessage The action message to send
     * @param colorRaw      The color of the embed
     */
    public void sendActionMessage(
            final @NotNull ChatType chatType,
            final @NotNull String playerName,
            final @NotNull String actionMessage,
            final int colorRaw
    ) {
        if (!this.isLoaded()) {
            return;
        }

        this.sendEmbeds(
                chatType,
                new MessageEmbed(
                        null,
                        null,
                        null,
                        EmbedType.RICH,
                        null,
                        colorRaw,
                        null,
                        null,
                        new MessageEmbed.AuthorInfo(
                                actionMessage,
                                null,
                                "https://cravatar.eu/helmavatar/" + playerName + "/128.png",
                                null
                        ),
                        null,
                        null,
                        null,
                        null
                )
        );
    }

    /**
     * Loads the Discord bot
     *
     * @throws IllegalStateException If the Discord bot is already loaded
     */
    public void load() throws IllegalStateException {
        if (this.isLoaded()) {
            throw new IllegalStateException("Discord bot is already loaded!");
        }

        final Logger logger = this.plugin.getLogger();
        final Config config = this.plugin.getConfiguration();
        final String botToken = config.getYaml().getString("discord.bot-token");

        this.plugin.runTaskAsync(() -> {
            try {
                this.jda = this.buildJda(botToken);
            } catch (final Throwable e) {
                throw new RuntimeException("An error occurred while loading Discord bot!", e);
            }

            if (this.jda == null) {
                logger.warning("Discord bot not found!");
                this.plugin.setLoadedCustoms(true);

                return;
            }

            this.mainGuild = this.jda.getGuildById(config.discordServerId);

            if (this.mainGuild == null) {
                logger.warning("Discord server not found!");
                this.plugin.setLoadedCustoms(true);
                this.unload();

                return;
            }

            if (config.memberRoleId != 0) {
                this.memberRole = this.mainGuild.getRoleById(config.memberRoleId);

                if (this.memberRole == null) {
                    logger.warning("Discord member role not found!");
                }
            }

            if (config.discordGlobalChannelId != 0) {
                this.globalChannel = this.jda.getTextChannelById(config.discordGlobalChannelId);

                if (this.globalChannel == null) {
                    logger.warning("Discord global channel not found!");
                }
            }

            if (config.discordLocalChannelId != 0) {
                this.localChannel = this.jda.getTextChannelById(config.discordLocalChannelId);

                if (this.localChannel == null) {
                    logger.warning("Discord local channel not found!");
                }
            }

            final Presence presence = this.jda.getPresence();

            if (config.developerMode) {
                presence.setStatus(OnlineStatus.DO_NOT_DISTURB);
            }

            presence.setActivity(
                    Activity.playing(ChatUtils.serializePlainComponent(
                            DISCORD_BOT_STATUS.args(Component.text(this.plugin.getServer().getMinecraftVersion())))
                    )
            );

            this.reloadDiscordListeners();
            this.reloadSlashCommands();

            this.sendMessage(ChatType.GLOBAL, SERVER_ENABLED);
            this.sendMessage(ChatType.LOCAL, SERVER_ENABLED);

            this.plugin.setLoadedCustoms(true);
        });
    }

    /**
     * Unloads the Discord bot
     */
    public void unload() {
        if (this.isLoaded()) {
            this.jda.shutdown();
        }
    }

    /**
     * Reloads the Discord listeners
     *
     * @see MSDiscordListener
     * @see AbstractMSDiscordListener
     */
    @SuppressWarnings("unchecked")
    public void reloadDiscordListeners() {
        if (!this.isLoaded()) {
            return;
        }

        final Logger logger = this.plugin.getLogger();
        final ClassLoader classLoader = this.plugin.getClass().getClassLoader();

        if (!this.listeners.isEmpty()) {
            this.listeners.clear();
        }

        this.plugin.getClassNames().parallelStream()
        .forEach(className -> {
            try {
                final var clazz = classLoader.loadClass(className);

                if (clazz.isAnnotationPresent(MSDiscordListener.class)) {
                    if (clazz.getDeclaredConstructor().newInstance() instanceof final AbstractMSDiscordListener<?> listener) {
                        final var castedListener = (AbstractMSDiscordListener<MSEssentials>) listener;

                        castedListener.register(this.plugin);
                        this.listeners.add(castedListener);
                    } else {
                        logger.warning(
                                "Annotated class with MSDiscordListener is not instance of AbstractMSDiscordListener (" + className + ")"
                        );
                    }
                }
            } catch (final Exception e) {
                logger.log(
                        Level.SEVERE,
                        "Failed to load discord listener",
                        e
                );
            }
        });
    }

    /**
     * Reloads the slash commands
     *
     * @see SlashCommand
     * @see SlashCommandExecutor
     */
    @SuppressWarnings("unchecked")
    public void reloadSlashCommands() {
        if (!this.isLoaded()) {
            return;
        }

        final Logger logger = this.plugin.getLogger();
        final ClassLoader classLoader = this.plugin.getClass().getClassLoader();

        if (!this.slashCommandMap.isEmpty()) {
            this.slashCommandMap.clear();
        }

        this.plugin.getClassNames().parallelStream()
        .forEach(className -> {
            try {
                final var clazz = classLoader.loadClass(className);

                if (clazz.isAnnotationPresent(SlashCommand.class)) {
                    if (clazz.getDeclaredConstructor().newInstance() instanceof final SlashCommandExecutor<?> executor) {
                        this.slashCommandMap.put(
                                executor.getData().getName(),
                                (SlashCommandExecutor<MSEssentials>) executor
                        );
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
    }

    private @Nullable JDA buildJda(final @Nullable String botToken) throws InterruptedException, IllegalStateException {
        return StringUtils.isBlank(botToken)
                ? null
                : JDABuilder
                .createDefault(botToken)
                .enableIntents(
                        Collections.singletonList(
                                GatewayIntent.MESSAGE_CONTENT
                        )
                )
                .addEventListeners(new ListenerAdapter() {

                    @Override
                    public void onShutdown(@NotNull ShutdownEvent event) {
                        DiscordHandler.this.jda = null;
                        DiscordHandler.this.mainGuild = null;
                        DiscordHandler.this.globalChannel = null;
                        DiscordHandler.this.localChannel = null;
                        DiscordHandler.this.memberRole = null;
                    }
                })
                .build()
                .awaitReady();
    }
}
