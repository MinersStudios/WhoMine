package com.github.minersstudios.msessentials.commands.admin.msessentials;

import com.github.minersstudios.mscore.logger.MSLogger;
import com.github.minersstudios.msessentials.MSEssentials;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

public class ReloadCommand {

    public static void runCommand(@NotNull CommandSender sender) {
        long time = System.currentTimeMillis();

        MSEssentials.getConfiguration().reload();
        MSLogger.fine(
                sender,
                Component.translatable(
                        "ms.command.msessentials.reload.success",
                        text(System.currentTimeMillis() - time)
                )
        );
    }
}
