package com.minersstudios.msitem.command;

import com.minersstudios.mscore.language.LanguageRegistry;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.msitem.MSItem;
import com.minersstudios.msitem.api.renameable.RenameableItemRegistry;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

public final class ReloadCommand {

    public static boolean runCommand(
            final @NotNull MSItem plugin,
            final @NotNull CommandSender sender
    ) {
        final long time = System.currentTimeMillis();
        final Server server = sender.getServer();
        final var crafts = server.recipeIterator();

        while (crafts.hasNext()) {
            final Recipe recipe = crafts.next();

            if (
                    recipe instanceof final Keyed keyed
                    && MSItem.NAMESPACE.equals(keyed.getKey().getNamespace())
            ) {
                server.removeRecipe(new NamespacedKey(MSItem.NAMESPACE, keyed.getKey().getKey()));
            }
        }

        MSPlugin.globalCache().customItemRecipes.clear();
        plugin.getCache().getRenameableMenuItems().clear();
        RenameableItemRegistry.unregisterAll();
        plugin.getConfiguration().reload();
        MSLogger.fine(
                sender,
                LanguageRegistry.Components.COMMAND_MSITEM_RELOAD_SUCCESS
                .args(text(System.currentTimeMillis() - time))
        );

        return true;
    }
}