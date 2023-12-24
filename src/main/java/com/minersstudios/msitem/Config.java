package com.minersstudios.msitem;

import com.minersstudios.mscore.plugin.config.PluginConfig;
import com.minersstudios.mscore.utility.MSPluginUtils;
import com.minersstudios.msitem.api.renameable.RenameableItem;
import com.minersstudios.msitem.api.renameable.RenameableItemRegistry;
import com.minersstudios.msitem.menu.RenamesMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.logging.Level;

/**
 * Configuration loader class.
 * <br>
 * Use {@link MSItem#getConfiguration()} to get configuration instance.
 * Use {@link #reload()} to reload configuration and {@link #save()} to save
 * configuration.
 */
public final class Config extends PluginConfig<MSItem> {
    private long dosimeterCheckRate;

    private static final String ITEMS_FOLDER = "items";
    private static final String YAML_EXTENSION = ".yml";
    private static final String EXAMPLE_YAML = "example.yml";

    /**
     * Configuration constructor
     *
     * @param plugin The plugin that owns this config
     */
    public Config(final @NotNull MSItem plugin) {
        super(plugin, plugin.getConfigFile());
    }

    /**
     * Reloads config variables
     */
    @Override
    public void reloadVariables() {
        this.dosimeterCheckRate = this.yaml.getLong("dosimeter-check-rate");

        final MSItem plugin = this.getPlugin();

        plugin.saveResource("items/example.yml", true);
        plugin.runTaskTimerAsync(task -> {
            if (MSPluginUtils.isLoadedCustoms()) {
                task.cancel();
                this.loadRenames();
                RenamesMenu.update(plugin);
            }
        }, 0L, 10L);
    }

    /**
     * Reloads default config variables
     */
    @Override
    public void reloadDefaultVariables() {
        this.setIfNotExists("dosimeter-check-rate", 100);
    }

    /**
     * @return The dosimeter check rate
     */
    public long getDosimeterCheckRate() {
        return this.dosimeterCheckRate;
    }

    private void loadRenames() {
        final long start = System.currentTimeMillis();
        final MSItem plugin = this.getPlugin();

        try (final var pathStream = Files.walk(Paths.get(this.file.getParent() + '/' + ITEMS_FOLDER))) {
            pathStream.parallel()
            .filter(file -> {
                final String fileName = file.getFileName().toString();
                return fileName.endsWith(YAML_EXTENSION)
                        && !fileName.equalsIgnoreCase(EXAMPLE_YAML);
            })
            .map(path -> RenameableItem.fromFile(plugin, path.toFile()))
            .filter(Objects::nonNull)
            .forEach(RenameableItemRegistry::register);

            plugin.getComponentLogger().info(
                    Component.text(
                            "Loaded " + RenameableItemRegistry.keysSize() + " renameable items in " + (System.currentTimeMillis() - start) + "ms",
                            NamedTextColor.GREEN
                    )
            );
        } catch (final IOException e) {
            plugin.getLogger().log(
                    Level.SEVERE,
                    "An error occurred while loading renameable items",
                    e
            );
        }
    }
}
