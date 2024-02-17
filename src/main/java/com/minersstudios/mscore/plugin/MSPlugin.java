package com.minersstudios.mscore.plugin;

import com.google.common.base.Charsets;
import com.minersstudios.mscore.command.api.AbstractCommandExecutor;
import com.minersstudios.mscore.command.api.Commodore;
import com.minersstudios.mscore.command.api.MSCommand;
import com.minersstudios.mscore.inventory.plugin.AbstractInventoryHolder;
import com.minersstudios.mscore.inventory.plugin.InventoryHolder;
import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.mscore.listener.api.packet.AbstractPacketListener;
import com.minersstudios.mscore.listener.api.packet.PacketListener;
import com.minersstudios.mscore.locale.Translations;
import com.minersstudios.mscore.packet.PacketEvent;
import com.minersstudios.mscore.packet.PacketListenersMap;
import com.minersstudios.mscore.packet.PacketRegistry;
import com.minersstudios.mscore.packet.PacketType;
import com.minersstudios.mscore.status.Status;
import com.minersstudios.mscore.status.StatusHandler;
import com.minersstudios.mscore.status.SuccessStatus;
import com.minersstudios.mscore.utility.*;
import com.mojang.brigadier.tree.LiteralCommandNode;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.minersstudios.mscore.status.Status.successHigh;
import static com.minersstudios.mscore.status.Status.successLow;
import static net.kyori.adventure.text.Component.text;

/**
 * Represents a Java plugin and its main class. It contains methods for auto
 * registering commands and listeners. This is an indirect implementation of
 * {@link JavaPlugin}.
 *
 * @see #load()
 * @see #enable()
 * @see #disable()
 */
@ApiStatus.Internal
public abstract class MSPlugin<T extends MSPlugin<T>> extends JavaPlugin {
    private final StatusHandler statusHandler;
    private final List<String> classNames;
    private final Map<MSCommand, AbstractCommandExecutor<T>> commandMap;
    private final Map<Class<? extends AbstractInventoryHolder<T>>, AbstractInventoryHolder<T>> inventoryHolderMap;
    private final List<AbstractEventListener<T>> eventListeners;
    private final List<AbstractPacketListener<T>> packetListeners;
    private final File pluginFolder;
    private final File configFile;
    private Commodore commodore;
    private FileConfiguration newConfig;

    //<editor-fold desc="Plugin statuses" defaultstate="collapsed">
    /** High-priority plugin initializing status */
    public static final SuccessStatus INITIALIZING = successHigh("INITIALIZING");
    /** Low-priority plugin initialized status */
    public static final SuccessStatus INITIALIZED = successLow("INITIALIZED");

    /** High-priority plugin loading status */
    public static final SuccessStatus LOADING = successHigh("LOADING");
    /** Low-priority plugin loaded status */
    public static final SuccessStatus LOADED = successLow("LOADED");

    /** High-priority plugin enabling status */
    public static final SuccessStatus ENABLING = successHigh("ENABLING");
    /** High-priority plugin enabled status */
    public static final SuccessStatus ENABLED = successHigh("ENABLED");

    /** High-priority plugin disabling status */
    public static final SuccessStatus DISABLING = successHigh("DISABLING");
    /** High-priority plugin disabled status */
    public static final SuccessStatus DISABLED = successHigh("DISABLED");
    //</editor-fold>

    private static final Field DATA_FOLDER_FIELD;
    private static final Constructor<PluginCommand> COMMAND_CONSTRUCTOR;
    private static final File GLOBAL_FOLDER;
    private static final GlobalCache GLOBAL_CACHE;
    private static final GlobalConfig GLOBAL_CONFIG;

    static {
        GLOBAL_FOLDER = new File(SharedConstants.GLOBAL_FOLDER_PATH);
        GLOBAL_CACHE = new GlobalCache();
        GLOBAL_CONFIG = new GlobalConfig();

        if (GLOBAL_FOLDER.mkdirs()) {
            MSLogger.info("The global folder was created");
        }

        GLOBAL_CONFIG.reload();

        try {
            DATA_FOLDER_FIELD = JavaPlugin.class.getDeclaredField("dataFolder");
            COMMAND_CONSTRUCTOR = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);

            DATA_FOLDER_FIELD.setAccessible(true);
            COMMAND_CONSTRUCTOR.setAccessible(true);
        } catch (final NoSuchFieldException e) {
            throw new IllegalStateException("Could not find data folder field", e);
        } catch (final NoSuchMethodException e) {
            throw new IllegalStateException("Could not find command constructor", e);
        }

        initClass(Translations.class);
        initClass(PacketRegistry.class);
        initClass(Font.class);
        initClass(BlockUtils.class);
        initClass(ChatUtils.class);
        initClass(DateUtils.class);
        initClass(LocationUtils.class);
    }

    protected MSPlugin() {
        this.statusHandler = new StatusHandler();

        this.assignStatus(INITIALIZING);

        this.classNames = this.loadClassNames(SharedConstants.GLOBAL_PACKAGE + '.' + this.getName().toLowerCase());
        this.commandMap = new Object2ObjectOpenHashMap<>();
        this.inventoryHolderMap = new Object2ObjectOpenHashMap<>();
        this.eventListeners = new ObjectArrayList<>();
        this.packetListeners = new ObjectArrayList<>();
        this.pluginFolder = new File(GLOBAL_FOLDER, this.getName() + '/');
        this.configFile = new File(this.pluginFolder, "config.yml");

        try {
            DATA_FOLDER_FIELD.set(this, this.pluginFolder);
        } catch (final Throwable e) {
            throw new IllegalStateException("Could not set data folder", e);
        }

        this.assignStatus(INITIALIZED);
    }

    /**
     * @return The status handler of the plugin
     */
    public final @NotNull StatusHandler getStatusHandler() {
        return this.statusHandler;
    }

    /**
     * @return An optional containing the high-priority status if present,
     *         otherwise an empty optional
     * @see StatusHandler
     */
    public final @NotNull Optional<Status> getStatus() {
        return this.statusHandler.getHighStatus();
    }

    /**
     * Assigns the specified status and runs all the registered watchers with
     * this status
     *
     * @param status Status to be assigned
     * @see StatusHandler
     */
    public final void assignStatus(final @NotNull Status status) {
        this.statusHandler.assignStatus(status);
    }

    /**
     * Gets the names of all plugin classes, similar to the package string
     * <br>
     * Example: "com.example.Example"
     *
     * @return The unmodifiable set of class names
     */
    public final @NotNull @UnmodifiableView List<String> getClassNames() {
        return Collections.unmodifiableList(this.classNames);
    }

    /**
     * @return The unmodifiable map of commands
     */
    public final @NotNull @UnmodifiableView Map<MSCommand, AbstractCommandExecutor<T>> getCommandMap() {
        return Collections.unmodifiableMap(this.commandMap);
    }

    /**
     * @return The unmodifiable map of custom inventories
     */
    public final @NotNull @UnmodifiableView Map<Class<? extends AbstractInventoryHolder<T>>, AbstractInventoryHolder<T>> getInventoryHolderMap() {
        return Collections.unmodifiableMap(this.inventoryHolderMap);
    }

    /**
     * @param clazz Class of the custom inventory holder
     * @return The optional custom inventory holder
     */
    public final @NotNull Optional<AbstractInventoryHolder<T>> getInventoryHolder(final @NotNull Class<? extends AbstractInventoryHolder<T>> clazz) {
        return Optional.ofNullable(this.inventoryHolderMap.get(clazz));
    }

    /**
     * @return The unmodifiable list of event listeners
     */
    public final @NotNull @UnmodifiableView List<AbstractEventListener<T>> getEventListeners() {
        return Collections.unmodifiableList(this.eventListeners);
    }

    /**
     * @return The unmodifiable list of packet listeners
     */
    public final @NotNull @UnmodifiableView List<AbstractPacketListener<T>> getPacketListeners() {
        return Collections.unmodifiableList(this.packetListeners);
    }

    /**
     * @return Plugin config file "/config/minersstudios/PLUGIN_NAME/config.yml"
     */
    public final @NotNull File getConfigFile() {
        return this.configFile;
    }

    /**
     * @return Plugin folder "/config/minersstudios/PLUGIN_NAME"
     */
    public final @NotNull File getPluginFolder() {
        return this.pluginFolder;
    }

    /**
     * @return Commodore instance
     */
    public final @NotNull Commodore getCommodore() {
        return this.commodore;
    }

    /**
     * Gets a {@link FileConfiguration} for this plugin, read through
     * "config.yml"
     * <br>
     * If there is a default config.yml embedded in this plugin, it will be
     * provided as a default for this Configuration.
     *
     * @return Plugin configuration
     */
    @Override
    public @NotNull FileConfiguration getConfig() {
        if (this.newConfig == null) {
            this.reloadConfig();
        }

        return this.newConfig;
    }

    /**
     * @param status Status to be checked
     * @return True if the status is present, false otherwise
     * @see StatusHandler
     */
    public boolean containsStatus(final @NotNull Status status) {
        return this.statusHandler.contains(status);
    }

    /**
     * @param first First status to be checked
     * @param rest  Rest of the statuses to be checked
     * @return True if any of the statuses are present, false otherwise
     * @see StatusHandler
     */
    public boolean containsAnyStatus(
            final @NotNull Status first,
            final Status @NotNull ... rest
    ) {
        return this.statusHandler.containsAny(first, rest);
    }

    /**
     * @param first First status to be checked
     * @param rest  Rest of the statuses to be checked
     * @return True if all the statuses are present, false otherwise
     * @throws IllegalArgumentException If there are multiple high-priority
     *                                  statuses
     * @see StatusHandler
     */
    public boolean containsAllStatuses(
            final @NotNull Status first,
            final Status @NotNull ... rest
    ) throws IllegalArgumentException {
        return this.statusHandler.containsAll(first, rest);
    }

    /**
     * Called after a plugin is loaded but before it has been enabled. When
     * multiple plugins are loaded, the onLoad() for all plugins is called
     * before any onEnable() is called. Also logs the time it took to load the
     * plugin.
     *
     * @see #loadAnnotated()
     * @see #load()
     */
    @Override
    public final void onLoad() {
        final long time = System.currentTimeMillis();

        this.assignStatus(LOADING);
        this.loadAnnotated();
        this.load();
        this.assignStatus(LOADED);

        this.getComponentLogger()
        .info(
                text(
                        "Loaded in " + (System.currentTimeMillis() - time) + "ms",
                        NamedTextColor.GREEN
                )
        );
    }

    /**
     * Called when this plugin is enabled. Registers all commands and listeners.
     * After that, it calls the enable() method. Also logs the time it took to
     * enable the plugin.
     *
     * @see #registerCommands()
     * @see #registerInventoryHolders()
     * @see #registerEventListeners()
     * @see #registerPacketListeners()
     * @see #enable()
     */
    @Override
    public final void onEnable() {
        final long time = System.currentTimeMillis();

        this.assignStatus(ENABLING);

        this.commodore = new Commodore(this);

        this.registerEventListeners();
        this.registerPacketListeners();
        this.registerInventoryHolders();
        this.registerCommands();
        this.enable();
        this.assignStatus(ENABLED);

        if (this.isEnabled()) {
            this.getComponentLogger()
            .info(
                    text(
                            "Enabled in " + (System.currentTimeMillis() - time) + "ms",
                            NamedTextColor.GREEN
                    )
            );
        }
    }

    /**
     * Called when this plugin is disabled. After that, it calls the disable()
     * method. Also logs the time it took to disable the plugin.
     * @see #disable()
     */
    @Override
    public final void onDisable() {
        final long time = System.currentTimeMillis();

        this.assignStatus(DISABLING);
        this.disable();
        this.assignStatus(DISABLED);

        this.getComponentLogger()
        .info(
                text(
                        "Disabled in " + (System.currentTimeMillis() - time) + "ms",
                        NamedTextColor.GREEN
                )
        );
    }

    /**
     * Called after a plugin is loaded but before it has been enabled. When
     * multiple plugins are loaded, the load() for all plugins is called before
     * any enable() is called.
     * Same as {@link JavaPlugin#onLoad()}
     *
     * @see MSPlugin#onLoad()
     */
    public void load() {
        // plugin load logic
    }

    /**
     * Called when this plugin is enabled.
     * Same as {@link JavaPlugin#onEnable()}
     *
     * @see MSPlugin#onEnable()
     */
    public void enable() {
        // plugin enable logic
    }

    /**
     * Called when this plugin is disabled.
     * Same as {@link JavaPlugin#onDisable()}
     *
     * @see MSPlugin#onDisable()
     */
    public void disable() {
        // plugin disable logic
    }

    /**
     * Discards any data in {@link #getConfig()} and reloads from disk.
     */
    @Override
    public void reloadConfig() {
        this.newConfig = YamlConfiguration.loadConfiguration(this.configFile);
        final InputStream defaultInput = this.getResource("config.yml");

        if (defaultInput == null) {
            return;
        }

        final InputStreamReader inputReader = new InputStreamReader(defaultInput, Charsets.UTF_8);
        final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(inputReader);

        this.newConfig.setDefaults(configuration);
    }

    /**
     * Saves the {@link FileConfiguration} retrievable by {@link #getConfig()}
     */
    @Override
    public void saveConfig() {
        try {
            this.getConfig().save(this.configFile);
        } catch (final IOException e) {
            this.getLogger().severe("Could not save config to " + this.configFile);
        }
    }

    /**
     * Saves the raw contents of any resource embedded with a plugin's .jar file
     * assuming it can be found using {@link #getResource(String)}.
     * <br>
     * The resource is saved into the plugin's data folder using the same
     * hierarchy as the .jar file (subdirectories are preserved).
     *
     * @param resourcePath The embedded resource path to look for within the
     *                     plugin's .jar file. (No preceding slash).
     * @param replace If true, the embedded resource will overwrite the contents
     *                of an existing file.
     * @throws IllegalArgumentException if the resource path is null, empty,
     *                                  or points to a nonexistent resource.
     */
    @Override
    public void saveResource(
            final @NotNull String resourcePath,
            final boolean replace
    ) throws IllegalArgumentException {
        if (ChatUtils.isBlank(resourcePath)) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        final String path = resourcePath.replace('\\', '/');
        final InputStream in = this.getResource(path);

        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + path + "' cannot be found");
        }

        final Logger logger = this.getLogger();
        final String dirPath = path.substring(0, Math.max(path.lastIndexOf('/'), 0));
        final File outFile = new File(this.pluginFolder, path);
        final File outDir = new File(this.pluginFolder, dirPath);
        final String outFileName = outFile.getName();
        final String outDirName = outDir.getName();

        if (
                !outDir.exists()
                && !outDir.mkdirs()
        ) {
            logger.warning("Directory " + outDirName + " creation failed");
        }

        if (
                !outFile.exists()
                || replace
        ) {
            try (
                    final var out = new FileOutputStream(outFile);
                    in
            ) {
                final byte[] buffer = new byte[1024];
                int read;

                while ((read = in.read(buffer)) >= 0) {
                    out.write(buffer, 0, read);
                }
            } catch (final IOException e) {
                logger.log(
                        Level.SEVERE,
                        "Could not save " + outFileName + " to " + outFile,
                        e
                );
            }
        } else {
            logger.warning(
                    "Could not save " + outFileName + " to " + outFile + " because " + outFileName + " already exists."
            );
        }
    }

    /**
     * Saves the raw contents of the default config.yml file to the location
     * retrievable by {@link #getConfig()}.
     * <br>
     * This should fail silently if the config.yml already exists.
     */
    @Override
    public void saveDefaultConfig() {
        if (!this.configFile.exists()) {
            this.saveResource("config.yml", false);
        }
    }

    /**
     * Calls a packet event to all registered packet listeners with the
     * whitelist containing the packet type of the event
     *
     * @param event Packet event to be called
     * @see PacketEvent
     */
    public void callPacketReceiveEvent(final @NotNull PacketEvent event) {
        final PacketType packetType = event.getPacketContainer().getType();
        final PacketListenersMap listenersMap = GLOBAL_CACHE.packetListenerMap;

        if (listenersMap.containsPacketType(packetType)) {
            for (final var listener : listenersMap.getListeners(packetType)) {
                listener.onPacketReceive(event);
            }
        }
    }

    /**
     * Calls a packet event to all registered packet listeners with the
     * whitelist containing the packet type of the event
     *
     * @param event Packet event to be called
     * @see PacketEvent
     */
    public void callPacketSendEvent(final @NotNull PacketEvent event) {
        final PacketType packetType = event.getPacketContainer().getType();
        final PacketListenersMap listenersMap = GLOBAL_CACHE.packetListenerMap;

        if (listenersMap.containsPacketType(packetType)) {
            for (final var listener : listenersMap.getListeners(packetType)) {
                listener.onPacketSend(event);
            }
        }
    }

    /**
     * Registers a command with the plugin
     *
     * @param command Command to be registered
     * @param executor  Command executor
     * @see MSCommand
     * @see AbstractCommandExecutor
     * @see #registerCommands()
     */
    public final void registerCommand(
            final @NotNull MSCommand command,
            final @NotNull AbstractCommandExecutor<T> executor
    ) {
        final String name = command.command();
        final var commandNode = executor.getCommandNode();
        final PluginCommand bukkitCommand = this.getCommand(name);
        final PluginCommand pluginCommand =
                bukkitCommand == null
                ? this.createCommand(name)
                : bukkitCommand;
        final Server server = this.getServer();

        if (pluginCommand == null) {
            this.getLogger().severe("Failed to register command : " + name);

            return;
        }

        final var aliases = Arrays.asList(command.aliases());
        final String usage = command.usage();
        final String description = command.description();
        final String permissionStr = command.permission();

        if (!aliases.isEmpty()) {
            pluginCommand.setAliases(aliases);
        }

        if (!usage.isEmpty()) {
            pluginCommand.setUsage(usage);
        }

        if (!description.isEmpty()) {
            pluginCommand.setDescription(description);
        }

        if (!permissionStr.isEmpty()) {
            final PluginManager pluginManager = server.getPluginManager();
            final var children = new Object2BooleanOpenHashMap<String>();
            final String[] keys = command.permissionParentKeys();
            final boolean[] values = command.permissionParentValues();

            if (keys.length != values.length) {
                this.getLogger().severe("Permission and boolean array lengths do not match in command : " + name);
            } else {
                for (int i = 0; i < keys.length; ++i) {
                    children.put(keys[i], values[i]);
                }
            }

            if (pluginManager.getPermission(permissionStr) == null) {
                final Permission permission = new Permission(permissionStr, command.permissionDefault(), children);

                pluginManager.addPermission(permission);
            }

            pluginCommand.setPermission(permissionStr);
        }

        if (command.playerOnly()) {
            GLOBAL_CACHE.onlyPlayerCommandSet.add(name);
            GLOBAL_CACHE.onlyPlayerCommandSet.addAll(aliases);
        }

        executor.setPlugin(this);
        pluginCommand.setExecutor(executor);
        pluginCommand.setTabCompleter(executor);

        if (commandNode != null) {
            this.commodore.register(
                    pluginCommand,
                    (LiteralCommandNode<?>) commandNode,
                    pluginCommand::testPermissionSilent
            );
        }

        server.getCommandMap().register(this.getName(), pluginCommand);
    }

    /**
     * Creates a new {@link PluginCommand} instance
     *
     * @param command Command name
     * @return A new {@link PluginCommand} instance or null if failed
     */
    public @Nullable PluginCommand createCommand(final @NotNull String command) {
        try {
            return COMMAND_CONSTRUCTOR.newInstance(command, this);
        } catch (final InstantiationException | IllegalAccessException | InvocationTargetException e) {
            this.getLogger().log(Level.SEVERE, "Failed to create command : " + command, e);
            return null;
        }
    }

    /**
     * Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to ensure the thread-safety of asynchronous tasks.
     * <br>
     * Returns a task that will run asynchronously.
     *
     * @param task The task to be run
     * @return A BukkitTask that contains the id number
     * @throws IllegalArgumentException If the current plugin is not enabled
     */
    public @NotNull BukkitTask runTaskAsync(final @NotNull Runnable task) {
        return this.getServer().getScheduler().runTaskAsynchronously(this, task);
    }

    /**
     * Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to ensure the thread-safety of asynchronous tasks.
     * <br>
     * Returns a task that will repeatedly run asynchronously until cancelled,
     * starting after the specified number of server ticks.
     *
     * @param task   The task to be run
     * @param delay  The ticks to wait before running task for the first time
     * @param period The ticks to wait between runs
     * @return A BukkitTask that contains the id number
     * @throws IllegalArgumentException If the current plugin is not enabled
     */
    public @NotNull BukkitTask runTaskTimerAsync(
            final @NotNull Runnable task,
            final long delay,
            final long period
    ) {
        return this.getServer().getScheduler().runTaskTimerAsynchronously(this, task, delay, period);
    }

    /**
     * Returns a task that will run on the next server tick
     *
     * @param task The task to be run
     * @return A BukkitTask that contains the id number
     * @throws IllegalArgumentException If the current plugin is not enabled
     */
    public @NotNull BukkitTask runTask(final @NotNull Runnable task) {
        return this.getServer().getScheduler().runTask(this, task);
    }

    /**
     * Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to ensure the thread-safety of asynchronous tasks.
     * <br>
     * Returns a task that will run asynchronously after the specified number
     * of server ticks.
     *
     * @param task  The task to be run
     * @param delay The ticks to wait before running the task
     * @return A BukkitTask that contains the id number
     * @throws IllegalArgumentException If the current plugin is not enabled
     */
    public @NotNull BukkitTask runTaskLaterAsync(
            final @NotNull Runnable task,
            final long delay
    ) {
        return this.getServer().getScheduler().runTaskLaterAsynchronously(this, task, delay);
    }

    /**
     * Returns a task that will run after the specified number of server ticks
     *
     * @param task  The task to be run
     * @param delay The ticks to wait before running the task
     * @return A BukkitTask that contains the id number
     * @throws IllegalArgumentException If the current plugin is not enabled
     */
    public @NotNull BukkitTask runTaskLater(
            final @NotNull Runnable task,
            final long delay
    ) {
        return this.getServer().getScheduler().runTaskLater(this, task, delay);
    }

    /**
     * Returns a task that will repeatedly run until cancelled, starting after
     * the specified number of server ticks
     *
     * @param task   The task to be run
     * @param delay  The ticks to wait before running the task
     * @param period The ticks to wait between runs
     * @return A BukkitTask that contains the id number
     * @throws IllegalArgumentException If the current plugin is not enabled
     */
    public @NotNull BukkitTask runTaskTimer(
            final @NotNull Runnable task,
            final long delay,
            final long period
    ) {
        return this.getServer().getScheduler().runTaskTimer(this, task, delay, period);
    }

    /**
     * Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to ensure the thread-safety of asynchronous tasks.
     * <br>
     * Returns a task that will run asynchronously.
     *
     * @param task The task to be run
     * @throws IllegalArgumentException If the current plugin is not enabled
     */
    public void runTaskAsync(final @NotNull Consumer<BukkitTask> task) {
        this.getServer().getScheduler().runTaskAsynchronously(this, task);
    }

    /**
     * Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to ensure the thread-safety of asynchronous tasks.
     * <br>
     * Returns a task that will repeatedly run asynchronously until cancelled,
     * starting after the specified number of server ticks.
     *
     * @param task   The task to be run
     * @param delay  The ticks to wait before running task for the first time
     * @param period The ticks to wait between runs
     * @throws IllegalArgumentException If the current plugin is not enabled
     */
    public void runTaskTimerAsync(
            final @NotNull Consumer<BukkitTask> task,
            final long delay,
            final long period
    ) {
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, task, delay, period);
    }

    /**
     * Returns a task that will run on the next server tick
     *
     * @param task The task to be run
     * @throws IllegalArgumentException If the current plugin is not enabled
     */
    public void runTask(final @NotNull Consumer<BukkitTask> task) {
        this.getServer().getScheduler().runTask(this, task);
    }

    /**
     * Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to ensure the thread-safety of asynchronous tasks.
     * <br>
     * Returns a task that will run asynchronously after the specified number
     * of server ticks.
     *
     * @param task  The task to be run
     * @param delay The ticks to wait before running the task
     * @throws IllegalArgumentException If the current plugin is not enabled
     */
    public void runTaskLaterAsync(
            final @NotNull Consumer<BukkitTask> task,
            final long delay
    ) {
        this.getServer().getScheduler().runTaskLaterAsynchronously(this, task, delay);
    }

    /**
     * Returns a task that will run after the specified number of server ticks
     *
     * @param task  The task to be run
     * @param delay The ticks to wait before running the task
     * @throws IllegalArgumentException If the current plugin is not enabled
     */
    public void runTaskLater(
            final @NotNull Consumer<BukkitTask> task,
            final long delay
    ) {
        this.getServer().getScheduler().runTaskLater(this, task, delay);
    }

    /**
     * Returns a task that will repeatedly run until cancelled, starting after
     * the specified number of server ticks
     *
     * @param task   The task to be run
     * @param delay  The ticks to wait before running the task
     * @param period The ticks to wait between runs
     * @throws IllegalArgumentException If the current plugin is not enabled
     */
    public void runTaskTimer(
            final @NotNull Consumer<BukkitTask> task,
            final long delay,
            final long period
    ) {
        this.getServer().getScheduler().runTaskTimer(this, task, delay, period);
    }

    /**
     * Opens a custom inventory for the given player if the custom inventory is
     * registered to the plugin
     *
     * @param clazz  Class of the custom inventory holder
     * @param player Player to open the custom inventory
     * @see AbstractInventoryHolder
     */
    public void openCustomInventory(
            final @NotNull Class<? extends AbstractInventoryHolder<T>> clazz,
            final @NotNull Player player
    ) {
        this.getInventoryHolder(clazz)
        .ifPresent(
                holder -> holder.open(player)
        );
    }

    /**
     * Loads all classes in the plugin jar file with these annotations:
     * <ul>
     *     <li>{@link PacketListener}</li>
     *     <li>{@link EventListener}</li>
     *     <li>{@link InventoryHolder}</li>
     *     <li>{@link MSCommand}</li>
     * </ul>
     */
    @SuppressWarnings("unchecked")
    private void loadAnnotated() {
        final Logger logger = this.getLogger();
        final ClassLoader classLoader = this.getClassLoader();

        this.classNames.parallelStream()
        .forEach(className -> {
            final Class<?> clazz;

            try {
                clazz = classLoader.loadClass(className);
            } catch (final ClassNotFoundException e) {
                logger.log(
                        Level.SEVERE,
                        "Failed to load class : " + className,
                        e
                );

                return;
            }

            final Class<? extends Annotation> annotationClass;
            final Class<?> expectedClass;
            final Consumer<Object> consumer;

            if (clazz.isAnnotationPresent(EventListener.class)) {
                annotationClass = EventListener.class;
                expectedClass = AbstractEventListener.class;
                consumer = listener -> {
                    synchronized (this.eventListeners) {
                        this.eventListeners.add((AbstractEventListener<T>) listener);
                    }
                };
            } else if (clazz.isAnnotationPresent(PacketListener.class)) {
                annotationClass = PacketListener.class;
                expectedClass = AbstractPacketListener.class;
                consumer = listener -> {
                    synchronized (this.packetListeners) {
                        this.packetListeners.add((AbstractPacketListener<T>) listener);
                    }
                };
            } else if (clazz.isAnnotationPresent(InventoryHolder.class)) {
                annotationClass = InventoryHolder.class;
                expectedClass = AbstractInventoryHolder.class;
                consumer = holder -> {
                    synchronized (this.inventoryHolderMap) {
                        this.inventoryHolderMap.put(
                                (Class<? extends AbstractInventoryHolder<T>>) clazz,
                                (AbstractInventoryHolder<T>) holder
                        );
                    }
                };
            } else {
                final MSCommand command = clazz.getAnnotation(MSCommand.class);

                if (command != null) {
                    annotationClass = MSCommand.class;
                    expectedClass = AbstractCommandExecutor.class;
                    consumer = executor -> {
                        synchronized (this.commandMap) {
                            this.commandMap.put(
                                    command,
                                    (AbstractCommandExecutor<T>) executor
                            );
                        }
                    };
                } else {
                    return;
                }
            }

            this.loadAnnotation(
                    clazz,
                    annotationClass,
                    expectedClass,
                    consumer
            );
        });
    }

    /**
     * @param givenClass    Class to be checked
     * @param annotation    Annotation to be checked
     * @param expectedClass Expected class of the given class
     * @param consumer      Consumer to be called if the given class is extended
     *                      using the expected class
     */
    private void loadAnnotation(
            final @NotNull Class<?> givenClass,
            final @NotNull Class<? extends Annotation> annotation,
            final @NotNull Class<?> expectedClass,
            final @NotNull Consumer<Object> consumer
    ) {
        final Logger logger = this.getLogger();

        try {
            final var instance = givenClass.getDeclaredConstructor().newInstance();

            if (expectedClass.isInstance(instance)) {
                consumer.accept(expectedClass.cast(instance));
            } else {
                logger.warning(
                        "Annotated class with " + annotation.getSimpleName() +
                        " must be extended using " + expectedClass.getSimpleName() +
                        '(' + givenClass.getName() + ')'
                );
            }
        } catch (final Throwable e) {
            throw new IllegalStateException("Failed to load annotated class : " + givenClass.getName(), e);
        }
    }

    /**
     * Registers all event listeners in the project that is annotated with
     * {@link EventListener}. All listeners must be extended using
     * {@link AbstractEventListener}.
     *
     * @see EventListener
     * @see AbstractEventListener
     */
    @SuppressWarnings("unchecked")
    private void registerEventListeners() {
        for (final var listener : this.eventListeners) {
            listener.register((T) this);
        }
    }

    /**
     * Registers all packet listeners in the project that is annotated with
     * {@link PacketListener}. All listeners must be extended using
     * {@link AbstractPacketListener}.
     *
     * @see PacketListener
     * @see AbstractPacketListener
     */
    @SuppressWarnings("unchecked")
    private void registerPacketListeners() {
        for (final var listener : this.packetListeners) {
            listener.register((T) this);
        }
    }

    /**
     * Registers all inventory holders in the project that is annotated with
     * {@link InventoryHolder}. All inventory holders must be extended using
     * {@link AbstractInventoryHolder}.
     *
     * @see InventoryHolder
     * @see AbstractInventoryHolder
     */
    @SuppressWarnings("unchecked")
    private void registerInventoryHolders() {
        for (final var holder : this.inventoryHolderMap.values()) {
            holder.register((T) this);
        }
    }

    /**
     * Registers all commands in the project that is annotated with
     * {@link MSCommand}. All commands must be implemented using
     * {@link AbstractCommandExecutor}.
     *
     * @see MSCommand
     * @see AbstractCommandExecutor
     * @see #registerCommand(MSCommand, AbstractCommandExecutor)
     */
    private void registerCommands() {
        for (final var entry : this.commandMap.entrySet()) {
            this.registerCommand(
                    entry.getKey(),
                    entry.getValue()
            );
        }
    }

    /**
     * @return The global folder of the MSPlugins
     */
    public static @NotNull File globalFolder() {
        return GLOBAL_FOLDER;
    }

    /**
     * @return The cache of the MSPlugins
     */
    public static @NotNull GlobalCache globalCache() {
        return GLOBAL_CACHE;
    }

    /**
     * @return The global config of the MSPlugins
     */
    public static @NotNull GlobalConfig globalConfig() {
        return GLOBAL_CONFIG;
    }

    /**
     * Loads the names of all classes in the given package
     *
     * @param packageName The package name
     * @return The list of class names
     */
    protected @NotNull List<String> loadClassNames(final @NotNull String packageName) {
        final ClassLoader classLoader = this.getClassLoader();
        final var classNames = new ObjectArrayList<String>();
        final var url = classLoader.getResource(packageName.replace(".", "/"));

        if (url == null) {
            MSLogger.severe("Package " + packageName + " does not exist, class names could not be loaded");

            return classNames;
        }

        try {
            final URI uri = new URI(url.getPath().split("!")[0]);

            if (uri.isAbsolute()) {
                try (final var jarFile = new JarFile(new File(uri))) {
                    jarFile.stream().parallel()
                    .map(JarEntry::getName)
                    .filter(name ->
                            name.endsWith(".class")
                            && !"module-info.class".equals(name)
                    )
                    .map(name ->
                            name
                            .replace('/', '.')
                            .replace(".class", "")
                    )
                    .forEach(className -> {
                        synchronized (classNames) {
                            classNames.add(className);
                        }
                    });
                }
            } else {
                try (final var paths = Files.list(Path.of(url.toURI()))) {
                    paths.parallel()
                    .forEach(path -> {
                        final String fileName = path.getFileName().toString();

                        if (Files.isDirectory(path)) {
                            synchronized (classNames) {
                                classNames.addAll(
                                        this.loadClassNames(packageName + '.' + fileName)
                                );
                            }
                        } else if (fileName.endsWith(".class")) {
                            synchronized (classNames) {
                                classNames.add(
                                        packageName + '.' + fileName.substring(0, fileName.length() - 6)
                                );
                            }
                        }
                    });
                }
            }
        } catch (final Throwable e) {
            throw new IllegalStateException("Failed to load class names", e);
        }

        return classNames;
    }

    /**
     * Initializes the class with the given name
     *
     * @param clazz The class to load
     * @see Class#forName(String)
     * @throws ExceptionInInitializerError If the class could not be initialized
     */
    protected static void initClass(final @NotNull Class<?> clazz) throws ExceptionInInitializerError {
        try {
            Class.forName(clazz.getName());
        } catch (final Throwable e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
