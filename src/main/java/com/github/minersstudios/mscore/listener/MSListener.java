package com.github.minersstudios.mscore.listener;

import org.bukkit.event.Listener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * All listeners implemented using this annotation will be registered automatically
 * <br>
 * All listeners must be implemented using {@link Listener}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MSListener {}
