package com.minersstudios.mscore.resource.github;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.Immutable;

/**
 * Represents a tag in a GitHub repository
 */
@Immutable
public final class Tag {
    private final String name;
    @SerializedName("zipball_url")
    private final String zipballUrl;
    @SerializedName("tarball_url")
    private final String tarballUrl;
    private final Commit commit;
    @SerializedName("node_id")
    private final String nodeId;

    /**
     * Constructs a new tag
     *
     * @param name       The name of the tag
     * @param zipballUrl The URL to download the tag as a zipball
     * @param tarballUrl The URL to download the tag as a tarball
     * @param commit     The commit associated with the tag
     * @param nodeId     The node ID of the tag
     */
    public Tag(
            final @NotNull String name,
            final @NotNull String zipballUrl,
            final @NotNull String tarballUrl,
            final @NotNull Commit commit,
            final @NotNull String nodeId
    ) {
        this.name = name;
        this.zipballUrl = zipballUrl;
        this.tarballUrl = tarballUrl;
        this.commit = commit;
        this.nodeId = nodeId;
    }

    /**
     * Gets the name of the tag
     *
     * @return The name of the tag
     */
    public @NotNull String getName() {
        return this.name;
    }

    /**
     * Gets the URL to download the tag as a zipball
     *
     * @return The URL to download the tag as a zipball
     */
    public @NotNull String getZipballUrl() {
        return this.zipballUrl;
    }

    /**
     * Gets the URL to download the tag as a tarball
     *
     * @return The URL to download the tag as a tarball
     */
    public @NotNull String getTarballUrl() {
        return this.tarballUrl;
    }

    /**
     * Gets the commit associated with the tag
     *
     * @return The commit associated with the tag
     */
    public @NotNull Commit getCommit() {
        return this.commit;
    }

    /**
     * Gets the node ID of the tag
     *
     * @return The node ID of the tag
     */
    public @NotNull String getNodeId() {
        return this.nodeId;
    }

    /**
     * Returns a hash code value for this tag
     *
     * @return A hash code value for this tag based on the name, zipballUrl,
     *         tarballUrl, commit, and nodeId
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + this.name.hashCode();
        result = prime * result + this.zipballUrl.hashCode();
        result = prime * result + this.tarballUrl.hashCode();
        result = prime * result + this.commit.hashCode();
        result = prime * result + this.nodeId.hashCode();

        return result;
    }

    /**
     * Checks if this tag is equal to another object
     *
     * @param obj The object to compare
     * @return True if the object is a {@code Tag} and all fields are equal
     */
    @Contract("null -> false")
    @Override
    public boolean equals(final @Nullable Object obj) {
        return obj == this
                || (
                        obj instanceof final Tag that
                        && this.name.equals(that.name)
                        && this.zipballUrl.equals(that.zipballUrl)
                        && this.tarballUrl.equals(that.tarballUrl)
                        && this.commit.equals(that.commit)
                        && this.nodeId.equals(that.nodeId)
                );
    }

    /**
     * Returns a string representation of this tag
     *
     * @return A string representation of this tag containing all fields
     */
    @Override
    public @NotNull String toString() {
        return "PublicTag{" +
                "name=\"" + this.name + '"' +
                ", zipballUrl=\"" + this.zipballUrl + '"' +
                ", tarballUrl=\"" + this.tarballUrl + '"' +
                ", commit=" + this.commit +
                ", nodeId=\"" + this.nodeId + '"' +
                '}';
    }

    /**
     * Represents a commit associated with a tag in a GitHub repository
     */
    @Immutable
    public static final class Commit {
        private final String sha;
        private final String url;

        /**
         * Constructs a new commit
         *
         * @param sha The SHA-1 hash of the commit
         * @param url The URL of the commit
         */
        public Commit(
                final @NotNull String sha,
                final @NotNull String url
        ) {
            this.sha = sha;
            this.url = url;
        }

        /**
         * Gets the SHA-1 hash of the commit
         *
         * @return The SHA-1 hash of the commit
         */
        public @NotNull String getSha() {
            return this.sha;
        }

        /**
         * Gets the URL of the commit
         *
         * @return The URL of the commit
         */
        public @NotNull String getUrl() {
            return this.url;
        }

        /**
         * Returns a hash code value for this commit
         *
         * @return A hash code value for this commit based on the sha and url
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;

            result = prime * result + this.sha.hashCode();
            result = prime * result + this.url.hashCode();

            return result;
        }

        /**
         * Checks if this commit is equal to another object
         *
         * @param obj The object to compare
         * @return True if the object is a {@code Commit} and the sha and url
         *         are equal
         */
        @Contract("null -> false")
        @Override
        public boolean equals(final @Nullable Object obj) {
            return obj == this
                    || (
                            obj instanceof final Commit that
                            && this.sha.equals(that.sha)
                            && this.url.equals(that.url)
                    );
        }

        /**
         * Returns a string representation of this commit
         *
         * @return A string representation of this commit containing the sha-1
         *         hash and url
         */
        @Override
        public @NotNull String toString() {
            return "Commit{" +
                    "sha=\"" + sha + '"' +
                    ", url=\"" + url + '"' +
                    '}';
        }
    }
}
