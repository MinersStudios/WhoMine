package com.minersstudios.msblock;

import com.minersstudios.msblock.api.CustomBlockData;
import com.minersstudios.msblock.api.CustomBlockRegistry;
import com.minersstudios.mscore.plugin.config.PluginConfig;
import com.minersstudios.mscore.utility.ChatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
 * Use {@link MSBlock#getConfiguration()} to get configuration instance.
 * Use {@link #reload()} to reload configuration and {@link #save()} to
 * save configuration.
 */
public final class Config extends PluginConfig<MSBlock> {
    private String woodSoundPlace;
    private String woodSoundBreak;
    private String woodSoundStep;
    private String woodSoundHit;

    private static final String BLOCKS_FOLDER = "blocks";

    /**
     * Configuration constructor
     *
     * @param plugin The plugin that owns this config
     * @param file The config file, where the configuration is stored
     * @throws IllegalArgumentException If the given file does not exist
     */
    public Config(
            final @NotNull MSBlock plugin,
            final @NotNull File file
    ) throws IllegalArgumentException {
        super(plugin, file);
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

        if (ChatUtils.isBlank(this.woodSoundPlace)) {
            this.woodSoundPlace = "block.wood.place";
        }

        if (ChatUtils.isBlank(this.woodSoundBreak)) {
            this.woodSoundBreak = "block.wood.break";
        }

        if (ChatUtils.isBlank(this.woodSoundStep)) {
            this.woodSoundStep = "block.wood.step";
        }

        if (ChatUtils.isBlank(this.woodSoundHit)) {
            this.woodSoundHit = "block.wood.hit";
        }

        final MSBlock plugin = this.getPlugin();

        plugin.saveResource("blocks/example.json", true);
        plugin.runTaskAsync(this::loadBlocks);
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

    /**
     * @return The wood sound place
     */
    public @NotNull String getWoodSoundPlace() {
        return this.woodSoundPlace;
    }

    /**
     * @return The wood sound break
     */
    public @NotNull String getWoodSoundBreak() {
        return this.woodSoundBreak;
    }

    /**
     * @return The wood sound step
     */
    public @NotNull String getWoodSoundStep() {
        return this.woodSoundStep;
    }

    /**
     * @return The wood sound hit
     */
    public @NotNull String getWoodSoundHit() {
        return this.woodSoundHit;
    }

    private void loadBlocks() {
        final long start = System.currentTimeMillis();
        final MSBlock plugin = this.getPlugin();

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
                final CustomBlockData data = CustomBlockData.fromFile(plugin, file);

                if (data != null) {
                    CustomBlockRegistry.register(data);
                }
            });

            plugin.setLoadedCustoms(true);
            plugin.getComponentLogger().info(
                    Component.text(
                            "Loaded " + CustomBlockRegistry.size() + " custom blocks in " + (System.currentTimeMillis() - start) + "ms",
                            NamedTextColor.GREEN
                    )
            );
        } catch (final IOException e) {
            plugin.getLogger().log(
                    Level.SEVERE,
                    "An error occurred while loading blocks",
                    e
            );
        }
    }
}
