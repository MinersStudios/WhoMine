package com.github.minersstudios.mscore.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.format.DateTimeFormatter;

public class Config {
    public final @NotNull String languageCode;
    public final @NotNull String languageUrl;
    public final @NotNull DateTimeFormatter timeFormatter;

    public Config(@NotNull File configFile) {
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(configFile);
        this.timeFormatter = DateTimeFormatter.ofPattern(yamlConfiguration.getString("date-format", "EEE, yyyy-MM-dd HH:mm z"));
        this.languageCode = yamlConfiguration.getString("language.code", "ru_ru");
        this.languageUrl = yamlConfiguration.getString("language.url", "https://github.com/MinersStudios/msTranslations/raw/release/lang/");
    }
}
