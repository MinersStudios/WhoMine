package com.minersstudios.msessentials.command.impl.minecraft.admin.msessentials;

import com.minersstudios.mscore.language.LanguageRegistry;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.msessentials.MSEssentials;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

public final class ReloadCommand {

    public static boolean runCommand(
            final @NotNull MSEssentials plugin,
            final @NotNull CommandSender sender
    ) {
        final long time = System.currentTimeMillis();

        plugin.getConfiguration().reload();
        MSLogger.fine(
                sender,
                LanguageRegistry.Components.COMMAND_MSESSENTIALS_RELOAD_SUCCESS
                .args(text(System.currentTimeMillis() - time))
        );

        return true;
    }
}
