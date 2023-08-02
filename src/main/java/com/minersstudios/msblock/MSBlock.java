package com.minersstudios.msblock;

import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.plugin.MSPlugin;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.minecraft.server.MinecraftServer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

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

    @Override
    public void load() {
        MinecraftServer server = MinecraftServer.getServer();
        File paperGlobalConfig = new File("config/paper-global.yml");
        YamlConfiguration paperConfig = YamlConfiguration.loadConfiguration(paperGlobalConfig);
        String noteBlockUpdates = "block-updates.disable-noteblock-updates";

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
    }

    @Override
    public void enable() {
        instance = this;
        this.coreProtectAPI = CoreProtect.getInstance().getAPI();
        this.cache = new Cache();
        this.config = new Config(this, this.getConfigFile());

        this.config.reload();
    }

    /**
     * @return The instance of the plugin,
     * @throws NullPointerException If the plugin is not enabled
     */
    public static @NotNull MSBlock getInstance() throws NullPointerException {
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
     * @return The CoreProtectAPI instance
     * @throws NullPointerException If the {@link CoreProtect} is not enabled
     */
    public static @NotNull CoreProtectAPI getCoreProtectAPI() throws NullPointerException {
        return instance.coreProtectAPI;
    }
}
