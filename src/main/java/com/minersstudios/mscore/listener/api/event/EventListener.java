package com.minersstudios.mscore.listener.api.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * All event listeners annotated using {@link EventListener} will be registered
 * automatically. Also, it must be extended by {@link AbstractEventListener}.
 *
 * @see AbstractEventListener
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EventListener {}
