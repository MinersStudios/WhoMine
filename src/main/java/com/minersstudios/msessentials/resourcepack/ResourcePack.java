package com.minersstudios.msessentials.resourcepack;

import com.minersstudios.msessentials.resourcepack.data.ResourcePackData;
import com.minersstudios.msessentials.resourcepack.throwable.FatalPackLoadException;
import com.minersstudios.msessentials.resourcepack.throwable.PackLoadException;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.Immutable;
import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

@Immutable
public final class ResourcePack {
    private final ResourcePackData data;
    private final Component name;
    private final Component[] description;

    private ResourcePack(
            final @NotNull ResourcePackData data,
            final @NotNull Component name,
            final Component @NotNull ... description
    ) {
        this.data = data;
        this.name = name;
        this.description = description;
    }

    public @NotNull ResourcePackData getData() {
        return this.data;
    }

    public @NotNull Component getName() {
        return this.name;
    }

    public Component @NotNull [] getDescription() {
        return this.description;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + this.data.hashCode();
        result = prime * result + this.name.hashCode();

        for (final var component : this.description) {
            result = prime * result + component.hashCode();
        }

        return result;
    }

    public boolean isDisabled() {
        return this.data.isEmpty();
    }

    @Contract("null -> false")
    @Override
    public boolean equals(final @Nullable Object obj) {
        return obj == this
                || (
                        obj instanceof final ResourcePack that
                        && this.data.equals(that.data)
                        && this.name.equals(that.name)
                        && this.description.length == that.description.length
                        && Arrays.equals(this.description, that.description)
                );
    }

    @Override
    public @NotNull String toString() {
        return "ResourcePack{" +
                "name=" + this.name +
                ", description=" + Arrays.toString(this.description) +
                ", data=" + this.data +
                '}';
    }

    public static @NotNull ResourcePack create(
            final @NotNull Component name,
            final Component @NotNull ... description
    ) {
        return create(
                ResourcePackData.empty(),
                name,
                description
        );
    }

    public static @NotNull ResourcePack create(
            final @NotNull ResourcePackData data,
            final @NotNull Component name,
            final Component @NotNull ... description
    ) {
        return new ResourcePack(
                data,
                name,
                description
        );
    }

    public static @NotNull Entry entry(
            final @NotNull String key,
            final @NotNull ResourcePack pack
    ) {
        return new Entry(key, pack);
    }

    public static @NotNull Map<String, CompletableFuture<ResourcePack>> loadAll(
            final @NotNull File file,
            final @NotNull YamlConfiguration configuration,
            final @NotNull ConfigurationSection section
    ) throws PackLoadException, FatalPackLoadException {
        return loadAll(
                file, configuration, section,
                null, null
        );
    }

    public static @NotNull Map<String, CompletableFuture<ResourcePack>> loadAll(
            final @NotNull File file,
            final @NotNull YamlConfiguration configuration,
            final @NotNull ConfigurationSection section,
            final @Nullable Function<Entry, Entry> onLoaded,
            final @Nullable BiFunction<Entry, Throwable, Entry> onFailed
    ) throws PackLoadException, FatalPackLoadException {
        final var keys = section.getKeys(false);
        final var futureMap = new Object2ObjectOpenHashMap<String, CompletableFuture<ResourcePack>>(keys.size());

        for (final var key : keys) {
            final ConfigurationSection packSection = section.getConfigurationSection(key);
            assert packSection != null;

            futureMap.put(
                    key,
                    load(
                            file, configuration, packSection,
                            onLoaded, onFailed
                    )
            );
        }

        return futureMap;
    }

    public static @NotNull CompletableFuture<ResourcePack> load(
            final @NotNull File file,
            final @NotNull YamlConfiguration configuration,
            final @NotNull ConfigurationSection section
    ) throws PackLoadException, FatalPackLoadException {
        return load(
                file, configuration, section,
                null, null
        );
    }

    public static @NotNull CompletableFuture<ResourcePack> load(
            final @NotNull File file,
            final @NotNull YamlConfiguration configuration,
            final @NotNull ConfigurationSection section,
            final @Nullable Function<Entry, Entry> onLoaded,
            final @Nullable BiFunction<Entry, Throwable, Entry> onFailed
    ) throws PackLoadException, FatalPackLoadException {
        return new ResourcePackLoader(
                file, configuration, section,
                onLoaded, onFailed
        ).load();
    }

    @Immutable
    public static final class Entry {
        private final String key;
        private final ResourcePack pack;

        private Entry(
                final @NotNull String key,
                final @NotNull ResourcePack pack
        ) {
            this.key = key;
            this.pack = pack;
        }

        public @NotNull String getKey() {
            return this.key;
        }

        public @NotNull ResourcePack getPack() {
            return this.pack;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;

            result = prime * result + this.key.hashCode();
            result = prime * result + this.pack.hashCode();

            return result;
        }

        @Contract("null -> false")
        @Override
        public boolean equals(final @Nullable Object obj) {
            return obj == this
                    || (
                            obj instanceof final Entry that
                            && this.key.equals(that.key)
                            && this.pack.equals(that.pack)
                    );
        }

        @Override
        public @NotNull String toString() {
            return "Entry{" +
                    "key='" + this.key + '\'' +
                    ", pack=" + this.pack +
                    '}';
        }
    }
}
