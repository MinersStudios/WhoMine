package com.github.minersstudios.mscore.config;

import com.github.minersstudios.mscore.logger.MSLogger;
import com.github.minersstudios.mscore.plugin.MSPlugin;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Abstract configuration class, which provides a simple way to load
 * and save yaml configurations. Override {@link #reloadVariables()}
 * to reload variables of the extending class.
 * Use {@link #reload()} to reload configuration and {@link #save()}
 * to save configuration.
 * Use {@link #reloadVariables()} to reload variables.
 */
public abstract class MSConfig {
    protected final @NotNull MSPlugin plugin;
    protected final @NotNull File file;
    protected final @NotNull YamlConfiguration config;

    /**
     * Configuration constructor.
     * All variables must be initialized in {@link #reloadVariables()}.
     *
     * @param plugin The plugin instance of the configuration
     * @param file The config file, where the configuration is stored
     * @throws IllegalArgumentException If the given file does not exist
     */
    public MSConfig(
            @NotNull MSPlugin plugin,
            @NotNull File file
    ) throws IllegalArgumentException {
        Preconditions.checkArgument(file.exists(), "The given file does not exist!");

        this.plugin = plugin;
        this.file = file;
        this.config = new YamlConfiguration();
    }

    /**
     * @return The plugin instance of the configuration
     */
    public final @NotNull MSPlugin getPlugin() {
        return this.plugin;
    }

    /**
     * @return The config file, where the configuration is stored
     */
    public final @NotNull File getFile() {
        return this.file;
    }

    /**
     * @return The yaml configuration
     */
    public final @NotNull YamlConfiguration getConfig() {
        return this.config;
    }

    /**
     * Reloads {@link #config} and variables from file.
     * The yaml configuration is reloaded first, then the variables.
     *
     * @return True if the config was reloaded successfully, or false
     *         when the given file cannot be read for any reason
     * @see #reloadYaml()
     * @see #reloadVariables()
     */
    public final boolean reload() {
        try {
            this.reloadYaml();
            this.reloadVariables();
            return true;
        } catch (ConfigurationException e) {
            MSLogger.log(Level.SEVERE, "An error occurred while loading the config!", e);
            return false;
        }
    }

    /**
     * Reloads {@link #config} from file
     *
     * @throws ConfigurationException If the config file cannot be read or is not a valid Configuration
     */
    public final void reloadYaml() throws ConfigurationException {
        try {
            this.config.load(this.file);
        } catch (IOException e) {
            throw new ConfigurationException("The config file : " + this.file + " cannot be read", e);
        } catch (InvalidConfigurationException e) {
            throw new ConfigurationException("The file : " + this.file + " is not a valid Configuration", e);
        }
    }

    /**
     * Reloads plugin variables
     */
    public abstract void reloadVariables();

    /**
     * Saves the config file
     *
     * @return True if the config was saved successfully, or false
     *         when the given file cannot be written to for any reason
     */
    public final boolean save() {
        try {
            this.config.save(this.file);
            return true;
        } catch (IOException e) {
            MSLogger.log(Level.SEVERE, "An error occurred while saving the config!", e);
            return false;
        }
    }

    /**
     * @return A string representation of this configuration.
     *         The string representation consists of the class name,
     *         the config file path and the config values.
     */
    @Override
    public @NotNull String toString() {
        String path = this.file.getPath();
        String configValues = Joiner.on(",").withKeyValueSeparator("=").join(this.config.getValues(true));
        return this.getClass().getName() + "{file=" + path + ", config=[" + configValues + "]}";
    }
}
