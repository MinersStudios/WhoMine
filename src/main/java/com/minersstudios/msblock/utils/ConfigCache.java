package com.minersstudios.msblock.utils;

import com.minersstudios.msblock.MSBlock;
import com.minersstudios.msblock.customblock.CustomBlockData;
import com.minersstudios.msblock.customblock.NoteBlockData;
import com.minersstudios.mscore.collections.ConcurrentHashDualMap;
import com.minersstudios.mscore.collections.DualMap;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.minersstudios.mscore.plugin.MSPlugin.getGlobalCache;

public final class ConfigCache {
    public final @NotNull String
            woodSoundPlace,
            woodSoundBreak,
            woodSoundStep,
            woodSoundHit;

    public final List<CustomBlockData> recipeBlocks = new ArrayList<>();
    public final Map<Player, Double> steps = new HashMap<>();
    public final Set<Player> farAway = new HashSet<>();
    public final DualMap<Block, Player, Integer> blocks = new ConcurrentHashDualMap<>();

    public ConfigCache() {
        File configFile = MSBlock.getInstance().getConfigFile();
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(configFile);

        this.woodSoundPlace = Objects.requireNonNull(yamlConfiguration.getString("wood-sound.place"), "wood-sound.place is null");
        this.woodSoundBreak = Objects.requireNonNull(yamlConfiguration.getString("wood-sound.break"));
        this.woodSoundStep = Objects.requireNonNull(yamlConfiguration.getString("wood-sound.step"));
        this.woodSoundHit = Objects.requireNonNull(yamlConfiguration.getString("wood-sound.hit"));
    }

    public void loadBlocks() {
        var customBlockMap = getGlobalCache().customBlockMap;
        var cachedNoteBlockData = getGlobalCache().cachedNoteBlockData;

        try (var paths = Files.walk(Paths.get(MSBlock.getInstance().getPluginFolder() + "/blocks"))) {
            paths
            .filter(Files::isRegularFile)
            .map(Path::toFile)
            .forEach(file -> {
                if (file.getName().equals("example.yml")) return;

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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
