package com.minersstudios.mscore.command.impl;

import com.minersstudios.mscore.language.LanguageRegistry;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.plugin.MSPlugin;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

public final class ReloadConfigCommand {

    public static boolean runCommand(final @NotNull CommandSender sender) {
        final long time = System.currentTimeMillis();

        MSPlugin.globalConfig().reload();
        MSLogger.fine(
                sender,
                LanguageRegistry.Components.COMMAND_MSCORE_RELOAD_CONFIG_SUCCESS
                .arguments(text(System.currentTimeMillis() - time))
        );

        return true;
    }
}
