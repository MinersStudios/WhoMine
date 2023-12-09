package com.minersstudios.mscore.commands;

import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.plugin.MSPlugin;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public final class ReloadConfigCommand {
    private static final TranslatableComponent RELOAD_CONFIG_SUCCESS = translatable("ms.command.mscore.reload_config.success");

    public static boolean runCommand(@NotNull CommandSender sender) {
        final long time = System.currentTimeMillis();

        MSPlugin.globalConfig().reload();
        MSLogger.fine(sender, RELOAD_CONFIG_SUCCESS.args(text(System.currentTimeMillis() - time)));

        return true;
    }
}
