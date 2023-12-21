package com.minersstudios.msessentials.command.api.discord;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * All discord slash commands annotated using {@link SlashCommand} will be
 * registered automatically. Also, it must be implemented using
 * {@link SlashCommandExecutor}
 *
 * @see SlashCommandExecutor
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SlashCommand {}
