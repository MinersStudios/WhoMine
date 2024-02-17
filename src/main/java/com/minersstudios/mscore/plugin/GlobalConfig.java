package com.minersstudios.mscore.plugin;

import com.minersstudios.mscore.locale.LanguageFile;
import com.minersstudios.mscore.locale.TranslationRegistry;
import com.minersstudios.mscore.plugin.config.MSConfig;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscore.utility.SharedConstants;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.kyori.adventure.translation.Translator;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import static net.kyori.adventure.text.Component.text;

/**
 * Configuration loader class.
 * Use {@link MSPlugin#globalConfig()} to get configuration instance.
 * Use {@link #reload()} to reload configuration and {@link #save()} to save
 * configuration.
 */
public final class GlobalConfig extends MSConfig {
    private String dateFormat;
    private boolean isChristmas;
    private boolean isHalloween;
    private String languageDefaultCode;

    private DateTimeFormatter dateFormatter;
    private Locale defaultLocale;
    private List<Locale> locales;

    //<editor-fold desc="Config keys" defaultstate="collapsed">
    public static final String KEY_DATE_FORMAT =      "date-format";
    public static final String KEY_IS_CHRISTMAS =     "is-christmas";
    public static final String KEY_IS_HALLOWEEN =     "is-halloween";

    public static final String KEY_LANGUAGE_SECTION = "language";
    public static final String KEY_DEFAULT_CODE =     "default-code";
    public static final String KEY_CODES =            "codes";
    //</editor-fold>

    private static final String JSON_EXTENSION = ".json";

    GlobalConfig() {
        super(new File(SharedConstants.GLOBAL_FOLDER_PATH + "config.yml"));
    }

    public void reloadVariables() {
        this.dateFormat = this.yaml.getString(KEY_DATE_FORMAT);
        this.isChristmas = this.yaml.getBoolean(KEY_IS_CHRISTMAS);
        this.isHalloween = this.yaml.getBoolean(KEY_IS_HALLOWEEN);

        final ConfigurationSection languageSection = this.yaml.getConfigurationSection(KEY_LANGUAGE_SECTION);

        if (languageSection == null) {
            throw new IllegalStateException("Language section cannot be null");
        }

        this.languageDefaultCode = languageSection.getString(KEY_DEFAULT_CODE);

        this.dateFormatter = DateTimeFormatter.ofPattern(
                ChatUtils.isBlank(this.dateFormat)
                ? SharedConstants.DATE_FORMAT
                : this.dateFormat
        );
        this.defaultLocale = Translator.parseLocale(this.languageDefaultCode);

        if (this.defaultLocale == null) {
            this.defaultLocale = SharedConstants.DEFAULT_LOCALE;
        }

        this.locales = new ObjectArrayList<>();

        this.locales.add(this.defaultLocale);

        for (final var tag : languageSection.getStringList(KEY_CODES)) {
            final Locale locale = Translator.parseLocale(tag);

            if (locale == null) {
                MSLogger.warning("Invalid language tag: " + tag);
            } else {
                this.locales.add(locale);
            }
        }

        this.loadLanguages();
    }

    public void reloadDefaultVariables() {
        this.setIfNotExists(KEY_DATE_FORMAT, SharedConstants.DATE_FORMAT);
        this.setIfNotExists(KEY_IS_CHRISTMAS, false);
        this.setIfNotExists(KEY_IS_HALLOWEEN, false);
        this.setIfNotExists(KEY_LANGUAGE_SECTION + '.' + KEY_DEFAULT_CODE, SharedConstants.DEFAULT_LANGUAGE_CODE);
    }

    /**
     * @return Time format
     */
    public @Nullable String getDateFormat() {
        return this.dateFormat;
    }

    /**
     * @return True if it is Christmas
     */
    public boolean isChristmas() {
        return this.isChristmas;
    }

    /**
     * @return True if it is Halloween
     */
    public boolean isHalloween() {
        return this.isHalloween;
    }

    /**
     * @return Language tag
     */
    public @UnknownNullability String getLanguageDefaultCode() {
        return this.languageDefaultCode;
    }

    /**
     * @return Date formatter
     */
    public @UnknownNullability DateTimeFormatter getDateFormatter() {
        return this.dateFormatter;
    }

    /**
     * @return Default locale
     */
    public @UnknownNullability Locale getDefaultLocale() {
        return this.defaultLocale;
    }

    /**
     * @return Locales
     */
    public @UnknownNullability List<Locale> getLocales() {
        return this.locales;
    }

    private void loadLanguages() {
        final ConfigurationSection languageSection =
                this.yaml.getConfigurationSection(KEY_LANGUAGE_SECTION + '.' + KEY_CODES);

        if (languageSection != null) {
            final long start = System.currentTimeMillis();

            CompletableFuture
            .allOf(
                    LanguageFile
                    .allFromSection(
                            this.file,
                            this.yaml,
                            languageSection,
                            languageFile -> {
                                TranslationRegistry.registry().registerAll(languageFile);
                                MSLogger.fine(
                                        text("Loaded language : ")
                                        .append(text(languageFile.getLocale().getDisplayName()))
                                        .append(text(" with "))
                                        .append(text(String.valueOf(languageFile.size())))
                                        .append(text(" translations in "))
                                        .append(text(System.currentTimeMillis() - start))
                                        .append(text("ms"))
                                );
                            },
                            (localeTag, throwable) ->
                                    MSLogger.warning(
                                            text("Failed to load language \"")
                                            .append(text(localeTag))
                                            .append(text('"')),
                                            throwable
                                    )
                    )
                    .values()
                    .toArray(CompletableFuture[]::new)
            )
            .thenRun(TranslationRegistry::registerGlobal);
        }
    }
}
