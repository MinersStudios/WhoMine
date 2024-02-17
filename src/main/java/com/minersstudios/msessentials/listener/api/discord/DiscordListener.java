package com.minersstudios.msessentials.listener.api.discord;

import java.lang.annotation.*;

/**
 * All event listeners annotated using {@link DiscordListener} will be
 * registered automatically. Also, must be extended by
 * {@link AbstractDiscordListener}
 *
 * @see AbstractDiscordListener
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DiscordListener {}
