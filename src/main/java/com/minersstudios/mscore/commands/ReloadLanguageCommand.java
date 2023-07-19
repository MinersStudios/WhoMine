package com.minersstudios.mscore.commands;

import com.minersstudios.mscore.config.LanguageFile;
import com.minersstudios.mscore.logger.MSLogger;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

public class ReloadLanguageCommand {

    public static void runCommand(@NotNull CommandSender sender) {
        long time = System.currentTimeMillis();

        LanguageFile.reloadLanguage();
        MSLogger.fine(
                sender,
                Component.translatable(
                        "ms.command.mscore.reload_language.success",
                        text(System.currentTimeMillis() - time)
                )
        );
    }
}
