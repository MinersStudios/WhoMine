package com.minersstudios.msblock;

import com.minersstudios.msblock.api.CustomBlockData;
import com.minersstudios.msblock.api.CustomBlockRegistry;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.mscore.plugin.config.PluginConfig;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.msessentials.menu.CraftsMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
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

    //<editor-fold desc="File paths" defaultstate="collapsed">
    private static final String JSON_EXTENSION = ".json";

    /** The custom block configurations folder */
    public static final String BLOCKS_FOLDER = "blocks";

    /** The example custom block configuration file name */
    public static final String EXAMPLE_BLOCK_FILE_NAME = "example" + JSON_EXTENSION;

    /** The path in the plugin folder to the example custom block configuration file */
    public static final String EXAMPLE_BLOCK_FILE_PATH = BLOCKS_FOLDER + '/' + EXAMPLE_BLOCK_FILE_NAME;
    //</editor-fold>

    /**
     * Configuration constructor
     *
     * @param plugin The plugin that owns this config
     */
    Config(final @NotNull MSBlock plugin) {
        super(plugin, plugin.getConfigFile());
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

        plugin.saveResource(EXAMPLE_BLOCK_FILE_PATH, true);
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
    public @UnknownNullability String getWoodSoundPlace() {
        return this.woodSoundPlace;
    }

    /**
     * @return The wood sound break
     */
    public @UnknownNullability String getWoodSoundBreak() {
        return this.woodSoundBreak;
    }

    /**
     * @return The wood sound step
     */
    public @UnknownNullability String getWoodSoundStep() {
        return this.woodSoundStep;
    }

    /**
     * @return The wood sound hit
     */
    public @UnknownNullability String getWoodSoundHit() {
        return this.woodSoundHit;
    }

    private void loadBlocks() {
        final long start = System.currentTimeMillis();
        final MSBlock plugin = this.getPlugin();

        plugin.setStatus(MSBlock.LOADING_BLOCKS);

        try (final var pathStream = Files.walk(Paths.get(this.file.getParent() + '/' + BLOCKS_FOLDER))) {
            pathStream.parallel()
            .filter(file -> {
                final String fileName = file.getFileName().toString();

                return fileName.endsWith(JSON_EXTENSION)
                        && !fileName.equalsIgnoreCase(EXAMPLE_BLOCK_FILE_NAME);
            })
            .map(path -> CustomBlockData.fromFile(plugin, path.toFile()))
            .filter(Objects::nonNull)
            .forEach(CustomBlockRegistry::register);

            plugin.setStatus(MSBlock.LOADED_BLOCKS);
            plugin.getComponentLogger().info(
                    Component.text(
                            "Loaded " + CustomBlockRegistry.size() + " custom blocks in " + (System.currentTimeMillis() - start) + "ms",
                            NamedTextColor.GREEN
                    )
            );
        } catch (final IOException e) {
            plugin.setStatus(MSBlock.FAILED_LOAD_BLOCKS);
            plugin.getLogger().log(
                    Level.SEVERE,
                    "An error occurred while loading blocks",
                    e
            );
        }

        final var recipes = MSPlugin.globalCache().customBlockRecipes;

        plugin.runTaskTimer(
                task -> {
                    if (
                            !recipes.isEmpty()
                            && plugin.containsStatus(MSBlock.LOADED_BLOCKS)
                    ) {
                        CraftsMenu.putCrafts(CraftsMenu.Type.BLOCKS, recipes);
                    }
                },
                0L, 10L
        );
    }
}
