package com.minersstudios.msessentials;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public final class MSEssentialsLoader implements PluginLoader {

    @Override
    public void classloader(final @NotNull PluginClasspathBuilder classpathBuilder) {
        final MavenLibraryResolver resolver = new MavenLibraryResolver();

        resolver.addRepository(new RemoteRepository.Builder("central", "default", "https://repo1.maven.org/maven2/").build());
        resolver.addDependency(new Dependency(new DefaultArtifact("net.dv8tion:JDA:5.0.0-beta.18"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("com.fasterxml.jackson.core:jackson-annotations:2.16.0"), null));

        classpathBuilder.addLibrary(resolver);
    }
}
