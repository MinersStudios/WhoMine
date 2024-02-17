package com.minersstudios.mscore.locale;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.renderer.TranslatableComponentRenderer;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.Translator;
import net.kyori.adventure.util.TriState;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.minersstudios.mscore.locale.TranslationRegistryImpl.SINGLETON;

public interface TranslationRegistry extends Translator {

    /**
     * Returns the namespaced key of this registry
     *
     * @return The namespaced key of this registry
     */
    @Override
    @NotNull Key name();

    /**
     * Returns the default locale of this registry
     *
     * @return The default locale of this registry
     */
    @NotNull Locale getDefaultLocale();

    /**
     * Returns an unmodifiable view of the translation map
     *
     * @return An unmodifiable view of the translation map
     */
    @NotNull @UnmodifiableView Map<String, Translation> getTranslationMap();

    /**
     * Returns the renderer of this registry
     *
     * @return The renderer of this registry
     */
    @NotNull TranslatableComponentRenderer<Locale> getRenderer();

    /**
     * Gets the translation for the given key
     *
     * @param key The key of the translation
     * @return The translation for the given key, or {@code null} if not found
     */
    @Nullable Translation getTranslation(final @NotNull String key);

    /**
     * Gets the translation object for the given key
     *
     * @param key      The key of the translation
     * @param fallback The fallback of the translation
     * @return The translation for the given key, or a new translation with the
     *         given fallback if not found
     */
    @NotNull Translation getTranslation(
            final @NotNull String key,
            final @Nullable String fallback
    );

    /**
     * Gets the translation for the given key, or key if not found
     *
     * @param key    The key of the translation
     * @param locale The locale of the translation
     * @return The translation for the given key and locale, or key if not found
     * @see #translate(String, String, Locale)
     */
    @Override
    @NotNull MessageFormat translate(
            final @NotNull String key,
            final @NotNull Locale locale
    );

    /**
     * Gets the translation for the given key, or fallback if not found
     *
     * @param key      The key of the translation
     * @param fallback The fallback of the translation
     * @param locale   The locale of the translation
     * @return The translation for the given key and locale, or fallback if not
     *         found
     * @see Translation#translate(Locale, String)
     */
    @NotNull MessageFormat translate(
            final @NotNull String key,
            final @Nullable String fallback,
            final @NotNull Locale locale
    );

    /**
     * Returns a component representing the given translatable component with
     * the given locale
     *
     * @param component The component to render
     * @param locale    The locale to render the component in
     * @return A component representing the given translatable component with
     *         the given locale
     * @deprecated Use {@link #render(Component, Locale)} instead
     */
    @Deprecated
    @Override
    @NotNull Component translate(
            final @NotNull TranslatableComponent component,
            final @NotNull Locale locale
    );

    /**
     * Returns a rendered component representing the given component with the
     * given locale
     *
     * @param component The component to render
     * @param locale    The locale to render the component in
     * @return A rendered component
     */
    @NotNull Component render(
            final @NotNull Component component,
            final @NotNull Locale locale
    );

    /**
     * Returns whether this registry has any translations
     *
     * @return Whether this registry has any translations
     */
    @Override
    @NotNull TriState hasAnyTranslations();

    /**
     * Checks whether this registry contains the given key
     *
     * @param key The key to check for
     * @return Whether this registry contains the given key
     */
    boolean contains(final @Nullable String key);

    /**
     * Registers a new translation for the given key
     *
     * @param key The key to register
     * @return The registered translation
     * @see #registerKey(String, String)
     */
    @NotNull Translation registerKey(final @NotNull String key);

    /**
     * Registers a new translation for the given key
     *
     * @param key      The key to register
     * @param fallback The fallback to register
     * @return The registered translation
     */
    @NotNull Translation registerKey(
            final @NotNull String key,
            final @Nullable String fallback
    );

    /**
     * Registers the given translation
     *
     * @param translation The translation to register
     */
    void register(final @NotNull Translation translation);

    /**
     * Registers the given translation for the given key and locale
     *
     * @param key         The key to register
     * @param locale      The locale to register the translation for
     * @param translation The translation to register
     * @return The registered translation
     */
    @NotNull Translation register(
            final @NotNull String key,
            final @NotNull Locale locale,
            final @NotNull String translation
    );

    /**
     * Registers the given translations for the given keys
     *
     * @param keys The keys to register
     * @return The registered translations
     * @see #registerAllKeys(Iterable)
     */
    @NotNull List<Translation> registerAllKeys(final String @NotNull ... keys);

    /**
     * Registers a new translations for the given keys
     *
     * @param keys The keys to register
     * @return The registered translations
     * @see #registerKey(String)
     */
    @NotNull List<Translation> registerAllKeys(final @NotNull Iterable<String> keys);

    /**
     * Registers a new translations for the given keys and their fallbacks
     *
     * @param keys The key map containing the keys and their fallbacks
     * @return The registered translations
     * @see #registerKey(String, String)
     */
    @NotNull List<Translation> registerAllKeys(final @NotNull Map<String, String> keys);

    /**
     * Registers the translations from the given language file
     *
     * @param languageFile The language file to register the translations from
     * @return The registered translations
     * @see #registerAll(Locale, Map)
     */
    @NotNull List<Translation> registerAll(final @NotNull LanguageFile languageFile);

    /**
     * Registers the given translations for the given locale
     *
     * @param locale         The locale to register the translations for
     * @param translationMap The translations to register
     * @return The registered translations
     * @see #register(String, Locale, String)
     */
    @NotNull List<Translation> registerAll(
            final @NotNull Locale locale,
            final @NotNull Map<String, String> translationMap
    );

    /**
     * Unregisters the translation for the given key
     *
     * @param key The key to unregister
     * @return Whether the translation was unregistered
     */
    boolean unregister(final @NotNull String key);

    /**
     * Unregisters the locale for every translation
     *
     * @param locale The locale to unregister
     * @return Whether the locale was unregistered
     */
    boolean unregister(final @NotNull Locale locale);

    /**
     * Unregisters the locale for the given key
     *
     * @param key    The key to unregister
     * @param locale The locale to unregister
     * @return Whether the locale was unregistered
     */
    boolean unregister(
            final @NotNull String key,
            final @NotNull Locale locale
    );

    /**
     * Unregisters the translations, which were registered by the given language
     * file
     *
     * @param languageFile The language file, which translations should be
     *                     unregistered
     * @return Whether any of the translation was unregistered
     * @see #unregister(Iterable)
     */
    boolean unregister(final @NotNull LanguageFile languageFile);

    /**
     * Unregisters all translations for the given keys
     *
     * @param keys The keys to unregister
     * @return Whether any of the translation was unregistered
     */
    boolean unregister(final @NotNull Iterable<String> keys);

    /**
     * Unregisters all translations
     */
    void unregisterAll();

    /**
     * Returns the translation registry singleton
     *
     * @return The translation registry singleton
     */
    static @NotNull TranslationRegistry registry() {
        return SINGLETON;
    }

    /**
     * Registers this registry to the global translator
     */
    static void registerGlobal() {
        GlobalTranslator.translator().addSource(registry());
    }

    /**
     * Unregisters this registry from the global translator
     */
    static void unregisterGlobal() {
        GlobalTranslator.translator().removeSource(registry());
    }

    static @NotNull Component renderAsComponent(
            final @NotNull String key,
            final Object @NotNull ... args
    ) {
        return renderAsComponent(key, registry().getDefaultLocale(), args);
    }

    static @NotNull Component renderAsComponent(
            final @NotNull String key,
            final @Nullable String fallback,
            final Object @NotNull ... args
    ) {
        return renderAsComponent(key, registry().getDefaultLocale(), fallback, args);
    }

    @Contract("_, _, _ -> new")
    static @NotNull Component renderAsComponent(
            final @NotNull String key,
            final @NotNull Locale locale,
            final Object @NotNull ... args
    ) {
        return renderAsComponent(key, null, locale, args);
    }

    @Contract("_, _, _, _ -> new")
    static @NotNull Component renderAsComponent(
            final @NotNull String key,
            final @Nullable String fallback,
            final @NotNull Locale locale,
            final Object @NotNull ... args
    ) {
        return Component.text(renderAsString(key, fallback, locale, args));
    }

    /**
     * Gets the translation for the given key and returns it as a string, or if
     * it doesn't exist, returns the key
     *
     * @param key  The key of the translation
     * @param args The arguments to format the translation with
     * @return A string representing the translation for the given key and the
     *         default locale
     * @see #renderAsString(String, Locale, Object...)
     */
    static @NotNull String renderAsString(
            final @NotNull String key,
            final Object @NotNull ... args
    ) {
        return renderAsString(key, registry().getDefaultLocale(), args);
    }

    /**
     * Gets the translation for the given key and returns it as a string, or if
     * it doesn't exist, returns the fallback
     *
     * @param key      The key of the translation
     * @param fallback The fallback of the translation
     * @param args     The arguments to format the translation with
     * @return A string representing the translation for the given key and the
     *         default locale
     * @see #renderAsString(String, String, Locale, Object...)
     */
    static @NotNull String renderAsString(
            final @NotNull String key,
            final @Nullable String fallback,
            final Object @NotNull ... args
    ) {
        return renderAsString(key, registry().getDefaultLocale(), fallback, args);
    }

    /**
     * Gets the translation for the given key and locale and returns it as a
     * string, or if it doesn't exist, returns the key
     *
     * @param key    The key of the translation
     * @param locale The locale of the translation
     * @return A string representing the translation for the given key and
     *         locale
     * @see #renderAsString(String, String, Locale, Object...)
     */
    static @NotNull String renderAsString(
            final @NotNull String key,
            final @NotNull Locale locale,
            final Object @NotNull ... args
    ) {
        return renderAsString(key, null, locale, args);
    }

    /**
     * Gets the translation for the given key and locale and returns it as a
     * string, or if it doesn't exist, returns the fallback
     *
     * @param key      The key of the translation
     * @param fallback The fallback of the translation
     * @param locale   The locale of the translation
     * @param args     The arguments to format the translation with
     * @return A string representing the translation for the given key and
     *         locale
     * @see #renderAsFormat(String, String, Locale)
     */
    static @NotNull String renderAsString(
            final @NotNull String key,
            final @Nullable String fallback,
            final @NotNull Locale locale,
            final Object @NotNull ... args
    ) {
        return renderAsFormat(key, fallback, locale)
                .format(args);
    }

    /**
     * Gets the translation for the given key and returns it as a message
     * format, or if it doesn't exist, returns the key
     *
     * @param key The key of the translation
     * @return A message format representing the translation for the given key
     *         and the default locale
     * @see #renderAsFormat(String, Locale)
     */
    static @NotNull MessageFormat renderAsFormat(final @NotNull String key) {
        return renderAsFormat(key, registry().getDefaultLocale());
    }

    /**
     * Gets the translation for the given key and returns it as a message
     * format, or if it doesn't exist, returns the fallback
     *
     * @param key      The key of the translation
     * @param fallback The fallback of the translation
     * @return A message format representing the translation for the given key
     *         and the default locale
     * @see #renderAsFormat(String, String, Locale)
     */
    static @NotNull MessageFormat renderAsFormat(
            final @NotNull String key,
            final @Nullable String fallback
    ) {
        return renderAsFormat(key, fallback, registry().getDefaultLocale());
    }

    /**
     * Gets the translation for the given key and locale and returns it as a
     * message format, or if it doesn't exist, returns the key
     *
     * @param key    The key of the translation
     * @param locale The locale of the translation
     * @return A message format representing the translation for the given key
     *         and locale
     * @see #renderAsFormat(String, String, Locale)
     */
    static @NotNull MessageFormat renderAsFormat(
            final @NotNull String key,
            final @NotNull Locale locale
    ) {
        return renderAsFormat(key, null, locale);
    }

    /**
     * Gets the translation for the given key and locale and returns it as a
     * message format, or if it doesn't exist, returns the fallback
     *
     * @param key      The key of the translation
     * @param fallback The fallback of the translation
     * @param locale   The locale of the translation
     * @return A message format representing the translation for the given key
     *         and locale
     * @see #translate(String, String, Locale)
     */
    static @NotNull MessageFormat renderAsFormat(
            final @NotNull String key,
            final @Nullable String fallback,
            final @NotNull Locale locale
    ) {
        return registry().translate(key, fallback, locale);
    }

    /**
     * Returns a component representing the given component with the default
     * locale
     *
     * @param component The component to render
     * @return A component representing the given component with the default
     *         locale
     * @see #renderComponent(Component, Locale)
     */
    static @NotNull Component renderComponent(final @NotNull Component component) {
        return renderComponent(component, registry().getDefaultLocale());
    }

    /**
     * Returns a component representing the given component with the given
     * locale
     *
     * @param component The component to render
     * @param locale    The locale to render the component in
     * @return A component representing the given component with the given
     *         locale
     */
    static @NotNull Component renderComponent(
            final @NotNull Component component,
            final @NotNull Locale locale
    ) {
        return registry().render(component, locale);
    }
}
