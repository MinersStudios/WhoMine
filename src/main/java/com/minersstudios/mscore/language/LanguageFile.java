package com.minersstudios.mscore.language;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.minersstudios.mscore.utility.SharedConstants;
import com.minersstudios.mscore.plugin.GlobalConfig;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.mscore.utility.ChatUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;

import static net.kyori.adventure.text.Component.translatable;

/**
 * Language file loader. Loads the language file from the language repository
 * and adds it to {@link GlobalTranslator}. All downloaded language files are
 * stored in the {@link SharedConstants#LANGUAGE_FOLDER_PATH} folder.
 */
public final class LanguageFile {
    private final String folderLink;
    private final String code;
    private final JsonObject translations;
    private File file;

    private static TranslationRegistry registry = TranslationRegistry.create(Key.key("ms"));
    private static final Field TRANSLATIONS_FIELD;

    static {
        try {
            final var registryImplClass = Class.forName("net.kyori.adventure.translation.TranslationRegistryImpl");
            TRANSLATIONS_FIELD = registryImplClass.getDeclaredField("translations");

            TRANSLATIONS_FIELD.setAccessible(true);
        } catch (final Exception e) {
            throw new RuntimeException("Failed to initialize LanguageFile", e);
        }
    }

    private LanguageFile(
            final @NotNull String folderLink,
            final @NotNull String code
    ) {
        this.folderLink = folderLink;
        this.code = code;
        this.file = this.loadFile();
        this.translations = this.loadTranslations();
    }

    /**
     * @return The folder link of the language repository
     */
    public @NotNull String getFolderLink() {
        return this.folderLink;
    }

    /**
     * @return The language code of the language file
     */
    public @NotNull String getCode() {
        return this.code;
    }

    /**
     * @return The language file
     */
    public @NotNull File getFile() {
        return this.file;
    }

    /**
     * @return The translation registry of the language file
     */
    public @NotNull TranslationRegistry getRegistry() {
        return registry;
    }

    /**
     * Loads the translations from the language file to {@link GlobalTranslator}.
     * If the language file does not exist, it will be downloaded from the
     * language repository.
     *
     * @param folderLink The folder link of the language repository
     * @param code Language code of the language file
     */
    public static void loadLanguage(
            final @NotNull String folderLink,
            final @NotNull String code
    ) {
        final long time = System.currentTimeMillis();
        final LanguageFile languageFile = new LanguageFile(folderLink, code);

        languageFile.translations.entrySet().forEach(
                entry -> {
                    final String key = entry.getKey();
                    final String value = entry.getValue().getAsString();

                    if (registry.contains(key)) {
                        registry.unregister(key);
                    }

                    registry.register(key, Locale.US, new MessageFormat(value));
                }
        );
        GlobalTranslator.translator().addSource(registry);

        MSLogger.fine(
                "Loaded language file: " + languageFile.file.getName() + " in " + (System.currentTimeMillis() - time) + "ms"
        );
    }

    /**
     * Unloads all languages registered in {@link #registry} from
     * {@link GlobalTranslator}
     */
    public static void unloadLanguage() {
        GlobalTranslator.translator().removeSource(registry);
        registry = TranslationRegistry.create(Key.key("ms"));
    }

    /**
     * Reloads language file
     *
     * @see #unloadLanguage()
     * @see #loadLanguage(String, String)
     */
    public static void reloadLanguage() {
        final GlobalConfig config = MSPlugin.globalConfig();

        unloadLanguage();
        loadLanguage(config.getLanguageFolderLink(), config.getLanguageCode());
    }

    /**
     * Renders the translation from {@link #registry} as
     * {@link TranslatableComponent}.
     * <br>
     * <b>NOTE:</b> Use only for custom translations loaded from the language
     * file. Usually used for item names and lore, because they are rendered it
     * without a fallback
     *
     * @param key Translation key
     * @return TranslatableComponent with translation from {@link #registry} or
     *         key if translation is not found
     * @see #renderTranslation(String)
     */
    public static @NotNull TranslatableComponent renderTranslationComponent(final @NotNull String key) {
        return translatable(key, renderTranslation(key));
    }

    /**
     * Renders the translation from {@link GlobalTranslator} as {@link Component}
     *
     * @param translatable TranslatableComponent to render
     * @return Translated component or key if translation is not found
     */
    public static @NotNull Component renderTranslationComponent(final @NotNull TranslatableComponent translatable) {
        return GlobalTranslator.render(translatable, Locale.US);
    }

    /**
     * Renders the translation from {@link #registry} as {@link String}
     * <br>
     * <b>NOTE:</b> Use only for custom translations loaded from language file
     *
     * @param key Translation key
     * @return Translated string or key if translation is not found
     */
    public static @NotNull String renderTranslation(final @NotNull String key) {
        final MessageFormat format = registry.translate(key, Locale.US);
        return format == null ? key : format.toPattern();
    }

    /**
     * Renders the translation from {@link GlobalTranslator} as {@link String}
     *
     * @param translatable TranslatableComponent to render
     * @return Translated string or key if translation is not found
     */
    public static @NotNull String renderTranslation(final @NotNull TranslatableComponent translatable) {
        return ChatUtils.serializePlainComponent(
                renderTranslationComponent(translatable)
        );
    }

    /**
     * @return True if language is loaded
     *         (if {@link #registry} is not empty)
     */
    public static boolean isLoaded() {
        try {
            return !((Map<?, ?>) TRANSLATIONS_FIELD.get(registry)).isEmpty();
        } catch (final IllegalAccessException e) {
            MSLogger.severe("Failed to check if language is loaded", e);
            return false;
        }
    }

    /**
     * Loads language file from the language repository. File will be downloaded
     * to {@link SharedConstants#LANGUAGE_FOLDER_PATH} folder.
     *
     * @return Language file
     */
    private @NotNull File loadFile() {
        final File langFolder = new File(SharedConstants.LANGUAGE_FOLDER_PATH);

        if (!langFolder.exists() && !langFolder.mkdirs()) {
            throw new RuntimeException("Failed to create language folder");
        }

        final File langFile = new File(langFolder, this.code + ".json");

        if (!langFile.exists()) {
            final String link =
                    this.folderLink
                    + (
                            this.folderLink.endsWith("/")
                            ? ""
                            : '/'
                    )
                    + this.code + ".json";

            try (
                    final var in = new URL(link).openStream();
                    final var out = new FileOutputStream(langFile)
            ) {
                in.transferTo(out);
            } catch (final IOException e) {
                MSLogger.severe("Failed to download language file: " + link, e);
            }
        }

        return langFile;
    }

    /**
     * Loads language file as {@link JsonObject} from the file path specified in
     * {@link #file}
     *
     * @return JsonObject of language file
     * @throws JsonSyntaxException If language file is corrupted
     */
    private @NotNull JsonObject loadTranslations() throws JsonSyntaxException {
        try {
            final Path path = Path.of(this.file.getAbsolutePath());
            final String content = Files.readString(path);
            final JsonElement element = JsonParser.parseString(content);

            return element.getAsJsonObject();
        } catch (final IOException | JsonSyntaxException e) {
            MSLogger.severe("Failed to load corrupted language file: " + this.code + ".json");
            MSLogger.severe("Creating backup file and trying to load language file again");

            this.createBackupFile();
            this.file = this.loadFile();

            return this.loadTranslations();
        }
    }

    /**
     * Creates a backup file of the corrupted language file. The backup file
     * will be named as the original file with ".OLD" appended to the end. If
     * the backup file already exists, it will be replaced
     *
     * @throws RuntimeException If failed to create backup file
     */
    private void createBackupFile() throws RuntimeException {
        final String backupFileName = this.file.getName() + ".OLD";
        final File backupFile = new File(this.file.getParent(), backupFileName);
        final Path filePath = Path.of(this.file.getAbsolutePath());
        final Path backupFilePath = Path.of(backupFile.getAbsolutePath());

        try {
            Files.move(filePath, backupFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            throw new RuntimeException("Failed to create" + backupFileName + " backup file", e);
        }
    }
}
