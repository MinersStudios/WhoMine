package com.minersstudios.msessentials.resourcepack.data;

import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.msessentials.resourcepack.resource.GitHubPackResourceManager;
import com.minersstudios.msessentials.resourcepack.resource.PackResourceManager;
import com.minersstudios.mscore.resource.github.Tag;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import javax.annotation.concurrent.Immutable;
import java.net.URI;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static java.util.concurrent.CompletableFuture.completedFuture;

@Immutable
final class ResourcePackDataImpl implements ResourcePackData {
    private final UUID uuid;
    private final URI uri;
    private final String hash;
    private final Component prompt;
    private final boolean required;

    ResourcePackDataImpl(final @NotNull Builder builder) {
        this.uuid = builder.uuid();
        this.uri = builder.uri();
        this.hash = builder.hash();
        this.prompt = builder.prompt();
        this.required = builder.required();
    }

    ResourcePackDataImpl(
            final @NotNull ResourcePackInfo info,
            final @Nullable Component prompt,
            final boolean required
    ) {
        this.uuid = info.id();
        this.uri = info.uri();
        this.hash = info.hash();
        this.prompt = prompt;
        this.required = required;
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public @NotNull URI getUri() {
        return this.uri;
    }

    @Override
    public @NotNull String getHash() {
        return this.hash;
    }

    @Override
    public @Nullable Component getPrompt() {
        return this.prompt;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + this.uuid.hashCode();
        result = prime * result + this.uri.hashCode();
        result = prime * result + this.hash.hashCode();
        result = prime * result + (this.prompt == null ? 0 : this.prompt.hashCode());

        return result;
    }

    @Override
    public boolean isRequired() {
        return this.required;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Contract("null -> false")
    @Override
    public boolean equals(final @Nullable Object obj) {
        return obj == this
                || (
                        obj instanceof final ResourcePackDataImpl that
                        && this.uuid.equals(that.uuid)
                        && this.uri.equals(that.uri)
                        && this.hash.equals(that.hash)
                        && Objects.equals(this.prompt, that.prompt)
                );
    }

    @Override
    public @NotNull String toString() {
        return "ResourcePackData{" +
                "uuid=" + this.uuid +
                ", url='" + this.uri + '\'' +
                ", hash='" + this.hash + '\'' +
                ", prompt='" + this.prompt + '\'' +
                '}';
    }

    @Contract(" -> new")
    @Override
    public @NotNull Builder toBuilder() {
        return new BuilderImpl(this);
    }

    @Contract(" -> new")
    @Override
    public @NotNull ResourcePackInfo asResourcePackInfo() {
        return ResourcePackInfo.resourcePackInfo(
                this.uuid,
                this.uri,
                this.hash
        );
    }

    static final class BuilderImpl implements Builder {
        private final AtomicReference<URI> uri;
        private final AtomicReference<String> hash;
        private UUID uuid;
        private Component prompt;
        private boolean required;
        private boolean autoUpdate;
        private PackResourceManager resourceManager;
        private Consumer<ResourcePackData.Builder> onBuilt;

        BuilderImpl() {
            this(null, null);
        }

        BuilderImpl(final @NotNull ResourcePackData data) {
            this(
                    data.getUri(),
                    data.getHash()
            );

            this.uuid = data.getUniqueId();
            this.prompt = data.getPrompt();
            this.required = data.isRequired();
        }

        BuilderImpl(final @NotNull ResourcePackInfo info) {
            this(
                    info.uri(),
                    info.hash()
            );

            this.uuid = info.id();
        }

        BuilderImpl(
                final @Nullable URI uri,
                final @Nullable String hash
        ) {
            this.uri = new AtomicReference<>(uri);
            this.hash = new AtomicReference<>(hash);
        }

        @Override
        public @UnknownNullability URI uri() {
            return this.uri.get();
        }

        @Contract("_ -> this")
        @Override
        public @NotNull ResourcePackData.Builder uri(final @Nullable URI uri) {
            this.uri.set(uri);

            return this;
        }

        @Contract("_ -> this")
        @Override
        public @NotNull ResourcePackData.Builder url(final @Nullable String url) throws IllegalArgumentException {
            return this.uri(
                    ChatUtils.isBlank(url)
                    ? null
                    : URI.create(url)
            );
        }

        @Override
        public @UnknownNullability String hash() {
            return this.hash.get();
        }

        @Contract("_ -> this")
        @Override
        public @NotNull ResourcePackData.Builder hash(final @Nullable String hash) {
            this.hash.set(hash);

            return this;
        }

        @Override
        public @UnknownNullability UUID uuid() {
            return this.uuid;
        }

        @Contract("_ -> this")
        @Override
        public @NotNull ResourcePackData.Builder uuid(final @Nullable UUID uuid) {
            this.uuid = uuid;

            return this;
        }

        @Contract("_ -> this")
        @Override
        public @NotNull ResourcePackData.Builder uuidString(final @Nullable String uuid) throws IllegalArgumentException {
            return this.uuid(
                    ChatUtils.isBlank(uuid)
                    ? null
                    : UUID.fromString(uuid)
            );
        }

        @Override
        public @UnknownNullability Component prompt() {
            return this.prompt;
        }

        @Contract("_ -> this")
        @Override
        public @NotNull ResourcePackData.Builder prompt(final @Nullable Component prompt) {
            this.prompt = prompt;

            return this;
        }

        @Override
        public boolean required() {
            return this.required;
        }

        @Contract("_ -> this")
        @Override
        public @NotNull ResourcePackData.Builder required(final boolean required) {
            this.required = required;

            return this;
        }

        @Override
        public boolean autoUpdate() {
            return this.autoUpdate;
        }

        @Contract("_ -> this")
        @Override
        public @NotNull ResourcePackData.Builder autoUpdate(final boolean autoUpdate) {
            this.autoUpdate = autoUpdate;

            return this;
        }

        @Override
        public @UnknownNullability PackResourceManager resourceManager() {
            return this.resourceManager;
        }

        @Contract("_ -> this")
        @Override
        public @NotNull ResourcePackData.Builder resourceManager(final @Nullable PackResourceManager resourceManager) {
            this.resourceManager = resourceManager;

            return this;
        }

        @Override
        public @UnknownNullability Consumer<ResourcePackData.Builder> onBuilt() {
            return this.onBuilt;
        }

        @Contract("_ -> this")
        @Override
        public @NotNull ResourcePackData.Builder onBuilt(final @Nullable Consumer<ResourcePackData.Builder> consumer) {
            this.onBuilt = consumer;

            return this;
        }

        @Contract(" -> new")
        @Override
        public @NotNull CompletableFuture<ResourcePackData> buildAsync() {
            if (this.uuid == null) {
                this.uuid = UUID.nameUUIDFromBytes(this.uri().toString().getBytes());
            }

            return (this.autoUpdate
                    ? this.getUpdatedData()
                    : this.getCurrentData())
                    .thenApply(
                            data -> {
                                if (this.onBuilt != null) {
                                    this.onBuilt.accept(this);
                                }

                                return data;
                            }
                    );
        }

        @Override
        public @NotNull String toString() {
            return "ResourcePackData.Builder{" +
                    ", uri=" + this.uri() +
                    ", hash=" + this.hash() +
                    ", uuid=" + this.uuid +
                    ", prompt=" + this.prompt +
                    ", required=" + this.required +
                    ", resourceManager=" + this.resourceManager +
                    ", autoUpdate=" + this.autoUpdate +
                    ", onBuilt=" + this.onBuilt +
                    '}';
        }

        private @NotNull CompletableFuture<ResourcePackData> getCurrentData() {
            if (ChatUtils.isBlank(this.hash())) {
                return failedState("Hash cannot be null or blank, provide a hash or use auto-update");
            }

            if (this.uri() == null) {
                if (this.resourceManager instanceof final GitHubPackResourceManager gitHubManager) {
                    final Tag[] tags = gitHubManager.getTagsNow();

                    if (
                            tags == null
                            || tags.length == 0
                    ) {
                        return failedState("Provide a tag for the github resource manager or use auto-update");
                    }
                }

                return this.resourceManager.getUri()
                        .thenApply(uri -> {
                            this.uri.set(uri);

                            return new ResourcePackDataImpl(this);
                        });
            }

            return completedFuture(new ResourcePackDataImpl(this));
        }

        private @NotNull CompletableFuture<ResourcePackData> getUpdatedData() {
            return this.resourceManager == null
                   ? failedState("Provide a resource manager for auto-updating")
                   : this.resourceManager
                           .generateHash()
                           .thenApply(
                                   hash -> {
                                       if (ChatUtils.isBlank(hash)) {
                                           throw new IllegalStateException("Failed to generate hash");
                                       }

                                       this.hash.set(hash);

                                       return new ResourcePackDataImpl(this);
                                   }
                           );
        }

        private static <U> @NotNull CompletableFuture<U> failedState(final @Nullable String message) {
            return CompletableFuture.failedFuture(new IllegalStateException(message));
        }
    }
}
