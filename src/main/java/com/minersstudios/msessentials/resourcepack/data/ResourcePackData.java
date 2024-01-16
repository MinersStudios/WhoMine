package com.minersstudios.msessentials.resourcepack.data;

import com.minersstudios.msessentials.resourcepack.resource.ResourceManager;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackInfoLike;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Resource pack data interface.
 * <br>
 * Represents the data with :
 * <ul>
 *     <li>A unique ID</li>
 *     <li>A URL to download the resource pack</li>
 *     <li>A SHA-1 hash of the resource pack file</li>
 *     <li>A prompt displayed to the user when accepting a resource pack</li>
 *     <li>A required status</li>
 * </ul>
 *
 * Resource pack data can be created with a couple of static methods:
 * <ul>
 *     <li>{@link #empty()}</li>
 *     <li>{@link #builder()}</li>
 *     <li>{@link #builder(ResourcePackInfo)}</li>
 *     <li>{@link #of(ResourcePackInfo)}</li>
 *     <li>{@link #of(ResourcePackInfo, Component, boolean)}</li>
 * </ul>
 *
 * @see Builder
 */
public interface ResourcePackData extends ResourcePackInfoLike {

    /**
     * Returns resource pack data with a random UUID, an empty URL, an empty
     * hash, a null prompt, and a required status of false. The hash code of
     * this data will always be 0.
     *
     * @return An empty resource pack data constant
     */
    static @NotNull ResourcePackData empty() {
        return EmptyResourcePackData.SINGLETON;
    }

    /**
     * Returns a new builder based on the specified resource pack info
     *
     * @param info The resource pack info
     * @return A new builder based on the specified resource pack info
     */
    @Contract("_ -> new")
    static @NotNull Builder builder(final @NotNull ResourcePackInfo info) {
        return new ResourcePackDataImpl.BuilderImpl(info);
    }

    /**
     * Creates a new builder for a {@link ResourcePackData}
     *
     * @return A new builder
     */
    @Contract(" -> new")
    static @NotNull Builder builder() {
        return new ResourcePackDataImpl.BuilderImpl();
    }

    /**
     * Creates new resource pack data based on the specified info with a null
     * prompt and a required status of false.
     *
     * @param info The resource pack info
     * @return New resource pack data based on the specified info with a null
     *         prompt and a required status of false
     * @see #of(ResourcePackInfo, Component, boolean)
     */
    @Contract("_ -> new")
    static @NotNull ResourcePackData of(final @NotNull ResourcePackInfo info) {
        return of(info, null, false);
    }

    /**
     * Creates new resource pack data based on the specified info, prompt, and
     * required status.
     *
     * @param info     The resource pack info
     * @param prompt   The prompt displayed to the user when accepting a pack
     * @param required Whether the resource pack is required
     * @return New resource pack data based on the specified info, prompt, and
     *         required status
     */
    @Contract("_, _, _ -> new")
    static @NotNull ResourcePackData of(
            final @NotNull ResourcePackInfo info,
            final @Nullable Component prompt,
            final boolean required
    ) {
        return new ResourcePackDataImpl(info, prompt, required);
    }

    /**
     * Gets the unique ID of the resource pack.
     * <br>
     * If the data {@link #isEmpty() is empty}, this will return a random UUID.
     *
     * @return The unique ID of the resource pack
     */
    @NotNull UUID getUniqueId();

    /**
     * Gets the url to download the resource pack
     *
     * @return The url to download the resource pack
     */
    @NotNull URI getUri();

    /**
     * Gets the SHA-1 hash of the resource pack file
     *
     * @return The SHA-1 hash of the resource pack file
     */
    @NotNull String getHash();

    /**
     * Gets the prompt displayed to the user when accepting a resource pack
     *
     * @return The prompt displayed to the user when accepting a resource pack
     */
    @Nullable Component getPrompt();

    /**
     * Returns a hash code value for this resource pack data.
     * <br>
     * If the data {@link #isEmpty() is empty}, this will return 0.
     *
     * @return A hash code value for this resource pack data
     */
    @Override
    int hashCode();

    /**
     * Returns whether the resource pack is required
     *
     * @return Whether the resource pack is required
     */
    boolean isRequired();

    /**
     * Returns whether the resource pack data is empty
     *
     * @return Whether the resource pack data is empty
     * @see #empty()
     */
    boolean isEmpty();

    /**
     * Checks if this resource pack data is equal to another object.
     * <br>
     * If the data {@link #isEmpty() is empty}, this will return true if the
     * object is also empty resource pack data.
     *
     * @param obj The object to compare
     * @return True if the object is a {@code ResourcePackData} and the states
     *         are equal
     */
    @Contract("null -> false")
    @Override
    boolean equals(final @Nullable Object obj);

    /**
     * Returns a string representation of this resource pack data
     *
     * @return A string representation of this resource pack data containing
     *         all the fields
     */
    @Override
    @NotNull String toString();

    /**
     * Creates a new builder with the same values as this resource pack data.
     * <br>
     * If the data {@link #isEmpty() is empty}, this will return just a new
     * builder.
     *
     * @return A new builder with the same values as this resource pack data
     */
    @Contract(" -> new")
    @NotNull Builder toBuilder();

    /**
     * Returns a {@link ResourcePackInfo} representation of this data.
     * <br>
     * If the data {@link #isEmpty() is empty}, this will throw an exception.
     *
     * @return A {@link ResourcePackInfo} representation of this data
     * @throws UnsupportedOperationException If the data {@link #isEmpty()}
     */
    @Contract(" -> new")
    @NotNull ResourcePackInfo asResourcePackInfo() throws UnsupportedOperationException;

    /**
     * A builder for a {@link ResourcePackData}
     */
    interface Builder {

        /**
         * Returns the url to download the resource pack
         *
         * @return The url to download the resource pack
         */
        @UnknownNullability URI uri();

        /**
         * Sets the uri to download the resource pack
         *
         * @param uri The uri to set
         * @return This builder, for chaining
         */
        @Contract("_ -> this")
        @NotNull Builder uri(final @Nullable URI uri);

        /**
         * Sets the url to download the resource pack
         *
         * @param url The url to set
         * @return This builder, for chaining
         * @throws IllegalArgumentException If the given string violates
         *                                  RFC&nbsp;2396
         * @see #uri(URI)
         */
        @Contract("_ -> this")
        @NotNull Builder url(final @Nullable String url) throws IllegalArgumentException;

        /**
         * Returns the SHA-1 hash of the resource pack file
         *
         * @return The SHA-1 hash of the resource pack file
         */
        @UnknownNullability String hash();

        /**
         * Sets the SHA-1 hash of the resource pack file
         *
         * @param hash The hash to set
         * @return This builder, for chaining
         */
        @Contract("_ -> this")
        @NotNull Builder hash(final @Nullable String hash);

        /**
         * Returns the unique ID of the resource pack
         *
         * @return The unique ID of the resource pack
         */
        @UnknownNullability UUID uuid();

        /**
         * Sets the unique ID of the resource pack
         *
         * @param uuid The UUID to set
         * @return This builder, for chaining
         * @see #uuidString(String)
         */
        @Contract("_ -> this")
        @NotNull Builder uuid(final @Nullable UUID uuid);

        /**
         * Sets the unique ID of the resource pack from the UUID string
         * representation
         *
         * @param uuid The UUID string to set
         * @return This builder, for chaining
         * @throws IllegalArgumentException If name does not conform to the
         *                                  string representation as described
         *                                  in {@link UUID#toString}
         * @see UUID#fromString(String)
         * @see #uuid(UUID)
         */
        @Contract("_ -> this")
        @NotNull Builder uuidString(final @Nullable String uuid) throws IllegalArgumentException;

        /**
         * Returns the prompt displayed to the user when accepting a resource
         * pack
         *
         * @return The prompt displayed to the user when accepting a resource
         *         pack
         */
        @UnknownNullability Component prompt();

        /**
         * Sets the prompt displayed to the user when accepting a resource pack
         *
         * @param prompt The prompt to set
         * @return This builder, for chaining
         */
        @Contract("_ -> this")
        @NotNull Builder prompt(final @Nullable Component prompt);

        /**
         * Returns whether the resource pack is required
         *
         * @return Whether the resource pack is required
         */
        boolean required();

        /**
         * Sets whether the resource pack is required
         *
         * @param required Whether the resource pack is required
         * @return This builder, for chaining
         */
        @Contract("_ -> this")
        @NotNull Builder required(final boolean required);

        /**
         * Returns whether the resource pack should be automatically updated
         *
         * @return Whether the resource pack should be automatically updated
         */
        boolean autoUpdate();

        /**
         * Sets whether the resource pack should be automatically updated
         *
         * @param autoUpdate Whether the resource pack should be automatically
         *                   updated
         * @return This builder, for chaining
         */
        @Contract("_ -> this")
        @NotNull Builder autoUpdate(final boolean autoUpdate);

        /**
         * Returns the resource manager
         *
         * @return The resource manager
         */
        @UnknownNullability ResourceManager resourceManager();

        /**
         * Sets the resource manager
         *
         * @param resourceManager The resource manager to set
         * @return This builder, for chaining
         */
        @Contract("_ -> this")
        @NotNull Builder resourceManager(final @Nullable ResourceManager resourceManager);

        /**
         * Returns the consumer to run when the resource pack data is built
         *
         * @return The consumer to run when the resource pack data is built
         */
        @UnknownNullability Consumer<Builder> onBuilt();

        /**
         * Sets the consumer to run when the resource pack data is built
         *
         * @param consumer The consumer to set
         * @return This builder, for chaining
         */
        @Contract("_ -> this")
        @NotNull Builder onBuilt(final @Nullable Consumer<Builder> consumer);

        /**
         * Builds the resource pack data
         *
         * @return The future containing the resource pack data when the build
         *         is complete. If the build fails, the future will be
         *         exceptionally completed with the exception that caused the
         *         build to fail.
         */
        @Contract(" -> new")
        @NotNull CompletableFuture<ResourcePackData> buildAsync();

        /**
         * Returns a string representation of this builder
         *
         * @return A string representation of this builder containing all the
         *         fields
         */
        @Override
        @NotNull String toString();
    }
}
