package com.minersstudios.msitem;

import com.minersstudios.mscore.plugin.config.MSConfig;
import com.minersstudios.mscore.util.MSPluginUtils;
import com.minersstudios.msitem.item.renameable.RenameableItem;
import com.minersstudios.msitem.item.renameable.RenameableItemRegistry;
import com.minersstudios.msitem.listeners.mechanic.DosimeterMechanic;
import com.minersstudios.msitem.menu.RenamesMenu;
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
 * Use {@link MSItem#getConfiguration()} to get configuration instance.
 * Use {@link #reload()} to reload configuration and {@link #save()} to
 * save configuration.
 */
public final class Config extends MSConfig {
    private final MSItem plugin;

    public long dosimeterCheckRate;

    /**
     * Configuration constructor
     *
     * @param file The config file, where the configuration is stored
     * @throws IllegalArgumentException If the given file does not exist
     */
    public Config(
            final @NotNull MSItem plugin,
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
        this.dosimeterCheckRate = this.yaml.getLong("dosimeter-check-rate");

        for (final var task : this.plugin.getServer().getScheduler().getPendingTasks()) {
            if (task.getOwner().equals(this.plugin)) {
                task.cancel();
            }
        }

        this.plugin.runTaskTimer(DosimeterMechanic.DosimeterTask::run, 0L, this.dosimeterCheckRate);
        this.plugin.saveResource("items/example.yml", true);
        this.plugin.setLoadedCustoms(true);
        this.plugin.runTaskTimer(task -> {
            if (MSPluginUtils.isLoadedCustoms()) {
                task.cancel();
                this.loadRenames();
                RenamesMenu.update();
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

    private void loadRenames() {
        final long start = System.currentTimeMillis();

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
                final RenameableItem renameableItem = RenameableItem.fromFile(file);

                if (renameableItem != null) {
                    RenameableItemRegistry.register(renameableItem);
                }
            });

            MSItem.logger().info("Loaded " + RenameableItemRegistry.keysSize() + " renameable items in " + (System.currentTimeMillis() - start) + "ms");
        } catch (IOException e) {
            MSItem.logger().log(Level.SEVERE, "An error occurred while renameable loading items", e);
        }
    }
}
