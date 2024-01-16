package com.minersstudios.msessentials.resourcepack;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public final class ResourcePackRegistry {
    private static final Map<String, ResourcePack> KEY_MAP = new Object2ObjectOpenHashMap<>();

    @Contract(" -> fail")
    private ResourcePackRegistry() throws AssertionError {
        throw new AssertionError("Utility class");
    }

    public static @NotNull @UnmodifiableView Set<String> keySet() {
        return Collections.unmodifiableSet(KEY_MAP.keySet());
    }

    public static @NotNull @UnmodifiableView Collection<ResourcePack> packs() {
        return Collections.unmodifiableCollection(KEY_MAP.values());
    }

    public static @NotNull @UnmodifiableView Set<Map.Entry<String, ResourcePack>> entrySet() {
        return Collections.unmodifiableSet(KEY_MAP.entrySet());
    }

    public static @NotNull Optional<ResourcePack> get(final @NotNull String key) {
        return Optional.ofNullable(KEY_MAP.get(key));
    }

    public static @NotNull Optional<String> getKey(final @NotNull ResourcePack pack) {
        for (final var entry : KEY_MAP.entrySet()) {
            if (entry.getValue().equals(pack)) {
                return Optional.of(entry.getKey());
            }
        }

        return Optional.empty();
    }

    public static boolean register(
            final @NotNull String key,
            final @NotNull ResourcePack pack
    ) {
        return KEY_MAP.putIfAbsent(key, pack) == null;
    }

    public static boolean unregister(final @NotNull String key) {
        return KEY_MAP.remove(key) != null;
    }

    public static boolean isRegistered(final @NotNull String key) {
        return KEY_MAP.containsKey(key);
    }

    public static boolean isRegistered(final @NotNull ResourcePack pack) {
        return KEY_MAP.containsValue(pack);
    }

    public static boolean isEmpty() {
        return KEY_MAP.isEmpty();
    }

    public static int size() {
        return KEY_MAP.size();
    }

    public static void unregisterAll() {
        KEY_MAP.clear();
    }
}
