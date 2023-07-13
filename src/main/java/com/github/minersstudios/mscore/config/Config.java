package com.github.minersstudios.mscore.config;

import com.github.minersstudios.mscore.MSCore;
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
    public String languageUrl;
    public DateTimeFormatter timeFormatter;

    /**
     * Configuration constructor, automatically loads the yaml configuration
     * from the specified file and initializes the variables of the class
     *
     * @param file The config file, where the configuration is stored
     * @throws IllegalArgumentException If the given file does not exist
     */
    public Config(@NotNull File file) throws IllegalArgumentException {
        super(file);
    }

    /**
     * Reloads config variables.
     * <br>
     * NOTE: Not updates the {@link LanguageFile}.
     * Use {@link LanguageFile#reloadLanguage()} to reload language file.
     */
    @Override
    public void reloadVariables() {
        this.timeFormatter = DateTimeFormatter.ofPattern(this.config.getString("date-format", "EEE, yyyy-MM-dd HH:mm z"));
        this.languageCode = this.config.getString("language.code", "ru_ru");
        this.languageUrl = this.config.getString("language.url", "https://github.com/minersstudios/mstranslations/raw/release/lang/");
    }
}
