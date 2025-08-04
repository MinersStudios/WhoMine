package com.minersstudios.wholib.property;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

public class PropertyStorage implements Iterable<Property<?>> {

    private final List<Property<?>> propertyList;

    private PropertyStorage() {
        this.propertyList = new ObjectArrayList<>();
    }

    @Override
    public @NotNull Iterator<Property<?>> iterator() {
        return this.propertyList.iterator();
    }

    public int size() {
        return this.propertyList.size();
    }

    @Contract(" -> this")
    public @NotNull PropertyStorage clear() {
        this.propertyList.clear();

        return this;
    }

    public boolean isEmpty() {
        return this.propertyList.isEmpty();
    }

    @Contract(" -> new")
    public static @NotNull Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final List<Property<?>> propertyList;

        private Builder() {
            this.propertyList = new ObjectArrayList<>();
        }

        @Contract("_ -> this")
        public @NotNull Builder store(final @NotNull Property<?> property) {
            this.propertyList.add(property);

            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder remove(final @NotNull Property<?> property) {
            this.propertyList.remove(property);

            return this;
        }

        @Contract(" -> new")
        public @NotNull PropertyStorage build() {
            final var propertyStorage = new PropertyStorage();

            propertyStorage.propertyList.addAll(this.propertyList);
            propertyStorage.propertyList.sort(Property::compareTo);

            return propertyStorage;
        }
    }
}
