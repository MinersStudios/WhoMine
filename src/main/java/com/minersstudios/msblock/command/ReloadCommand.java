package com.minersstudios.msblock.command;

import com.minersstudios.msblock.MSBlock;
import com.minersstudios.msblock.api.CustomBlockRegistry;
import com.minersstudios.mscore.language.LanguageRegistry;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.plugin.MSPlugin;
import org.bukkit.Keyed;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

public final class ReloadCommand {

    public static boolean runCommand(
            final @NotNull MSBlock plugin,
            final @NotNull CommandSender sender
    ) {
        final long time = System.currentTimeMillis();
        final Server server = sender.getServer();
        final var crafts = server.recipeIterator();

        while (crafts.hasNext()) {
            final Recipe recipe = crafts.next();

            if (
                    recipe instanceof final Keyed keyed
                    && MSBlock.NAMESPACE.equals(keyed.getKey().getNamespace())
            ) {
                server.removeRecipe(keyed.getKey());
            }
        }

        MSPlugin.globalCache().customBlockRecipes.clear();
        CustomBlockRegistry.unregisterAll();
        plugin.getConfiguration().reload();
        MSLogger.fine(
                sender,
                LanguageRegistry.Components.COMMAND_MSBLOCK_RELOAD_SUCCESS
                .args(text(System.currentTimeMillis() - time))
        );

        return true;
    }
}