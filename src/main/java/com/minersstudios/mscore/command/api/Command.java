package com.minersstudios.mscore.command.api;

import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * All commands annotated using {@link Command} will be registered automatically.
 * Also, must be implemented using {@link CommandExecutor}
 *
 * @see CommandExecutor
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Command {

    /**
     * @return Command name
     */
    @NotNull String command();

    /**
     * @return Command usage, default is empty
     */
    @NotNull String usage() default "";

    /**
     * @return Command description, default is empty
     */
    @NotNull String description() default "";

    /**
     * @return Command aliases, default is empty
     */
    String @NotNull [] aliases() default {};

    /**
     * @return Command permission, default is empty
     */
    @NotNull String permission() default "";

    /**
     * @return Command permission default, default is {@link PermissionDefault#NOT_OP}
     */
    @NotNull PermissionDefault permissionDefault() default PermissionDefault.NOT_OP;

    /**
     * This and {@link #permissionParentValues()} must have the same length
     *
     * @return Command permission parent keys
     */
    String @NotNull [] permissionParentKeys() default {};

    /**
     * This and {@link #permissionParentKeys()} must have the same length
     *
     * @return Command permission parent values
     */
    boolean[] permissionParentValues() default {};

    /**
     * @return True if command can be executed only by player,
     *         default is false
     */
    boolean playerOnly() default false;
}
