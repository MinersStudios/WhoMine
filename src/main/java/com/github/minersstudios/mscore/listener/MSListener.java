package com.github.minersstudios.mscore.listener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * All listeners annotated using {@link MSListener} will be registered automatically.
 * Also, must be extended by {@link AbstractMSListener}
 *
 * @see AbstractMSListener
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MSListener {}
