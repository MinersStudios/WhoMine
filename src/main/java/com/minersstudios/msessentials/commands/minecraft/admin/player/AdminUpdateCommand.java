package com.minersstudios.msessentials.commands.minecraft.admin.player;

import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.msessentials.player.PlayerInfo;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public final class AdminUpdateCommand {
    private static final TranslatableComponent UPDATE_SUCCESS = translatable("ms.command.player.update.success");

    public static boolean runCommand(
            final @NotNull CommandSender sender,
            final @NotNull PlayerInfo playerInfo
    ) {
        playerInfo.update();
        MSLogger.fine(
                sender,
                UPDATE_SUCCESS.args(
                        playerInfo.getGrayIDGreenName(),
                        text(playerInfo.getNickname())
                )
        );
        return true;
    }
}
