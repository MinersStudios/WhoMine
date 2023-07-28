package com.minersstudios.msblock;

import com.minersstudios.msblock.customblock.CustomBlockData;
import com.minersstudios.msblock.customblock.CustomBlockRegistry;
import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.plugin.config.MSConfig;
import com.minersstudios.mscore.util.MSPluginUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

public final class Config extends MSConfig {
    private final MSBlock plugin;

    public String woodSoundPlace;
    public String woodSoundBreak;
    public String woodSoundStep;
    public String woodSoundHit;

    /**
     * Configuration constructor
     *
     * @param file   The config file, where the configuration is stored
     * @throws IllegalArgumentException If the given file does not exist
     */
    public Config(
            @NotNull MSBlock plugin,
            @NotNull File file
    ) throws IllegalArgumentException {
        super(file);
        this.plugin = plugin;
    }

    /**
     * Reloads config variables
     */
    @Override
    public void reloadVariables() {
        this.woodSoundPlace = this.yaml.getString("wood-sound.place");
        this.woodSoundBreak = this.yaml.getString("wood-sound.break");
        this.woodSoundStep = this.yaml.getString("wood-sound.step");
        this.woodSoundHit = this.yaml.getString("wood-sound.hit");

        var recipeBlocks = MSBlock.getCache().recipeBlocks;

        this.plugin.saveResource("blocks/example.yml", true);
        this.loadBlocks();
        this.plugin.runTaskTimer(task -> {
            if (MSPluginUtils.isLoadedCustoms()) {
                recipeBlocks.forEach(CustomBlockData::registerRecipes);
                recipeBlocks.clear();
                task.cancel();
            }
        }, 0L, 10L);
    }

    /**
     * Reloads default config variables
     */
    @Override
    public void reloadDefaultVariables() {
        this.setIfNotExists("wood-sound.place", "custom.block.wood.place");
        this.setIfNotExists("wood-sound.break", "custom.block.wood.break");
        this.setIfNotExists("wood-sound.step", "custom.block.wood.step");
        this.setIfNotExists("wood-sound.hit", "custom.block.wood.hit");
    }

    private void loadBlocks() {
        try (var path = Files.walk(Paths.get(this.file.getParent() + "/blocks"))) {
            path
            .filter(file -> {
                String fileName = file.getFileName().toString();
                return Files.isRegularFile(file)
                        && !fileName.equalsIgnoreCase("example.yml")
                        && fileName.endsWith(".yml");
            })
            .map(Path::toFile)
            .forEach(file -> CustomBlockRegistry.registerData(CustomBlockData.fromConfig(file, YamlConfiguration.loadConfiguration(file))));

            this.plugin.setLoadedCustoms(true);
        } catch (IOException e) {
            MSLogger.log(Level.SEVERE, "An error occurred while loading blocks", e);
        }
    }
}
