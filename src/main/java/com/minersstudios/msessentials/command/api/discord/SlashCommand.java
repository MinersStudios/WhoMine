package com.minersstudios.msessentials.command.api.discord;

import java.lang.annotation.*;

/**
 * All discord slash commands annotated using {@link SlashCommand} will be
 * registered automatically. Also, it must be implemented using
 * {@link SlashCommandExecutor}
 *
 * @see SlashCommandExecutor
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SlashCommand {}
