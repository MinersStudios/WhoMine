package com.minersstudios.wholib.property.type;

import com.minersstudios.wholib.property.Property;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractShortProperty<T extends AbstractShortProperty<T>> implements Property<T> {

    public final @NotNull Type getType() {
        return Type.SHORT;
    }
}
