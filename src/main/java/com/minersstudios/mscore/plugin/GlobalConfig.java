package com.minersstudios.mscore.plugin;

import com.minersstudios.mscore.util.SharedConstants;
import com.minersstudios.mscore.plugin.config.LanguageFile;
import com.minersstudios.mscore.plugin.config.MSConfig;

import java.io.File;
import java.time.format.DateTimeFormatter;

/**
 * Configuration loader class.
 * Use {@link MSPlugin#globalConfig()} to get configuration instance.
 * Use {@link #reload()} to reload configuration and {@link #save()} to save
 * configuration.
 */
public final class GlobalConfig extends MSConfig {
    private String languageCode;
    private String languageFolderLink;
    private String timeFormat;
    private DateTimeFormatter timeFormatter;

    public static final String FILE_PATH = SharedConstants.GLOBAL_FOLDER_PATH + "config.yml";

    /**
     * Global configuration constructor.
     * All variables must be initialized in {@link #reloadVariables()}
     */
    public GlobalConfig() {
        super(new File(FILE_PATH));
    }

    /**
     * @return Time formatter
     */
    public String getLanguageCode() {
        return this.languageCode;
    }

    /**
     * @return Language folder link
     */
    public String getLanguageFolderLink() {
        return this.languageFolderLink;
    }

    /**
     * @return Time format
     */
    public String getTimeFormat() {
        return this.timeFormat;
    }

    /**
     * @return Time formatter
     */
    public DateTimeFormatter getTimeFormatter() {
        return this.timeFormatter;
    }

    /**
     * Reloads config variables.
     * <br>
     * NOTE: Not updates the {@link LanguageFile}. Use
     * {@link LanguageFile#reloadLanguage()} to reload language file.
     */
    public void reloadVariables() {
        this.languageCode = this.yaml.getString("language.code");
        this.languageFolderLink = this.yaml.getString("language.folder-link");
        this.timeFormat = this.yaml.getString("date-format");
        this.timeFormatter = DateTimeFormatter.ofPattern(
                this.timeFormat == null
                ? SharedConstants.DATE_FORMAT
                : this.timeFormat
        );
    }

    /**
     * Reloads default config variables
     */
    public void reloadDefaultVariables() {
        this.setIfNotExists("language.code", SharedConstants.LANGUAGE_CODE);
        this.setIfNotExists("language.folder-link", SharedConstants.LANGUAGE_FOLDER_LINK);
        this.setIfNotExists("date-format", SharedConstants.DATE_FORMAT);
    }
}
