package com.minersstudios.mscore.listener.packet;

import com.minersstudios.mscore.listener.event.AbstractMSListener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * All packet listeners annotated using {@link MSPacketListener}
 * will be registered automatically. Also, must be extended by
 * {@link AbstractMSListener}
 *
 * @see AbstractMSListener
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MSPacketListener {}
