package com.minersstudios.msblock;

import com.minersstudios.msblock.customblock.CustomBlockData;
import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.plugin.MSPlugin;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.minecraft.server.MinecraftServer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * The main class of the MSBlock plugin
 *
 * @see MSPlugin
 */
public final class MSBlock extends MSPlugin {
    private static MSBlock instance;
    private Cache cache;
    private Config config;
    private CoreProtectAPI coreProtectAPI;

    public MSBlock() {
        instance = this;
    }

    @Override
    public void load() {
        final MinecraftServer server = MinecraftServer.getServer();
        final File paperGlobalConfig = new File("config/paper-global.yml");
        final YamlConfiguration paperConfig = YamlConfiguration.loadConfiguration(paperGlobalConfig);
        final String noteBlockUpdates = "block-updates.disable-noteblock-updates";

        if (!paperConfig.getBoolean(noteBlockUpdates, false)) {
            paperConfig.set(noteBlockUpdates, true);

            try {
                paperConfig.save(paperGlobalConfig);
            } catch (IOException e) {
                MSLogger.log(Level.SEVERE, "Failed to save paper-global.yml with " + noteBlockUpdates + " enabled", e);
            }

            server.paperConfigurations.reloadConfigs(server);
            server.server.reloadCount++;
        }

        initClass(CustomBlockData.class);

    }

    @Override
    public void enable() {
        this.coreProtectAPI = CoreProtect.getInstance().getAPI();
        this.cache = new Cache();
        this.config = new Config(this, this.getConfigFile());

        this.config.reload();
    }

    /**
     * @return The instance of the plugin
     * @throws NullPointerException If the plugin is not enabled
     */
    public static MSBlock getInstance() throws NullPointerException {
        return instance;
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
     * @return The CoreProtectAPI instance
     * @throws NullPointerException If the {@link CoreProtect} is not enabled
     */
    public static CoreProtectAPI getCoreProtectAPI() throws NullPointerException {
        return instance.coreProtectAPI;
    }
}
