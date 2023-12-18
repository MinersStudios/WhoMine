package com.minersstudios.mscore.listener.api.packet;

import com.minersstudios.mscore.listener.api.event.AbstractEventListener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * All packet listeners annotated using {@link PacketListener}
 * will be registered automatically. Also, must be extended by
 * {@link AbstractEventListener}
 *
 * @see AbstractEventListener
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PacketListener {}
