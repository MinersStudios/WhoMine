package com.minersstudios.mscore.locale;

import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscore.utility.SharedConstants;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.renderer.TranslatableComponentRenderer;
import net.kyori.adventure.util.TriState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

final class TranslationRegistryImpl implements TranslationRegistry {
    private final Key name;
    private final Locale defaultLocale;
    private final Map<String, Translation> translationMap;
    private final TranslatableComponentRenderer<Locale> renderer;

    static final TranslationRegistryImpl SINGLETON =
            new TranslationRegistryImpl(
                    Key.key(SharedConstants.WHOMINE_NAMESPACE, "translations"),
                    MSPlugin.globalConfig().getDefaultLocale()
            );

    TranslationRegistryImpl(
            final @NotNull Key name,
            final @NotNull Locale defaultLocale
    ) {
        this.name = name;
        this.defaultLocale = defaultLocale;
        this.translationMap = new ConcurrentHashMap<>();
        this.renderer = new TranslatableComponentRenderer<>() {

            protected @Nullable MessageFormat translate(
                    final @NotNull String path,
                    final @NotNull Locale locale
            ) {
                return this.translate(path, null, locale);
            }

            protected @Nullable MessageFormat translate(
                    final @NotNull String path,
                    final @Nullable String fallback,
                    final @NotNull Locale locale
            ) {
                final Translation translation = TranslationRegistryImpl.this.getTranslation(path);

                return translation == null
                        ? null
                        : translation.translateNullable(locale);
            }

            protected @NotNull Component renderTranslatable(
                    final @NotNull TranslatableComponent component,
                    final @NotNull Locale locale
            ) {
                return TranslationRegistryImpl.this.translationMap.isEmpty()
                        ? component
                        : super.renderTranslatable(component, locale);
            }
        };
    }

    @Override
    public @NotNull Key name() {
        return this.name;
    }

    @Override
    public @NotNull Locale getDefaultLocale() {
        return this.defaultLocale;
    }

    @Override
    public @NotNull @UnmodifiableView Map<String, Translation> getTranslationMap() {
        return Collections.unmodifiableMap(this.translationMap);
    }

    @Override
    public @NotNull TranslatableComponentRenderer<Locale> getRenderer() {
        return this.renderer;
    }

    public @Nullable Translation getTranslation(final @NotNull String path) {
        return this.translationMap.get(path);
    }

    public @NotNull Translation getTranslation(
            final @NotNull String path,
            final @Nullable String fallback
    ) {
        return this.translationMap.getOrDefault(
                path,
                new Translation(path, fallback)
        );
    }

    @Override
    public @NotNull MessageFormat translate(
            final @NotNull String path,
            final @NotNull Locale locale
    ) {
        return this.translate(path, null, locale);
    }

    @Override
    public @NotNull MessageFormat translate(
            final @NotNull String path,
            final @Nullable String fallback,
            final @NotNull Locale locale
    ) {
        return this
                .getTranslation(path, fallback)
                .translate(locale, fallback);
    }

    @Deprecated
    @Override
    public @NotNull Component translate(
            final @NotNull TranslatableComponent component,
            final @NotNull Locale locale
    ) {
        return this.render(component, locale);
    }

    @Override
    public @NotNull Component render(
            final @NotNull Component component,
            final @NotNull Locale locale
    ) {
        return this.renderer.render(component, locale);
    }

    @Override
    public @NotNull TriState hasAnyTranslations() {
        return TriState.byBoolean(!this.translationMap.isEmpty());
    }

    @Override
    public boolean contains(final @Nullable String path) {
        return ChatUtils.isNotBlank(path)
                && this.translationMap.containsKey(path);
    }

    @Override
    public @NotNull Translation registerPath(final @NotNull String path) {
        return this.registerPath(path, null);
    }

    @Override
    public @NotNull Translation registerPath(
            final @NotNull String path,
            final @Nullable String fallback
    ) {
        return this.translationMap.computeIfAbsent(
                path,
                k -> new Translation(path, fallback)
        );
    }

    @Override
    public void register(final @NotNull Translation translation) {
        final String path = translation.getPath();
        final Translation existing = this.translationMap.get(path);

        if (existing == null) {
            this.translationMap.put(path, translation);
        } else {
            for (final var entry : translation.entrySet()) {
                existing.register(
                        entry.getKey(),
                        entry.getValue()
                );
            }
        }
    }

    @Override
    public @NotNull Translation register(
            final @NotNull String path,
            final @NotNull Locale locale,
            final @NotNull String translation
    ) {
        final Translation translationObj = this.registerPath(path, translation);

        translationObj.register(
                locale,
                new MessageFormat(translation)
        );

        return translationObj;
    }

    @Override
    public @NotNull List<Translation> registerAllPaths(final String @NotNull ... paths) {
        return this.registerAllPaths(
                Arrays.asList(paths)
        );
    }

    @Override
    public @NotNull List<Translation> registerAllPaths(final @NotNull Iterable<String> paths) {
        final var translations = new ObjectArrayList<Translation>();

        for (final var path : paths) {
            translations.add(
                    this.registerPath(path)
            );
        }

        return translations;
    }

    @Override
    public @NotNull List<Translation> registerAllPaths(final @NotNull Map<String, String> paths) {
        final var translations = new ObjectArrayList<Translation>(paths.size());

        for (final var entry : paths.entrySet()) {
            translations.add(
                    this.registerPath(
                            entry.getKey(),
                            entry.getValue()
                    )
            );
        }

        return translations;
    }

    @Override
    public @NotNull List<Translation> registerAll(final @NotNull LanguageFile languageFile) {
        return this.registerAll(
                languageFile.getLocale(),
                languageFile.getTranslationMap()
        );
    }

    @Override
    public @NotNull List<Translation> registerAll(
            final @NotNull Locale locale,
            final @NotNull Map<String, String> translationMap
    ) {
        final var translations = new ObjectArrayList<Translation>(translationMap.size());

        for (final var entry : translationMap.entrySet()) {
            translations.add(
                    this.register(
                            entry.getKey(),
                            locale,
                            entry.getValue()
                    )
            );
        }

        return translations;
    }

    @Override
    public boolean unregister(final @NotNull String path) {
        return this.translationMap.remove(path) != null;
    }

    @Override
    public boolean unregister(final @NotNull Locale locale) {
        boolean removed = false;

        for (final var translation : this.translationMap.values()) {
            removed |= translation.unregister(locale);
        }

        return removed;
    }

    @Override
    public boolean unregister(
            final @NotNull String path,
            final @NotNull Locale locale
    ) {
        final Translation translation = this.translationMap.get(path);

        return translation != null
                && translation.unregister(locale);
    }

    @Override
    public boolean unregister(final @NotNull LanguageFile languageFile) {
        return this.unregister(languageFile.getTranslationMap().keySet());
    }

    @Override
    public boolean unregister(final @NotNull Iterable<String> paths) {
        boolean removed = false;

        for (final var path : paths) {
            removed |= this.unregister(path);
        }

        return removed;
    }

    @Override
    public void unregisterAll() {
        this.translationMap.clear();
    }
}
