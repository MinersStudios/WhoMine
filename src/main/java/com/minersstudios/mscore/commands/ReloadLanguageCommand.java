package com.minersstudios.mscore.commands;

import com.minersstudios.mscore.plugin.config.LanguageFile;
import com.minersstudios.mscore.logger.MSLogger;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class ReloadLanguageCommand {
    private static final TranslatableComponent RELOAD_LANG_SUCCESS = translatable("ms.command.mscore.reload_language.success");

    public static boolean runCommand(@NotNull CommandSender sender) {
        long time = System.currentTimeMillis();

        LanguageFile.reloadLanguage();
        MSLogger.fine(sender, RELOAD_LANG_SUCCESS.args(text(System.currentTimeMillis() - time)));
        return true;
    }
}
