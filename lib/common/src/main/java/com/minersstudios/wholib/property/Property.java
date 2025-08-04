package com.minersstudios.wholib.property;

import com.minersstudios.wholib.key.ResourceKeyed;
import com.minersstudios.wholib.property.type.*;
import org.jetbrains.annotations.NotNull;

public interface Property<T extends Property<T>> extends ResourceKeyed, Comparable<Property<?>> {

    @NotNull PropertyAdapter<T> getAdapter();

    default @NotNull Type getType() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("#getType() is not implemented");
    }

    @Override
    default int compareTo(final @NotNull Property<?> that) throws UnsupportedOperationException {
        final boolean thisIsPrimitive = this.getType().isPrimitive();
        final boolean thatIsPrimitive = that.getType().isPrimitive();

        return (thisIsPrimitive && thatIsPrimitive)
               || (!thisIsPrimitive && !thatIsPrimitive)
               ? this.getResourceKey().compareTo(that.getResourceKey())
               : thisIsPrimitive ? -1 : 1;
    }

    enum Type {
        BOOLEAN  (AbstractBooleanProperty.class),
        BYTE     (AbstractByteProperty.class),
        CHARACTER(AbstractCharacterProperty.class),
        DOUBLE   (AbstractDoubleProperty.class),
        FLOAT    (AbstractFloatProperty.class),
        INTEGER  (AbstractIntegerProperty.class),
        LONG     (AbstractLongProperty.class),
        SHORT    (AbstractShortProperty.class),
        STRING   (AbstractStringProperty.class),
        ARRAY    (AbstractArrayProperty.class, false),
        GROUP    (AbstractGroupProperty.class, false);

        private final Class<? extends Property<?>> propertyClass;
        private final boolean isPrimitive;

        <P extends Property<P>> Type(final @NotNull Class<P> propertyClass) {
            this(propertyClass, true);
        }

        <P extends Property<P>> Type(
                final @NotNull Class<P> propertyClass,
                final boolean isPrimitive
        ) {
            this.propertyClass = propertyClass;
            this.isPrimitive = isPrimitive;
        }

        public @NotNull Class<? extends Property<?>> getPropertyClass() {
            return this.propertyClass;
        }

        public boolean isPrimitive() {
            return this.isPrimitive;
        }
    }
}
