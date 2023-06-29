package com.github.minersstudios.mscore.commands;

import com.github.minersstudios.mscore.config.LanguageFile;
import com.github.minersstudios.mscore.utils.ChatUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

public class ReloadLanguageCommand {

    public static void runCommand(@NotNull CommandSender sender) {
        long time = System.currentTimeMillis();

        LanguageFile.reloadLanguage();
        ChatUtils.sendFine(
                sender,
                Component.translatable(
                        "ms.command.mscore.reload_language.success",
                        text(System.currentTimeMillis() - time)
                )
        );
    }
}
