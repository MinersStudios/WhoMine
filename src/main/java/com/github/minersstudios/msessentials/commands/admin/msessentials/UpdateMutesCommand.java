package com.github.minersstudios.msessentials.commands.admin.msessentials;

import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.msessentials.MSEssentials;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

public class UpdateMutesCommand {

    public static void runCommand(@NotNull CommandSender sender) {
        long time = System.currentTimeMillis();

        MSEssentials.getConfigCache().muteMap.reloadMutes();
        ChatUtils.sendFine(
                sender,
                Component.translatable(
                        "ms.command.msessentials.update_mutes.success",
                        text(System.currentTimeMillis() - time)
                )
        );
    }
}