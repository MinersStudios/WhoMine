package com.github.minersstudios.mscore.config;

import com.github.minersstudios.mscore.MSCore;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

/**
 * Configuration loader class.
 * Use {@link MSCore#getConfiguration()} to get configuration instance.
 * Use {@link #reload()} to reload configuration and {@link #save(YamlConfiguration)} to save configuration.
 */
public final class Config {
    private final File file;

    public String languageCode;
    public String languageUrl;
    public DateTimeFormatter timeFormatter;

    public Config() {
        this.file = MSCore.getInstance().getConfigFile();
        this.reload();
    }

    /**
     * @return The config file, where the configuration is stored
     */
    public @NotNull File getFile() {
        return this.file;
    }

    /**
     * Reloads plugin config and variables.
     * Used to reload plugin config when it is changed.
     * <br>
     * <b>NOTE:</b> Not updates the {@link LanguageFile}.
     * Use {@link LanguageFile#reloadLanguage()} to reload language file.
     */
    public void reload() {
        MSCore plugin = MSCore.getInstance();
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(this.file);

        this.timeFormatter = DateTimeFormatter.ofPattern(yamlConfiguration.getString("date-format", "EEE, yyyy-MM-dd HH:mm z"));
        this.languageCode = yamlConfiguration.getString("language.code", "ru_ru");
        this.languageUrl = yamlConfiguration.getString("language.url", "https://github.com/minersstudios/mstranslations/raw/release/lang/");

        plugin.saveDefaultConfig();
        plugin.reloadConfig();
    }

    /**
     * Saves the configuration to the config file
     *
     * @param configuration The configuration to save
     */
    public void save(@NotNull YamlConfiguration configuration) {
        try {
            configuration.save(MSCore.getInstance().getConfigFile());
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "An error occurred while saving the config!", e);
        }
    }
}
