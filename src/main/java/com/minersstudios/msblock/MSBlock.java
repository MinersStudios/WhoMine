package com.minersstudios.msblock;

import com.minersstudios.msblock.customblock.CustomBlockData;
import com.minersstudios.msblock.customblock.file.*;
import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.mscore.util.ChatUtils;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public final class MSBlock extends MSPlugin {
    private static MSBlock instance;
    private CoreProtectAPI coreProtectAPI;
    private Cache cache;
    private Config config;

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

        ItemStack item = new ItemStack(Material.PAPER, 5);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(ChatUtils.createDefaultStyledText("Custom Block"));
        meta.setCustomModelData(1);
        item.setItemMeta(meta);

        CustomBlockFile file = CustomBlockFile.create(new File(this.getDataFolder(), "blocks/default0.json"), new CustomBlockData(
                "default0",
                new BlockSettings(
                        11.0f,
                        new BlockSettings.Tool(
                                ToolType.AXE,
                                false
                        ),
                        new BlockSettings.Placing(
                                new PlacingType.Orientable(
                                        NoteBlockData.fromParams(Instrument.BELL, 0, false),
                                        NoteBlockData.fromParams(Instrument.BELL, 1, false),
                                        NoteBlockData.fromParams(Instrument.BELL, 2, false)
                                )
                        )
                ),
                new DropSettings(
                        item,
                        10
                ),
                SoundGroup.wood()
        ));
        file.create();

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
