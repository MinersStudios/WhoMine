package com.minersstudios.wholib.paper.utility;

import com.minersstudios.wholib.event.EventOrder;
import com.minersstudios.wholib.key.ResourceKey;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventPriority;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class ApiConverter {

    @Contract(" -> fail")
    private ApiConverter() throws AssertionError {
        throw new AssertionError("Utility class");
    }

    public static @NotNull NamespacedKey apiToBukkit(final @NotNull ResourceKey resourceKey) {
        return new NamespacedKey(resourceKey.getResource(), resourceKey.getKey());
    }

    public static @NotNull ResourceKey bukkitToApi(final @NotNull NamespacedKey key) {
        final @Subst("resource") String resource = key.getNamespace();
        final @Subst("path") String path = key.getKey();

        return ResourceKey.of(resource, path);
    }

    public static @NotNull EventPriority apiToBukkit(final @NotNull EventOrder order) {
        return switch (order) {
            case LOWEST  -> EventPriority.LOWEST;
            case LOW     -> EventPriority.LOW;
            case NORMAL  -> EventPriority.NORMAL;
            case HIGH    -> EventPriority.HIGH;
            case HIGHEST -> EventPriority.HIGHEST;
            case CUSTOM  -> EventPriority.MONITOR;
        };
    }

    public static @NotNull EventOrder bukkitToApi(final @NotNull EventPriority priority) {
        return switch (priority) {
            case LOWEST  -> EventOrder.LOWEST;
            case LOW     -> EventOrder.LOW;
            case NORMAL  -> EventOrder.NORMAL;
            case HIGH    -> EventOrder.HIGH;
            case HIGHEST -> EventOrder.HIGHEST;
            case MONITOR -> EventOrder.CUSTOM;
        };
    }
}
