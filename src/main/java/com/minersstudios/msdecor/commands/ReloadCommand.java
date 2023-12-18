package com.minersstudios.msdecor.commands;

import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.msdecor.MSDecor;
import com.minersstudios.msdecor.api.CustomDecorType;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.Keyed;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public final class ReloadCommand {
    private static final TranslatableComponent RELOAD_SUCCESS = translatable("ms.command.msdecor.reload.success");

    public static boolean runCommand(
            final @NotNull MSDecor plugin,
            final @NotNull CommandSender sender
    ) {
        final long time = System.currentTimeMillis();
        final Server server = sender.getServer();
        final var crafts = server.recipeIterator();

        while (crafts.hasNext()) {
            final Recipe recipe = crafts.next();

            if (
                    recipe instanceof final Keyed keyed
                    && CustomDecorType.NAMESPACE.equals(keyed.getKey().getNamespace())
            ) {
                server.removeRecipe(keyed.getKey());
            }
        }

        MSPlugin.globalCache().customDecorRecipes.clear();
        plugin.getConfiguration().reload();
        MSLogger.fine(sender, RELOAD_SUCCESS.args(text(System.currentTimeMillis() - time)));

        return true;
    }
}
