package com.minersstudios.wholib.status;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.Immutable;
import java.util.function.Consumer;
import java.util.function.Function;

@Immutable
abstract class ImplStatus implements Status {

    private final String key;
    private final StatusPriority priority;

    protected ImplStatus(
            final @StatusKey @NotNull String key,
            final @NotNull StatusPriority priority
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
    public final @NotNull StatusPriority getPriority() {
        return this.priority;
    }

    @Override
    public final boolean isHighPriority() {
        return this.priority == StatusPriority.HIGH;
    }

    @Override
    public final boolean isLowPriority() {
        return this.priority == StatusPriority.LOW;
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
        return this == obj
                || (
                        obj instanceof Status that
                        && this.key.equals(that.getKey())
                        && this.priority == that.getPriority()
                );
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
