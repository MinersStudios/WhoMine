package com.minersstudios.mscustoms.custom.item.damageable;

import org.jetbrains.annotations.NotNull;

/**
 * The {@code Damageable} interface defines a contract for building instances of
 * {@link DamageableItem}. Classes implementing this interface are expected to
 * provide a method for constructing damageable items.
 *
 * @see DamageableItem
 */
public interface Damageable {

    /**
     * Build and return an instance of {@link DamageableItem} based on the
     * implementation's logic
     *
     * @return A constructed {@link DamageableItem} instance
     */
    @NotNull DamageableItem buildDamageable();
}
