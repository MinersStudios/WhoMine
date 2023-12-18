package com.minersstudios.msblock;

import com.minersstudios.msblock.api.CustomBlockData;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.mscore.util.SharedConstants;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.minecraft.server.MinecraftServer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.UnknownNullability;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main class of the MSBlock plugin
 *
 * @see MSPlugin
 */
public final class MSBlock extends MSPlugin<MSBlock> {
    private static MSBlock singleton;

    private Cache cache;
    private Config config;

    private static final String NOTE_BLOCK_UPDATES = "block-updates.disable-noteblock-updates";

    public MSBlock() {
        singleton = this;
    }

    @Override
    public void load() {
        this.cache = new Cache(this);
        this.config = new Config(this, this.getConfigFile());

        disableNoteBlockUpdates();
        initClass(CustomBlockData.class);
    }

    @Override
    public void enable() {
        this.cache.load();
        this.config.reload();
    }

    @Override
    public void disable() {
        this.cache.unload();
    }

    /**
     * @return The cache of the plugin
     *         or null if the plugin is not enabled
     */
    public @UnknownNullability Cache getCache() {
        return this.cache;
    }

    /**
     * @return The configuration of the plugin
     *         or null if the plugin is not enabled
     */
    public @UnknownNullability Config getConfiguration() {
        return this.config;
    }

    /**
     * @return The instance of the plugin
     *         or null if the plugin is not enabled
     */
    public static @UnknownNullability MSBlock singleton() {
        return singleton;
    }

    /**
     * @return The logger of the plugin
     *         or null if the plugin is not enabled
     */
    public static @UnknownNullability Logger logger() {
        return singleton == null ? null : singleton.getLogger();
    }

    /**
     * @return The component logger of the plugin
     *         or null if the plugin is not enabled
     */
    public static @UnknownNullability ComponentLogger componentLogger() {
        return singleton == null ? null : singleton.getComponentLogger();
    }

    /**
     * @return The cache of the plugin
     *         or null if the plugin is not enabled
     */
    public static @UnknownNullability Cache cache()  {
        return singleton == null ? null : singleton.cache;
    }

    /**
     * @return The configuration of the plugin
     *         or null if the plugin is not enabled
     */
    public static @UnknownNullability Config config() {
        return singleton == null ? null : singleton.config;
    }

    private static void disableNoteBlockUpdates() {
        final MinecraftServer server = MinecraftServer.getServer();
        final File paperGlobalConfig = new File(SharedConstants.PAPER_GLOBAL_CONFIG_PATH);
        final YamlConfiguration paperConfig = YamlConfiguration.loadConfiguration(paperGlobalConfig);

        if (!paperConfig.getBoolean(NOTE_BLOCK_UPDATES, false)) {
            paperConfig.set(NOTE_BLOCK_UPDATES, true);

            try {
                paperConfig.save(paperGlobalConfig);
            } catch (final Exception e) {
                MSLogger.log(
                        Level.SEVERE,
                        "Failed to save paper-global.yml with " + NOTE_BLOCK_UPDATES + " enabled",
                        e
                );
            }

            server.paperConfigurations.reloadConfigs(server);
            server.server.reloadCount++;
        }
    }
}
