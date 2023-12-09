package com.minersstudios.msblock;

import com.minersstudios.msblock.api.CustomBlockData;
import com.minersstudios.msblock.api.CustomBlockRegistry;
import com.minersstudios.mscore.plugin.config.MSConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

/**
 * Configuration loader class.
 * <br>
 * Use {@link MSBlock#config()} to get configuration instance.
 * Use {@link #reload()} to reload configuration and {@link #save()} to
 * save configuration.
 */
public final class Config extends MSConfig {
    private final MSBlock plugin;

    public String woodSoundPlace;
    public String woodSoundBreak;
    public String woodSoundStep;
    public String woodSoundHit;

    private static final String BLOCKS_FOLDER = "blocks";

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

        if (StringUtils.isBlank(this.woodSoundPlace)) {
            this.woodSoundPlace = "block.wood.place";
        }

        if (StringUtils.isBlank(this.woodSoundBreak)) {
            this.woodSoundBreak = "block.wood.break";
        }

        if (StringUtils.isBlank(this.woodSoundStep)) {
            this.woodSoundStep = "block.wood.step";
        }

        if (StringUtils.isBlank(this.woodSoundHit)) {
            this.woodSoundHit = "block.wood.hit";
        }

        this.plugin.saveResource("blocks/example.json", true);
        this.plugin.runTaskAsync(this::loadBlocks);
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

        try (final var pathStream = Files.walk(Paths.get(this.file.getParent() + '/' + BLOCKS_FOLDER))) {
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
            this.plugin.getComponentLogger().info(
                    Component.text(
                            "Loaded " + CustomBlockRegistry.size() + " custom blocks in " + (System.currentTimeMillis() - start) + "ms",
                            NamedTextColor.GREEN
                    )
            );
        } catch (final IOException e) {
            this.plugin.getLogger().log(
                    Level.SEVERE,
                    "An error occurred while loading blocks",
                    e
            );
        }
    }
}
