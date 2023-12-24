package com.minersstudios.mscore.inventory.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * All event listeners annotated using {@link InventoryHolder} will be registered
 * automatically. Also, it must be extended by {@link AbstractInventoryHolder}.
 *
 * @see AbstractInventoryHolder
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface InventoryHolder {}
