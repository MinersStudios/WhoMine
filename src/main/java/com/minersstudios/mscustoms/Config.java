package com.minersstudios.mscustoms;

import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.mscore.plugin.config.PluginConfig;
import com.minersstudios.mscore.status.StatusWatcher;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscustoms.custom.block.CustomBlockData;
import com.minersstudios.mscustoms.custom.block.CustomBlockRegistry;
import com.minersstudios.mscustoms.custom.item.renameable.RenameableItem;
import com.minersstudios.mscustoms.custom.item.renameable.RenameableItemRegistry;
import com.minersstudios.mscustoms.menu.CraftsMenu;
import com.minersstudios.mscustoms.menu.RenamesMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.configuration.ConfigurationSection;
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
 * Use {@link MSCustoms#getConfiguration()} ()} to get configuration instance.
 * Use {@link #reload()} to reload configuration and {@link #save()} to
 * save configuration.
 */
public final class Config extends PluginConfig<MSCustoms> {
    private long dosimeterCheckRate;
    private String woodSoundPlace;
    private String woodSoundBreak;
    private String woodSoundStep;
    private String woodSoundHit;

    //<editor-fold desc="File paths" defaultstate="collapsed">
    private static final String JSON_EXTENSION = ".json";
    private static final String YAML_EXTENSION = ".yml";

    /** The custom block configurations folder */
    public static final String BLOCKS_FOLDER = "blocks";

    /** The example custom block configuration file name */
    public static final String EXAMPLE_BLOCK_FILE_NAME = "example" + JSON_EXTENSION;

    /** The path in the plugin folder to the example custom block configuration file */
    public static final String EXAMPLE_BLOCK_FILE_PATH = BLOCKS_FOLDER + '/' + EXAMPLE_BLOCK_FILE_NAME;

    /** The name of the renameable item configurations folder */
    public static final String ITEMS_FOLDER = "items";

    /** The name of the example renameable item configuration file */
    public static final String EXAMPLE_RENAMEABLE_FILE_NAME = "example" + YAML_EXTENSION;

    /** The path in the plugin folder to the example renameable item configuration file */
    public static final String EXAMPLE_RENAMEABLE_FILE_PATH = ITEMS_FOLDER + '/' + EXAMPLE_RENAMEABLE_FILE_NAME;
    //</editor-fold>

    //<editor-fold desc="Config keys" defaultstate="collapsed">
    public static final String KEY_DOSIMETER_CHECK_RATE = "dosimeter-check-rate";

    public static final String KEY_WOOD_SOUND_SECTION =   "wood-sound";
    public static final String KEY_PLACE =                "place";
    public static final String KEY_BREAK =                "break";
    public static final String KEY_STEP =                 "step";
    public static final String KEY_HIT =                  "hit";
    //</editor-fold>

    //<editor-fold desc="Config default values" defaultstate="collapsed">
    public static final long DEFAULT_DOSIMETER_CHECK_RATE = 100;
    public static final String DEFAULT_WOOD_SOUND_PLACE =   "custom.block.wood.place";
    public static final String DEFAULT_WOOD_SOUND_BREAK =   "custom.block.wood.break";
    public static final String DEFAULT_WOOD_SOUND_STEP =    "custom.block.wood.step";
    public static final String DEFAULT_WOOD_SOUND_HIT =     "custom.block.wood.hit";
    //</editor-fold>

    Config(final @NotNull MSCustoms plugin) {
        super(plugin, plugin.getConfigFile());
    }

    @Override
    public void reloadVariables() {
        this.dosimeterCheckRate = this.yaml.getLong(KEY_DOSIMETER_CHECK_RATE, DEFAULT_DOSIMETER_CHECK_RATE);

        final ConfigurationSection woodSoundSection = this.yaml.getConfigurationSection(KEY_WOOD_SOUND_SECTION);

        if (woodSoundSection != null) {
            this.woodSoundPlace = woodSoundSection.getString(KEY_PLACE);
            this.woodSoundBreak = woodSoundSection.getString(KEY_BREAK);
            this.woodSoundStep = woodSoundSection.getString(KEY_STEP);
            this.woodSoundHit = woodSoundSection.getString(KEY_HIT);
        }

        if (ChatUtils.isBlank(this.woodSoundPlace)) {
            this.woodSoundPlace = DEFAULT_WOOD_SOUND_PLACE;
        }

        if (ChatUtils.isBlank(this.woodSoundBreak)) {
            this.woodSoundBreak = DEFAULT_WOOD_SOUND_BREAK;
        }

        if (ChatUtils.isBlank(this.woodSoundStep)) {
            this.woodSoundStep = DEFAULT_WOOD_SOUND_STEP;
        }

        if (ChatUtils.isBlank(this.woodSoundHit)) {
            this.woodSoundHit = DEFAULT_WOOD_SOUND_HIT;
        }

        final MSCustoms plugin = this.getPlugin();

        plugin.saveResource(EXAMPLE_BLOCK_FILE_PATH, true);
        plugin.saveResource(EXAMPLE_RENAMEABLE_FILE_PATH, true);

        plugin.runTaskAsync(this::loadBlocks);
        plugin.getStatusHandler().addWatcher(
                StatusWatcher.builder()
                .successStatuses(
                        MSCustoms.LOADED_BLOCKS,
                        MSCustoms.LOADED_ITEMS,
                        MSCustoms.LOADED_DECORATIONS
                )
                .successRunnable(
                        () -> {
                            plugin.runTaskAsync(this::loadRenames);
                            plugin.runTask(() -> {
                                final var list = plugin.getCache().getBlockDataRecipes();

                                for (final var entry : list) {
                                    entry.getKey().registerRecipes(
                                            plugin,
                                            entry.getValue()
                                    );
                                }

                                list.clear();
                                CraftsMenu.putCrafts(
                                        CraftsMenu.Type.BLOCKS,
                                        MSPlugin.globalCache().customBlockRecipes
                                );
                            });
                        }
                )
                .build()
        );
    }

    @Override
    public void reloadDefaultVariables() {
        this.setIfNotExists(KEY_DOSIMETER_CHECK_RATE, DEFAULT_DOSIMETER_CHECK_RATE);

        this.setIfNotExists(KEY_WOOD_SOUND_SECTION + '.' + KEY_PLACE, DEFAULT_WOOD_SOUND_PLACE);
        this.setIfNotExists(KEY_WOOD_SOUND_SECTION + '.' + KEY_BREAK, DEFAULT_WOOD_SOUND_BREAK);
        this.setIfNotExists(KEY_WOOD_SOUND_SECTION + '.' + KEY_STEP, DEFAULT_WOOD_SOUND_STEP);
        this.setIfNotExists(KEY_WOOD_SOUND_SECTION + '.' + KEY_HIT, DEFAULT_WOOD_SOUND_HIT);
    }

    /**
     * @return The dosimeter check rate
     */
    public long getDosimeterCheckRate() {
        return this.dosimeterCheckRate;
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
        final MSCustoms plugin = this.getPlugin();

        plugin.assignStatus(MSCustoms.LOADING_BLOCKS);

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

            plugin.assignStatus(MSCustoms.LOADED_BLOCKS);
            plugin.getComponentLogger().info(
                    Component.text(
                            "Loaded " + CustomBlockRegistry.size() + " custom blocks in " + (System.currentTimeMillis() - start) + "ms",
                            NamedTextColor.GREEN
                    )
            );
        } catch (final IOException e) {
            plugin.assignStatus(MSCustoms.FAILED_LOAD_BLOCKS);
            plugin.getLogger().log(
                    Level.SEVERE,
                    "An error occurred while loading blocks",
                    e
            );
        }
    }

    private void loadRenames() {
        final long start = System.currentTimeMillis();
        final MSCustoms plugin = this.getPlugin();

        plugin.assignStatus(MSCustoms.LOADING_RENAMEABLES);

        try (final var pathStream = Files.walk(Paths.get(this.file.getParent() + '/' + ITEMS_FOLDER))) {
            pathStream.parallel()
            .filter(file -> {
                final String fileName = file.getFileName().toString();

                return fileName.endsWith(YAML_EXTENSION)
                        && !fileName.equalsIgnoreCase(EXAMPLE_RENAMEABLE_FILE_NAME);
            })
            .map(path -> RenameableItem.fromFile(plugin, path.toFile()))
            .filter(Objects::nonNull)
            .forEach(RenameableItemRegistry::register);

            plugin.assignStatus(MSCustoms.LOADED_RENAMEABLES);
            plugin.getComponentLogger().info(
                    Component.text(
                            "Loaded " + RenameableItemRegistry.keysSize() + " renameable items in " + (System.currentTimeMillis() - start) + "ms",
                            NamedTextColor.GREEN
                    )
            );

            RenamesMenu.update(plugin);
        } catch (final IOException e) {
            plugin.assignStatus(MSCustoms.FAILED_LOAD_RENAMEABLES);
            plugin.getLogger().log(
                    Level.SEVERE,
                    "An error occurred while loading renameable items",
                    e
            );
        }
    }
}
