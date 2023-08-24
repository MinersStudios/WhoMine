package com.minersstudios.msessentials.commands.admin.msessentials;

import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.msessentials.MSEssentials;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class ReloadCommand {
    private static final TranslatableComponent RELOAD_SUCCESS = translatable("ms.command.msessentials.reload.success");

    public static boolean runCommand(@NotNull CommandSender sender) {
        final long time = System.currentTimeMillis();

        MSEssentials.getConfiguration().reload();
        MSLogger.fine(sender, RELOAD_SUCCESS.args(text(System.currentTimeMillis() - time)));
        return true;
    }
}
