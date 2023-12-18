package com.minersstudios.msessentials.listener.api.discord;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * All event listeners annotated using {@link DiscordListener}
 * will be registered automatically. Also, must be extended
 * by {@link AbstractDiscordListener}
 *
 * @see AbstractDiscordListener
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DiscordListener {}
