package com.minersstudios.msessentials.discord.listener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * All event listeners annotated using {@link MSDiscordListener}
 * will be registered automatically. Also, must be extended
 * by {@link AbstractMSDiscordListener}
 *
 * @see AbstractMSDiscordListener
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MSDiscordListener {}
