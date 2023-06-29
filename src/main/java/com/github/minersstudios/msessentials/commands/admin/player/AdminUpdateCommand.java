package com.github.minersstudios.msessentials.commands.admin.player;

import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class AdminUpdateCommand {

    public static boolean runCommand(
            @NotNull CommandSender sender,
            @NotNull PlayerInfo playerInfo
    ) {
        playerInfo.update();
        ChatUtils.sendFine(
                sender,
                Component.translatable(
                        "ms.command.player.update.success",
                        playerInfo.getGrayIDGreenName(),
                        Component.text(playerInfo.getNickname())
                )
        );
        return true;
    }
}
