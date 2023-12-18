package com.minersstudios.msitem;

import com.minersstudios.mscore.plugin.config.PluginConfig;
import com.minersstudios.mscore.utility.MSPluginUtils;
import com.minersstudios.msitem.api.renameable.RenameableItem;
import com.minersstudios.msitem.api.renameable.RenameableItemRegistry;
import com.minersstudios.msitem.menu.RenamesMenu;
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
 * Use {@link MSItem#getConfiguration()} to get configuration instance.
 * Use {@link #reload()} to reload configuration and {@link #save()} to
 * save configuration.
 */
public final class Config extends PluginConfig<MSItem> {
    private long dosimeterCheckRate;

    /**
     * Configuration constructor
     *
     * @param plugin The plugin that owns this config
     * @param file The config file, where the configuration is stored
     * @throws IllegalArgumentException If the given file does not exist
     */
    public Config(
            final @NotNull MSItem plugin,
            final @NotNull File file
    ) throws IllegalArgumentException {
        super(plugin, file);
    }

    /**
     * Reloads config variables
     */
    @Override
    public void reloadVariables() {
        this.dosimeterCheckRate = this.yaml.getLong("dosimeter-check-rate");

        final MSItem plugin = this.getPlugin();

        plugin.saveResource("items/example.yml", true);
        plugin.setLoadedCustoms(true);
        plugin.runTaskTimer(task -> {
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

        try (final var pathStream = Files.walk(Paths.get(this.file.getParent() + "/items"))) {
            pathStream.parallel()
            .filter(file -> {
                final String fileName = file.getFileName().toString();
                return Files.isRegularFile(file)
                        && !fileName.equalsIgnoreCase("example.yml")
                        && fileName.endsWith(".yml");
            })
            .map(Path::toFile)
            .forEach(file -> {
                final RenameableItem renameableItem = RenameableItem.fromFile(plugin, file);

                if (renameableItem != null) {
                    RenameableItemRegistry.register(renameableItem);
                }
            });

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
