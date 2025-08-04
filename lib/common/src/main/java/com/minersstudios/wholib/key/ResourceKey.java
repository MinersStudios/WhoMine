package com.minersstudios.wholib.key;

import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.Immutable;
import java.util.UUID;

/**
 * Represents a resource key used in the API.
 * <p>
 * <table>
 *     <caption>Available factory methods</caption>
 *     <tr>
 *         <th>Method</th>
 *         <th>Description</th>
 *         <th>Example</th>
 *     </tr>
 *     <tr>
 *         <td>{@link #empty(String)}</td>
 *         <td>Creates a new resource key with the given key and empty
 *             resource</td>
 *         <td>{@code ResourceKey.empty("key")}</td>
 *     </tr>
 *     <tr>
 *         <td>{@link #minecraft(String)}</td>
 *         <td>Creates a new resource key with the given key and the
 *             {@link Resource#MINECRAFT minecraft} resource</td>
 *         <td>{@code ResourceKey.minecraft("key")}</td>
 *     </tr>
 *     <tr>
 *         <td>{@link #realms(String)}</td>
 *         <td>Creates a new resource key with the given key and the
 *             {@link Resource#REALMS realms} resource</td>
 *         <td>{@code ResourceKey.realms("key")}</td>
 *     </tr>
 *     <tr>
 *         <td>{@link #paper(String)}</td>
 *         <td>Creates a new resource key with the given key and the
 *             {@link Resource#PAPER paper} resource</td>
 *         <td>{@code ResourceKey.paper("key")}</td>
 *     </tr>
 *     <tr>
 *         <td>{@link #whomine(String)}</td>
 *         <td>Creates a new resource key with the given key and the
 *             {@link Resource#WHOMINE whomine} resource</td>
 *         <td>{@code ResourceKey.whomine("key")}</td>
 *     </tr>
 *     <tr>
 *         <td>{@link #of(String, String)}</td>
 *         <td>Creates a new resource key with the given resource and key</td>
 *         <td>{@code ResourceKey.of("resource", "key")}</td>
 *     </tr>
 *     <tr>
 *         <td>{@link #of(String)}</td>
 *         <td>Creates a new resource key with the given resource key string</td>
 *         <td>{@code ResourceKey.of("resource:key")}</td>
 *     </tr>
 * </table>
 *
 * @see Resource
 * @see Key
 * @see ResKey
 */
@SuppressWarnings("unused")
@Immutable
public final class ResourceKey implements Resourced, Keyed, Comparable<ResourceKey> {
    /** The separator used to separate the resource and the key, by default ':' */
    public static final char SEPARATOR = ':';
    /** The none resource key "none" by default */
    public static final ResourceKey NONE = ResourceKey.empty("none");

    private final @Resource String resource;
    private final @Key String key;
    private final int length;

    private ResourceKey(
            final @Resource @Nullable String resource,
            final @Key @NotNull String key
    ) {
        if (
                resource == null
                || resource.isBlank()
        ) {
            this.resource = Resource.EMPTY;
            this.length = key.length();
        } else {
            this.resource = resource;
            this.length = resource.length() + key.length() + 1;
        }

        this.key = key;
    }

    /**
     * Returns the resource of the resource key
     *
     * @return The resource of the resource key
     */
    @Override
    public @Resource @NotNull String getResource() {
        return this.resource == null
               ? Resource.EMPTY
               : this.resource;
    }

    /**
     * Returns the key of the resource key
     *
     * @return The key of the resource key
     */
    @Override
    public @Key @NotNull String getKey() {
        return this.key;
    }

    /**
     * Returns the length of the resource key
     *
     * @return The length of the resource key
     */
    public int length() {
        return this.length;
    }

    /**
     * Returns a hash code value for the resource key
     *
     * @return A hash code value for the resource key
     */
    @Override
    public int hashCode() {
        return 31 * this.resource.hashCode() + this.key.hashCode();
    }

    /**
     * Compares this resource key with the specified resource key for order.
     * <p>
     * The comparison is based on the string representation of the resource key.
     *
     * @param that The resource key to be compared
     * @return The value {@code 0} if this resource key is equal to the argument
     *         resource key; a value less than {@code 0} if this resource key is
     *         lexicographically less than the argument resource key; and a
     *         value greater than {@code 0} if this resource key is
     *         lexicographically greater than the argument resource key
     * @see #toString()
     * @see String#compareTo(String)
     */
    @Override
    public int compareTo(final @NotNull ResourceKey that) {
        return this.toString().compareTo(that.toString());
    }

    /**
     * Indicates whether some other object is "equal to" this one
     *
     * @param obj The reference object with which to compare
     * @return True if this object is the same as the obj argument
     */
    @Contract("null -> false")
    @Override
    public boolean equals(final @Nullable Object obj) {
        return this == obj
                || (
                        obj instanceof ResourceKey that
                        && this.length == that.length
                        && this.resource.equals(that.resource)
                        && this.key.equals(that.key)
                );
    }

    /**
     * Returns whether the resource is not blank or empty
     *
     * @return Whether the resource is not blank or empty
     */
    public boolean hasResource() {
        return !Resource.EMPTY.equals(this.resource);
    }

    /**
     * Returns the string representation of the resource key.
     * <p>
     * Example of the string representation :
     * <pre>{@code
     * ResourceKey.of("resource", "key").toString(); // "resource:key"
     * ResourceKey.empty("key").toString(); // "key"
     * }</pre>
     *
     * @return The string representation of the resource key
     */
    @Contract(" -> new")
    @Override
    public @Subst("resource:key") @ResKey @NotNull String toString() {
        return this.hasResource()
               ? this.resource + SEPARATOR + this.key
               : this.key;
    }

    /**
     * Returns the adventure key of the resource key
     *
     * @return The adventure key of the resource key
     */
    @Contract(" -> new")
    public @NotNull net.kyori.adventure.key.Key toAdventure() {
        return net.kyori.adventure.key.Key.key(this.resource, this.key);
    }

    /**
     * Asserts that the resource key is not empty
     *
     * @return The resource key if it is not empty
     * @throws ResourceKeyException If the resource key is empty
     */
    @Contract(" -> this")
    public @NotNull ResourceKey assertNotEmpty() throws ResourceKeyException {
        ResKey.Validator.validateNotEmpty(this.toString());

        return this;
    }

    /**
     * Asserts that the resource of the resource key is not empty
     *
     * @return The resource key if the resource is not empty
     * @throws ResourceException If the resource is empty
     */
    @Contract(" -> this")
    public @NotNull ResourceKey assertResourceNotEmpty() throws ResourceException {
        Resource.Validator.validateNotEmpty(this.resource);

        return this;
    }

    /**
     * Asserts that the key of the resource key is not empty
     *
     * @return The resource key if the key is not empty
     * @throws KeyException If the key is empty
     */
    @Contract(" -> this")
    public @NotNull ResourceKey assertKeyNotEmpty() throws KeyException {
        Key.Validator.validateNotEmpty(this.key);

        return this;
    }

    /**
     * Returns the character at the specified index of the resource key
     *
     * @param index The index of the character to be returned
     * @return The character at the specified index of the resource key
     * @throws StringIndexOutOfBoundsException If the index is out of range
     */
    public char charAt(final int index) {
        if (
                index < 0
                || index >= this.length
        ) {
            throw new StringIndexOutOfBoundsException(
                    "String index out of range: " + index +
                    ", expected range: [0, " + this.length + ')'
            );
        }

        if (this.hasResource()) {
            final int resourceLength = this.resource.length();

            if (index < resourceLength) {
                return this.resource.charAt(index);
            } else if (index == resourceLength) {
                return SEPARATOR;
            }
        }

        return this.key.charAt(index);
    }

    /**
     * Returns a random resource key with the empty resource and a random key.
     * <p>
     * The key provides from {@link UUID#randomUUID()}.
     *
     * @return A random resource key with the empty resource and a random key
     */
    public static @NotNull ResourceKey random() {
        final @Subst("key") String key = UUID.randomUUID().toString();

        return ResourceKey.empty(key);
    }

    /**
     * Creates a new resource key with the given key and no resource
     *
     * @param key The key of the resource key
     * @return A new resource key with the given key and no resource
     * @throws KeyException         If the key is invalid
     * @throws ResourceKeyException If the resource key is too long
     */
    @Contract("_ -> new")
    public static @NotNull ResourceKey empty(
            final @Key @NotNull String key
    ) throws KeyException, ResourceKeyException {
        return dummyResource(null, key);
    }

    /**
     * Creates a new resource key with the given key and the resource
     * {@value Resource#MINECRAFT}
     *
     * @param key The key of the resource key
     * @return A new resource key with the given key and the resource
     *         {@value Resource#MINECRAFT}
     * @throws KeyException         If the key is invalid
     * @throws ResourceKeyException If the resource key is too long
     */
    @Contract("_ -> new")
    public static @NotNull ResourceKey minecraft(
            final @Key @NotNull String key
    ) throws KeyException, ResourceKeyException {
        return dummyResource(Resource.MINECRAFT, key);
    }

    /**
     * Creates a new resource key with the given key and the resource
     * {@value Resource#REALMS}
     *
     * @param key The key of the resource key
     * @return A new resource key with the given key and the resource
     *         {@value Resource#REALMS}
     * @throws KeyException         If the key is invalid
     * @throws ResourceKeyException If the resource key is too long
     */
    @Contract("_ -> new")
    public static @NotNull ResourceKey realms(
            final @Key @NotNull String key
    ) throws KeyException, ResourceKeyException {
        return dummyResource(Resource.REALMS, key);
    }

    /**
     * Creates a new resource key with the given key and the resource
     * {@value Resource#PAPER}
     *
     * @param key The key of the resource key
     * @return A new resource key with the given key and the resource
     *         {@value Resource#PAPER}
     * @throws KeyException         If the key is invalid
     * @throws ResourceKeyException If the resource key is too long
     */
    @Contract("_ -> new")
    public static @NotNull ResourceKey paper(
            final @Key @NotNull String key
    ) throws KeyException, ResourceKeyException {
        return dummyResource(Resource.PAPER, key);
    }

    /**
     * Creates a new resource key with the given key and the resource
     * {@value Resource#WHOMINE}
     *
     * @param key The key of the resource key
     * @return A new resource key with the given key and the resource
     *         {@value Resource#WHOMINE}
     * @throws KeyException         If the key is invalid
     * @throws ResourceKeyException If the resource key is too long
     */
    @Contract("_ -> new")
    public static @NotNull ResourceKey whomine(
            final @Key @NotNull String key
    ) throws KeyException, ResourceKeyException {
        return dummyResource(Resource.WHOMINE, key);
    }

    /**
     * Creates a new resource key with the given resource and key
     *
     * @param resource The resource of the resource key
     * @param key      The key of the resource key
     * @return A new resource key with the given resource and key
     * @throws ResourceException    If the resource is invalid
     * @throws KeyException         If the key is invalid
     * @throws ResourceKeyException If the resource key is too long
     */
    @Contract("_, _ -> new")
    public static @NotNull ResourceKey of(
            final @Resource @Nullable String resource,
            final @Key @NotNull String key
    ) throws ResourceException, KeyException, ResourceKeyException {
        Resource.Validator.validatePattern(resource);

        return dummyResource(resource, key);
    }

    /**
     * Creates a new resource key with the given resource key string
     *
     * @param resourceKey The resource key string
     * @return A new resource key with the given resource key string
     * @throws ResourceException    If the resource is invalid
     * @throws KeyException         If the key is invalid
     * @throws ResourceKeyException If the resource key is too long
     */
    @Contract("_ -> new")
    public static @NotNull ResourceKey of(
            final @ResKey @NotNull String resourceKey
    ) throws ResourceKeyException, ResourceException, KeyException {
        ResKey.Validator.validateLength(resourceKey);

        final int colonIndex = resourceKey.indexOf(SEPARATOR);

        @Subst("resource") String resource = null;
        @Subst("key") String key = resourceKey;

        if (colonIndex >= 0) {
            key = resourceKey.substring(colonIndex + 1);

            if (colonIndex >= 1) {
                resource = resourceKey.substring(0, colonIndex);
            }
        }

        Resource.Validator.validatePattern(resource);
        Key.Validator.validatePattern(key);

        return new ResourceKey(resource, key);
    }

    @Contract("_, _ -> new")
    private static @NotNull ResourceKey dummyResource(
            final @Resource @Nullable String resource,
            final @Key @NotNull String key
    ) throws KeyException, ResourceKeyException {
        Key.Validator.validatePattern(key);

        final @Subst("resource:key") String resourceKey = resource + SEPARATOR + key;

        ResKey.Validator.validateLength(resourceKey);

        return new ResourceKey(resource, key);
    }
}
