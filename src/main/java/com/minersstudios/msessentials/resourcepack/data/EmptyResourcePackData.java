package com.minersstudios.msessentials.resourcepack.data;

import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.Immutable;
import java.net.URI;
import java.util.UUID;

@Immutable
final class EmptyResourcePackData implements ResourcePackData {
    static final ResourcePackData SINGLETON = new EmptyResourcePackData();

    private EmptyResourcePackData() {}

    @Override
    public @NotNull UUID getUniqueId() {
        return UUID.randomUUID();
    }

    @Override
    public @NotNull URI getUri() {
        return URI.create("");
    }

    @Override
    public @NotNull String getHash() {
        return "";
    }

    @Override
    public @Nullable Component getPrompt() {
        return null;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean isRequired() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Contract("null -> false")
    @Override
    public boolean equals(final @Nullable Object obj) {
        return obj instanceof EmptyResourcePackData;
    }

    @Override
    public @NotNull String toString() {
        return "EmptyResourcePackData{}";
    }

    @Contract(" -> new")
    @Override
    public @NotNull Builder toBuilder() {
        return ResourcePackData.builder();
    }

    @Contract(" -> fail")
    @Override
    public @NotNull ResourcePackInfo asResourcePackInfo() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("EmptyResourcePackData cannot be converted to ResourcePackInfo");
    }
}
