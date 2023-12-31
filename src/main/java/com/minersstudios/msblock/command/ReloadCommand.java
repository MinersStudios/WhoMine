package com.minersstudios.msblock.command;

import com.minersstudios.msblock.MSBlock;
import com.minersstudios.msblock.api.CustomBlockRegistry;
import com.minersstudios.mscore.language.LanguageRegistry;
import com.minersstudios.mscore.plugin.MSLogger;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

public final class ReloadCommand {

    public static boolean runCommand(
            final @NotNull MSBlock plugin,
            final @NotNull CommandSender sender
    ) {
        final long time = System.currentTimeMillis();
        final Server server = sender.getServer();

        for (final var data : CustomBlockRegistry.customBlockDataCollection()) {
            data.unregisterRecipes(server);
        }

        CustomBlockRegistry.unregisterAll();
        plugin.getConfiguration().reload();
        MSLogger.fine(
                sender,
                LanguageRegistry.Components.COMMAND_MSBLOCK_RELOAD_SUCCESS
                .arguments(text(System.currentTimeMillis() - time))
        );

        return true;
    }
}
