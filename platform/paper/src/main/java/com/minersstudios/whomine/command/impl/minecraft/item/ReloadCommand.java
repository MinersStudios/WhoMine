package com.minersstudios.whomine.command.impl.minecraft.item;

import com.minersstudios.wholib.paper.WhoMine;
import com.minersstudios.wholib.key.Resource;
import com.minersstudios.wholib.locale.Translations;
import com.minersstudios.wholib.paper.utility.MSLogger;
import com.minersstudios.wholib.paper.custom.item.renameable.RenameableItemRegistry;
import org.bukkit.Keyed;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

public final class ReloadCommand {

    public static boolean runCommand(
            final @NotNull WhoMine plugin,
            final @NotNull CommandSender sender
    ) {
        final long time = System.currentTimeMillis();
        final Server server = sender.getServer();
        final var crafts = server.recipeIterator();

        while (crafts.hasNext()) {
            final Recipe recipe = crafts.next();

            if (
                    recipe instanceof final Keyed keyed
                    && Resource.WMITEM.equals(keyed.getKey().getNamespace())
            ) {
                server.removeRecipe(keyed.getKey());
            }
        }

        plugin.getCache().getCustomItemRecipes().clear();
        plugin.getCache().getRenameableMenuItems().clear();
        RenameableItemRegistry.unregisterAll();
        plugin.getConfiguration().reload();
        MSLogger.fine(
                sender,
                Translations.COMMAND_MSITEM_RELOAD_SUCCESS.asTranslatable()
                .arguments(text(System.currentTimeMillis() - time))
        );

        return true;
    }
}
