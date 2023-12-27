package com.minersstudios.mscore.plugin.status;

import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;

@Immutable
abstract class ImplPluginStatus implements PluginStatus {
    private final String name;
    private final PluginStatus.Priority priority;

    protected ImplPluginStatus(
            final @NotNull String name,
            final @NotNull Priority priority
    ) {
        this.name = name;
        this.priority = priority;
    }

    @Override
    public final @NotNull String getName() {
        return this.name;
    }

    @Override
    public final @NotNull Priority getPriority() {
        return this.priority;
    }

    @Override
    public final boolean isHighPriority() {
        return this.priority == Priority.HIGH;
    }

    @Override
    public final boolean isLowPriority() {
        return this.priority == Priority.LOW;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.priority);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final ImplPluginStatus that = (ImplPluginStatus) obj;

        return this.name.equals(that.name)
                && this.priority == that.priority;
    }

    @Override
    public @NotNull String toString() {
        return this.name + '{' + this.priority + '}';
    }
}
