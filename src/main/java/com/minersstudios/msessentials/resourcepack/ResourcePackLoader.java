package com.minersstudios.msessentials.resourcepack;

import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.msessentials.resourcepack.data.ResourcePackData;
import com.minersstudios.msessentials.resourcepack.resource.GitHubPackResourceManager;
import com.minersstudios.msessentials.resourcepack.resource.PackResourceManager;
import com.minersstudios.mscore.resource.github.Tag;
import com.minersstudios.msessentials.resourcepack.throwable.FatalPackLoadException;
import com.minersstudios.msessentials.resourcepack.throwable.PackLoadException;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.minersstudios.msessentials.resourcepack.ResourcePack.create;
import static com.minersstudios.msessentials.resourcepack.ResourcePack.entry;
import static java.util.concurrent.CompletableFuture.completedFuture;

public final class ResourcePackLoader {
    private final File configFile;
    private final YamlConfiguration yaml;
    private final Function<ResourcePack.Entry, ResourcePack.Entry> onLoaded;
    private final BiFunction<ResourcePack.Entry, Throwable, ResourcePack.Entry> onFailed;
    private final List<Consumer<ResourcePackData.Builder>> onBuiltConsumers;

    //<editor-fold desc="Config Values" defaultstate="collapsed">
    private final ConfigurationSection section;
    private boolean isEnabled;
    private boolean isAutoUpdateEnabled;
    private Component name;
    private Component[] description;
    private String fileName;

    private ConfigurationSection dataSection;
    private String uuid;
    private String url;
    private String hash;
    private Component prompt;
    private boolean isRequired;

    private ConfigurationSection githubSection;
    private String user;
    private String repo;
    private String tag;
    //</editor-fold>

    //<editor-fold desc="Config Keys" defaultstate="collapsed">
    public static final String KEY_ENABLED =     "enabled";
    public static final String KEY_AUTO_UPDATE = "auto-update";
    public static final String KEY_NAME =        "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_FILE_NAME =   "file-name";

    public static final String KEY_DATA_SECTION = "data";
    public static final String KEY_UUID =         "uuid";
    public static final String KEY_URL =          "url";
    public static final String KEY_HASH =         "hash";
    public static final String KEY_PROMPT =       "prompt";
    public static final String KEY_REQUIRED =     "required";

    public static final String KEY_GITHUB_SECTION = "github";
    public static final String KEY_TOKEN =          "token";
    public static final String KEY_USER =           "user";
    public static final String KEY_REPO =           "repo";
    public static final String KEY_TAG =            "tag";
    //</editor-fold>

    ResourcePackLoader(
            final @NotNull File configFile,
            final @NotNull YamlConfiguration yaml,
            final @NotNull ConfigurationSection section,
            final @Nullable Function<ResourcePack.Entry, ResourcePack.Entry> onLoaded,
            final @Nullable BiFunction<ResourcePack.Entry, Throwable, ResourcePack.Entry> onFailed
    ) {
        this.configFile = configFile;
        this.yaml = yaml;
        this.section = section;
        this.onLoaded = onLoaded;
        this.onFailed = onFailed;
        this.onBuiltConsumers = new ObjectArrayList<>();
    }

    public @NotNull CompletableFuture<ResourcePack> load() throws PackLoadException, FatalPackLoadException {
        return this.setupBase()
                .thenCompose(ignored ->
                        this.isEnabled
                        ? this.setupBuilder()
                              .thenCompose(ResourcePackData.Builder::buildAsync)
                        : completedFuture(ResourcePackData.empty())
                )
                .thenApply(
                        data -> {
                            final var pack = create(
                                    data,
                                    this.name,
                                    this.description
                            );

                            if (this.onLoaded != null) {
                                final ResourcePack.Entry entry = this.onLoaded.apply(
                                        entry(
                                                this.section.getName(),
                                                pack
                                        )
                                );

                                if (entry != null) {
                                    return entry.getPack();
                                }
                            }

                            return pack;
                        }
                )
                .exceptionallyCompose(
                        throwable -> {
                            final String message = throwable.getMessage();
                            final PackLoadException exception = new PackLoadException(message);

                            if (this.onFailed != null) {
                                final ResourcePack.Entry newEntry =
                                        this.onFailed.apply(
                                                entry(
                                                        this.section.getName(),
                                                        create(this.name, this.description)
                                                ),
                                                exception
                                        );

                                if (newEntry != null) {
                                    return completedFuture(newEntry.getPack());
                                }
                            }

                            return CompletableFuture.failedFuture(exception);
                        }
                );
    }

    private @NotNull CompletableFuture<Void> setupBase() throws FatalPackLoadException {
        final String nameString = this.section.getString(KEY_NAME);

        if (ChatUtils.isBlank(nameString)) {
            throw new FatalPackLoadException("Name cannot be null or blank");
        }

        final var descriptionList = this.section.getStringList(KEY_DESCRIPTION);
        final int descriptionSize = descriptionList.size();

        this.isEnabled = this.section.getBoolean(KEY_ENABLED);
        this.isAutoUpdateEnabled = this.section.getBoolean(KEY_AUTO_UPDATE);
        this.name = MiniMessage.miniMessage().deserialize(nameString, TagResolver.standard());
        this.description = new Component[descriptionSize];

        for (int i = 0; i < descriptionSize; ++i) {
            this.description[i] =
                    MiniMessage.miniMessage().deserialize(
                            descriptionList.get(i),
                            TagResolver.standard()
                    );
        }

        if (!this.isEnabled) {
            return completedFuture(null);
        }

        this.fileName = this.section.getString(KEY_FILE_NAME);

        if (ChatUtils.isBlank(this.fileName)) {
            return this.failed("File name cannot be null or blank");
        }

        this.dataSection = this.section.getConfigurationSection(KEY_DATA_SECTION);

        if (this.dataSection == null) {
            return this.failed("Data section cannot be null");
        }

        this.uuid = this.dataSection.getString(KEY_UUID);

        if (ChatUtils.isBlank(this.uuid)) {
            this.onBuiltConsumers.add(
                    builder -> this.dataSection.set(KEY_UUID, builder.uuid().toString())
            );
        }

        this.url = this.dataSection.getString(KEY_URL);
        this.hash = this.dataSection.getString(KEY_HASH);
        final String promptString = dataSection.getString(KEY_PROMPT);

        this.prompt =
                ChatUtils.isBlank(promptString)
                ? null
                : MiniMessage.miniMessage().deserialize(promptString, TagResolver.standard());
        this.isRequired = this.dataSection.getBoolean(KEY_REQUIRED);

        this.githubSection = this.section.getConfigurationSection(KEY_GITHUB_SECTION);

        if (this.githubSection != null) {
            this.user = this.githubSection.getString(KEY_USER);
            this.repo = this.githubSection.getString(KEY_REPO);
            this.tag = this.githubSection.getString(KEY_TAG);
        }

        return completedFuture(null);
    }

    private @NotNull CompletableFuture<ResourcePackData.Builder> setupBuilder() throws IllegalArgumentException {
        final ResourcePackData.Builder builder = ResourcePackData.builder();

        if (ChatUtils.isNotBlank(this.url)) {
            if (this.isAutoUpdateEnabled) {
                this.onBuiltConsumers.add(
                        builder0 -> this.dataSection.set(KEY_HASH, builder0.hash())
                );
                builder
                .resourceManager(PackResourceManager.url(this.url));
            }
        } else {
            if (this.githubSection == null) {
                return this.failed("GitHub section cannot be null if URL is not provided");
            }

            this.user = this.githubSection.getString(KEY_USER);

            if (ChatUtils.isBlank(this.user)) {
                return this.failed("User cannot be null or blank");
            }

            this.repo = this.githubSection.getString(KEY_REPO);

            if (ChatUtils.isBlank(this.repo)) {
                return this.failed("Repo cannot be null or blank");
            }

            this.tag = this.githubSection.getString(KEY_TAG);

            this.onBuiltConsumers.add(
                    builder0 -> {
                        final GitHubPackResourceManager manager = (GitHubPackResourceManager) builder0.resourceManager();
                        final Tag tag = manager.getLatestTagNow();

                        if (tag != null) {
                            this.githubSection.set(KEY_TAG, tag.getName());
                        }

                        this.dataSection.set(KEY_HASH, builder0.hash());
                    }
            );
            builder
            .resourceManager(
                    PackResourceManager.github(
                            new File(this.configFile.getParent(), this.fileName),
                            this.user, this.repo, this.tag,
                            this.githubSection.getString(KEY_TOKEN)
                    )
            );
        }

        return completedFuture(
                builder
                .uuidString(this.uuid)
                .url(this.url)
                .hash(this.hash)
                .prompt(this.prompt)
                .required(this.isRequired)
                .autoUpdate(this.isAutoUpdateEnabled)
                .onBuilt(
                        builder0 -> {
                            if (this.onBuiltConsumers.isEmpty()) {
                                return;
                            }

                            synchronized (ResourcePackLoader.class) {
                                for (final var consumer : this.onBuiltConsumers) {
                                    consumer.accept(builder0);
                                }

                                try {
                                    this.yaml.save(this.configFile);
                                } catch (final Throwable e) {
                                    MSLogger.warning(
                                            "Failed to save the configuration file: " + this.configFile,
                                            e
                                    );
                                }
                            }
                        }
                )
        );
    }

    private <U> @NotNull CompletableFuture<U> failed(final @NotNull String message) {
        return CompletableFuture.failedFuture(new PackLoadException(message));
    }
}
