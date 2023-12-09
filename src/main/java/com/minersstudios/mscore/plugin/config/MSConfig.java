package com.minersstudios.mscore.plugin.config;

import com.google.common.base.Joiner;
import com.minersstudios.mscore.plugin.MSLogger;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

/**
 * Abstract configuration class, which provides a simple way to load and save 
 * yaml configurations. Override {@link #reloadVariables()} to reload variables 
 * of the extending class.
 * <br>
 * Use {@link #reload()} to reload configuration and {@link #save()} to save
 * configuration.
 * Use {@link #reloadVariables()} to reload variables.
 */
public abstract class MSConfig {
    protected final @NotNull File file;
    protected final @NotNull YamlConfiguration yaml;

    /**
     * Configuration constructor. All variables must be initialized in
     * {@link #reloadVariables()}.
     *
     * @param file The config file, where the configuration is stored
     * @throws IllegalArgumentException If the given file does not exist
     */
    public MSConfig(final @NotNull File file) throws IllegalArgumentException {
        this.file = file;
        this.yaml = new YamlConfiguration();
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
    public final @NotNull YamlConfiguration getYaml() {
        return this.yaml;
    }

    /**
     * Sets the value of the given path to the given value. If the path does not
     * exist, it will be created.
     *
     * @param path  The path of the value
     * @param value The value to set
     */
    public void setIfNotExists(
            final @NotNull String path,
            final @Nullable Object value
    ) {
        if (!this.yaml.isSet(path)) {
            this.yaml.set(path, value);
        }
    }

    /**
     * Reloads {@link #yaml} and variables from file. The yaml configuration is
     * reloaded first, then the variables.
     *
     * @return True if the config was reloaded successfully, or false when the
     *         given file cannot be read for any reason
     * @see #reloadYaml()
     * @see #reloadVariables()
     */
    public boolean reload() {
        try {
            this.saveDefaultConfig();
            this.reloadVariables();
            return true;
        } catch (final ConfigurationException e) {
            MSLogger.severe("An error occurred while loading the config!", e);
            return false;
        }
    }

    /**
     * Reloads {@link #yaml} from file
     *
     * @throws ConfigurationException If the config file cannot be read or is
     *                                not a valid Configuration
     */
    public void reloadYaml() throws ConfigurationException {
        try {
            this.yaml.load(this.file);
        } catch (final IOException e) {
            throw new ConfigurationException("The config file : " + this.file + " cannot be read", e);
        } catch (final InvalidConfigurationException e) {
            throw new ConfigurationException("The file : " + this.file + " is not a valid configuration", e);
        }
    }

    /**
     * Reloads config variables
     *
     * @see #reload()
     */
    public abstract void reloadVariables();

    /**
     * Reloads default config variables
     *
     * @see #saveDefaultConfig()
     */
    public abstract void reloadDefaultVariables();

    /**
     * Saves the config file
     *
     * @return True if the config was saved successfully, or false when the
     *         given file cannot be written to for any reason
     */
    public boolean save() {
        try {
            this.yaml.save(this.file);
            return true;
        } catch (final IOException e) {
            MSLogger.severe("An error occurred while saving the config!", e);
            return false;
        }
    }

    /**
     * Saves the default config file with default values. The default values are
     * set in {@link #reloadDefaultVariables()}.
     *
     * @see #reloadDefaultVariables()
     * @see #save()
     */
    public void saveDefaultConfig() throws ConfigurationException {
        try {
            if (!this.file.exists()) {
                if (this.file.getParentFile().mkdirs()) {
                    MSLogger.info("The config directory : " + this.file.getParentFile() + " was created");
                }

                if (this.file.createNewFile()) {
                    MSLogger.info("The config file : " + this.file + " was created");
                }
            }

            this.reloadYaml();
            this.reloadDefaultVariables();
            this.yaml.save(this.file);
        } catch (final IOException e) {
            throw new ConfigurationException("The config file : " + this.file + " cannot be written to", e);
        } catch (final ConfigurationException e) {
            throw new ConfigurationException("The config file : " + this.file + " cannot be read", e);
        }
    }

    /**
     * @return A string representation of this configuration. The string
     *         representation consists of the class name, the config file path
     *         and the config values.
     */
    @Override
    public @NotNull String toString() {
        final String path = this.file.getPath();
        final String configValues =
                Joiner.on(",")
                .withKeyValueSeparator("=")
                .join(this.yaml.getValues(true));
        return this.getClass().getName()
                + "{file=" + path
                + ", config=[" + configValues
                + "]}";
    }
}
