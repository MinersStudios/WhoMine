package com.minersstudios.msitem.commands;

import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.msitem.MSItem;
import com.minersstudios.msitem.item.CustomItemType;
import com.minersstudios.msitem.item.renameable.RenameableItemRegistry;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class ReloadCommand {
    private static final TranslatableComponent RELOAD_SUCCESS = translatable("ms.command.msitem.reload.success");

    public static boolean runCommand(final @NotNull CommandSender sender) {
        final long time = System.currentTimeMillis();
        final Server server = sender.getServer();
        final var crafts = server.recipeIterator();

        while (crafts.hasNext()) {
            final Recipe recipe = crafts.next();

            if (
                    recipe instanceof final Keyed keyed
                    && CustomItemType.NAMESPACE.equals(keyed.getKey().getNamespace())
            ) {
                server.removeRecipe(new NamespacedKey(CustomItemType.NAMESPACE, keyed.getKey().getKey()));
            }
        }

        MSPlugin.getGlobalCache().customItemRecipes.clear();
        MSItem.getCache().renameableItemsMenu.clear();
        RenameableItemRegistry.unregisterAll();
        MSItem.getConfiguration().reload();
        MSLogger.fine(sender, RELOAD_SUCCESS.args(text(System.currentTimeMillis() - time)));
        return true;
    }
}
