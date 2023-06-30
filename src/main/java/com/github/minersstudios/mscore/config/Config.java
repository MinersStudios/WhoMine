package com.github.minersstudios.mscore.config;

import com.github.minersstudios.mscore.MSCore;
import org.bukkit.configuration.file.YamlConfiguration;

import java.time.format.DateTimeFormatter;

/**
 * Configuration loader class.
 * Use {@link MSCore#getConfiguration()} to get configuration instance.
 * Use {@link #reload()} to reload configuration.
 */
public final class Config {
    public String languageCode;
    public String languageUrl;
    public DateTimeFormatter timeFormatter;

    public Config() {
        this.reload();
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
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(plugin.getConfigFile());

        this.timeFormatter = DateTimeFormatter.ofPattern(yamlConfiguration.getString("date-format", "EEE, yyyy-MM-dd HH:mm z"));
        this.languageCode = yamlConfiguration.getString("language.code", "ru_ru");
        this.languageUrl = yamlConfiguration.getString("language.url", "https://github.com/MinersStudios/msTranslations/raw/release/lang/");

        plugin.saveDefaultConfig();
        plugin.reloadConfig();
    }
}
