package com.minersstudios.msblock;

import com.minersstudios.msblock.customblock.CustomBlockData;
import com.minersstudios.msblock.customblock.NoteBlockData;
import com.minersstudios.mscore.config.MSConfig;
import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.mscore.utils.MSPluginUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

import static com.minersstudios.mscore.plugin.MSPlugin.getGlobalCache;

public final class Config extends MSConfig {
    public String woodSoundPlace;
    public String woodSoundBreak;
    public String woodSoundStep;
    public String woodSoundHit;

    /**
     * Configuration constructor
     *
     * @param plugin The plugin instance of the configuration
     * @param file   The config file, where the configuration is stored
     * @throws IllegalArgumentException If the given file does not exist
     */
    public Config(
            @NotNull MSPlugin plugin,
            @NotNull File file
    ) throws IllegalArgumentException {
        super(plugin, file);
    }

    /**
     * Reloads config variables
     */
    @Override
    public void reloadVariables() {
        this.woodSoundPlace = this.yaml.getString("wood-sound.place", "custom.block.wood.place");
        this.woodSoundBreak = this.yaml.getString("wood-sound.break", "custom.block.wood.break");
        this.woodSoundStep = this.yaml.getString("wood-sound.step", "custom.block.wood.step");
        this.woodSoundHit = this.yaml.getString("wood-sound.hit", "custom.block.wood.hit");

        var recipeBlocks = MSBlock.getCache().recipeBlocks;

        this.loadBlocks();
        this.getPlugin().runTaskTimer(task -> {
            if (MSPluginUtils.isLoadedCustoms()) {
                recipeBlocks.forEach(CustomBlockData::registerRecipes);
                recipeBlocks.clear();
                task.cancel();
            }
        }, 0L, 10L);
    }

    private void loadBlocks() {
        var customBlockMap = getGlobalCache().customBlockMap;
        var cachedNoteBlockData = getGlobalCache().cachedNoteBlockData;

        try (var path = Files.walk(Paths.get(this.getPlugin().getPluginFolder() + "/blocks"))) {
            path
            .filter(file -> {
                String fileName = file.getFileName().toString();
                return Files.isRegularFile(file)
                        && !fileName.equalsIgnoreCase("example.yml")
                        && fileName.endsWith(".yml");
            })
            .map(Path::toFile)
            .forEach(file -> {
                CustomBlockData customBlockData = CustomBlockData.fromConfig(file, YamlConfiguration.loadConfiguration(file));
                NoteBlockData noteBlockData = customBlockData.getNoteBlockData();

                customBlockMap.put(customBlockData.getNamespacedKey().getKey(), customBlockData.getItemCustomModelData(), customBlockData);

                if (noteBlockData == null) {
                    var map = customBlockData.getBlockFaceMap() == null
                            ? customBlockData.getBlockAxisMap()
                            : customBlockData.getBlockFaceMap();

                    if (map != null) {
                        for (NoteBlockData data : map.values()) {
                            cachedNoteBlockData.put(data.toInt(), customBlockData);
                        }
                    }
                } else {
                    cachedNoteBlockData.put(noteBlockData.toInt(), customBlockData);
                }
            });

            this.getPlugin().setLoadedCustoms(true);
        } catch (IOException e) {
            MSLogger.log(Level.SEVERE, "An error occurred while loading blocks", e);
        }
    }
}
