package com.github.minersstudios.msutils.commands.admin.msutils;

import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.msutils.MSUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

public class ReloadCommand {

    public static void runCommand(@NotNull CommandSender sender) {
        long time = System.currentTimeMillis();

        MSUtils.getConfigCache().playerAnomalyActionMap.clear();
        MSUtils.reloadConfigs();
        ChatUtils.sendFine(
                sender,
                Component.translatable(
                        "ms.command.msutils.reload.success",
                        text(System.currentTimeMillis() - time)
                )
        );
    }
}
