package com.minersstudios.mscore.listener.api.packet;

import com.minersstudios.mscore.listener.api.event.AbstractEventListener;

import java.lang.annotation.*;

/**
 * All packet listeners annotated using {@link PacketListener} will be
 * registered automatically. Also, it must be extended by
 * {@link AbstractEventListener}.
 *
 * @see AbstractEventListener
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PacketListener {}
