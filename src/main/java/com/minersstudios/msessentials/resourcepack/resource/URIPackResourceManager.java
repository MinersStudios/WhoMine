package com.minersstudios.msessentials.resourcepack.resource;

import com.minersstudios.mscore.resource.uri.AbstractURIResourceManager;
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
 * You can create it using the static methods of {@link PackResourceManager} :
 * <ul>
 *     <li>{@link PackResourceManager#uri(URI)}</li>
 *     <li>{@link PackResourceManager#url(String)}</li>
 * </ul>
 *
 * @see PackResourceManager
 */
@Immutable
public final class URIPackResourceManager extends AbstractURIResourceManager implements PackResourceManager {

    URIPackResourceManager(final @NotNull URI uri) {
        super(uri);
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
            try (final var in = this.openStream()) {
                return PackResourceManager.generateSHA1(in);
            } catch (final Throwable e) {
                throw new IllegalStateException("Failed to generate SHA-1 hash for resource pack file: " + this.uri, e);
            }
        });
    }
}
