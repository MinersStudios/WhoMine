package com.minersstudios.mscore.status;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.Immutable;
import java.util.function.Consumer;
import java.util.function.Function;

@Immutable
abstract class ImplStatus implements Status {
    private final String key;
    private final Status.Priority priority;

    protected ImplStatus(
            final @StatusKey @NotNull String key,
            final @NotNull Priority priority
    ) {
        this.key = key;
        this.priority = priority;
    }

    @StatusKey
    @Override
    public final @NotNull String getKey() {
        return this.key;
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
        final int prime = 31;
        int result = 1;

        result = prime * result + this.key.hashCode();
        result = prime * result + this.priority.hashCode();

        return result;
    }

    @Contract("null -> false")
    @Override
    public boolean equals(final @Nullable Object obj) {
        if (this == obj) {
            return true;
        }

        if (
                obj == null
                || this.getClass() != obj.getClass()
        ) {
            return false;
        }

        final ImplStatus that = (ImplStatus) obj;

        return this.key.equals(that.key)
                && this.priority == that.priority;
    }

    @Override
    public @NotNull String toString() {
        return this.key + '{' + this.priority + '}';
    }

    @Override
    public void accept(
            final @NotNull Consumer<SuccessStatus> onSuccess,
            final @NotNull Consumer<FailureStatus> onFailure
    ) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Provided status is not supported");
    }

    @Override
    public <U> U apply(
            final Function<SuccessStatus, U> onSuccess,
            final Function<FailureStatus, U> onFailure
    ) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Provided status is not supported");
    }
}
