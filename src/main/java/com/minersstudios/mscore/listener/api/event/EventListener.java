package com.minersstudios.mscore.listener.api.event;

import java.lang.annotation.*;

/**
 * All event listeners annotated using {@link EventListener} will be registered
 * automatically. Also, it must be extended by {@link AbstractEventListener}.
 *
 * @see AbstractEventListener
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EventListener {}
