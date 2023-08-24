package com.minersstudios.msblock;

import com.minersstudios.msblock.customblock.CustomBlockData;
import com.minersstudios.msblock.customblock.CustomBlockRegistry;
import com.minersstudios.mscore.plugin.config.MSConfig;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

/**
 * Configuration loader class.
 * <p>
 * Use {@link MSBlock#getConfiguration()} to get configuration instance.
 * Use {@link #reload()} to reload configuration and {@link #save()} to
 * save configuration.
 */
public final class Config extends MSConfig {
    private final MSBlock plugin;

    public String woodSoundPlace;
    public String woodSoundBreak;
    public String woodSoundStep;
    public String woodSoundHit;

    /**
     * Configuration constructor
     *
     * @param file The config file, where the configuration is stored
     * @throws IllegalArgumentException If the given file does not exist
     */
    public Config(
            final @NotNull MSBlock plugin,
            final @NotNull File file
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

        this.plugin.saveResource("blocks/example.json", true);
        this.loadBlocks();
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
        final long start = System.currentTimeMillis();

        try (final var pathStream = Files.walk(Paths.get(this.file.getParent() + "/blocks"))) {
            pathStream.parallel()
            .filter(file -> {
                final String fileName = file.getFileName().toString();
                return Files.isRegularFile(file)
                        && !fileName.equalsIgnoreCase("example.json")
                        && fileName.endsWith(".json");
            })
            .map(Path::toFile)
            .forEach(file -> {
                final CustomBlockData data = CustomBlockData.fromFile(file);

                if (data != null) {
                    CustomBlockRegistry.register(data);
                }
            });

            this.plugin.setLoadedCustoms(true);
            MSBlock.logger().info("Loaded " + CustomBlockRegistry.size() + " custom blocks in " + (System.currentTimeMillis() - start) + "ms");
        } catch (IOException e) {
            MSBlock.logger().log(Level.SEVERE, "An error occurred while loading blocks", e);
        }
    }
}
