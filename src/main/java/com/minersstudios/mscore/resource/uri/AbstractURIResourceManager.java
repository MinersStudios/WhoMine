package com.minersstudios.mscore.resource.uri;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractURIResourceManager implements URIResourceManager {
    protected final URI uri;

    protected AbstractURIResourceManager(final @NotNull URI uri) {
        this.uri = uri;
    }

    @Override
    public @NotNull CompletableFuture<URI> getUri() {
        return CompletableFuture.completedFuture(this.uri);
    }

    @Override
    public @NotNull InputStream openStream() throws IOException {
        return this.uri.toURL().openStream();
    }

    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() + "{uri=" + this.uri + '}';
    }
}
