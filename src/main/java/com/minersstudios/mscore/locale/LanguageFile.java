package com.minersstudios.mscore.locale;

import com.google.common.base.Joiner;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.minersstudios.mscore.locale.resource.GitHubTranslationResourceManager;
import com.minersstudios.mscore.locale.resource.TranslationResourceManager;
import com.minersstudios.mscore.locale.resource.URITranslationResourceManager;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.resource.github.Tag;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscore.utility.SharedConstants;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.kyori.adventure.translation.Translator;
import net.minecraft.locale.Language;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.*;

import javax.annotation.concurrent.Immutable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static java.util.concurrent.CompletableFuture.failedFuture;

/**
 * Represents a language file containing translations for a specific locale.
 * <br>
 * Language file can be created with a couple of static methods :
 * <ul>
 *     <li>{@link #fromResource(Locale, TranslationResourceManager)}</li>
 *     <li>{@link #fromStream(Locale, InputStream)}</li>
 * </ul>
 */
@Immutable
public final class LanguageFile {
    private final Locale locale;
    private final Map<String, String> translationMap;

    //<editor-fold desc="Config keys">
    private static final String KEY_URL =         "url";
    private static final String KEY_TOKEN =       "token";
    private static final String KEY_USER =        "user";
    private static final String KEY_REPO =        "repo";
    private static final String KEY_TAG =         "tag";
    private static final String KEY_FOLDER_PATH = "folder-path";
    //</editor-fold>

    private LanguageFile(final @NotNull Locale locale) {
        this.locale = locale;
        this.translationMap = new Object2ObjectOpenHashMap<>();
    }

    /**
     * Returns the locale of this language file
     *
     * @return The locale of this language file
     */
    public @NotNull Locale getLocale() {
        return this.locale;
    }

    /**
     * Returns the unmodifiable map of translations in this language file
     *
     * @return The unmodifiable map of translations in this language file
     */
    public @NotNull @Unmodifiable Map<String, String> getTranslationMap() {
        return Collections.unmodifiableMap(this.translationMap);
    }

    /**
     * Gets the translation for the given path
     *
     * @param path The path to get the translation for
     * @return The translation for the given path, or null if it doesn't exist
     */
    public @Nullable String get(final @NotNull String path) {
        return this.getOrDefault(path, null);
    }

    /**
     * Gets the translation for the given path, or the fallback if it doesn't
     * exist
     *
     * @param path     The path to get the translation for
     * @param fallback The fallback to return if the translation doesn't exist
     * @return The translation for the given path, or the fallback if it doesn't
     *         exist
     */
    public @UnknownNullability String getOrDefault(
            final @NotNull String path,
            final @Nullable String fallback
    ) {
        return this.translationMap.getOrDefault(path, fallback);
    }

    /**
     * Returns the number of translations in this language file
     *
     * @return The number of translations in this language file
     */
    public int size() {
        return this.translationMap.size();
    }

    /**
     * Returns a hash code based on the locale and translations
     *
     * @return A hash code based on the locale and translations
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + this.locale.hashCode();
        result = prime * result + this.translationMap.hashCode();

        return result;
    }

    /**
     * Compares the specified object with this language file for equality
     *
     * @param obj The object to compare
     * @return True if the object is a language file and has the same locale and
     *         translations
     */
    @Contract("null -> false")
    @Override
    public boolean equals(final @Nullable Object obj) {
        return this == obj
                || (
                        obj instanceof LanguageFile that
                        && this.locale.equals(that.locale)
                        && this.translationMap.equals(that.translationMap)
                );
    }

    /**
     * Checks whether the given path exists in this language file
     *
     * @param path The path to check for
     * @return Whether the given path exists in this language file
     */
    public boolean containsPath(final @NotNull String path) {
        return this.translationMap.containsKey(path);
    }

    /**
     * Returns whether this language file contains the given translation
     *
     * @param translation The translation to check for
     * @return True if this language file contains the given translation
     */
    public boolean containsTranslation(final @NotNull String translation) {
        return this.translationMap.containsValue(translation);
    }

    /**
     * Returns whether this language file contains no translations
     *
     * @return True if this language file contains no translations
     */
    public boolean isEmpty() {
        return this.translationMap.isEmpty();
    }

    /**
     * Returns a string representation of this language file
     *
     * @return A string representation of this language file containing the
     *         translations
     */
    @Override
    public @NotNull String toString() {
        return "LanguageFile{" +
                "locale=" + this.locale +
                ", translations=[" + Joiner.on(", ").withKeyValueSeparator("=").join(this.translationMap) + ']' +
                '}';
    }

    /**
     * Creates and loads all languages from the given configuration section.
     * <br>
     * Completes exceptionally with :
     * <ul>
     *     <li>{@link IllegalStateException} - If the locale cannot be parsed
     *                                         or the URL or user and repository
     *                                         are not specified</li>
     *     <li>{@link JsonIOException}       - If the file cannot be read</li>
     *     <li>{@link JsonSyntaxException}   - If the file is not a valid JSON
     *                                         file</li>
     * </ul>
     *
     * Example of a configuration section :
     * <pre>
     * languages:
     *   en_us: '' # Loads from the file
     *   en_gb:
     *     url: https:\\example.com\en_us.json
     *   uk_ua:
     *     user: ExampleUser
     *     repo: ConfigRepo
     *     tag: v1.0
     *     token: ~some-github-token~
     *     folder-path: lang
     * </pre>
     *
     * @param file     The configuration file
     * @param config   The yaml configuration
     * @param section  The configuration section
     * @param onLoaded The consumer to accept the loaded language file if the
     *                 language file was loaded successfully
     * @param onFailed The consumer to accept the key and the exception if the
     *                 language file failed to load
     * @return A map containing the language keys and their respective
     *         completable futures of language files
     * @see #fromResource(Locale, TranslationResourceManager)
     * @see #fromSection(File, YamlConfiguration, ConfigurationSection)
     */
    public static @NotNull Map<String, CompletableFuture<LanguageFile>> allFromSection(
            final @NotNull File file,
            final @NotNull YamlConfiguration config,
            final @NotNull ConfigurationSection section,
            final @Nullable Consumer<LanguageFile> onLoaded,
            final @Nullable BiConsumer<String, Throwable> onFailed
    ) {
        final var keySet = section.getKeys(false);
        final var futureMap = new Object2ObjectOpenHashMap<String, CompletableFuture<LanguageFile>>(keySet.size());

        for (final var key : keySet) {
            final ConfigurationSection langSection = section.getConfigurationSection(key);
            CompletableFuture<LanguageFile> future;

            if (langSection == null) {
                final Locale locale = Translator.parseLocale(key);

                if (locale == null) {
                    MSLogger.warning("Failed to parse locale: " + key);

                    continue;
                }

                try {
                    future = CompletableFuture.completedFuture(
                            fromResource(
                                    locale,
                                    TranslationResourceManager.file(getFile(key))
                            )
                    );
                } catch (final Throwable e) {
                    future = failedFuture(e);
                }
            } else {
                future = fromSection(file, config, langSection);
            }

            futureMap.put(
                    key,
                    future
                    .thenApply(
                            languageFile -> {
                                if (onLoaded != null) {
                                    onLoaded.accept(languageFile);
                                }

                                return languageFile;
                            }
                    )
                    .exceptionallyCompose(
                            throwable -> {
                                if (onFailed != null) {
                                    onFailed.accept(key, throwable);
                                }

                                return failedFuture(throwable);
                            }
                    )
            );
        }

        return futureMap;
    }

    /**
     * Creates and loads a new language file from the given locale and
     * configuration section.
     * <br>
     * Completes exceptionally with :
     * <ul>
     *     <li>{@link IllegalStateException} - If the locale cannot be parsed,
     *                                         or the URL or user and repository
     *                                         are not specified</li>
     *     <li>{@link JsonIOException}       - If the file cannot be read</li>
     *     <li>{@link JsonSyntaxException}   - If the file is not a valid JSON
     *                                         file</li>
     * </ul>
     *
     * Configuration sections example :
     * <pre>
     * en_us:
     *   url: https:\\example.com\en_us.json
     * uk_ua:
     *   user: ExampleUser
     *   repo: ConfigRepo
     *   tag: v1.0
     *   token: ~some-github-token~
     *   folder-path: lang
     * </pre>
     *
     * @param file    The configuration file
     * @param config  The yaml configuration
     * @param section The configuration section
     * @return A future containing the language file
     * @see #fromURI(Locale, URITranslationResourceManager)
     * @see #fromGitHub(Locale, GitHubTranslationResourceManager)
     */
    public static @NotNull CompletableFuture<LanguageFile> fromSection(
            final @NotNull File file,
            final @NotNull YamlConfiguration config,
            final @NotNull ConfigurationSection section
    ) {
        final String localeCode = section.getName();
        final Locale locale = Translator.parseLocale(localeCode);

        if (locale == null) {
            return failedFuture(new IllegalStateException("Failed to parse locale: " + localeCode));
        }

        final String url = section.getString(KEY_URL);

        if (ChatUtils.isNotBlank(url)) {
            return fromURI(
                    locale,
                    TranslationResourceManager.url(url)
            );
        }

        final String user = section.getString(KEY_USER);
        final String repo = section.getString(KEY_REPO);

        if (
                ChatUtils.isBlank(user)
                || ChatUtils.isBlank(repo)
        ) {
            return failedFuture(new IllegalStateException("Specify the URL or user and repository for : " + section));
        }

        final GitHubTranslationResourceManager resourceManager =
                TranslationResourceManager.github(
                        getFile(localeCode),
                        user,
                        repo,
                        section.getString(KEY_TAG),
                        section.getString(KEY_TOKEN),
                        section.getString(KEY_FOLDER_PATH)
                );

        return fromGitHub(locale, resourceManager)
                .thenApply(languageFile -> {
                    final Tag latestTag = resourceManager.getLatestTagNow();

                    if (latestTag != null) {
                        section.set(KEY_TAG, latestTag.getName());

                        synchronized (LanguageFile.class) {
                            try {
                                config.save(file);
                            } catch (final Throwable e) {
                                MSLogger.warning(
                                        "Failed to save the configuration file: " + file,
                                        e
                                );
                            }
                        }
                    }

                    return languageFile;
                });
    }

    /**
     * Creates and loads a new language file from the given locale and resource
     * manager.
     * <br>
     * Completes exceptionally with :
     * <ul>
     *     <li>{@link JsonIOException}     - If the file cannot be read</li>
     *     <li>{@link JsonSyntaxException} - If the file is not a valid JSON
     *                                       file</li>
     * </ul>
     *
     * @param locale          The locale of the language file
     * @param resourceManager The resource manager of the language file
     * @return A future containing the language file
     * @see #fromResource(Locale, TranslationResourceManager)
     */
    public static @NotNull CompletableFuture<LanguageFile> fromGitHub(
            final @NotNull Locale locale,
            final @NotNull GitHubTranslationResourceManager resourceManager
    ) {
        return resourceManager
                .updateFile(false)
                .thenApplyAsync(ignored -> fromResource(locale, resourceManager));
    }

    /**
     * Creates and loads a new language file from the given locale and uri
     * resource manager.
     * <br>
     * Completes exceptionally with :
     * <ul>
     *     <li>{@link JsonIOException}     - If the file cannot be read</li>
     *     <li>{@link JsonSyntaxException} - If the file is not a valid JSON
     *                                       file</li>
     * </ul>
     *
     * @param locale          The locale of the language file
     * @param resourceManager The resource manager of the language file
     * @return A future containing the language file
     * @see #fromResource(Locale, TranslationResourceManager)
     */
    public static @NotNull CompletableFuture<LanguageFile> fromURI(
            final @NotNull Locale locale,
            final @NotNull URITranslationResourceManager resourceManager
    ) {
        return CompletableFuture.supplyAsync(() -> fromResource(locale, resourceManager));
    }

    /**
     * Creates and loads a new language file from the given locale and resource
     * manager
     *
     * @param locale          The locale of the language file
     * @param resourceManager The resource manager of the language file
     * @return A future containing the language file
     * @throws JsonIOException     If the file cannot be read
     * @throws JsonSyntaxException If the file is not a valid JSON file
     * @see #fromStream(Locale, InputStream)
     */
    public static @NotNull LanguageFile fromResource(
            final @NotNull Locale locale,
            final @NotNull TranslationResourceManager resourceManager
    ) throws JsonIOException, JsonSyntaxException {
        try (final var in = resourceManager.openStream()) {
            return fromStream(locale, in);
        } catch (final IOException e) {
            throw new JsonIOException(
                    "Failed to read resource : " + resourceManager,
                    e
            );
        }
    }

    /**
     * Creates and loads a new language file from the given locale and input
     * stream
     *
     * @param locale      The locale of the language file
     * @param inputStream The input stream of the language file
     * @return The language file
     * @throws JsonIOException     If the file cannot be read
     * @throws JsonSyntaxException If the file is not a valid JSON file
     */
    public static @NotNull LanguageFile fromStream(
            final @NotNull Locale locale,
            final @NotNull InputStream inputStream
    ) throws JsonIOException, JsonSyntaxException {
        final LanguageFile file = new LanguageFile(locale);

        try (inputStream) {
            Language.loadFromJson(
                    inputStream,
                    file.translationMap::put
            );
        } catch (final IOException e) {
            throw new JsonIOException(e);
        }

        return file;
    }

    private static @NotNull File getFile(final @NotNull String path) {
        return new File(SharedConstants.LANGUAGE_FOLDER_PATH, path + ".json");
    }
}
