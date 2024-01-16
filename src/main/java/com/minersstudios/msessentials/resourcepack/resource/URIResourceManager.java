package com.minersstudios.msessentials.resourcepack.resource;

import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;
import java.net.URI;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a URI resource manager for a resource pack.
 * <br>
 * It provides methods to get the URI and the hash of the resource pack file
 * via {@link URI}.
 * <br>
 * You can create it using the static methods of {@link ResourceManager} :
 * <ul>
 *     <li>{@link ResourceManager#uri(URI)}</li>
 *     <li>{@link ResourceManager#url(String)}</li>
 * </ul>
 *
 * @see ResourceManager
 */
@Immutable
public class URIResourceManager implements ResourceManager {
    private final URI uri;

    URIResourceManager(final @NotNull URI uri) {
        this.uri = uri;
    }

    /**
     * Returns the URI passed in the constructor
     *
     * @return The future containing the URI of the resource pack file
     */
    @Override
    public @NotNull CompletableFuture<URI> getUri() {
        return CompletableFuture.completedFuture(this.uri);
    }

    /**
     * Generates the hash of the file pointed by the URI using {@link #SHA1}
     * algorithm and returns it
     *
     * @return The future containing the hash of the resource pack file
     * @throws IllegalStateException If an error occurs while generating the
     *                               hash
     */
    @Override
    public @NotNull CompletableFuture<String> generateHash() throws IllegalStateException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return ResourceManager.generateSHA1(
                        this.uri
                        .toURL()
                        .openConnection()
                        .getInputStream()
                );
            } catch (final Throwable e) {
                throw new IllegalStateException("Failed to generate SHA-1 hash for resource pack file: " + this.uri, e);
            }
        });
    }

    @Override
    public @NotNull String toString() {
        return "URIResourceManager{uri=" + this.uri + '}';
    }
}
