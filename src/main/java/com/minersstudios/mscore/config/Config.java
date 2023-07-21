package com.minersstudios.mscore.config;

import com.minersstudios.mscore.MSCore;
import com.minersstudios.mscore.plugin.MSPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.format.DateTimeFormatter;

/**
 * Configuration loader class.
 * Use {@link MSCore#getConfiguration()} to get configuration instance.
 * Use {@link #reload()} to reload configuration and {@link #save()} to save configuration.
 */
public final class Config extends MSConfig {
    public String languageCode;
    public String languageUser;
    public String languageRepo;
    public DateTimeFormatter timeFormatter;

    /**
     * Configuration constructor, automatically loads the yaml configuration
     * from the specified file and initializes the variables of the class
     *
     * @param plugin The plugin instance of the configuration
     * @param file The config file, where the configuration is stored
     * @throws IllegalArgumentException If the given file does not exist
     */
    public Config(
            @NotNull MSPlugin plugin,
            @NotNull File file
    ) throws IllegalArgumentException {
        super(plugin, file);
    }

    /**
     * Reloads config variables.
     * <br>
     * NOTE: Not updates the {@link LanguageFile}.
     * Use {@link LanguageFile#reloadLanguage()} to reload language file.
     */
    @Override
    public void reloadVariables() {
        this.timeFormatter = DateTimeFormatter.ofPattern(this.yaml.getString("date-format", "EEE, yyyy-MM-dd HH:mm z"));
        this.languageCode = this.yaml.getString("language.code", "ru_ru");
        this.languageUser = this.yaml.getString("language.user", "MinersStudios");
        this.languageRepo = this.yaml.getString("language.repo", "MSTranslations");
    }
}
