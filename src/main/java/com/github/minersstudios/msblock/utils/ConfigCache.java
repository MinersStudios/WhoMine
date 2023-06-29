package com.github.minersstudios.msblock.utils;

import com.github.minersstudios.msblock.MSBlock;
import com.github.minersstudios.msblock.customblock.CustomBlockData;
import com.github.minersstudios.msblock.customblock.NoteBlockData;
import com.github.minersstudios.mscore.collections.ConcurrentDualMap;
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
import java.util.stream.Stream;

import static com.github.minersstudios.mscore.MSCore.getCache;

public final class ConfigCache {
	public final @NotNull String
			woodSoundPlace,
			woodSoundBreak,
			woodSoundStep,
			woodSoundHit;

	public final List<CustomBlockData> recipeBlocks = new ArrayList<>();

	public final Map<Player, Double> steps = new HashMap<>();
	public final Set<Player> farAway = new HashSet<>();
	public final ConcurrentDualMap<Block, Player, Integer> blocks = new ConcurrentDualMap<>();

	public ConfigCache() {
		File configFile = new File(MSBlock.getInstance().getPluginFolder(), "config.yml");
		YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(configFile);

		this.woodSoundPlace = Objects.requireNonNull(yamlConfiguration.getString("wood-sound.place"), "wood-sound.place is null");
		this.woodSoundBreak = Objects.requireNonNull(yamlConfiguration.getString("wood-sound.break"));
		this.woodSoundStep = Objects.requireNonNull(yamlConfiguration.getString("wood-sound.step"));
		this.woodSoundHit = Objects.requireNonNull(yamlConfiguration.getString("wood-sound.hit"));
	}

	public void loadBlocks() {
		try (Stream<Path> paths = Files.walk(Paths.get(MSBlock.getInstance().getPluginFolder() + "/blocks"))) {
			paths
			.filter(Files::isRegularFile)
			.map(Path::toFile)
			.forEach(file -> {
				if (file.getName().equals("example.yml")) return;
				CustomBlockData customBlockData = CustomBlockData.fromConfig(file, YamlConfiguration.loadConfiguration(file));
				getCache().customBlockMap.put(customBlockData.getNamespacedKey().getKey(), customBlockData.getItemCustomModelData(), customBlockData);

				NoteBlockData noteBlockData = customBlockData.getNoteBlockData();
				if (noteBlockData == null) {
					Map<?, NoteBlockData> map =
							customBlockData.getBlockFaceMap() == null
							? customBlockData.getBlockAxisMap()
							: customBlockData.getBlockFaceMap();

					if (map != null) {
						for (NoteBlockData data : map.values()) {
							getCache().cachedNoteBlockData.put(data.toInt(), customBlockData);
						}
					}
				} else {
					getCache().cachedNoteBlockData.put(noteBlockData.toInt(), customBlockData);
				}
			});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
