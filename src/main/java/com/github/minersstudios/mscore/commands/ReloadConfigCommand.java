package com.github.minersstudios.mscore.commands;

import com.github.minersstudios.mscore.MSCore;
import com.github.minersstudios.mscore.config.Config;
import com.github.minersstudios.mscore.utils.ChatUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

public class ReloadConfigCommand {

    public static void runCommand(@NotNull CommandSender sender) {
        long time = System.currentTimeMillis();

        MSCore.getConfiguration().reload();
        ChatUtils.sendFine(
                sender,
                Component.translatable(
                        "ms.command.mscore.reload_config.success",
                        text(System.currentTimeMillis() - time)
                )
        );
    }
}
