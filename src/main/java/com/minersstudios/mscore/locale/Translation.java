package com.minersstudios.mscore.locale;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TranslatableComponent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.minersstudios.mscore.locale.TranslationRegistry.registry;

/**
 * Represents a translation
 */
public final class Translation {
    private final String path;
    private final MessageFormat fallback;
    private final Map<Locale, MessageFormat> map;
    private final TranslatableComponent cachedTranslatable;

    Translation(
            final @NotNull String path,
            final @Nullable String fallback
    ) {
        this.path = path;
        this.fallback =
                new MessageFormat(
                        fallback == null
                        ? path
                        : fallback
                );
        this.map = new ConcurrentHashMap<>();
        this.cachedTranslatable =
                Component.translatable(
                        this.path,
                        this.fallback.toPattern()
                );
    }

    /**
     * Returns the path of this translation
     *
     * @return The path of this translation
     */
    public @NotNull String getPath() {
        return this.path;
    }

    /**
     * Returns the fallback of this translation
     *
     * @return The fallback of this translation, or the path if no fallback was
     *         specified
     */
    public @NotNull MessageFormat getFallback() {
        return this.fallback;
    }

    /**
     * Returns an unmodifiable view of the locale set
     *
     * @return An unmodifiable view of the locale set
     */
    public @NotNull @UnmodifiableView Set<Locale> localeSet() {
        return Collections.unmodifiableSet(this.map.keySet());
    }

    /**
     * Returns an unmodifiable view of the translations
     *
     * @return An unmodifiable view of the translations
     */
    public @NotNull @UnmodifiableView Collection<MessageFormat> formats() {
        return Collections.unmodifiableCollection(this.map.values());
    }

    /**
     * Returns an unmodifiable view of the translation entries
     *
     * @return An unmodifiable view of the translation entries
     */
    public @NotNull @UnmodifiableView Set<Map.Entry<Locale, MessageFormat>> entrySet() {
        return Collections.unmodifiableSet(this.map.entrySet());
    }

    /**
     * Gets the translation for the given locale
     *
     * @param locale The locale to get the translation for
     * @return The translation for the given locale, or the fallback if it
     *         doesn't exist
     * @see #translate(Locale, String)
     */
    public @NotNull MessageFormat translate(final @NotNull Locale locale) {
        return this.translate(locale, null);
    }

    /**
     * Gets the translation for the given locale
     *
     * @param locale   The locale to get the translation for
     * @param fallback The fallback to return if the translation doesn't exist
     * @return The translation for the given locale, or the fallback if it
     *         doesn't exist
     * @see #translateNullable(Locale, String)
     */
    public @NotNull MessageFormat translate(
            final @NotNull Locale locale,
            final @Nullable String fallback
    ) {
        final MessageFormat format = this.translateNullable(locale, fallback);

        return format == null
               ? this.fallback
               : format;
    }

    /**
     * Gets the translation for the given locale
     *
     * @param locale The locale to get the translation for
     * @return The translation for the given locale
     * @see #translateNullable(Locale, String)
     */
    public @Nullable MessageFormat translateNullable(final @NotNull Locale locale) {
        return this.translateNullable(locale, null);
    }

    /**
     * Gets the translation for the given locale
     *
     * @param locale   The locale to get the translation for
     * @param fallback The fallback to return if the translation doesn't exist
     * @return The translation for the given locale, or the fallback if it
     *         doesn't exist
     */
    public @Nullable MessageFormat translateNullable(
            final @NotNull Locale locale,
            final @Nullable String fallback
    ) {
        final MessageFormat format = this.map.get(locale);

        if (format == null) {
            final MessageFormat defaultFormat = this.map.get(registry().getDefaultLocale());

            return defaultFormat == null
                   ? (
                           fallback == null
                           ? null
                           : new MessageFormat(fallback)
                   )
                   : defaultFormat;
        }

        return format;
    }

    /**
     * Returns the hash code of this translation
     *
     * @return The hash code of this translation
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + this.path.hashCode();
        result = prime * result + this.fallback.hashCode();
        result = prime * result + this.map.hashCode();
        result = prime * result + this.cachedTranslatable.hashCode();

        return result;
    }

    /**
     * Returns whether this translation contains the given locale
     *
     * @param locale The locale to check
     * @return Whether this translation contains the given locale
     */
    @Contract("null -> false")
    public boolean containsLocale(final @Nullable Locale locale) {
        return locale != null
                && this.map.containsKey(locale);
    }

    /**
     * Returns whether this translation contains the given format
     *
     * @param format The format to check
     * @return Whether this translation contains the given format
     */
    @Contract("null -> false")
    public boolean containsFormat(final @Nullable MessageFormat format) {
        return format != null
                && this.map.containsValue(format);
    }

    /**
     * Returns whether this translation contains no translations
     *
     * @return Whether this translation contains no translations
     */
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    /**
     * Returns whether the given object is equal to this translation
     *
     * @param obj The object to compare to
     * @return Whether the given object is equal to this translation
     */
    @Contract("null -> false")
    @Override
    public boolean equals(final @Nullable Object obj) {
        return obj == this
                || (
                        obj instanceof final Translation that
                        && that.path.equals(this.path)
                        && that.fallback.equals(this.fallback)
                        && that.map.values().equals(this.map.values())
                        && that.cachedTranslatable.equals(this.cachedTranslatable)
                );
    }

    /**
     * Returns a string representation of this translation
     *
     * @return A string representation of this translation
     */
    @Override
    public @NotNull String toString() {
        final StringBuilder builder = new StringBuilder();

        for (final var entry : this.map.entrySet()) {
            builder
            .append(entry.getKey())
            .append('=')
            .append(entry.getValue().toPattern())
            .append(", ");
        }

        if (!this.map.isEmpty()) {
            builder.setLength(builder.length() - 2);
        }

        return "Translation{" +
                "path=" + this.path +
                ", fallback=" + this.fallback +
                ", translations=[" + builder + ']' +
                '}';
    }

    /**
     * Returns a translatable component representing this translation
     *
     * @return A translatable component representing this translation
     */
    public @NotNull TranslatableComponent asTranslatable() {
        return this.cachedTranslatable;
    }

    /**
     * Returns a translatable component representing this translation
     *
     * @param fallback The fallback to return if the translation doesn't exist
     * @return A translatable component representing this translation
     */
    @Contract("null -> new")
    public @NotNull TranslatableComponent asTranslatable(final @Nullable String fallback) {
        return fallback == null
               ? this.cachedTranslatable
               : Component.translatable(this.path, fallback);
    }

    /**
     * Returns a formatted translation
     *
     * @param args The arguments to format the translation with
     * @return The formatted translation with the default locale and the given
     *         arguments
     * @see #asString(Locale, Object...)
     */
    public @NotNull String asString(final Object @NotNull ... args) {
        return this.asString(registry().getDefaultLocale(), args);
    }

    /**
     * Returns a formatted translation
     *
     * @param locale The locale to format the translation with
     * @param args   The arguments to format the translation with
     * @return The formatted translation with the given locale and the given
     *         arguments
     * @see #asFormat(Locale)
     */
    public @NotNull String asString(
            final @NotNull Locale locale,
            final Object @NotNull ... args
    ) {
        return this.asFormat(locale).format(args);
    }

    /**
     * Returns a message format representing this translation
     *
     * @return A message format representing this translation with the default
     *         locale
     * @see #asFormat(Locale)
     */
    public @NotNull MessageFormat asFormat() {
        return this.asFormat(registry().getDefaultLocale());
    }

    /**
     * Returns a message format representing this translation
     *
     * @param locale The locale to format the translation with
     * @return A message format representing this translation with the given
     *         locale
     */
    public @NotNull MessageFormat asFormat(final @NotNull Locale locale) {
        return this.translate(locale);
    }

    /**
     * Returns a rendered component representing this translation
     *
     * @param args The arguments to format the translation with
     * @return A component representing this translation with the default locale
     *         and the given arguments
     * @see #asComponent(Locale, ComponentLike...)
     */
    public @NotNull Component asComponent(final ComponentLike @NotNull ... args) {
        return this.asComponent(registry().getDefaultLocale(), args);
    }

    /**
     * Returns a rendered component representing this translation
     *
     * @param locale The locale to format the translation with
     * @param args   The arguments to format the translation with
     * @return A component representing this translation with the given locale
     *         and the given arguments
     */
    public @NotNull Component asComponent(
            final @NotNull Locale locale,
            final ComponentLike @NotNull ... args
    ) {
        return registry().render(
                this.asTranslatable().arguments(args),
                locale
        );
    }

    /**
     * Registers the given translation for the given locale
     *
     * @param locale      The locale to register the translation for
     * @param translation The translation to register
     */
    public void register(
            final @NotNull Locale locale,
            final @NotNull MessageFormat translation
    ) {
        this.map.put(locale, translation);
    }

    /**
     * Registers the given translations
     *
     * @param translations The translations to register
     */
    public void registerAll(final @NotNull Map<Locale, MessageFormat> translations) {
        this.map.putAll(translations);
    }

    /**
     * Unregisters the specified locale
     *
     * @param locale The locale to unregister
     * @return Whether the locale was unregistered
     */
    public boolean unregister(final @NotNull Locale locale) {
        return this.map.remove(locale) != null;
    }

    /**
     * Unregisters the specified translation
     *
     * @param translation The translation to unregister
     * @return Whether any translation was unregistered
     */
    public boolean unregister(final @NotNull MessageFormat translation) {
        boolean removed = false;

        for (final var entry : this.map.entrySet()) {
            if (entry.getValue().equals(translation)) {
                removed = true;

                this.map.remove(entry.getKey());
            }
        }

        return removed;
    }

    /**
     * Unregisters the specified translation for the specified locale
     *
     * @param locale      The locale to unregister
     * @param translation The translation to unregister
     * @return Whether the translation was unregistered
     */
    public boolean unregister(
            final @NotNull Locale locale,
            final @NotNull MessageFormat translation
    ) {
        return this.map.remove(locale, translation);
    }

    /**
     * Unregisters all translations
     */
    public void unregisterAll() {
        this.map.clear();
    }

    /**
     * Gets the translation for the given path from the translation registry
     *
     * @param path The path to get the translation for
     * @return The translation for the given path, or path if it doesn't exist
     * @see #of(String, String)
     */
    public static @NotNull Translation of(final @NotNull String path) {
        return of(path, null);
    }

    /**
     * Gets the translation for the given path from the translation registry
     *
     * @param path     The path to get the translation for
     * @param fallback The fallback to return if the translation doesn't exist
     * @return The translation for the given path, or the fallback if it doesn't
     * @see TranslationRegistry#getTranslation(String, String)
     */
    public static @NotNull Translation of(
            final @NotNull String path,
            final @Nullable String fallback
    ) {
        return registry().getTranslation(path, fallback);
    }
}
